/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.entity.plan;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.entity.plan.AbstractLoadPlanBasedEntityLoader;
import org.hibernate.loader.plan.exec.internal.EntityLoadQueryDetails;
import org.hibernate.loader.plan.exec.query.internal.QueryBuildingParametersImpl;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class EntityLoader
extends AbstractLoadPlanBasedEntityLoader {
    private static final Logger log = CoreLogging.logger(EntityLoader.class);

    public static Builder forEntity(OuterJoinLoadable persister) {
        return new Builder(persister);
    }

    private EntityLoader(SessionFactoryImplementor factory, OuterJoinLoadable persister, String[] uniqueKeyColumnNames, Type uniqueKeyType, QueryBuildingParameters buildingParameters) throws MappingException {
        super(persister, factory, uniqueKeyColumnNames, uniqueKeyType, buildingParameters);
        if (log.isDebugEnabled()) {
            if (buildingParameters.getLockOptions() != null) {
                log.debugf("Static select for entity %s [%s:%s]: %s", new Object[]{this.getEntityName(), buildingParameters.getLockOptions().getLockMode(), buildingParameters.getLockOptions().getTimeOut(), this.getStaticLoadQuery().getSqlStatement()});
            } else if (buildingParameters.getLockMode() != null) {
                log.debugf("Static select for entity %s [%s]: %s", (Object)this.getEntityName(), (Object)buildingParameters.getLockMode(), (Object)this.getStaticLoadQuery().getSqlStatement());
            }
        }
    }

    private EntityLoader(SessionFactoryImplementor factory, OuterJoinLoadable persister, EntityLoader entityLoaderTemplate, Type uniqueKeyType, QueryBuildingParameters buildingParameters) throws MappingException {
        super(persister, factory, entityLoaderTemplate.getStaticLoadQuery(), uniqueKeyType, buildingParameters);
        if (log.isDebugEnabled()) {
            if (buildingParameters.getLockOptions() != null) {
                log.debugf("Static select for entity %s [%s:%s]: %s", new Object[]{this.getEntityName(), buildingParameters.getLockOptions().getLockMode(), buildingParameters.getLockOptions().getTimeOut(), this.getStaticLoadQuery().getSqlStatement()});
            } else if (buildingParameters.getLockMode() != null) {
                log.debugf("Static select for entity %s [%s]: %s", (Object)this.getEntityName(), (Object)buildingParameters.getLockMode(), (Object)this.getStaticLoadQuery().getSqlStatement());
            }
        }
    }

    @Override
    protected EntityLoadQueryDetails getStaticLoadQuery() {
        return (EntityLoadQueryDetails)super.getStaticLoadQuery();
    }

    public static class Builder {
        private final OuterJoinLoadable persister;
        private EntityLoader entityLoaderTemplate;
        private int batchSize = 1;
        private LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
        private LockMode lockMode = LockMode.NONE;
        private LockOptions lockOptions;

        public Builder(OuterJoinLoadable persister) {
            this.persister = persister;
        }

        public Builder withEntityLoaderTemplate(EntityLoader entityLoaderTemplate) {
            this.entityLoaderTemplate = entityLoaderTemplate;
            return this;
        }

        public Builder withBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder withInfluencers(LoadQueryInfluencers influencers) {
            this.influencers = influencers;
            return this;
        }

        public Builder withLockMode(LockMode lockMode) {
            this.lockMode = lockMode;
            return this;
        }

        public Builder withLockOptions(LockOptions lockOptions) {
            this.lockOptions = lockOptions;
            return this;
        }

        public EntityLoader byPrimaryKey() {
            return this.byUniqueKey(this.persister.getIdentifierColumnNames(), this.persister.getIdentifierType());
        }

        public EntityLoader byUniqueKey(String[] keyColumnNames, Type keyType) {
            if (this.entityLoaderTemplate == null) {
                return new EntityLoader(this.persister.getFactory(), this.persister, keyColumnNames, keyType, (QueryBuildingParameters)new QueryBuildingParametersImpl(this.influencers, this.batchSize, this.lockMode, this.lockOptions));
            }
            return new EntityLoader(this.persister.getFactory(), this.persister, this.entityLoaderTemplate, keyType, (QueryBuildingParameters)new QueryBuildingParametersImpl(this.influencers, this.batchSize, this.lockMode, this.lockOptions));
        }
    }
}

