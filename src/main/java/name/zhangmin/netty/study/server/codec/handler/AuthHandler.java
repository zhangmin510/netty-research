package name.zhangmin.netty.study.server.codec.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import name.zhangmin.netty.study.common.Operation;
import name.zhangmin.netty.study.common.RequestMessage;
import name.zhangmin.netty.study.common.auth.AuthOperation;
import name.zhangmin.netty.study.common.auth.AuthOperationResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangmin.name
 * @date 2020/3/14
 */
@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        try {
            Operation operation = msg.getMessageBody();
            if (operation instanceof AuthOperation) {
                AuthOperation cast = AuthOperation.class.cast(operation);
                AuthOperationResult authOperationResult = cast.execute();
                if (authOperationResult.isPassAuth()) {
                    log.info("auth pass");
                } else {
                    log.error("first msg is auth, close connection");
                    ctx.close();
                }

            } else {
                log.error("first msg is auth, close connection");
                ctx.close();
            }
        } catch (Exception e) {
            log.error("first msg is auth, close connection");
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }
    }
}
