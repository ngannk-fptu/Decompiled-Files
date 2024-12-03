/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.type.Type;

public interface AttributeDefinition {
    public AttributeSource getSource();

    public String getName();

    public Type getType();

    public boolean isNullable();
}

