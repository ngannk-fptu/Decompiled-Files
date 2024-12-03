/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.AttributeDefinition;

public interface AttributeSource {
    public Iterable<AttributeDefinition> getAttributes();
}

