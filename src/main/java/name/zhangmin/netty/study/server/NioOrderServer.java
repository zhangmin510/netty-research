package name.zhangmin.netty.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import name.zhangmin.netty.study.server.codec.OrderFrameDecoder;
import name.zhangmin.netty.study.server.codec.OrderFrameEncoder;
import name.zhangmin.netty.study.server.codec.OrderProtocolDecoder;
import name.zhangmin.netty.study.server.codec.OrderProtocolEncoder;
import name.zhangmin.netty.study.server.codec.handler.OrderServerProcessHandler;
import name.zhangmin.netty.study.server.handler.MetricHandler;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

import java.util.concurrent.ExecutionException;

/**
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class NioOrderServer {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));

        // 设置线程名称
        NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));

        serverBootstrap.group(boss, worker);

        // 设置系统参数
        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(NioChannelOption.SO_BACKLOG, 1024);

        UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));
        NioEventLoopGroup executors = new NioEventLoopGroup(0, new DefaultThreadFactory("Nio"));

        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(new NioEventLoopGroup(),
                100 * 1024 * 1024, 100 * 1024 * 1024);

        MetricHandler metricHandler = new MetricHandler();
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();

                // 放到不同的位置来方便打印日志
                pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                pipeline.addLast("tsHandler", globalTrafficShapingHandler);
                // 设置handler名称
                pipeline.addLast("frameDecoder", new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast("metricHandler", metricHandler);

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
