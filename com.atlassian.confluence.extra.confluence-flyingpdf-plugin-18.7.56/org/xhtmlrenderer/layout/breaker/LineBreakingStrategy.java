/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout.breaker;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.breaker.BreakPointsProvider;

public interface LineBreakingStrategy {
    public BreakPointsProvider getBreakPointsProvider(String var1, String var2, CalculatedStyle var3);
}

