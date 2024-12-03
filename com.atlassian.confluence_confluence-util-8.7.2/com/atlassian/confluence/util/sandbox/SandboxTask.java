/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;

@ExperimentalApi
public interface SandboxTask<T, R> {
    public R apply(SandboxTaskContext var1, T var2);

    public SandboxSerializer<T> inputSerializer();

    public SandboxSerializer<R> outputSerializer();
}

