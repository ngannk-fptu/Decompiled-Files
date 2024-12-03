/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import org.aspectj.weaver.tools.ContextBasedMatcher;

public interface PointcutDesignatorHandler {
    public String getDesignatorName();

    public ContextBasedMatcher parse(String var1);
}

