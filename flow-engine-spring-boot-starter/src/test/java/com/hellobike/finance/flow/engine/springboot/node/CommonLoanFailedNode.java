package com.hellobike.finance.flow.engine.springboot.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.model.node.CommonFlowNode;
import com.hellobike.finance.flow.engine.springboot.FlowComponent;

/**
 * @author 徐磊080
 */
@FlowComponent("commonLoanFailedNode")
public class CommonLoanFailedNode extends CommonFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("更新借据状态");
        System.out.println("更新交易流水状态");
        System.out.println("释放当日借款限额");
        System.out.println("释放预占用额度");
    }
}
