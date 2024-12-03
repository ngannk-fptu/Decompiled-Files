/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.MatchingContext;

public interface ShadowMatch {
    public boolean alwaysMatches();

    public boolean maybeMatches();

    public boolean neverMatches();

    public JoinPointMatch matchesJoinPoint(Object var1, Object var2, Object[] var3);

    public void setMatchingContext(MatchingContext var1);
}

