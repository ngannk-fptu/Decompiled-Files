/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import java.time.Duration;

@ExperimentalApi
public interface Sandbox {
    public <T, R> R execute(SandboxTask<T, R> var1, T var2);

    public <T, R> R execute(SandboxTask<T, R> var1, T var2, Duration var3);
}

