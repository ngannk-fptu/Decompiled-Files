/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.spi.MetadataBuildingContext;

public interface LocalMetadataBuildingContext
extends MetadataBuildingContext {
    public Origin getOrigin();
}

