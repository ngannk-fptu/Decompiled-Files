/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import com.atlassian.confluence.impl.util.sandbox.SandboxRequest;

interface SandboxPool {
    public <T, R> R execute(SandboxRequest<T, R> var1);

    public void shutdown();

    public SandboxPoolConfiguration getConfiguration();
}

