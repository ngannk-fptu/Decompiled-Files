/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxPool;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import java.util.Objects;

abstract class AbstractSandboxPool
implements SandboxPool {
    protected final SandboxPoolConfiguration configuration;

    protected AbstractSandboxPool(SandboxPoolConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    @Override
    public SandboxPoolConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void shutdown() {
    }
}

