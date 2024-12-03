/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.Loader;
import org.hibernate.loader.custom.CollectionFetchReturn;
import org.hibernate.loader.custom.CollectionReturn;
import org.hibernate.loader.custom.ConstructorResultColumnProcessor;
import org.hibernate.loader.custom.ConstructorReturn;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.loader.custom.EntityFetchReturn;
import org.hibernate.loader.custom.FetchReturn;
import org.hibernate.loader.custom.JdbcResultMetadata;
import org.hibernate.loader.custom.NonScalarResultColumnProcessor;
import org.hibernate.loader.custom.NonScalarReturn;
import org.hibernate.loader.custom.NonUniqueDiscoveredSqlAliasException;
import org.hibernate.loader.custom.ResultColumnProcessor;
import org.hibernate.loader.custom.ResultRowProcessor;
import org.hibernate.loader.custom.Return;
import org.hibernate.loader.custom.RootReturn;
import org.hibernate.loader.custom.ScalarResultColumnProcessor;
import org.hibernate.loader.custom.ScalarReturn;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.param.ParameterBinder;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class CustomLoader
extends Loader {
    private final String sql;
    private final Set<Serializable> querySpaces = new HashSet<Serializable>();
    private final List<ParameterBinder> paramValueBinders;
    private final Queryable[] entityPersisters;
    private final int[] entiytOwners;
    private final EntityAliases[] entityAliases;
    private final QueryableCollection[] collectionPersisters;
    private final int[] collectionOwners;
    private final CollectionAliases[] collectionAliases;
    private final LockMode[] lockModes;
    private boolean[] includeInResultRow;
    private final ResultRowProcessor rowProcessor;
    private Type[] resultTypes;
    private String[] transformerAliases;

    public CustomLoader(CustomQuery customQuery, SessionFactoryImplementor factory) {
        super(factory);
        int i;
        this.sql = customQuery.getSQL();
        this.querySpaces.addAll(customQuery.getQuerySpaces());
        this.paramValueBinders = customQuery.getParameterValueBinders();
        ArrayList<Queryable> entityPersisters = new ArrayList<Queryable>();
        ArrayList<Integer> entityOwners = new ArrayList<Integer>();
        ArrayList<EntityAliases> entityAliases = new ArrayList<EntityAliases>();
        ArrayList<QueryableCollection> collectionPersisters = new ArrayList<QueryableCollection>();
        ArrayList<Integer> collectionOwners = new ArrayList<Integer>();
        ArrayList<CollectionAliases> collectionAliases = new ArrayList<CollectionAliases>();
        ArrayList<LockMode> lockModes = new ArrayList<LockMode>();
        ArrayList<ResultColumnProcessor> resultColumnProcessors = new ArrayList<ResultColumnProcessor>();
        ArrayList<Return> nonScalarReturnList = new ArrayList<Return>();
        ArrayList<Type> resultTypes = new ArrayList<Type>();
        ArrayList<String> specifiedAliases = new ArrayList<String>();
        int returnableCounter = 0;
        boolean hasScalars = false;
        ArrayList<Boolean> includeInResultRowList = new ArrayList<Boolean>();
        for (Return rtn : customQuery.getCustomQueryReturns()) {
            Queryable ownerPersister;
            int ownerIndex;
            NonScalarReturn ownerDescriptor;
            FetchReturn fetchRtn;
            if (rtn instanceof ScalarReturn) {
                ScalarReturn scalarRtn = (ScalarReturn)rtn;
                resultTypes.add(scalarRtn.getType());
                specifiedAliases.add(scalarRtn.getColumnAlias());
                resultColumnProcessors.add(new ScalarResultColumnProcessor(StringHelper.unquote(scalarRtn.getColumnAlias(), factory.getJdbcServices().getDialect()), scalarRtn.getType()));
                includeInResultRowList.add(true);
                hasScalars = true;
                continue;
            }
            if (ConstructorReturn.class.isInstance(rtn)) {
                ConstructorReturn constructorReturn = (ConstructorReturn)rtn;
                resultTypes.add(null);
                includeInResultRowList.add(true);
                hasScalars = true;
                ScalarResultColumnProcessor[] scalarProcessors = new ScalarResultColumnProcessor[constructorReturn.getScalars().length];
                int i2 = 0;
                for (ScalarReturn scalarReturn : constructorReturn.getScalars()) {
                    scalarProcessors[i2++] = new ScalarResultColumnProcessor(StringHelper.unquote(scalarReturn.getColumnAlias(), factory.getJdbcServices().getDialect()), scalarReturn.getType());
                }
                resultColumnProcessors.add(new ConstructorResultColumnProcessor(constructorReturn.getTargetClass(), scalarProcessors));
                continue;
            }
            if (rtn instanceof RootReturn) {
                RootReturn rootRtn = (RootReturn)rtn;
                Queryable persister = (Queryable)factory.getMetamodel().entityPersister(rootRtn.getEntityName());
                entityPersisters.add(persister);
                lockModes.add(rootRtn.getLockMode());
                resultColumnProcessors.add(new NonScalarResultColumnProcessor(returnableCounter++));
                nonScalarReturnList.add(rtn);
                entityOwners.add(-1);
                resultTypes.add(persister.getType());
                specifiedAliases.add(rootRtn.getAlias());
                entityAliases.add(rootRtn.getEntityAliases());
                ArrayHelper.addAll(this.querySpaces, persister.getQuerySpaces());
                includeInResultRowList.add(true);
                continue;
            }
            if (rtn instanceof CollectionReturn) {
                CollectionReturn collRtn = (CollectionReturn)rtn;
                String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
                QueryableCollection persister = (QueryableCollection)factory.getMetamodel().collectionPersister(role);
                collectionPersisters.add(persister);
                lockModes.add(collRtn.getLockMode());
                resultColumnProcessors.add(new NonScalarResultColumnProcessor(returnableCounter++));
                nonScalarReturnList.add(rtn);
                collectionOwners.add(-1);
                resultTypes.add(persister.getType());
                specifiedAliases.add(collRtn.getAlias());
                collectionAliases.add(collRtn.getCollectionAliases());
                Type elementType = persister.getElementType();
                if (elementType.isEntityType()) {
                    Queryable elementPersister = (Queryable)((EntityType)elementType).getAssociatedJoinable(factory);
                    entityPersisters.add(elementPersister);
                    entityOwners.add(-1);
                    entityAliases.add(collRtn.getElementEntityAliases());
                    ArrayHelper.addAll(this.querySpaces, elementPersister.getQuerySpaces());
                }
                includeInResultRowList.add(true);
                continue;
            }
            if (rtn instanceof EntityFetchReturn) {
                fetchRtn = (EntityFetchReturn)rtn;
                ownerDescriptor = fetchRtn.getOwner();
                ownerIndex = nonScalarReturnList.indexOf(ownerDescriptor);
                entityOwners.add(ownerIndex);
                lockModes.add(fetchRtn.getLockMode());
                ownerPersister = this.determineAppropriateOwnerPersister(ownerDescriptor);
                EntityType fetchedType = (EntityType)ownerPersister.getPropertyType(fetchRtn.getOwnerProperty());
                String entityName = fetchedType.getAssociatedEntityName(this.getFactory());
                Queryable persister = (Queryable)factory.getMetamodel().entityPersister(entityName);
                entityPersisters.add(persister);
                nonScalarReturnList.add(rtn);
                specifiedAliases.add(fetchRtn.getAlias());
                entityAliases.add(((EntityFetchReturn)fetchRtn).getEntityAliases());
                ArrayHelper.addAll(this.querySpaces, persister.getQuerySpaces());
                includeInResultRowList.add(false);
                continue;
            }
            if (rtn instanceof CollectionFetchReturn) {
                fetchRtn = (CollectionFetchReturn)rtn;
                ownerDescriptor = fetchRtn.getOwner();
                ownerIndex = nonScalarReturnList.indexOf(ownerDescriptor);
                collectionOwners.add(ownerIndex);
                lockModes.add(fetchRtn.getLockMode());
                ownerPersister = this.determineAppropriateOwnerPersister(ownerDescriptor);
                String role = ownerPersister.getEntityName() + '.' + fetchRtn.getOwnerProperty();
                QueryableCollection persister = (QueryableCollection)factory.getMetamodel().collectionPersister(role);
                collectionPersisters.add(persister);
                nonScalarReturnList.add(rtn);
                specifiedAliases.add(fetchRtn.getAlias());
                collectionAliases.add(((CollectionFetchReturn)fetchRtn).getCollectionAliases());
                Type elementType = persister.getElementType();
                if (elementType.isEntityType()) {
                    Queryable elementPersister = (Queryable)((EntityType)elementType).getAssociatedJoinable(factory);
                    entityPersisters.add(elementPersister);
                    entityOwners.add(ownerIndex);
                    entityAliases.add(((CollectionFetchReturn)fetchRtn).getElementEntityAliases());
                    ArrayHelper.addAll(this.querySpaces, elementPersister.getQuerySpaces());
                }
                includeInResultRowList.add(false);
                continue;
            }
            throw new HibernateException("unexpected custom query return type : " + rtn.getClass().getName());
        }
        this.entityPersisters = new Queryable[entityPersisters.size()];
        for (i = 0; i < entityPersisters.size(); ++i) {
            this.entityPersisters[i] = (Queryable)entityPersisters.get(i);
        }
        this.entiytOwners = ArrayHelper.toIntArray(entityOwners);
        this.entityAliases = new EntityAliases[entityAliases.size()];
        for (i = 0; i < entityAliases.size(); ++i) {
            this.entityAliases[i] = (EntityAliases)entityAliases.get(i);
        }
        this.collectionPersisters = new QueryableCollection[collectionPersisters.size()];
        for (i = 0; i < collectionPersisters.size(); ++i) {
            this.collectionPersisters[i] = (QueryableCollection)collectionPersisters.get(i);
        }
        this.collectionOwners = ArrayHelper.toIntArray(collectionOwners);
        this.collectionAliases = new CollectionAliases[collectionAliases.size()];
        for (i = 0; i < collectionAliases.size(); ++i) {
            this.collectionAliases[i] = (CollectionAliases)collectionAliases.get(i);
        }
        this.lockModes = new LockMode[lockModes.size()];
        for (i = 0; i < lockModes.size(); ++i) {
            this.lockModes[i] = (LockMode)((Object)lockModes.get(i));
        }
        this.resultTypes = ArrayHelper.toTypeArray(resultTypes);
        this.transformerAliases = ArrayHelper.toStringArray(specifiedAliases);
        this.rowProcessor = new ResultRowProcessor(hasScalars, resultColumnProcessors.toArray(new ResultColumnProcessor[resultColumnProcessors.size()]));
        this.includeInResultRow = ArrayHelper.toBooleanArray(includeInResultRowList);
    }

    private Queryable determineAppropriateOwnerPersister(NonScalarReturn ownerDescriptor) {
        String entityName = null;
        if (ownerDescriptor instanceof RootReturn) {
            entityName = ((RootReturn)ownerDescriptor).getEntityName();
        } else if (ownerDescriptor instanceof CollectionReturn) {
            CollectionReturn collRtn = (CollectionReturn)ownerDescriptor;
            String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
            CollectionPersister persister = this.getFactory().getMetamodel().collectionPersister(role);
            EntityType ownerType = (EntityType)persister.getElementType();
            entityName = ownerType.getAssociatedEntityName(this.getFactory());
        } else if (ownerDescriptor instanceof FetchReturn) {
            Type ownerCollectionElementType;
            FetchReturn fetchRtn = (FetchReturn)ownerDescriptor;
            Queryable persister = this.determineAppropriateOwnerPersister(fetchRtn.getOwner());
            Type ownerType = persister.getPropertyType(fetchRtn.getOwnerProperty());
            if (ownerType.isEntityType()) {
                entityName = ((EntityType)ownerType).getAssociatedEntityName(this.getFactory());
            } else if (ownerType.isCollectionType() && (ownerCollectionElementType = ((CollectionType)ownerType).getElementType(this.getFactory())).isEntityType()) {
                entityName = ((EntityType)ownerCollectionElementType).getAssociatedEntityName(this.getFactory());
            }
        }
        if (entityName == null) {
            throw new HibernateException("Could not determine fetch owner : " + ownerDescriptor);
        }
        return (Queryable)this.getFactory().getMetamodel().entityPersister(entityName);
    }

    @Override
    protected String getQueryIdentifier() {
        return this.sql;
    }

    @Override
    public String getSQLString() {
        return this.sql;
    }

    public Set getQuerySpaces() {
        return this.querySpaces;
    }

    @Override
    protected LockMode[] getLockModes(LockOptions lockOptions) {
        return this.lockModes;
    }

    @Override
    protected Loadable[] getEntityPersisters() {
        return this.entityPersisters;
    }

    @Override
    protected CollectionPersister[] getCollectionPersisters() {
        return this.collectionPersisters;
    }

    @Override
    protected int[] getCollectionOwners() {
        return this.collectionOwners;
    }

    @Override
    protected int[] getOwners() {
        return this.entiytOwners;
    }

    public List list(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
        return this.list(session, queryParameters, this.querySpaces, this.resultTypes);
    }

    @Override
    protected String applyLocks(String sql, QueryParameters parameters, Dialect dialect, List<AfterLoadAction> afterLoadActions) throws QueryException {
        final LockOptions lockOptions = parameters.getLockOptions();
        if (lockOptions == null || lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0) {
            return sql;
        }
        afterLoadActions.add(new AfterLoadAction(){
            private final LockOptions originalLockOptions;
            {
                this.originalLockOptions = lockOptions.makeCopy();
            }

            @Override
            public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
                ((Session)((Object)session)).buildLockRequest(this.originalLockOptions).lock(persister.getEntityName(), entity);
            }
        });
        parameters.getLockOptions().setLockMode(LockMode.READ);
        return sql;
    }

    public ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        ResultTransformer resultTransformer = queryParameters.getResultTransformer();
        HolderInstantiator holderInstantiator = resultTransformer == null ? HolderInstantiator.NOOP_INSTANTIATOR : new HolderInstantiator(resultTransformer, this::getReturnAliasesForTransformer);
        return this.scroll(queryParameters, this.resultTypes, holderInstantiator, session);
    }

    @Override
    protected String[] getResultRowAliases() {
        return this.transformerAliases;
    }

    @Override
    protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
        return HolderInstantiator.resolveResultTransformer(null, resultTransformer);
    }

    @Override
    protected boolean[] includeInResultRow() {
        return this.includeInResultRow;
    }

    @Override
    protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        return this.rowProcessor.buildResultRow(row, rs, transformer != null, session);
    }

    @Override
    protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        return this.rowProcessor.buildResultRow(row, rs, session);
    }

    @Override
    protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
        HolderInstantiator holderInstantiator = HolderInstantiator.getHolderInstantiator(null, resultTransformer, this.getReturnAliasesForTransformer());
        if (holderInstantiator.isRequired()) {
            for (int i = 0; i < results.size(); ++i) {
                Object[] row = (Object[])results.get(i);
                Object result = holderInstantiator.instantiate(row);
                results.set(i, result);
            }
            return resultTransformer.transformList(results);
        }
        return results;
    }

    private String[] getReturnAliasesForTransformer() {
        return this.transformerAliases;
    }

    @Override
    protected EntityAliases[] getEntityAliases() {
        return this.entityAliases;
    }

    @Override
    protected CollectionAliases[] getCollectionAliases() {
        return this.collectionAliases;
    }

    @Override
    protected int bindParameterValues(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        Serializable optionalId = queryParameters.getOptionalId();
        if (optionalId != null) {
            this.paramValueBinders.get(0).bind(statement, queryParameters, session, startIndex);
            return session.getFactory().getMetamodel().entityPersister(queryParameters.getOptionalEntityName()).getIdentifierType().getColumnSpan(session.getFactory());
        }
        int span = 0;
        for (ParameterBinder paramValueBinder : this.paramValueBinders) {
            span += paramValueBinder.bind(statement, queryParameters, session, startIndex + span);
        }
        return span;
    }

    @Override
    protected void autoDiscoverTypes(ResultSet rs) {
        try {
            JdbcResultMetadata metadata = new JdbcResultMetadata(this.getFactory(), rs);
            this.rowProcessor.prepareForAutoDiscovery(metadata);
            ArrayList<String> aliases = new ArrayList<String>();
            ArrayList<Type> types = new ArrayList<Type>();
            for (ResultColumnProcessor resultProcessor : this.rowProcessor.getColumnProcessors()) {
                resultProcessor.performDiscovery(metadata, types, aliases);
            }
            this.validateAliases(aliases);
            this.resultTypes = ArrayHelper.toTypeArray(types);
            this.transformerAliases = ArrayHelper.toStringArray(aliases);
        }
        catch (SQLException e) {
            throw new HibernateException("Exception while trying to autodiscover types.", e);
        }
    }

    private void validateAliases(List<String> aliases) {
        HashSet<String> aliasesSet = new HashSet<String>();
        for (String alias : aliases) {
            this.validateAlias(alias);
            boolean alreadyExisted = !aliasesSet.add(alias);
            if (!alreadyExisted) continue;
            throw new NonUniqueDiscoveredSqlAliasException("Encountered a duplicated sql alias [" + alias + "] during auto-discovery of a native-sql query");
        }
    }

    protected void validateAlias(String alias) {
    }

    @Override
    protected void putResultInQueryCache(SharedSessionContractImplementor session, QueryParameters queryParameters, Type[] resultTypes, QueryResultsCache queryCache, QueryKey key, List result) {
        super.putResultInQueryCache(session, queryParameters, this.resultTypes, queryCache, key, result);
    }
}

