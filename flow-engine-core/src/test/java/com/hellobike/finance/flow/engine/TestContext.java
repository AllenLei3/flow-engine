package com.hellobike.finance.flow.engine;

import com.hellobike.finance.flow.engine.execute.FlowContext;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 徐磊080
 */
@Getter
@Setter
public class TestContext implements FlowContext {

    /**
     * 借据ID
     */
    private Long loanAgreementId;

    /**
     * 融担授信协议ID
     */
    private String guarantorCreditId;

    /**
     * 融担影像资料ID
     */
    private String guarantorImageId;

    /**
     * 融担授信申请ID
     */
    private String guarantorCreditApplyId;

    /**
     * 资方放款申请ID
     */
    private String funderLoanApplyId;
}
