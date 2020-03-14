package name.zhangmin.netty.study.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 一次解码器，处理半包，粘包问题
 *
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
