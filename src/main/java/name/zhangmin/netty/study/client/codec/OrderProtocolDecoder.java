package name.zhangmin.netty.study.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import name.zhangmin.netty.study.common.ResponseMessage;
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

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.decode(msg);

        out.add(responseMessage);
    }
}
