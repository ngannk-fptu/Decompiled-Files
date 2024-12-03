/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.util.Container;

public class ContainerTweaks {
    private final int container = Container.get();

    public boolean shouldAutoCreateSession() {
        return false;
    }

    public boolean shouldLogUnhandledExceptions() {
        return this.container == 1;
    }

    public boolean shouldIgnoreIllegalStateExceptionOnErrorPage() {
        return this.container == 4;
    }
}

