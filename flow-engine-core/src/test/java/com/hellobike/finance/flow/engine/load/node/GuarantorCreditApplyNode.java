package com.hellobike.finance.flow.engine.load.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.load.TestContext;
import com.hellobike.finance.flow.engine.load.common.Constants;
import com.hellobike.finance.flow.engine.model.node.SuspendFlowNode;

/**
 * @author 徐磊080
 */
public class GuarantorCreditApplyNode extends SuspendFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("发起融担授信申请");

        TestContext testContext = context.getInstance(TestContext.class);
        testContext.setGuarantorCreditApplyId(Constants.GUARANTOR_CREDIT_APPLY_ID);
    }

    @Override
    protected String suspendId(FlowContext context) {
        TestContext testContext = context.getInstance(TestContext.class);
        return testContext.getGuarantorCreditApplyId();
    }
}
