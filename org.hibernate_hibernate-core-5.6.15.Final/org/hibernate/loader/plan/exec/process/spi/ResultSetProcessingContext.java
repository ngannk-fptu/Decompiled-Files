/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import org.hibernate.LockMode;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.plan.exec.spi.LockModeResolver;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.EntityPersister;

public interface ResultSetProcessingContext
extends LockModeResolver {
    public SharedSessionContractImplementor getSession();

    public QueryParameters getQueryParameters();

    public boolean shouldUseOptionalEntityInformation();

    public boolean shouldReturnProxies();

    public LoadPlan getLoadPlan();

    public EntityReferenceProcessingState getProcessingState(EntityReference var1);

    public EntityReferenceProcessingState getOwnerProcessingState(Fetch var1);

    public void registerHydratedEntity(EntityReference var1, EntityKey var2, Object var3);

    public static interface EntityKeyResolutionContext {
        public EntityPersister getEntityPersister();

        public LockMode getLockMode();

        public EntityReference getEntityReference();
    }

    public static interface EntityReferenceProcessingState {
        public EntityReference getEntityReference();

        public void registerMissingIdentifier();

        public boolean isMissingIdentifier();

        public void registerIdentifierHydratedForm(Object var1);

        public Object getIdentifierHydratedForm();

        public void registerEntityKey(EntityKey var1);

        public EntityKey getEntityKey();

        public void registerHydratedState(Object[] var1);

        public Object[] getHydratedState();

        public void registerEntityInstance(Object var1);

        public Object getEntityInstance();
    }
}

