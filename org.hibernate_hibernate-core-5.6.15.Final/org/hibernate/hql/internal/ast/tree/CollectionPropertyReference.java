/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.type.Type;

public interface CollectionPropertyReference {
    public Type getType();

    public String[] toColumns(String var1);
}

