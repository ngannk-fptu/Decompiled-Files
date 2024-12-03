/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.sandbox.DefaultSandboxSpec;
import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import java.time.Duration;

@ExperimentalApi
public interface SandboxSpec {
    public int minimumMemoryInMb();

    public Duration requestTimeLimit();

    public int minimumStackInMb();

    public SandboxCallbackContext callbackContext();

    public static SandboxSpec of(Duration requestTimeLimit) {
        return SandboxSpec.builder().build(requestTimeLimit);
    }

    public static SpecBuilder builder() {
        return new DefaultSandboxSpec.SpecBuilder();
    }

    public static interface SpecBuilder {
        public SpecBuilder withMinimumMemoryInMb(int var1);

        public SpecBuilder withMinimumStackInMb(int var1);

        public SpecBuilder registerCallbackContextObject(Class<?> var1, Object var2);

        public SandboxSpec build(Duration var1);
    }
}

