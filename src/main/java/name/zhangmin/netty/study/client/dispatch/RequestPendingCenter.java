package name.zhangmin.netty.study.client.dispatch;

import com.google.common.collect.Maps;
import name.zhangmin.netty.study.common.OperationResult;

import java.util.Map;
import java.util.Objects;

/**
 * @author zhangmin.name
 * @date 2020/3/1
 */
public class RequestPendingCenter {

    private Map<Long, OperationResultFuture> map = Maps.newConcurrentMap();

    public void add(Long streamId, OperationResultFuture future) {
        map.put(streamId, future);
    }

    public void set(Long streamId, OperationResult operationResult) {
        OperationResultFuture operationResultFuture = map.get(streamId);

        if (Objects.nonNull(operationResultFuture)) {
            operationResultFuture.setSuccess(operationResult);
            map.remove(streamId);
        }
    }
}
