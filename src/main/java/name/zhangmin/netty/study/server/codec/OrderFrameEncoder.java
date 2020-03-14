package name.zhangmin.netty.study.server.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 一次编码器，处理半包，粘包问题
 *
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class OrderFrameEncoder extends LengthFieldPrepender {

    public OrderFrameEncoder() {
        super(2);
    }
}
