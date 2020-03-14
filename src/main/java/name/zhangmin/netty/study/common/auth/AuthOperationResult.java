package name.zhangmin.netty.study.common.auth;

import name.zhangmin.netty.study.common.OperationResult;
import lombok.Data;

@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;

}
