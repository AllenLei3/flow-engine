package com.hellobike.finance.flow.engine.springboot.job;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.model.node.SwitchFlowNode;
import com.hellobike.finance.flow.engine.springboot.FlowComponent;
import com.hellobike.finance.flow.engine.springboot.TestContext;
import com.hellobike.finance.flow.engine.springboot.common.Constants;

/**
 * @author xulei
 */
@FlowComponent("guarantorCreditApplyJobNode")
public class GuarantorCreditApplyJobNode extends SwitchFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("查询融担授信状态, 状态为:" + Constants.GUARANTOR_CREDIT_APPLY_STATUS);

        // 暂停继续后可恢复原始上下文
        TestContext testContext = context.getInstance(TestContext.class);
        System.out.println("LoanAgreementId: " + testContext.getLoanAgreementId());
        System.out.println("GuarantorCreditId: " + testContext.getGuarantorCreditId());
        System.out.println("GuarantorImageId: " + testContext.getGuarantorImageId());
    }

    @Override
    protected String switchValue(FlowContext context) {
        return Constants.GUARANTOR_CREDIT_APPLY_STATUS;
    }
}
