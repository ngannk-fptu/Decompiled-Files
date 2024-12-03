/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.service.Service;

public interface SessionFactoryBuilderService
extends Service {
    public SessionFactoryBuilderImplementor createSessionFactoryBuilder(MetadataImpl var1, BootstrapContext var2);
}

