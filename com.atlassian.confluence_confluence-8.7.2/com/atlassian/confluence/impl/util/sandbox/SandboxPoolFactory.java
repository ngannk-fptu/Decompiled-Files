/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxPool;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;

interface SandboxPoolFactory {
    public SandboxPool create(SandboxPoolConfiguration var1);
}

