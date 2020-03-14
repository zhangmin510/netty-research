package name.zhangmin.netty.study.client.codec;

import io.netty.channel.ChannelHandlerContext;
import name.zhangmin.netty.study.common.Operation;
import name.zhangmin.netty.study.common.RequestMessage;
import name.zhangmin.netty.study.util.IdUtil;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Operation msg, List<Object> out) {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), msg);

        out.add(requestMessage);
    }
}
