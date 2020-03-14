package name.zhangmin.netty.study.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import name.zhangmin.netty.study.common.ResponseMessage;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 二次编码码器，转换业务消息
 *
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class OrderProtocolEncoder extends MessageToMessageEncoder<ResponseMessage> {


    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseMessage msg, List<Object> out) {
        ByteBuf buffer = ctx.alloc().buffer();
        msg.encode(buffer);

        out.add(buffer);
    }
}
