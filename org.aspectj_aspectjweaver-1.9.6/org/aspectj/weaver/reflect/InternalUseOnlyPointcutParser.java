/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;

public class InternalUseOnlyPointcutParser
extends PointcutParser {
    public InternalUseOnlyPointcutParser(ClassLoader classLoader, ReflectionWorld world) {
        this.setClassLoader(classLoader);
        this.setWorld(world);
    }

    public InternalUseOnlyPointcutParser(ClassLoader classLoader) {
        this.setClassLoader(classLoader);
    }

    public Pointcut resolvePointcutExpression(String expression, Class inScope, PointcutParameter[] formalParameters) {
        return super.resolvePointcutExpression(expression, inScope, formalParameters);
    }

    public Pointcut concretizePointcutExpression(Pointcut pc, Class inScope, PointcutParameter[] formalParameters) {
        return super.concretizePointcutExpression(pc, inScope, formalParameters);
    }
}

