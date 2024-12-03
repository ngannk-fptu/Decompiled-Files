/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.collection.plan;

import java.sql.ResultSet;
import org.hibernate.LockMode;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.collection.plan.AbstractLoadPlanBasedCollectionInitializer;
import org.hibernate.loader.plan.exec.query.internal.QueryBuildingParametersImpl;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class CollectionLoader
extends AbstractLoadPlanBasedCollectionInitializer {
    private static final Logger log = CoreLogging.logger(CollectionLoader.class);

    public static Builder forCollection(QueryableCollection collectionPersister) {
        return new Builder(collectionPersister);
    }

    @Override
    protected int[] getNamedParameterLocs(String name) {
        return new int[0];
    }

    @Override
    protected void autoDiscoverTypes(ResultSet rs) {
    }

    public CollectionLoader(QueryableCollection collectionPersister, QueryBuildingParameters buildingParameters) {
        super(collectionPersister, buildingParameters);
        if (log.isDebugEnabled()) {
            log.debugf("Static select for collection %s: %s", (Object)collectionPersister.getRole(), (Object)this.getStaticLoadQuery().getSqlStatement());
        }
    }

    protected Type getKeyType() {
        return this.collectionPersister().getKeyType();
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.collectionPersister().getRole() + ')';
    }

    protected static class Builder {
        private final QueryableCollection collectionPersister;
        private int batchSize = 1;
        private LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;

        private Builder(QueryableCollection collectionPersister) {
            this.collectionPersister = collectionPersister;
        }

        public Builder withBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder withInfluencers(LoadQueryInfluencers influencers) {
            this.influencers = influencers;
            return this;
        }

        public CollectionLoader byKey() {
            QueryBuildingParametersImpl currentBuildingParameters = new QueryBuildingParametersImpl(this.influencers, this.batchSize, LockMode.NONE, null);
            return new CollectionLoader(this.collectionPersister, currentBuildingParameters);
        }
    }
}

