/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

import com.atlassian.voorhees.JsonError;

public interface ErrorMapper {
    public JsonError mapError(String var1, Throwable var2);
}

