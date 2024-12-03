/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Map;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.HierarchicalBuilderProperties;
import org.apache.commons.configuration2.tree.ExpressionEngine;

public class HierarchicalBuilderParametersImpl
extends FileBasedBuilderParametersImpl
implements HierarchicalBuilderProperties<HierarchicalBuilderParametersImpl> {
    private static final String PROP_EXPRESSION_ENGINE = "expressionEngine";

    @Override
    public void inheritFrom(Map<String, ?> source) {
        super.inheritFrom(source);
        this.copyPropertiesFrom(source, PROP_EXPRESSION_ENGINE);
    }

    @Override
    public HierarchicalBuilderParametersImpl setExpressionEngine(ExpressionEngine engine) {
        this.storeProperty(PROP_EXPRESSION_ENGINE, engine);
        return this;
    }
}

