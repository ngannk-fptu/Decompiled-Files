/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.ShadowMatch;

public interface PointcutExpression {
    public void setMatchingContext(MatchingContext var1);

    public boolean couldMatchJoinPointsInType(Class var1);

    public boolean mayNeedDynamicTest();

    public ShadowMatch matchesMethodExecution(Method var1);

    public ShadowMatch matchesConstructorExecution(Constructor var1);

    public ShadowMatch matchesStaticInitialization(Class var1);

    public ShadowMatch matchesAdviceExecution(Method var1);

    public ShadowMatch matchesInitialization(Constructor var1);

    public ShadowMatch matchesPreInitialization(Constructor var1);

    public ShadowMatch matchesMethodCall(Method var1, Member var2);

    public ShadowMatch matchesMethodCall(Method var1, Class var2);

    public ShadowMatch matchesConstructorCall(Constructor var1, Member var2);

    public ShadowMatch matchesConstructorCall(Constructor var1, Class var2);

    public ShadowMatch matchesHandler(Class var1, Member var2);

    public ShadowMatch matchesHandler(Class var1, Class var2);

    public ShadowMatch matchesFieldSet(Field var1, Member var2);

    public ShadowMatch matchesFieldSet(Field var1, Class var2);

    public ShadowMatch matchesFieldGet(Field var1, Member var2);

    public ShadowMatch matchesFieldGet(Field var1, Class var2);

    public String getPointcutExpression();
}

