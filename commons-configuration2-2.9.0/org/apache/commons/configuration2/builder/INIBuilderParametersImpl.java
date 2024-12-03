/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Map;
import org.apache.commons.configuration2.builder.HierarchicalBuilderParametersImpl;
import org.apache.commons.configuration2.builder.INIBuilderProperties;

public class INIBuilderParametersImpl
extends HierarchicalBuilderParametersImpl
implements INIBuilderProperties<INIBuilderParametersImpl> {
    private static final String PROP_SEPARATOR_USED_IN_INI_OUTPUT = "separatorUsedInOutput";
    private static final String PROP_SEPARATOR_USED_IN_INI_INPUT = "separatorUsedInInput";
    private static final String PROP_COMMENT_LEADING_SEPARATOR_USED_IN_INI_INPUT = "commentLeadingCharsUsedInInput";

    @Override
    public void inheritFrom(Map<String, ?> source) {
        super.inheritFrom(source);
        this.copyPropertiesFrom(source, PROP_SEPARATOR_USED_IN_INI_OUTPUT);
        this.copyPropertiesFrom(source, PROP_SEPARATOR_USED_IN_INI_INPUT);
        this.copyPropertiesFrom(source, PROP_COMMENT_LEADING_SEPARATOR_USED_IN_INI_INPUT);
    }

    @Override
    public INIBuilderParametersImpl setSeparatorUsedInOutput(String separator) {
        this.storeProperty(PROP_SEPARATOR_USED_IN_INI_OUTPUT, separator);
        return this;
    }

    @Override
    public INIBuilderParametersImpl setSeparatorUsedInInput(String separator) {
        this.storeProperty(PROP_SEPARATOR_USED_IN_INI_INPUT, separator);
        return this;
    }

    @Override
    public INIBuilderParametersImpl setCommentLeadingCharsUsedInInput(String separator) {
        this.storeProperty(PROP_COMMENT_LEADING_SEPARATOR_USED_IN_INI_INPUT, separator);
        return this;
    }
}

