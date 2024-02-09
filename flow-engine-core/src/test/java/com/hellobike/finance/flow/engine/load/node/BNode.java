package com.hellobike.finance.flow.engine.load.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.model.node.SwitchFlowNode;

/**
 * @author 徐磊080
 */
public class BNode extends SwitchFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("执行B节点");
    }

    @Override
    protected String switchValue(FlowContext context) {
        return "70004";
    }
}
