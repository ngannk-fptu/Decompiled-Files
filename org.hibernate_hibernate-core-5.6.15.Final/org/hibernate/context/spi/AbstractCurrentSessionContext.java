/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.context.spi;

import java.util.Objects;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.context.TenantIdentifierMismatchException;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public abstract class AbstractCurrentSessionContext
implements CurrentSessionContext {
    private final SessionFactoryImplementor factory;

    protected AbstractCurrentSessionContext(SessionFactoryImplementor factory) {
        this.factory = factory;
    }

    public SessionFactoryImplementor factory() {
        return this.factory;
    }

    protected SessionBuilder baseSessionBuilder() {
        SessionBuilderImplementor builder = this.factory.withOptions();
        CurrentTenantIdentifierResolver resolver = this.factory.getCurrentTenantIdentifierResolver();
        if (resolver != null) {
            builder.tenantIdentifier(resolver.resolveCurrentTenantIdentifier());
        }
        return builder;
    }

    protected void validateExistingSession(Session existingSession) {
        CurrentTenantIdentifierResolver resolver = this.factory.getCurrentTenantIdentifierResolver();
        if (resolver != null && resolver.validateExistingCurrentSessions()) {
            String current = resolver.resolveCurrentTenantIdentifier();
            if (!Objects.equals(existingSession.getTenantIdentifier(), current)) {
                throw new TenantIdentifierMismatchException(String.format("Reported current tenant identifier [%s] did not match tenant identifier from existing session [%s]", current, existingSession.getTenantIdentifier()));
            }
        }
    }
}

