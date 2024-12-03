/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PointcutExpression;

public interface PointcutBasedPerClause
extends PerClause {
    public PointcutExpression getPointcutExpression();
}

