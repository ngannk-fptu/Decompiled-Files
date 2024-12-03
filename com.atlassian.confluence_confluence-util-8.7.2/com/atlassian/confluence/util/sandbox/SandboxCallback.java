/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;

@ExperimentalApi
public interface SandboxCallback<T, R> {
    public R apply(SandboxCallbackContext var1, T var2);

    public SandboxSerializer<T> inputSerializer();

    public SandboxSerializer<R> outputSerializer();
}

