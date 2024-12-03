/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;

public interface EncapsulatedEntityIdentifierDefinition
extends EntityIdentifierDefinition {
    public AttributeDefinition getAttributeDefinition();
}

