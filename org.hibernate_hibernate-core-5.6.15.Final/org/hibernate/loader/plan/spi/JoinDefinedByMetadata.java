/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.Join;
import org.hibernate.type.Type;

public interface JoinDefinedByMetadata
extends Join {
    public String getJoinedPropertyName();

    public Type getJoinedPropertyType();
}

