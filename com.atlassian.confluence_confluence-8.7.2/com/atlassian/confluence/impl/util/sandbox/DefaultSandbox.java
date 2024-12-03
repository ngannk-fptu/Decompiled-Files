/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxSpec
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxPool;
import com.atlassian.confluence.impl.util.sandbox.SandboxRequest;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxSpec;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import java.time.Duration;

class DefaultSandbox
implements Sandbox {
    private final SandboxPool pool;
    private final SandboxSpec spec;

    DefaultSandbox(SandboxPool pool, SandboxSpec spec) {
        this.pool = pool;
        this.spec = spec;
    }

    public <T, R> R execute(SandboxTask<T, R> task, T input) {
        return this.pool.execute(new SandboxRequest<T, R>(task, input, this.spec.requestTimeLimit(), this.spec.callbackContext()));
    }

    public <T, R> R execute(SandboxTask<T, R> task, T input, Duration timeLimit) {
        return this.pool.execute(new SandboxRequest<T, R>(task, input, timeLimit, this.spec.callbackContext()));
    }
}

