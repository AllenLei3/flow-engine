package com.hellobike.finance.flow.engine.model.node;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.execute.FlowNodeResult;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import com.hellobike.finance.flow.engine.execute.FlowStatus;
import com.hellobike.finance.flow.engine.model.Flow;

/**
 * 暂停节点
 * 入线: 零、一个、多个
 * 出线: 一个、多个
 *
 * @author 徐磊080
 */
public abstract class SuspendFlowNode extends FlowNode {

    @Override
    public void build(Flow flow, FlowConfiguration configuration) throws FlowBuildException {
        super.build(flow, configuration);
        if (getTargetLines().isEmpty()) {
            throw new FlowBuildException("SUSPEND节点 [" + getName() + "] 至少有一条出线!");
        }
    }

    @Override
    protected void nodeExtension(FlowContext context, FlowNodeResult nodeResult, FlowResponse response) {
        nodeResult.setSuspendId(suspendId(context));
        response.setStatus(FlowStatus.SUSPEND);
    }

    /**
     * 返回暂停ID,该ID用于后续驱动流程继续执行
     */
    protected abstract String suspendId(FlowContext context);
}
