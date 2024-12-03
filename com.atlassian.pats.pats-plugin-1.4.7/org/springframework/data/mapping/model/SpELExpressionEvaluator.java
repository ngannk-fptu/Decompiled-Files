/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping.model;

import org.springframework.lang.Nullable;

public interface SpELExpressionEvaluator {
    @Nullable
    public <T> T evaluate(String var1);
}

