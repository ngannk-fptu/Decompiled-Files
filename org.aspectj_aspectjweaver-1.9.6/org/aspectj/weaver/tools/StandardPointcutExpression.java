/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.ShadowMatch;

public interface StandardPointcutExpression {
    public void setMatchingContext(MatchingContext var1);

    public boolean couldMatchJoinPointsInType(Class var1);

    public boolean mayNeedDynamicTest();

    public ShadowMatch matchesMethodExecution(ResolvedMember var1);

    public ShadowMatch matchesStaticInitialization(ResolvedType var1);

    public ShadowMatch matchesMethodCall(ResolvedMember var1, ResolvedMember var2);

    public String getPointcutExpression();
}

