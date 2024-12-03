/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.AttributeSource;

public interface AssociationSource {
    public AttributeSource getAttributeSource();

    public String getReferencedEntityName();

    public boolean isIgnoreNotFound();

    public boolean isMappedBy();
}

