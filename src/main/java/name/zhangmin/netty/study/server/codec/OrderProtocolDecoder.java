package name.zhangmin.netty.study.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import name.zhangmin.netty.study.common.RequestMessage;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 二次解码器，转化成业务的消息
 *
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.decode(msg);

        out.add(requestMessage);
    }
}
