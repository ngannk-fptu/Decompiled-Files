/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity.plan;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.EffectiveEntityGraph;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.plan.build.internal.AbstractLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.internal.FetchGraphLoadPlanBuildingStrategy;
import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.internal.LoadGraphLoadPlanBuildingStrategy;
import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
import org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader;
import org.hibernate.loader.plan.exec.internal.BatchingLoadQueryDetailsFactory;
import org.hibernate.loader.plan.exec.internal.EntityLoadQueryDetails;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessorResolver;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;

public abstract class AbstractLoadPlanBasedEntityLoader
extends AbstractLoadPlanBasedLoader
implements UniqueEntityLoader {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(AbstractLoadPlanBasedEntityLoader.class);
    private final OuterJoinLoadable entityPersister;
    private final Type uniqueKeyType;
    private final String entityName;
    private final EntityLoadQueryDetails staticLoadQuery;

    public AbstractLoadPlanBasedEntityLoader(OuterJoinLoadable entityPersister, SessionFactoryImplementor factory, String[] uniqueKeyColumnNames, Type uniqueKeyType, QueryBuildingParameters buildingParameters, ResultSetProcessorResolver resultSetProcessorResolver) {
        super(factory);
        this.entityPersister = entityPersister;
        this.uniqueKeyType = uniqueKeyType;
        this.entityName = entityPersister.getEntityName();
        EffectiveEntityGraph effectiveEntityGraph = buildingParameters.getQueryInfluencers().getEffectiveEntityGraph();
        AbstractLoadPlanBuildingAssociationVisitationStrategy strategy = effectiveEntityGraph.getSemantic() == GraphSemantic.FETCH ? new FetchGraphLoadPlanBuildingStrategy(factory, effectiveEntityGraph.getGraph(), buildingParameters.getQueryInfluencers(), buildingParameters.getLockOptions() != null ? buildingParameters.getLockOptions().getLockMode() : buildingParameters.getLockMode()) : (effectiveEntityGraph.getSemantic() == GraphSemantic.LOAD ? new LoadGraphLoadPlanBuildingStrategy(factory, effectiveEntityGraph.getGraph(), buildingParameters.getQueryInfluencers(), buildingParameters.getLockOptions() != null ? buildingParameters.getLockOptions().getLockMode() : buildingParameters.getLockMode()) : new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(factory, buildingParameters.getQueryInfluencers(), buildingParameters.getLockOptions() != null ? buildingParameters.getLockOptions().getLockMode() : buildingParameters.getLockMode()));
        LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan(strategy, entityPersister);
        this.staticLoadQuery = BatchingLoadQueryDetailsFactory.INSTANCE.makeEntityLoadQueryDetails(plan, uniqueKeyColumnNames, buildingParameters, factory, resultSetProcessorResolver);
    }

    public AbstractLoadPlanBasedEntityLoader(OuterJoinLoadable entityPersister, SessionFactoryImplementor factory, String[] uniqueKeyColumnNames, Type uniqueKeyType, QueryBuildingParameters buildingParameters) {
        this(entityPersister, factory, uniqueKeyColumnNames, uniqueKeyType, buildingParameters, ResultSetProcessorResolver.DEFAULT);
    }

    protected AbstractLoadPlanBasedEntityLoader(OuterJoinLoadable entityPersister, SessionFactoryImplementor factory, EntityLoadQueryDetails entityLoaderQueryDetailsTemplate, Type uniqueKeyType, QueryBuildingParameters buildingParameters, ResultSetProcessorResolver resultSetProcessorResolver) {
        super(factory);
        this.entityPersister = entityPersister;
        this.uniqueKeyType = uniqueKeyType;
        this.entityName = entityPersister.getEntityName();
        this.staticLoadQuery = BatchingLoadQueryDetailsFactory.INSTANCE.makeEntityLoadQueryDetails(entityLoaderQueryDetailsTemplate, buildingParameters, resultSetProcessorResolver);
    }

    protected AbstractLoadPlanBasedEntityLoader(OuterJoinLoadable entityPersister, SessionFactoryImplementor factory, EntityLoadQueryDetails entityLoaderQueryDetailsTemplate, Type uniqueKeyType, QueryBuildingParameters buildingParameters) {
        this(entityPersister, factory, entityLoaderQueryDetailsTemplate, uniqueKeyType, buildingParameters, ResultSetProcessorResolver.DEFAULT);
    }

    public OuterJoinLoadable getEntityPersister() {
        return this.entityPersister;
    }

    @Override
    protected LoadQueryDetails getStaticLoadQuery() {
        return this.staticLoadQuery;
    }

    protected String getEntityName() {
        return this.entityName;
    }

    public List<?> loadEntityBatch(Serializable[] idsInBatch, OuterJoinLoadable persister, LockOptions lockOptions, SharedSessionContractImplementor session) {
        Type idType = persister.getIdentifierType();
        return this.loadEntityBatch(session, idsInBatch, persister.getIdentifierType(), null, null, null, persister, lockOptions);
    }

    public final List loadEntityBatch(SharedSessionContractImplementor session, Serializable[] ids, Type idType, Object optionalObject, String optionalEntityName, Serializable optionalId, EntityPersister persister, LockOptions lockOptions) throws HibernateException {
        return this.loadEntityBatch(session, ids, idType, optionalObject, optionalEntityName, optionalId, persister, lockOptions, null);
    }

    public final List loadEntityBatch(SharedSessionContractImplementor session, Serializable[] ids, Type idType, Object optionalObject, String optionalEntityName, Serializable optionalId, EntityPersister persister, LockOptions lockOptions, Boolean readOnly) throws HibernateException {
        List result;
        if (log.isDebugEnabled()) {
            log.debugf("Batch loading entity: %s", MessageHelper.infoString(persister, ids, this.getFactory()));
        }
        Object[] types = new Type[ids.length];
        Arrays.fill(types, idType);
        try {
            QueryParameters qp = new QueryParameters();
            qp.setPositionalParameterTypes((Type[])types);
            qp.setPositionalParameterValues(ids);
            qp.setOptionalObject(optionalObject);
            qp.setOptionalEntityName(optionalEntityName);
            qp.setOptionalId(optionalId);
            qp.setLockOptions(lockOptions);
            if (readOnly != null) {
                qp.setReadOnly(readOnly);
            }
            result = this.executeLoad(session, qp, this.staticLoadQuery, false, null);
        }
        catch (SQLException sqle) {
            throw this.getFactory().getSQLExceptionHelper().convert(sqle, "could not load an entity batch: " + MessageHelper.infoString((EntityPersister)this.entityPersister, ids, this.getFactory()), this.staticLoadQuery.getSqlStatement());
        }
        log.debug("Done entity batch load");
        return result;
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) throws HibernateException {
        return this.load(id, optionalObject, session, (Boolean)null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, Boolean readOnly) throws HibernateException {
        return this.load(id, optionalObject, session, LockOptions.NONE, readOnly);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
        return this.load(id, optionalObject, session, lockOptions, null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions, Boolean readOnly) {
        Object result;
        try {
            QueryParameters qp = new QueryParameters();
            qp.setPositionalParameterTypes(new Type[]{this.entityPersister.getIdentifierType()});
            qp.setPositionalParameterValues(new Object[]{id});
            qp.setOptionalObject(optionalObject);
            qp.setOptionalEntityName(this.entityPersister.getEntityName());
            qp.setOptionalId(id);
            qp.setLockOptions(lockOptions);
            if (readOnly != null) {
                qp.setReadOnly(readOnly);
            }
            List results = this.executeLoad(session, qp, this.staticLoadQuery, false, null);
            result = this.extractEntityResult(results, id);
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not load an entity: " + MessageHelper.infoString(this.entityPersister, id, this.entityPersister.getIdentifierType(), this.getFactory()), this.staticLoadQuery.getSqlStatement());
        }
        log.debugf("Done entity load : %s#%s", this.getEntityName(), id);
        return result;
    }

    @Deprecated
    protected Object extractEntityResult(List results) {
        return this.extractEntityResult(results, null);
    }

    protected Object extractEntityResult(List results, Serializable id) {
        if (results.size() == 0) {
            return null;
        }
        if (results.size() == 1) {
            return results.get(0);
        }
        if (this.staticLoadQuery.hasCollectionInitializers()) {
            Object row = results.get(0);
            if (row.getClass().isArray()) {
                Object[] rowArray = (Object[])row;
                if (rowArray.length == 1) {
                    return rowArray[0];
                }
            } else {
                return row;
            }
        }
        if (id == null) {
            throw new HibernateException("Unable to interpret given query results in terms of a load-entity query for " + this.entityName);
        }
        throw new HibernateException("More than one row with the given identifier was found: " + id + ", for class: " + this.entityName);
    }

    @Override
    protected int[] getNamedParameterLocs(String name) {
        throw new AssertionFailure("no named parameters");
    }

    @Override
    protected void autoDiscoverTypes(ResultSet rs) {
        throw new AssertionFailure("Auto discover types not supported in this loader");
    }
}

