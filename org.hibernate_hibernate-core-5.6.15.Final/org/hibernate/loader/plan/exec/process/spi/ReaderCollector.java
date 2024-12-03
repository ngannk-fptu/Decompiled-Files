/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import java.util.List;
import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
import org.hibernate.loader.plan.exec.process.spi.RowReader;

public interface ReaderCollector {
    public ReturnReader getReturnReader();

    public void add(CollectionReferenceInitializer var1);

    public List<CollectionReferenceInitializer> getArrayReferenceInitializers();

    public List<CollectionReferenceInitializer> getNonArrayCollectionReferenceInitializers();

    public void add(EntityReferenceInitializer var1);

    public List<EntityReferenceInitializer> getEntityReferenceInitializers();

    public RowReader buildRowReader();
}

