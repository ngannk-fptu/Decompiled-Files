/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.sandbox.SandboxCallback;
import java.util.logging.Level;

@ExperimentalApi
public interface SandboxTaskContext {
    public <T, R> R execute(SandboxCallback<T, R> var1, T var2);

    public void log(Level var1, Object var2);
}

