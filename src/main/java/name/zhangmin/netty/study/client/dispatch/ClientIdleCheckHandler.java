package name.zhangmin.netty.study.client.dispatch;

import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangmin.name
 * @date 2020/3/14
 */
@Slf4j
public class ClientIdleCheckHandler extends IdleStateHandler {

    public ClientIdleCheckHandler() {
        super(0, 5, 0, TimeUnit.SECONDS);
    }
}
