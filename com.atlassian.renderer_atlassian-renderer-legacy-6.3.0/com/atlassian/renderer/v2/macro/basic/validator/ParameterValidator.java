/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.basic.validator;

import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;

public interface ParameterValidator {
    public void assertValid(String var1) throws MacroParameterValidationException;
}

