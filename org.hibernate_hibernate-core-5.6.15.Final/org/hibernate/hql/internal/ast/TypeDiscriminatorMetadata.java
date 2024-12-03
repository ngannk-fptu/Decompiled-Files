/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast;

import org.hibernate.type.Type;

public interface TypeDiscriminatorMetadata {
    public String getSqlFragment();

    public Type getResolutionType();
}

