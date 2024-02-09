package com.hellobike.finance.flow.engine.model;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.definition.BaseDefinition;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.exception.FlowParseException;

import java.io.Serializable;

/**
 * 流程内部元素
 *
 * @author 徐磊080
 */
public interface FlowElement<E extends BaseDefinition> extends Serializable {

    void parse(E definition, FlowConfiguration configuration) throws FlowParseException;

    void build(Flow flow, FlowConfiguration configuration) throws FlowBuildException;
}
