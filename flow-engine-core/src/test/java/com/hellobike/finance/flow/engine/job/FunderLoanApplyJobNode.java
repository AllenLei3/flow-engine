package com.hellobike.finance.flow.engine.job;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.TestContext;
import com.hellobike.finance.flow.engine.common.Constants;
import com.hellobike.finance.flow.engine.model.node.SwitchFlowNode;

/**
 * @author xulei
 */
public class FunderLoanApplyJobNode extends SwitchFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("查询资方放款状态, 状态为:" + Constants.FUNDER_LOAN_APPLY_STATUS);

        // 暂停继续后可恢复原始上下文
        TestContext testContext = context.getInstance(TestContext.class);
        System.out.println("LoanAgreementId: " + testContext.getLoanAgreementId());
        System.out.println("GuarantorCreditId: " + testContext.getGuarantorCreditId());
        System.out.println("GuarantorImageId: " + testContext.getGuarantorImageId());
        System.out.println("GuarantorCreditApplyId: " + testContext.getGuarantorCreditApplyId());
    }

    @Override
    protected String switchValue(FlowContext context) {
        return Constants.FUNDER_LOAN_APPLY_STATUS;
    }
}
