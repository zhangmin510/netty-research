package name.zhangmin.netty.study.common.keepalive;

import name.zhangmin.netty.study.common.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
