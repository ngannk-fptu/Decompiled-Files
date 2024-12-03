/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.AssertionFailure;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.proxy.HibernateProxy;

public class EntityReturnReader
implements ReturnReader {
    private final EntityReturn entityReturn;

    public EntityReturnReader(EntityReturn entityReturn) {
        this.entityReturn = entityReturn;
    }

    public ResultSetProcessingContext.EntityReferenceProcessingState getIdentifierResolutionContext(ResultSetProcessingContext context) {
        ResultSetProcessingContext.EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState(this.entityReturn);
        if (entityReferenceProcessingState == null) {
            throw new AssertionFailure(String.format("Could not locate EntityReferenceProcessingState for root entity return [%s (%s)]", this.entityReturn.getPropertyPath().getFullPath(), this.entityReturn.getEntityPersister().getEntityName()));
        }
        return entityReferenceProcessingState;
    }

    @Override
    public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
        Object proxy;
        ResultSetProcessingContext.EntityReferenceProcessingState processingState = this.getIdentifierResolutionContext(context);
        EntityKey entityKey = processingState.getEntityKey();
        Object entityInstance = context.getProcessingState(this.entityReturn).getEntityInstance();
        if (context.shouldReturnProxies() && (proxy = context.getSession().getPersistenceContextInternal().proxyFor(this.entityReturn.getEntityPersister(), entityKey, entityInstance)) != entityInstance) {
            ((HibernateProxy)proxy).getHibernateLazyInitializer().setImplementation(proxy);
            return proxy;
        }
        return entityInstance;
    }
}

