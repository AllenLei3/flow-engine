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
@FlowComponent("guarantorCreditAgreementJobNode")
public class GuarantorCreditAgreementJobNode extends SwitchFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("更新融担【授信协议】状态, 状态为:" + Constants.UPLOAD_GUARANTOR_CREDIT_STATUS);

        // 暂停继续后可恢复原始上下文
        TestContext testContext = context.getInstance(TestContext.class);
        System.out.println("LoanAgreementId: " + testContext.getLoanAgreementId());
    }

    @Override
    protected String switchValue(FlowContext context) {
        return Constants.UPLOAD_GUARANTOR_CREDIT_STATUS;
    }
}
