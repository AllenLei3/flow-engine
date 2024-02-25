package com.hellobike.finance.flow.engine.springboot;

import com.hellobike.finance.flow.engine.FlowEngineExecutor;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import com.hellobike.finance.flow.engine.execute.FlowStatus;
import com.hellobike.finance.flow.engine.springboot.common.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author 徐磊080
 */
@SpringBootTest(classes = HelloSpringApplication.class)
public class FlowLoaderTest {

    @Resource
    private FlowEngineExecutor flowEngineExecutor;

    @Test
    public void startLoanTest() {
        TestContext context = new TestContext();
        FlowResponse response = flowEngineExecutor.execute("70004LoanFlow", context);
        System.out.println(response);

        Assertions.assertEquals(Constants.LOAN_AGREEMENT_ID, (long) context.getLoanAgreementId());
        Assertions.assertEquals(Constants.UPLOAD_GUARANTOR_CREDIT_ID, context.getGuarantorCreditId());
        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("uploadGuarantorCreditAgreementNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-授信协议-回调
     */
    @Test
    public void uploadGuarantorCreditCallbackTest() {
        // 获取回调中的授信id
        String guarantorCreditId = Constants.UPLOAD_GUARANTOR_CREDIT_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorCreditId, "guarantorCreditAgreementCallbackNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("uploadGuarantorImageNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-授信协议-job
     */
    @Test
    public void uploadGuarantorCreditJobTest() {
        String guarantorCreditId = Constants.UPLOAD_GUARANTOR_CREDIT_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorCreditId, "guarantorCreditAgreementJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("uploadGuarantorImageNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-影像资料-回调
     */
    @Test
    public void uploadGuarantorImageCallbackTest() {
        String guarantorImageId = Constants.UPLOAD_GUARANTOR_IMAGE_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorImageId, "guarantorImageCallbackNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("guarantorCreditApplyNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-影像资料-job
     */
    @Test
    public void uploadGuarantorImageJobTest() {
        String guarantorImageId = Constants.UPLOAD_GUARANTOR_IMAGE_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorImageId, "guarantorImageJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("guarantorCreditApplyNode", response.getCurrentFlowNodeName());
    }

    /**
     * 融担-授信申请-job
     */
    @Test
    public void guarantorCreditApplyJobTest() {
        String guarantorCreditApplyId = Constants.GUARANTOR_CREDIT_APPLY_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", guarantorCreditApplyId, "guarantorCreditApplyJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.SUSPEND, response.getStatus());
        Assertions.assertEquals("funderLoanApplyNode", response.getCurrentFlowNodeName());
    }

    /**
     * 资方-放款申请-job
     */
    @Test
    public void funderLoanApplyJobTest() {
        String funderLoanApplyId = Constants.FUNDER_LOAN_APPLY_ID;
        FlowResponse response = flowEngineExecutor.resume("70004LoanFlow", funderLoanApplyId, "funderLoanApplyJobNode");
        System.out.println(response);

        Assertions.assertEquals(FlowStatus.END, response.getStatus());
        Assertions.assertEquals("commonLoanSuccessNode", response.getCurrentFlowNodeName());
    }
}
