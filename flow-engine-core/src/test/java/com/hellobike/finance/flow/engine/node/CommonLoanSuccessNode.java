package com.hellobike.finance.flow.engine.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.model.node.CommonFlowNode;

/**
 * @author 徐磊080
 */
public class CommonLoanSuccessNode extends CommonFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("更新借据状态");
        System.out.println("更新交易流水状态");
        System.out.println("占用授信额度");
    }
}
