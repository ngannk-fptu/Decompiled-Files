/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.type.Type;

public interface FunctionNode {
    public SQLFunction getSQLFunction();

    public Type getFirstArgumentType();
}

