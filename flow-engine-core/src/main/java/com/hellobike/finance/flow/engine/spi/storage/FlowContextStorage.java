package com.hellobike.finance.flow.engine.spi.storage;

import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.spi.SPI;

/**
 * @author 徐磊080
 */
@SPI
public interface FlowContextStorage {

    /**
     * 保存流程执行上下文
     *
     * @param flowName 流程名称
     * @param suspendId 暂停id
     * @param context 执行上下文
     */
    void saveContext(String flowName, String suspendId, FlowContext context);

    /**
     * 恢复流程执行上下文
     *
     * @param flowName 流程名称
     * @param suspendId 暂停id
     */
    FlowContext restoreContext(String flowName, String suspendId);

    /**
     * 删除流程执行上下文
     *
     * @param flowName 流程名称
     * @param suspendId 暂停id
     */
    void removeContext(String flowName, String suspendId);
}