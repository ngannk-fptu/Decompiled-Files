/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.type.Type;

public interface ExpectedTypeAwareNode {
    public void setExpectedType(Type var1);

    public Type getExpectedType();
}

