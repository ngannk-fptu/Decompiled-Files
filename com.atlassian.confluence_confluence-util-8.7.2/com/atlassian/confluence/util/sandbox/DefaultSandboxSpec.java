/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.confluence.util.sandbox.DefaultSandboxCallbackContext;
import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import com.atlassian.confluence.util.sandbox.SandboxSpec;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

class DefaultSandboxSpec
implements SandboxSpec {
    private final int minMemoryInMb;
    private final int minStackInMb;
    private final Duration requestTimeLimit;
    private final SandboxCallbackContext callbackContext;

    private DefaultSandboxSpec(Duration requestTimeLimit, int minMemoryInMb, int minStackInMb, SandboxCallbackContext callbackContext) {
        this.minMemoryInMb = minMemoryInMb;
        this.requestTimeLimit = requestTimeLimit;
        this.callbackContext = callbackContext;
        this.minStackInMb = minStackInMb;
    }

    @Override
    public int minimumMemoryInMb() {
        return this.minMemoryInMb;
    }

    @Override
    public Duration requestTimeLimit() {
        return this.requestTimeLimit;
    }

    @Override
    public int minimumStackInMb() {
        return this.minStackInMb;
    }

    @Override
    public SandboxCallbackContext callbackContext() {
        return this.callbackContext;
    }

    static class SpecBuilder
    implements SandboxSpec.SpecBuilder {
        private int minMemoryInMb = 0;
        private int minStackInMb = 0;
        private Map<Class<?>, Object> callbackContext = new HashMap();

        SpecBuilder() {
        }

        @Override
        public SandboxSpec.SpecBuilder withMinimumStackInMb(int minStackInMb) {
            this.minStackInMb = minStackInMb;
            return this;
        }

        @Override
        public SandboxSpec.SpecBuilder withMinimumMemoryInMb(int minimumMemoryInMb) {
            this.minMemoryInMb = minimumMemoryInMb;
            return this;
        }

        @Override
        public SandboxSpec.SpecBuilder registerCallbackContextObject(Class<?> type, Object instance) {
            this.callbackContext.put(type, instance);
            return this;
        }

        @Override
        public SandboxSpec build(Duration requestTimeLimit) {
            return new DefaultSandboxSpec(requestTimeLimit, this.minMemoryInMb, this.minStackInMb, new DefaultSandboxCallbackContext(this.callbackContext));
        }
    }
}

