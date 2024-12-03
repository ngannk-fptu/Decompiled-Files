/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.MacroExecutionException
 */
package com.atlassian.confluence.plugins.jiracharts;

import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.jiracharts.model.JQLValidationResult;
import java.util.Map;

public interface JQLValidator {
    public JQLValidationResult doValidate(Map<String, String> var1, boolean var2) throws MacroExecutionException;
}

