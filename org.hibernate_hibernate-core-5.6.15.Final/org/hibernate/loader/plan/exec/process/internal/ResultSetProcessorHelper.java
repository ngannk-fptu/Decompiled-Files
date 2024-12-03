/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CompositeType;

public class ResultSetProcessorHelper {
    public static final ResultSetProcessorHelper INSTANCE = new ResultSetProcessorHelper();

    public static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SharedSessionContractImplementor session) {
        Object optionalObject = queryParameters.getOptionalObject();
        Serializable optionalId = queryParameters.getOptionalId();
        String optionalEntityName = queryParameters.getOptionalEntityName();
        return INSTANCE.interpretEntityKey(session, optionalEntityName, optionalId, optionalObject);
    }

    public EntityKey interpretEntityKey(SharedSessionContractImplementor session, String optionalEntityName, Serializable optionalId, Object optionalObject) {
        if (optionalEntityName != null) {
            EntityPersister entityPersister = optionalObject != null ? session.getEntityPersister(optionalEntityName, optionalObject) : session.getFactory().getMetamodel().entityPersister(optionalEntityName);
            if (entityPersister.isInstance(optionalId) && !entityPersister.getEntityMetamodel().getIdentifierProperty().isVirtual() && entityPersister.getEntityMetamodel().getIdentifierProperty().isEmbedded()) {
                Object[] identifierState = ((CompositeType)entityPersister.getIdentifierType()).getPropertyValues((Object)optionalId, session);
                return session.generateEntityKey((Serializable)identifierState, entityPersister);
            }
            return session.generateEntityKey(optionalId, entityPersister);
        }
        return null;
    }

    public static Map<String, int[]> buildNamedParameterLocMap(QueryParameters queryParameters, NamedParameterContext namedParameterContext) {
        if (queryParameters.getNamedParameters() == null || queryParameters.getNamedParameters().isEmpty()) {
            return null;
        }
        HashMap<String, int[]> namedParameterLocMap = new HashMap<String, int[]>();
        for (String name : queryParameters.getNamedParameters().keySet()) {
            namedParameterLocMap.put(name, namedParameterContext.getNamedParameterLocations(name));
        }
        return namedParameterLocMap;
    }
}

