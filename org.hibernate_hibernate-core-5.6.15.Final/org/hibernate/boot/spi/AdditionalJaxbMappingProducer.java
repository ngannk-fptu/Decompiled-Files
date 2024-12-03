/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.jandex.IndexView
 */
package org.hibernate.boot.spi;

import java.util.Collection;
import org.hibernate.boot.jaxb.internal.MappingBinder;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.jboss.jandex.IndexView;

@Deprecated
public interface AdditionalJaxbMappingProducer {
    public Collection<MappingDocument> produceAdditionalMappings(MetadataImplementor var1, IndexView var2, MappingBinder var3, MetadataBuildingContext var4);
}

