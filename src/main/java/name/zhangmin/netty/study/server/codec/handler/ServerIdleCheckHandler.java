package name.zhangmin.netty.study.server.codec.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangmin.name
 * @date 2020/3/14
 */
@Slf4j
public class ServerIdleCheckHandler extends IdleStateHandler {

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            log.info("idle check happen, close connection");
            ctx.close();
            return;
        }
    }

    public ServerIdleCheckHandler() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }
}
