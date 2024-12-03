/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.google.template.soy.shared.internal;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum NonpluginFunction {
    IS_FIRST(1),
    IS_LAST(1),
    INDEX(1),
    QUOTE_KEYS_IF_JS(1);

    private static final Map<String, NonpluginFunction> NONPLUGIN_FUNCTIONS_BY_NAME;
    private final String functionName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    private final int numArgs;

    public static NonpluginFunction forFunctionName(String functionName) {
        return NONPLUGIN_FUNCTIONS_BY_NAME.get(functionName);
    }

    private NonpluginFunction(int numArgs) {
        this.numArgs = numArgs;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public int getNumArgs() {
        return this.numArgs;
    }

    static {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (NonpluginFunction nonpluginFn : NonpluginFunction.values()) {
            mapBuilder.put((Object)nonpluginFn.getFunctionName(), (Object)nonpluginFn);
        }
        NONPLUGIN_FUNCTIONS_BY_NAME = mapBuilder.build();
    }
}

