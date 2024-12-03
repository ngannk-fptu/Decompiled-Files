/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.boot.SessionFactoryBuilder
 *  org.hibernate.boot.spi.AbstractDelegatingSessionFactoryBuilder
 *  org.hibernate.boot.spi.MetadataImplementor
 *  org.hibernate.boot.spi.SessionFactoryBuilderFactory
 *  org.hibernate.boot.spi.SessionFactoryBuilderImplementor
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.confluence.impl.core.persistence.hibernate.SwitchableCachingSessionFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.AbstractDelegatingSessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public final class SwitchableCachingSessionFactoryBuilderFactory
implements SessionFactoryBuilderFactory {
    public SessionFactoryBuilder getSessionFactoryBuilder(MetadataImplementor metadata, SessionFactoryBuilderImplementor defaultBuilder) {
        return new AbstractDelegatingSessionFactoryBuilder((SessionFactoryBuilder)defaultBuilder){

            public SessionFactory build() {
                SessionFactoryImplementor delegate = (SessionFactoryImplementor)super.build();
                return new SwitchableCachingSessionFactory(delegate);
            }

            protected SessionFactoryBuilder getThis() {
                return this;
            }
        };
    }
}

