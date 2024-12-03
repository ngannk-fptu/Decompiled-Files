/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSortedSet
 */
package com.google.template.soy.parseinfo;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

public class SoyTemplateInfo {
    private final String name;
    private final String partialName;
    private final ImmutableMap<String, ParamRequisiteness> paramMap;
    private final ImmutableSortedSet<String> ijParamSet;
    private final boolean mayHaveIjParamsInExternalCalls;
    private final boolean mayHaveIjParamsInExternalDelCalls;

    @Deprecated
    public SoyTemplateInfo(String name, ImmutableMap<String, ParamRequisiteness> paramMap) {
        this(name, paramMap, (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
    }

    public SoyTemplateInfo(String name, ImmutableMap<String, ParamRequisiteness> paramMap, ImmutableSortedSet<String> ijParamSet, boolean mayHaveIjParamsInExternalCalls, boolean mayHaveIjParamsInExternalDelCalls) {
        this.name = name;
        int lastDotPos = name.lastIndexOf(46);
        Preconditions.checkArgument((lastDotPos > 0 ? 1 : 0) != 0);
        this.partialName = name.substring(lastDotPos);
        this.paramMap = paramMap;
        this.ijParamSet = ijParamSet;
        this.mayHaveIjParamsInExternalCalls = mayHaveIjParamsInExternalCalls;
        this.mayHaveIjParamsInExternalDelCalls = mayHaveIjParamsInExternalDelCalls;
    }

    public String getName() {
        return this.name;
    }

    public String getPartialName() {
        return this.partialName;
    }

    public ImmutableMap<String, ParamRequisiteness> getParams() {
        return this.paramMap;
    }

    public ImmutableSortedSet<String> getUsedIjParams() {
        return this.ijParamSet;
    }

    public boolean mayHaveIjParamsInExternalCalls() {
        return this.mayHaveIjParamsInExternalCalls;
    }

    public boolean mayHaveIjParamsInExternalDelCalls() {
        return this.mayHaveIjParamsInExternalDelCalls;
    }

    public static enum ParamRequisiteness {
        REQUIRED,
        OPTIONAL;

    }
}

