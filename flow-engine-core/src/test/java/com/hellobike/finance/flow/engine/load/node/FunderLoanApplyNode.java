package com.hellobike.finance.flow.engine.load.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.load.TestContext;
import com.hellobike.finance.flow.engine.load.common.Constants;
import com.hellobike.finance.flow.engine.model.node.SuspendFlowNode;

/**
 * @author 徐磊080
 */
public class FunderLoanApplyNode extends SuspendFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("发起资方放款申请");

        TestContext testContext = context.getInstance(TestContext.class);
        testContext.setFunderLoanApplyId(Constants.FUNDER_LOAN_APPLY_ID);
    }

    @Override
    protected String suspendId(FlowContext context) {
        TestContext testContext = context.getInstance(TestContext.class);
        return testContext.getFunderLoanApplyId();
    }
}
