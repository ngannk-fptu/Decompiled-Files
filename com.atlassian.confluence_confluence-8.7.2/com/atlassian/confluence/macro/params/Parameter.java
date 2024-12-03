/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.params;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.ParameterException;

public interface Parameter<T> {
    public T findValue(MacroExecutionContext var1) throws ParameterException;

    public void setValidate(boolean var1);
}

