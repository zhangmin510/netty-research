package name.zhangmin.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import name.zhangmin.netty.study.client.dispatch.RequestPendingCenter;
import name.zhangmin.netty.study.common.ResponseMessage;

/**
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class ResponseDispatcherHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    private RequestPendingCenter requestPendingCenter;

    public ResponseDispatcherHandler(RequestPendingCenter requestPendingCenter) {
        this.requestPendingCenter = requestPendingCenter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
        requestPendingCenter.set(msg.getMessageHeader().getStreamId(), msg.getMessageBody());
    }
}
