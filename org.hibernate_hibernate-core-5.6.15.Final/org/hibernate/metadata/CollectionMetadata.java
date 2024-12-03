/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metadata;

import org.hibernate.type.Type;

public interface CollectionMetadata {
    public Type getKeyType();

    public Type getElementType();

    public Type getIndexType();

    public boolean hasIndex();

    public String getRole();

    public boolean isArray();

    public boolean isPrimitiveArray();

    public boolean isLazy();
}

