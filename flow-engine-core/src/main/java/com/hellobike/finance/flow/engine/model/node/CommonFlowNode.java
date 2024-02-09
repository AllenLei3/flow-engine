package com.hellobike.finance.flow.engine.model.node;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.model.Flow;

/**
 * 普通节点
 * 入线: 零、一个、多个
 * 出线: 零、一个
 *
 * @author 徐磊080
 */
public abstract class CommonFlowNode extends FlowNode {

    @Override
    public void build(Flow flow, FlowConfiguration configuration) throws FlowBuildException {
        super.build(flow, configuration);
        if (targetLines.size() > 1) {
            throw new FlowBuildException("COMMON节点 [" + name + "] 最多只能有一条出线!");
        }
    }

}
