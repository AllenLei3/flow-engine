package com.hellobike.finance.flow.engine.node;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.TestContext;
import com.hellobike.finance.flow.engine.common.Constants;
import com.hellobike.finance.flow.engine.model.node.SuspendFlowNode;

/**
 * @author 徐磊080
 */
public class UploadGuarantorImageNode extends SuspendFlowNode {

    @Override
    protected void execute(FlowContext context) throws FlowExecuteException {
        System.out.println("上传融担【影像资料】");

        TestContext testContext = context.getInstance(TestContext.class);
        testContext.setGuarantorImageId(Constants.UPLOAD_GUARANTOR_IMAGE_ID);
    }

    @Override
    protected String suspendId(FlowContext context) {
        TestContext testContext = context.getInstance(TestContext.class);
        return testContext.getGuarantorImageId();
    }
}
