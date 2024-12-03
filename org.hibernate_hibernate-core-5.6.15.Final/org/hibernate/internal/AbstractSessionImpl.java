/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.io.Serializable;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.AbstractSharedSessionContract;
import org.hibernate.internal.SessionCreationOptions;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.type.descriptor.WrapperOptions;

public abstract class AbstractSessionImpl
extends AbstractSharedSessionContract
implements Serializable,
SharedSessionContractImplementor,
JdbcSessionOwner,
SessionImplementor,
EventSource,
TransactionCoordinatorBuilder.Options,
WrapperOptions {
    protected AbstractSessionImpl(SessionFactoryImpl factory, SessionCreationOptions options) {
        super(factory, options);
    }
}

