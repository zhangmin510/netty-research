package name.zhangmin.netty.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.kqueue.KQueueChannelOption;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import name.zhangmin.netty.study.server.codec.OrderFrameDecoder;
import name.zhangmin.netty.study.server.codec.OrderFrameEncoder;
import name.zhangmin.netty.study.server.codec.OrderProtocolDecoder;
import name.zhangmin.netty.study.server.codec.OrderProtocolEncoder;
import name.zhangmin.netty.study.server.codec.handler.AuthHandler;
import name.zhangmin.netty.study.server.codec.handler.OrderServerProcessHandler;
import name.zhangmin.netty.study.server.codec.handler.ServerIdleCheckHandler;
import name.zhangmin.netty.study.server.handler.MetricHandler;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;

/**
 * 使用本地的实现
 *
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class KQueueOrderServer {
    public static void main(String[] args) throws InterruptedException, ExecutionException, CertificateException, SSLException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(KQueueServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));

        // 设置线程名称
        KQueueEventLoopGroup boss = new KQueueEventLoopGroup(0, new DefaultThreadFactory("boss"));
        KQueueEventLoopGroup worker = new KQueueEventLoopGroup(0, new DefaultThreadFactory("worker"));

        serverBootstrap.group(boss, worker);

        // 设置系统参数
        serverBootstrap.childOption(KQueueChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(KQueueChannelOption.SO_BACKLOG, 1024);

        UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));
        //KQueueEventLoopGroup executors = new KQueueEventLoopGroup(0, new DefaultThreadFactory("KQueue"));

        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(new KQueueEventLoopGroup(),
                100 * 1024 * 1024, 100 * 1024 * 1024);

        IpSubnetFilterRule ipSubnetFilterRule = new IpSubnetFilterRule("127.1.0.1", 16, IpFilterRuleType.REJECT);
        RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(ipSubnetFilterRule);

        AuthHandler authHandler = new AuthHandler();

        MetricHandler metricHandler = new MetricHandler();

        // 增加ssl
        //SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
        //System.out.println(selfSignedCertificate.certificate());

        //SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();

        serverBootstrap.childHandler(new ChannelInitializer<KQueueSocketChannel>() {
            @Override
            protected void initChannel(KQueueSocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();

                // 放到不同的位置来方便打印日志
                pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                pipeline.addLast("ipFilter", ruleBasedIpFilter);
                pipeline.addLast("tsHandler", globalTrafficShapingHandler);

                pipeline.addLast("idleCheckHandler", new ServerIdleCheckHandler());

                //SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                //pipeline.addLast("ssl", sslHandler);
                // 设置handler名称
                pipeline.addLast("frameDecoder", new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast("metricHandler", metricHandler);
                pipeline.addLast("authHandler", authHandler);
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                // 写数据flush加强
                pipeline.addLast("flushEnhance", new FlushConsolidationHandler(5, true));

                pipeline.addLast(business, new OrderServerProcessHandler());
                //pipeline.addLast(executors, new OrderServerProcessHandler());

            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

        channelFuture.channel().closeFuture().get();

    }
}
