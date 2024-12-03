/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.Sandbox
 */
package com.atlassian.confluence.plugins.conversion.impl.sandbox;

import com.atlassian.confluence.util.sandbox.Sandbox;

class SandboxHolder {
    private static final SandboxHolder instance = new SandboxHolder();
    private Sandbox sandbox;

    SandboxHolder() {
    }

    static SandboxHolder getInstance() {
        return instance;
    }

    Sandbox getSandbox() {
        return this.sandbox;
    }

    void setSandbox(Sandbox sandbox) {
        this.sandbox = sandbox;
    }
}

