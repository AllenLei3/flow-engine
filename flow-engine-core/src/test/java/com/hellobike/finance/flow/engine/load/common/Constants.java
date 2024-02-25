package com.hellobike.finance.flow.engine.load.common;

/**
 * @author xulei
 */
public interface Constants {

    /**
     * 借据ID
     */
    Long LOAN_AGREEMENT_ID = 123456789L;

    /**
     * 融担上传【授信协议】
     */
    String UPLOAD_GUARANTOR_CREDIT_ID = "1111111111";
    String UPLOAD_GUARANTOR_CREDIT_STATUS = "success";     // success\failed

    /**
     * 融担上传【影像资料】ID
     */
    String UPLOAD_GUARANTOR_IMAGE_ID = "2222222222";
    String UPLOAD_GUARANTOR_IMAGE_STATUS = "success";     // success\failed


    /**
     * 融担授信申请ID
     */
    String GUARANTOR_CREDIT_APPLY_ID = "3333333333";
    String GUARANTOR_CREDIT_APPLY_STATUS = "success";     // success\failed

    /**
     * 资方放款申请ID
     */
    String FUNDER_LOAN_APPLY_ID = "4444444444";
    String FUNDER_LOAN_APPLY_STATUS = "success";     // success\failed
}
