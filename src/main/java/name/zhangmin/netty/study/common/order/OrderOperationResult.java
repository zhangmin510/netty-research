package name.zhangmin.netty.study.common.order;

import name.zhangmin.netty.study.common.OperationResult;
import lombok.Data;

@Data
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean complete;

}
