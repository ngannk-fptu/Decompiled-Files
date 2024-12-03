/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.tools.MatchingContext;

public class ReflectionFastMatchInfo
extends FastMatchInfo {
    private final MatchingContext context;

    public ReflectionFastMatchInfo(ResolvedType type, Shadow.Kind kind, MatchingContext context, World world) {
        super(type, kind, world);
        this.context = context;
    }

    public MatchingContext getMatchingContext() {
        return this.context;
    }
}

