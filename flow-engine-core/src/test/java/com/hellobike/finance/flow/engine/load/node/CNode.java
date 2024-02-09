package com.hellobike.finance.flow.engine.load.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.model.node.CommonFlowNode;

/**
 * @author 徐磊080
 */
public class CNode extends CommonFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("执行C节点");
    }
}
