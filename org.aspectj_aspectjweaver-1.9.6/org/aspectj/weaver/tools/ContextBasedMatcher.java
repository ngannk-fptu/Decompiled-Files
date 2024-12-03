/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import org.aspectj.weaver.tools.FuzzyBoolean;
import org.aspectj.weaver.tools.MatchingContext;

public interface ContextBasedMatcher {
    public boolean couldMatchJoinPointsInType(Class var1);

    public boolean couldMatchJoinPointsInType(Class var1, MatchingContext var2);

    public boolean mayNeedDynamicTest();

    public FuzzyBoolean matchesStatically(MatchingContext var1);

    public boolean matchesDynamically(MatchingContext var1);
}

