package com.hellobike.finance.flow.engine.springboot.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.model.node.CommonFlowNode;
import com.hellobike.finance.flow.engine.springboot.FlowComponent;
import com.hellobike.finance.flow.engine.springboot.TestContext;
import com.hellobike.finance.flow.engine.springboot.common.Constants;

/**
 * @author 徐磊080
 */
@FlowComponent("commonLoanNode")
public class CommonLoanNode extends CommonFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("创建借据");
        System.out.println("当日限额累加");
        System.out.println("预占用额度");
        System.out.println("保存还款计划");
        System.out.println("保存交易流水");

        TestContext testContext = context.getInstance(TestContext.class);
        testContext.setLoanAgreementId(Constants.LOAN_AGREEMENT_ID);
    }
}
