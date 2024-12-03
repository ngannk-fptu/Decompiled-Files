/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxNotFoundException
 *  com.atlassian.confluence.util.sandbox.SandboxRegistry
 *  com.atlassian.confluence.util.sandbox.SandboxSpec
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.DefaultSandbox;
import com.atlassian.confluence.impl.util.sandbox.SandboxPool;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxNotFoundException;
import com.atlassian.confluence.util.sandbox.SandboxRegistry;
import com.atlassian.confluence.util.sandbox.SandboxSpec;
import java.util.Collection;

class ConfluenceSandboxRegistry
implements SandboxRegistry {
    private final Collection<SandboxPool> availablePools;

    public ConfluenceSandboxRegistry(Collection<SandboxPool> availablePools) {
        this.availablePools = availablePools;
    }

    public Sandbox get(SandboxSpec spec) {
        return this.availablePools.stream().filter(p -> p.getConfiguration().getMemoryInMegabytes() >= spec.minimumMemoryInMb() && p.getConfiguration().getStackInMegabytes() >= spec.minimumStackInMb()).findFirst().map(p -> new DefaultSandbox((SandboxPool)p, spec)).orElseThrow(() -> new SandboxNotFoundException("Sandbox with the given specification can't be found. Please see Confluence documentation to see what kind of sandboxes are available."));
    }
}

