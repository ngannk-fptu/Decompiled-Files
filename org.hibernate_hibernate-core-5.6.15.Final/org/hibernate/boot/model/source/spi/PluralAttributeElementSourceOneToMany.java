/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceAssociation;

public interface PluralAttributeElementSourceOneToMany
extends PluralAttributeElementSourceAssociation {
    @Override
    public String getReferencedEntityName();

    @Override
    public boolean isIgnoreNotFound();

    public String getXmlNodeName();
}

