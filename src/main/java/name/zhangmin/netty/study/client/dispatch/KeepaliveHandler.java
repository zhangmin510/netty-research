package name.zhangmin.netty.study.client.dispatch;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import name.zhangmin.netty.study.common.RequestMessage;
import name.zhangmin.netty.study.common.keepalive.KeepaliveOperation;
import name.zhangmin.netty.study.util.IdUtil;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangmin.name
 * @date 2020/3/14
 */
@Slf4j
@ChannelHandler.Sharable
public class KeepaliveHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.info("write idle happen, write keepalive msg");
            KeepaliveOperation keepaliveOperation = new KeepaliveOperation();
            RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), keepaliveOperation);
            ctx.writeAndFlush(requestMessage);
        }
        super.userEventTriggered(ctx, evt);
    }
}
