package com.hellobike.finance.flow.engine;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import com.hellobike.finance.flow.engine.execute.FlowStatus;
import com.hellobike.finance.flow.engine.common.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author 徐磊080
 */
public class FlowLoaderTest {

    private static FlowEngineExecutor flowEngineExecutor;

    @BeforeAll
    public static void initFlowEngine() {
        FlowConfiguration configuration = new FlowConfiguration();
        configuration.setFlowDefinitionPath("flow/loan/*.xml");
        flowEngineExecutor = new FlowEngineExecutor(configuration);
    }

    @Test
    public void startLoanTest() {
        TestContext context = new TestContext();
        FlowResponse response = flowEngineExecutor.execute("70004LoanFlow", context);
        System.out.println(response);

        Assertions.assertEquals(Constants.LOAN_AGREEMENT_ID, (long) context.getLoanAgreementId());
        Assertions.assertEquals(Constants.UPLOAD_GUARANTOR_CREDIT_ID, context.getGuarantorCreditId());
        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("UploadGuarantorCreditAgreementNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-授信协议-回调
     */
    @Test
    public void uploadGuarantorCreditCallbackTest() {
        // 获取回调中的授信id
        String guarantorCreditId = Constants.UPLOAD_GUARANTOR_CREDIT_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorCreditId, "GuarantorCreditAgreementCallbackNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("UploadGuarantorImageNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-授信协议-job
     */
    @Test
    public void uploadGuarantorCreditJobTest() {
        String guarantorCreditId = Constants.UPLOAD_GUARANTOR_CREDIT_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorCreditId, "GuarantorCreditAgreementJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("UploadGuarantorImageNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-影像资料-回调
     */
    @Test
    public void uploadGuarantorImageCallbackTest() {
        String guarantorImageId = Constants.UPLOAD_GUARANTOR_IMAGE_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorImageId, "GuarantorImageCallbackNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("GuarantorCreditApplyNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-影像资料-job
     */
    @Test
    public void uploadGuarantorImageJobTest() {
        String guarantorImageId = Constants.UPLOAD_GUARANTOR_IMAGE_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorImageId, "GuarantorImageJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("GuarantorCreditApplyNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-授信申请-job
     */
    @Test
    public void guarantorCreditApplyJobTest() {
        String guarantorCreditApplyId = Constants.GUARANTOR_CREDIT_APPLY_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorCreditApplyId, "GuarantorCreditApplyJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("FunderLoanApplyNode", response.getCurrentFlowNodeName());
    }

    /**
     * 资方-放款申请-job
     */
    @Test
    public void funderLoanApplyJobTest() {
        String funderLoanApplyId = Constants.FUNDER_LOAN_APPLY_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", funderLoanApplyId, "FunderLoanApplyJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.END, response.getStatus());
        Assertions.assertEquals("CommonLoanSuccessNode", response.getCurrentFlowNodeName());
    }
}
