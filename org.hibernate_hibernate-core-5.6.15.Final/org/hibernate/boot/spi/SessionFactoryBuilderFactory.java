/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;

public interface SessionFactoryBuilderFactory {
    public SessionFactoryBuilder getSessionFactoryBuilder(MetadataImplementor var1, SessionFactoryBuilderImplementor var2);
}

