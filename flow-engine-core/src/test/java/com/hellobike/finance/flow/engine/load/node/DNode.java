package com.hellobike.finance.flow.engine.load.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.model.node.CommonFlowNode;
import com.hellobike.finance.flow.engine.model.node.SuspendFlowNode;

/**
 * @author 徐磊080
 */
public class DNode extends SuspendFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("执行D节点");
    }

    @Override
    protected String suspendId(FlowContext context) {
        return "123";
    }
}
