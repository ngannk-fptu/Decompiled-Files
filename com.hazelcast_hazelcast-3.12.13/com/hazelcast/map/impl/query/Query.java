/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class Query
implements IdentifiedDataSerializable {
    private String mapName;
    private Predicate predicate;
    private IterationType iterationType;
    private Aggregator aggregator;
    private Projection projection;

    public Query() {
    }

    public Query(String mapName, Predicate predicate, IterationType iterationType, Aggregator aggregator, Projection projection) {
        this.mapName = Preconditions.checkNotNull(mapName);
        this.predicate = Preconditions.checkNotNull(predicate);
        this.iterationType = Preconditions.checkNotNull(iterationType);
        this.aggregator = aggregator;
        this.projection = projection;
        if (aggregator != null && projection != null) {
            throw new IllegalArgumentException("It's forbidden to use a Projection with an Aggregator.");
        }
    }

    public String getMapName() {
        return this.mapName;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public IterationType getIterationType() {
        return this.iterationType;
    }

    public Aggregator getAggregator() {
        return this.aggregator;
    }

    public Class<? extends Result> getResultType() {
        if (this.isAggregationQuery()) {
            return AggregationResult.class;
        }
        return QueryResult.class;
    }

    public boolean isAggregationQuery() {
        return this.aggregator != null;
    }

    public Projection getProjection() {
        return this.projection;
    }

    public boolean isProjectionQuery() {
        return this.projection != null;
    }

    public Result createResult(SerializationService serializationService, long limit) {
        if (this.isAggregationQuery()) {
            Aggregator aggregatorClone = (Aggregator)serializationService.toObject(serializationService.toData(this.aggregator));
            return new AggregationResult(aggregatorClone, serializationService);
        }
        return new QueryResult(this.iterationType, this.projection, serializationService, limit, this.predicate instanceof PagingPredicate);
    }

    public static QueryBuilder of() {
        return new QueryBuilder();
    }

    public static QueryBuilder of(Query query) {
        return new QueryBuilder(query);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 116;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        out.writeObject(this.predicate);
        out.writeByte(this.iterationType.getId());
        out.writeObject(this.aggregator);
        out.writeObject(this.projection);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        this.predicate = (Predicate)in.readObject();
        this.iterationType = IterationType.getById(in.readByte());
        this.aggregator = (Aggregator)in.readObject();
        this.projection = (Projection)in.readObject();
    }

    public static final class QueryBuilder {
        private String mapName;
        private Predicate predicate;
        private IterationType iterationType;
        private Aggregator aggregator;
        private Projection projection;

        private QueryBuilder() {
        }

        private QueryBuilder(Query query) {
            this.mapName = query.mapName;
            this.predicate = query.predicate;
            this.iterationType = query.iterationType;
            this.aggregator = query.aggregator;
            this.projection = query.projection;
        }

        public QueryBuilder mapName(String mapName) {
            this.mapName = mapName;
            return this;
        }

        public QueryBuilder predicate(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public QueryBuilder iterationType(IterationType iterationType) {
            this.iterationType = iterationType;
            return this;
        }

        public QueryBuilder aggregator(Aggregator aggregator) {
            this.aggregator = aggregator;
            return this;
        }

        public QueryBuilder projection(Projection projection) {
            this.projection = projection;
            return this;
        }

        public Query build() {
            return new Query(this.mapName, this.predicate, this.iterationType, this.aggregator, this.projection);
        }
    }
}

