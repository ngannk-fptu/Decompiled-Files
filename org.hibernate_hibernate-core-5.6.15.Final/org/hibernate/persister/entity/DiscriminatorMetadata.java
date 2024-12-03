/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.type.Type;

public interface DiscriminatorMetadata {
    public String getSqlFragment(String var1);

    public Type getResolutionType();
}

