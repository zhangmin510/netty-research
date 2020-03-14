package name.zhangmin.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import name.zhangmin.netty.study.client.codec.*;
import name.zhangmin.netty.study.client.dispatch.ClientIdleCheckHandler;
import name.zhangmin.netty.study.client.dispatch.KeepaliveHandler;
import name.zhangmin.netty.study.client.dispatch.OperationResultFuture;
import name.zhangmin.netty.study.client.dispatch.RequestPendingCenter;
import name.zhangmin.netty.study.client.handler.ResponseDispatcherHandler;
import name.zhangmin.netty.study.common.OperationResult;
import name.zhangmin.netty.study.common.RequestMessage;
import name.zhangmin.netty.study.common.auth.AuthOperation;
import name.zhangmin.netty.study.common.order.OrderOperation;
import name.zhangmin.netty.study.util.IdUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.net.ssl.SSLException;
import java.util.concurrent.ExecutionException;

/**
 * 客户端
 *
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class OrderClient {

    public static void main(String[] args) throws ExecutionException, InterruptedException, SSLException {
        Bootstrap bootstrap = new Bootstrap();

        // 请求响应分发器
        final RequestPendingCenter requestPendingCenter = new RequestPendingCenter();

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();

        //SslContext sslContext = SslContextBuilder.forClient().build();

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ClientIdleCheckHandler());
                //pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast(keepaliveHandler);
                pipeline.addLast(new ResponseDispatcherHandler(requestPendingCenter));
                pipeline.addLast(new OperationToRequestMessageEncoder());
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            }
        });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);

        channelFuture.sync();

        long streamId = IdUtil.nextId();
        RequestMessage requestMessage = new RequestMessage(streamId, new OrderOperation(1001, "土豆"));

        OperationResultFuture operationResultFuture = new OperationResultFuture();
        requestPendingCenter.add(streamId, operationResultFuture);

        AuthOperation authOperation = new AuthOperation("admin", "admin");
        channelFuture.channel().writeAndFlush(new RequestMessage(IdUtil.nextId(), authOperation));



        for (int i = 0; i < 10; i++) {
            channelFuture.channel().writeAndFlush(requestMessage);
        }

        OperationResult operationResult = operationResultFuture.get();
        System.out.println(operationResult);

        // 使用OperationToRequestMessageEncoder处理
        // OrderOperation orderOperation = new OrderOperation(1001, "土豆");
        // channelFuture.channel().writeAndFlush(orderOperation);

        channelFuture.channel().closeFuture().get();
    }
}
