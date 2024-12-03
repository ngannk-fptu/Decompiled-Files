/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.query.internal.NativeQueryInterpreterStandardImpl;
import org.hibernate.engine.query.spi.NativeQueryInterpreter;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;
import org.hibernate.service.spi.SessionFactoryServiceInitiatorContext;

public class NativeQueryInterpreterInitiator
implements SessionFactoryServiceInitiator<NativeQueryInterpreter> {
    public static final NativeQueryInterpreterInitiator INSTANCE = new NativeQueryInterpreterInitiator();

    @Override
    public NativeQueryInterpreter initiateService(SessionFactoryImplementor sessionFactory, SessionFactoryOptions sessionFactoryOptions, ServiceRegistryImplementor registry) {
        return new NativeQueryInterpreterStandardImpl(sessionFactory);
    }

    @Override
    public NativeQueryInterpreter initiateService(SessionFactoryServiceInitiatorContext context) {
        return new NativeQueryInterpreterStandardImpl(context.getSessionFactory());
    }

    @Override
    public Class<NativeQueryInterpreter> getServiceInitiated() {
        return NativeQueryInterpreter.class;
    }
}

