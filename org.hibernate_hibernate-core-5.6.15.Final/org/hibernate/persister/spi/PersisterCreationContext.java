/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.spi;

import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public interface PersisterCreationContext {
    public SessionFactoryImplementor getSessionFactory();

    public MetadataImplementor getMetadata();
}

