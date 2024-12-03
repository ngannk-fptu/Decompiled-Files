/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.internal;

import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.internal.SessionFactoryBuilderImpl;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderService;

public final class DefaultSessionFactoryBuilderService
implements SessionFactoryBuilderService {
    protected static final DefaultSessionFactoryBuilderService INSTANCE = new DefaultSessionFactoryBuilderService();

    private DefaultSessionFactoryBuilderService() {
    }

    @Override
    public SessionFactoryBuilderImplementor createSessionFactoryBuilder(MetadataImpl metadata, BootstrapContext bootstrapContext) {
        return new SessionFactoryBuilderImpl((MetadataImplementor)metadata, bootstrapContext);
    }
}

