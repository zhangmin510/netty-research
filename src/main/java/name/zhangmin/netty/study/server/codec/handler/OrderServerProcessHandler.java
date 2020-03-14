package name.zhangmin.netty.study.server.codec.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import name.zhangmin.netty.study.common.Operation;
import name.zhangmin.netty.study.common.OperationResult;
import name.zhangmin.netty.study.common.RequestMessage;
import name.zhangmin.netty.study.common.ResponseMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端业务处理
 *
 * @author zhangmin.name
 * @date 2020/3/1
 */
@Slf4j
public class OrderServerProcessHandler extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) {
        // 伪造内存泄漏
        // ByteBuf buffer = ctx.alloc().buffer();

        Operation operation= msg.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(msg.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        // 判断是否可以写
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(responseMessage);
        } else {
            log.error("message dropped");
        }

    }
}
