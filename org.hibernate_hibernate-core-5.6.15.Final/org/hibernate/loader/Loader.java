/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.QueryException;
import org.hibernate.ScrollMode;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.WrongClassException;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.cache.spi.FilterKey;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.dialect.pagination.NoopLimitHandler;
import org.hibernate.engine.internal.CacheHelper;
import org.hibernate.engine.internal.TwoPhaseLoad;
import org.hibernate.engine.jdbc.ColumnNameCache;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.loading.internal.CollectionLoadContext;
import org.hibernate.engine.spi.BatchFetchQueue;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.EntityUniqueKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.engine.spi.SubselectFetch;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.FetchingScrollableResultsImpl;
import org.hibernate.internal.ScrollableResultsImpl;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.UniqueKeyLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.transform.CacheableResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;

public abstract class Loader {
    public static final String SELECT = "select";
    public static final String SELECT_DISTINCT = "select distinct";
    protected static final CoreMessageLogger LOG = CoreLogging.messageLogger(Loader.class);
    private final SessionFactoryImplementor factory;
    private volatile ColumnNameCache columnNameCache;
    private boolean isJdbc4 = true;

    public Loader(SessionFactoryImplementor factory) {
        this.factory = factory;
    }

    public abstract String getSQLString();

    protected abstract Loadable[] getEntityPersisters();

    protected boolean[] getEntityEagerPropertyFetches() {
        return null;
    }

    protected boolean[][] getEntityEagerPerPropertyFetches() {
        return null;
    }

    protected int[] getOwners() {
        return null;
    }

    protected EntityType[] getOwnerAssociationTypes() {
        return null;
    }

    protected CollectionPersister[] getCollectionPersisters() {
        return null;
    }

    protected int[] getCollectionOwners() {
        return null;
    }

    protected int[][] getCompositeKeyManyToOneTargetIndices() {
        return null;
    }

    protected abstract LockMode[] getLockModes(LockOptions var1);

    protected String applyLocks(String sql, QueryParameters parameters, Dialect dialect, List<AfterLoadAction> afterLoadActions) throws HibernateException {
        return sql;
    }

    protected boolean upgradeLocks() {
        return false;
    }

    protected boolean isSingleRowLoader() {
        return false;
    }

    protected String[] getAliases() {
        return null;
    }

    protected String preprocessSQL(String sql, QueryParameters parameters, SessionFactoryImplementor sessionFactory, List<AfterLoadAction> afterLoadActions) throws HibernateException {
        Dialect dialect = sessionFactory.getFastSessionServices().dialect;
        sql = this.applyLocks(sql, parameters, dialect, afterLoadActions);
        sql = dialect.addSqlHintOrComment(sql, parameters, sessionFactory.getSessionFactoryOptions().isCommentsEnabled());
        return this.processDistinctKeyword(sql, parameters);
    }

    protected boolean shouldUseFollowOnLocking(QueryParameters parameters, Dialect dialect, List<AfterLoadAction> afterLoadActions) {
        LockMode lockMode;
        LockOptions lockOptions;
        if ((parameters.getLockOptions().getFollowOnLocking() == null && dialect.useFollowOnLocking(parameters) || parameters.getLockOptions().getFollowOnLocking() != null && parameters.getLockOptions().getFollowOnLocking().booleanValue()) && (lockOptions = new LockOptions(lockMode = this.determineFollowOnLockMode(parameters.getLockOptions()))).getLockMode() != LockMode.UPGRADE_SKIPLOCKED) {
            if (lockOptions.getLockMode() != LockMode.NONE) {
                LOG.usingFollowOnLocking();
            }
            lockOptions.setTimeOut(parameters.getLockOptions().getTimeOut());
            lockOptions.setScope(parameters.getLockOptions().getScope());
            afterLoadActions.add(new AfterLoadAction(){

                @Override
                public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
                    ((Session)((Object)session)).buildLockRequest(lockOptions).lock(persister.getEntityName(), entity);
                }
            });
            parameters.setLockOptions(new LockOptions());
            return true;
        }
        return false;
    }

    protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
        LockMode lockModeToUse = lockOptions.findGreatestLockMode();
        if (lockOptions.hasAliasSpecificLockModes()) {
            if (lockOptions.getLockMode() == LockMode.NONE && lockModeToUse == LockMode.NONE) {
                return lockModeToUse;
            }
            LOG.aliasSpecificLockingWithFollowOnLocking(lockModeToUse);
        }
        return lockModeToUse;
    }

    public List doQueryAndInitializeNonLazyCollections(SharedSessionContractImplementor session, QueryParameters queryParameters, boolean returnProxies) throws HibernateException, SQLException {
        return this.doQueryAndInitializeNonLazyCollections(session, queryParameters, returnProxies, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List doQueryAndInitializeNonLazyCollections(SharedSessionContractImplementor session, QueryParameters queryParameters, boolean returnProxies, ResultTransformer forcedResultTransformer) throws HibernateException, SQLException {
        List result;
        PersistenceContext persistenceContext = session.getPersistenceContext();
        boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
        if (queryParameters.isReadOnlyInitialized()) {
            persistenceContext.setDefaultReadOnly(queryParameters.isReadOnly());
        } else {
            queryParameters.setReadOnly(persistenceContext.isDefaultReadOnly());
        }
        persistenceContext.beforeLoad();
        try {
            try {
                result = this.doQuery(session, queryParameters, returnProxies, forcedResultTransformer);
            }
            finally {
                persistenceContext.afterLoad();
            }
            persistenceContext.initializeNonLazyCollections();
        }
        finally {
            persistenceContext.setDefaultReadOnly(defaultReadOnlyOrig);
        }
        return result;
    }

    public Object loadSingleRow(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, boolean returnProxies) throws HibernateException {
        Object result;
        int entitySpan = this.getEntityPersisters().length;
        ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList(entitySpan);
        try {
            result = this.getRowFromResultSet(resultSet, session, queryParameters, this.getLockModes(queryParameters.getLockOptions()), null, hydratedObjects, new EntityKey[entitySpan], returnProxies);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not read next row of results", this.getSQLString());
        }
        this.initializeEntitiesAndCollections(hydratedObjects, resultSet, session, queryParameters.isReadOnly(session));
        session.getPersistenceContextInternal().initializeNonLazyCollections();
        return result;
    }

    private Object sequentialLoad(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, boolean returnProxies, EntityKey keyToRead) throws HibernateException {
        int entitySpan = this.getEntityPersisters().length;
        ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList(entitySpan);
        Object result = null;
        EntityKey[] loadedKeys = new EntityKey[entitySpan];
        try {
            do {
                Object loaded = this.getRowFromResultSet(resultSet, session, queryParameters, this.getLockModes(queryParameters.getLockOptions()), null, hydratedObjects, loadedKeys, returnProxies);
                if (!keyToRead.equals(loadedKeys[0])) {
                    throw new AssertionFailure(String.format("Unexpected key read for row; expected [%s]; actual [%s]", keyToRead, loadedKeys[0]));
                }
                if (result != null) continue;
                result = loaded;
            } while (resultSet.next() && this.isCurrentRowForSameEntity(keyToRead, 0, resultSet, session));
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not doAfterTransactionCompletion sequential read of results (forward)", this.getSQLString());
        }
        this.initializeEntitiesAndCollections(hydratedObjects, resultSet, session, queryParameters.isReadOnly(session));
        session.getPersistenceContextInternal().initializeNonLazyCollections();
        return result;
    }

    private boolean isCurrentRowForSameEntity(EntityKey keyToRead, int persisterIndex, ResultSet resultSet, SharedSessionContractImplementor session) throws SQLException {
        EntityKey currentRowKey = this.getKeyFromResultSet(persisterIndex, this.getEntityPersisters()[persisterIndex], null, resultSet, session);
        return keyToRead.equals(currentRowKey);
    }

    public Object loadSequentialRowsForward(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, boolean returnProxies) throws HibernateException {
        try {
            if (resultSet.isAfterLast()) {
                return null;
            }
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
            }
            EntityKey currentKey = this.getKeyFromResultSet(0, this.getEntityPersisters()[0], null, resultSet, session);
            return this.sequentialLoad(resultSet, session, queryParameters, returnProxies, currentKey);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not perform sequential read of results (forward)", this.getSQLString());
        }
    }

    public Object loadSequentialRowsReverse(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, boolean returnProxies, boolean isLogicallyAfterLast) throws HibernateException {
        try {
            EntityKey checkKey;
            if (resultSet.isFirst()) {
                return null;
            }
            EntityKey keyToRead = null;
            if (resultSet.isAfterLast() && isLogicallyAfterLast) {
                resultSet.last();
                keyToRead = this.getKeyFromResultSet(0, this.getEntityPersisters()[0], null, resultSet, session);
            } else {
                resultSet.previous();
                boolean firstPass = true;
                EntityKey lastKey = this.getKeyFromResultSet(0, this.getEntityPersisters()[0], null, resultSet, session);
                while (resultSet.previous()) {
                    EntityKey checkKey2 = this.getKeyFromResultSet(0, this.getEntityPersisters()[0], null, resultSet, session);
                    if (firstPass) {
                        firstPass = false;
                        keyToRead = checkKey2;
                    }
                    if (lastKey.equals(checkKey2)) continue;
                }
            }
            while (resultSet.previous() && keyToRead.equals(checkKey = this.getKeyFromResultSet(0, this.getEntityPersisters()[0], null, resultSet, session))) {
            }
            resultSet.next();
            return this.sequentialLoad(resultSet, session, queryParameters, returnProxies, keyToRead);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not doAfterTransactionCompletion sequential read of results (forward)", this.getSQLString());
        }
    }

    protected static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SharedSessionContractImplementor session) {
        Object optionalObject = queryParameters.getOptionalObject();
        Serializable optionalId = queryParameters.getOptionalId();
        String optionalEntityName = queryParameters.getOptionalEntityName();
        if (optionalObject != null && optionalEntityName != null) {
            return session.generateEntityKey(optionalId, session.getEntityPersister(optionalEntityName, optionalObject));
        }
        return null;
    }

    private Object getRowFromResultSet(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, LockMode[] lockModesArray, EntityKey optionalObjectKey, List hydratedObjects, EntityKey[] keys, boolean returnProxies) throws SQLException, HibernateException {
        return this.getRowFromResultSet(resultSet, session, queryParameters, lockModesArray, optionalObjectKey, hydratedObjects, keys, returnProxies, null);
    }

    private Object getRowFromResultSet(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, LockMode[] lockModesArray, EntityKey optionalObjectKey, List hydratedObjects, EntityKey[] keys, boolean returnProxies, ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
        Loadable[] persisters = this.getEntityPersisters();
        int entitySpan = persisters.length;
        this.extractKeysFromResultSet(persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects);
        this.registerNonExists(keys, persisters, session);
        Object[] row = this.getRow(resultSet, persisters, keys, queryParameters.getOptionalObject(), optionalObjectKey, lockModesArray, hydratedObjects, session);
        this.readCollectionElements(row, resultSet, session);
        if (returnProxies) {
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            for (int i = 0; i < entitySpan; ++i) {
                Object entity = row[i];
                Object proxy = persistenceContext.proxyFor(persisters[i], keys[i], entity);
                if (entity == proxy) continue;
                ((HibernateProxy)proxy).getHibernateLazyInitializer().setImplementation(entity);
                row[i] = proxy;
            }
        }
        this.applyPostLoadLocks(row, lockModesArray, session);
        return forcedResultTransformer == null ? this.getResultColumnOrRow(row, queryParameters.getResultTransformer(), resultSet, session) : forcedResultTransformer.transformTuple(this.getResultRow(row, resultSet, session), this.getResultRowAliases());
    }

    protected void extractKeysFromResultSet(Loadable[] persisters, QueryParameters queryParameters, ResultSet resultSet, SharedSessionContractImplementor session, EntityKey[] keys, LockMode[] lockModes, List hydratedObjects) throws SQLException {
        Type idType;
        int i;
        int numberOfPersistersToProcess;
        int entitySpan = persisters.length;
        Serializable optionalId = queryParameters.getOptionalId();
        if (this.isSingleRowLoader() && optionalId != null) {
            keys[entitySpan - 1] = session.generateEntityKey(optionalId, persisters[entitySpan - 1]);
            numberOfPersistersToProcess = entitySpan - 1;
        } else {
            numberOfPersistersToProcess = entitySpan;
        }
        Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
        for (i = 0; i < numberOfPersistersToProcess; ++i) {
            idType = persisters[i].getIdentifierType();
            hydratedKeyState[i] = idType.hydrate(resultSet, this.getEntityAliases()[i].getSuffixedKeyAliases(), session, null);
        }
        for (i = 0; i < numberOfPersistersToProcess; ++i) {
            int[] keyManyToOneTargetIndices;
            idType = persisters[i].getIdentifierType();
            if (idType.isComponentType() && this.getCompositeKeyManyToOneTargetIndices() != null && (keyManyToOneTargetIndices = this.getCompositeKeyManyToOneTargetIndices()[i]) != null) {
                for (int targetIndex : keyManyToOneTargetIndices) {
                    Object object;
                    if (targetIndex < numberOfPersistersToProcess) {
                        Type targetIdType = persisters[targetIndex].getIdentifierType();
                        Serializable targetId = (Serializable)targetIdType.resolve(hydratedKeyState[targetIndex], session, null);
                        keys[targetIndex] = session.generateEntityKey(targetId, persisters[targetIndex]);
                    }
                    if ((object = session.getEntityUsingInterceptor(keys[targetIndex])) != null) {
                        this.instanceAlreadyLoaded(resultSet, targetIndex, persisters[targetIndex], keys[targetIndex], object, lockModes[targetIndex], hydratedObjects, session);
                        continue;
                    }
                    this.instanceNotYetLoaded(resultSet, targetIndex, persisters[targetIndex], this.getEntityAliases()[targetIndex].getRowIdAlias(), keys[targetIndex], lockModes[targetIndex], Loader.getOptionalObjectKey(queryParameters, session), queryParameters.getOptionalObject(), hydratedObjects, session);
                }
            }
            Serializable resolvedId = hydratedKeyState[i] != null ? (Serializable)idType.resolve(hydratedKeyState[i], session, null) : null;
            keys[i] = resolvedId == null ? null : session.generateEntityKey(resolvedId, persisters[i]);
        }
    }

    protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SharedSessionContractImplementor session) {
    }

    private void readCollectionElements(Object[] row, ResultSet resultSet, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        CollectionPersister[] collectionPersisters = this.getCollectionPersisters();
        if (collectionPersisters != null) {
            CollectionAliases[] descriptors = this.getCollectionAliases();
            int[] collectionOwners = this.getCollectionOwners();
            for (int i = 0; i < collectionPersisters.length; ++i) {
                boolean hasCollectionOwners = collectionOwners != null && collectionOwners[i] > -1;
                Object owner = hasCollectionOwners ? row[collectionOwners[i]] : null;
                CollectionPersister collectionPersister = collectionPersisters[i];
                Serializable key = owner == null ? null : collectionPersister.getCollectionType().getKeyOfOwner(owner, session);
                this.readCollectionElement(owner, key, collectionPersister, descriptors[i], resultSet, session);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List doQuery(SharedSessionContractImplementor session, QueryParameters queryParameters, boolean returnProxies, ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
        RowSelection selection = queryParameters.getRowSelection();
        int maxRows = LimitHelper.hasMaxRows(selection) ? selection.getMaxRows() : Integer.MAX_VALUE;
        ArrayList<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
        SqlStatementWrapper wrapper = this.executeQueryStatement(queryParameters, false, afterLoadActions, session);
        ResultSet rs = wrapper.getResultSet();
        Statement st = wrapper.getStatement();
        try {
            List list = this.processResultSet(rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions);
            return list;
        }
        finally {
            JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
            jdbcCoordinator.getLogicalConnection().getResourceRegistry().release(st);
            jdbcCoordinator.afterStatementExecution();
        }
    }

    protected List processResultSet(ResultSet rs, QueryParameters queryParameters, SharedSessionContractImplementor session, boolean returnProxies, ResultTransformer forcedResultTransformer, int maxRows, List<AfterLoadAction> afterLoadActions) throws SQLException {
        int entitySpan = this.getEntityPersisters().length;
        boolean createSubselects = this.isSubselectLoadingEnabled();
        ArrayList<EntityKey[]> subselectResultKeys = createSubselects ? new ArrayList<EntityKey[]>() : null;
        ArrayList<Object> hydratedObjects = entitySpan == 0 ? null : new ArrayList<Object>(entitySpan * 10);
        List<Object> results = this.getRowsFromResultSet(rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, hydratedObjects, subselectResultKeys);
        this.initializeEntitiesAndCollections(hydratedObjects, rs, session, queryParameters.isReadOnly(session), afterLoadActions);
        if (createSubselects) {
            this.createSubselects(subselectResultKeys, queryParameters, session);
        }
        return results;
    }

    protected List<Object> getRowsFromResultSet(ResultSet rs, QueryParameters queryParameters, SharedSessionContractImplementor session, boolean returnProxies, ResultTransformer forcedResultTransformer, int maxRows, List<Object> hydratedObjects, List<EntityKey[]> subselectResultKeys) throws SQLException {
        int count;
        int entitySpan = this.getEntityPersisters().length;
        boolean createSubselects = this.isSubselectLoadingEnabled();
        EntityKey optionalObjectKey = Loader.getOptionalObjectKey(queryParameters, session);
        LockMode[] lockModesArray = this.getLockModes(queryParameters.getLockOptions());
        ArrayList<Object> results = new ArrayList<Object>();
        this.handleEmptyCollections(queryParameters.getCollectionKeys(), rs, session);
        EntityKey[] keys = new EntityKey[entitySpan];
        LOG.trace("Processing result set");
        boolean debugEnabled = LOG.isDebugEnabled();
        for (count = 0; count < maxRows && rs.next(); ++count) {
            if (debugEnabled) {
                LOG.debugf("Result set row: %s", count);
            }
            Object result = this.getRowFromResultSet(rs, session, queryParameters, lockModesArray, optionalObjectKey, hydratedObjects, keys, returnProxies, forcedResultTransformer);
            results.add(result);
            if (!createSubselects) continue;
            subselectResultKeys.add(keys);
            keys = new EntityKey[entitySpan];
        }
        LOG.tracev("Done processing result set ({0} rows)", count);
        return results;
    }

    protected boolean isSubselectLoadingEnabled() {
        return false;
    }

    protected boolean hasSubselectLoadableCollections() {
        Loadable[] loadables;
        for (Loadable loadable : loadables = this.getEntityPersisters()) {
            if (!loadable.hasSubselectLoadableCollections()) continue;
            return true;
        }
        return false;
    }

    private static Set[] transpose(List keys) {
        Set[] result = new Set[((EntityKey[])keys.get(0)).length];
        for (int j = 0; j < result.length; ++j) {
            result[j] = new HashSet(keys.size());
            for (Object key : keys) {
                result[j].add(((EntityKey[])key)[j]);
            }
        }
        return result;
    }

    protected void createSubselects(List keys, QueryParameters queryParameters, SharedSessionContractImplementor session) {
        if (keys.size() > 1) {
            Set[] keySets = Loader.transpose(keys);
            Map namedParameterLocMap = this.buildNamedParameterLocMap(queryParameters);
            Loadable[] loadables = this.getEntityPersisters();
            String[] aliases = this.getAliases();
            String subselectQueryString = SubselectFetch.createSubselectFetchQueryFragment(queryParameters);
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            BatchFetchQueue batchFetchQueue = persistenceContext.getBatchFetchQueue();
            for (Object key : keys) {
                EntityKey[] rowKeys = (EntityKey[])key;
                for (int i = 0; i < rowKeys.length; ++i) {
                    if (rowKeys[i] == null || !loadables[i].hasSubselectLoadableCollections()) continue;
                    SubselectFetch subselectFetch = new SubselectFetch(subselectQueryString, aliases[i], loadables[i], queryParameters, keySets[i], namedParameterLocMap);
                    batchFetchQueue.addSubselect(rowKeys[i], subselectFetch);
                }
            }
        }
    }

    private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
        if (queryParameters.getNamedParameters() != null) {
            HashMap<String, int[]> namedParameterLocMap = new HashMap<String, int[]>();
            for (String name : queryParameters.getNamedParameters().keySet()) {
                namedParameterLocMap.put(name, this.getNamedParameterLocs(name));
            }
            return namedParameterLocMap;
        }
        return null;
    }

    private void initializeEntitiesAndCollections(List hydratedObjects, Object resultSetId, SharedSessionContractImplementor session, boolean readOnly) throws HibernateException {
        this.initializeEntitiesAndCollections(hydratedObjects, resultSetId, session, readOnly, Collections.emptyList());
    }

    private void initializeEntitiesAndCollections(List hydratedObjects, Object resultSetId, SharedSessionContractImplementor session, boolean readOnly, List<AfterLoadAction> afterLoadActions) throws HibernateException {
        PostLoadEvent post;
        PreLoadEvent pre;
        CollectionPersister[] collectionPersisters = this.getCollectionPersisters();
        if (collectionPersisters != null) {
            for (CollectionPersister collectionPersister : collectionPersisters) {
                if (!collectionPersister.isArray()) continue;
                this.endCollectionLoad(resultSetId, session, collectionPersister);
            }
        }
        if (session.isEventSource()) {
            pre = new PreLoadEvent((EventSource)session);
            post = new PostLoadEvent((EventSource)session);
        } else {
            pre = null;
            post = null;
        }
        if (hydratedObjects != null) {
            int hydratedObjectsSize = hydratedObjects.size();
            LOG.tracev("Total objects hydrated: {0}", hydratedObjectsSize);
            if (hydratedObjectsSize != 0) {
                for (Object hydratedObject : hydratedObjects) {
                    TwoPhaseLoad.initializeEntity(hydratedObject, readOnly, session, pre);
                }
            }
        }
        if (collectionPersisters != null) {
            for (CollectionPersister collectionPersister : collectionPersisters) {
                if (collectionPersister.isArray()) continue;
                this.endCollectionLoad(resultSetId, session, collectionPersister);
            }
        }
        if (hydratedObjects != null) {
            for (Object hydratedObject : hydratedObjects) {
                TwoPhaseLoad.afterInitialize(hydratedObject, session);
            }
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        if (hydratedObjects != null && hydratedObjects.size() > 0) {
            for (Object hydratedObject : hydratedObjects) {
                TwoPhaseLoad.postLoad(hydratedObject, session, post);
                if (afterLoadActions == null) continue;
                for (AfterLoadAction afterLoadAction : afterLoadActions) {
                    EntityEntry entityEntry = persistenceContext.getEntry(hydratedObject);
                    if (entityEntry == null) {
                        throw new HibernateException("Could not locate EntityEntry immediately after two-phase load");
                    }
                    afterLoadAction.afterLoad(session, hydratedObject, (Loadable)entityEntry.getPersister());
                }
            }
        }
    }

    protected void endCollectionLoad(Object resultSetId, SharedSessionContractImplementor session, CollectionPersister collectionPersister) {
        session.getPersistenceContextInternal().getLoadContexts().getCollectionLoadContext((ResultSet)resultSetId).endLoadingCollections(collectionPersister);
    }

    protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
        return resultTransformer;
    }

    protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
        return results;
    }

    protected boolean areResultSetRowsTransformedImmediately() {
        return false;
    }

    protected String[] getResultRowAliases() {
        return null;
    }

    protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        return row;
    }

    protected boolean[] includeInResultRow() {
        return null;
    }

    protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        return row;
    }

    protected void registerNonExists(EntityKey[] keys, Loadable[] persisters, SharedSessionContractImplementor session) {
        int[] owners = this.getOwners();
        if (owners != null) {
            EntityType[] ownerAssociationTypes = this.getOwnerAssociationTypes();
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            for (int i = 0; i < keys.length; ++i) {
                boolean isOneToOneAssociation;
                int owner = owners[i];
                if (owner <= -1) continue;
                EntityKey ownerKey = keys[owner];
                if (keys[i] != null || ownerKey == null) continue;
                boolean bl = isOneToOneAssociation = ownerAssociationTypes != null && ownerAssociationTypes[i] != null && ownerAssociationTypes[i].isOneToOne();
                if (!isOneToOneAssociation) continue;
                persistenceContext.addNullProperty(ownerKey, ownerAssociationTypes[i].getPropertyName());
            }
        }
    }

    private void readCollectionElement(Object optionalOwner, Serializable optionalKey, CollectionPersister persister, CollectionAliases descriptor, ResultSet rs, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        Serializable collectionRowKey = (Serializable)persister.readKey(rs, descriptor.getSuffixedKeyAliases(), session);
        if (collectionRowKey != null) {
            PersistentCollection rowCollection;
            Object owner;
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Found row of collection: %s", MessageHelper.collectionInfoString(persister, collectionRowKey, this.getFactory()));
            }
            if ((owner = optionalOwner) != null || (owner = persistenceContext.getCollectionOwner(collectionRowKey, persister)) == null) {
                // empty if block
            }
            if ((rowCollection = persistenceContext.getLoadContexts().getCollectionLoadContext(rs).getLoadingCollection(persister, collectionRowKey)) != null) {
                rowCollection.readFrom(rs, persister, descriptor, owner);
            }
        } else if (optionalKey != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Result set contains (possibly empty) collection: %s", MessageHelper.collectionInfoString(persister, optionalKey, this.getFactory()));
            }
            persistenceContext.getLoadContexts().getCollectionLoadContext(rs).getLoadingCollection(persister, optionalKey);
        }
    }

    protected void handleEmptyCollections(Serializable[] keys, Object resultSetId, SharedSessionContractImplementor session) {
        if (keys != null) {
            CollectionPersister[] collectionPersisters = this.getCollectionPersisters();
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            boolean debugEnabled = LOG.isDebugEnabled();
            CollectionLoadContext collectionLoadContext = persistenceContext.getLoadContexts().getCollectionLoadContext((ResultSet)resultSetId);
            for (CollectionPersister collectionPersister : collectionPersisters) {
                for (Serializable key : keys) {
                    if (debugEnabled) {
                        LOG.debugf("Result set contains (possibly empty) collection: %s", MessageHelper.collectionInfoString(collectionPersister, key, this.getFactory()));
                    }
                    collectionLoadContext.getLoadingCollection(collectionPersister, key);
                }
            }
        }
    }

    private EntityKey getKeyFromResultSet(int i, Loadable persister, Serializable id, ResultSet rs, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Serializable resultId;
        if (this.isSingleRowLoader() && id != null) {
            resultId = id;
        } else {
            boolean idIsResultId;
            Type idType = persister.getIdentifierType();
            resultId = (Serializable)idType.nullSafeGet(rs, this.getEntityAliases()[i].getSuffixedKeyAliases(), session, null);
            boolean bl = idIsResultId = id != null && resultId != null && idType.isEqual(id, resultId, this.factory);
            if (idIsResultId) {
                resultId = id;
            }
        }
        return resultId == null ? null : session.generateEntityKey(resultId, persister);
    }

    private void checkVersion(int i, Loadable persister, Serializable id, Object entity, ResultSet rs, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object currentVersion;
        VersionType versionType;
        Object version = session.getPersistenceContextInternal().getEntry(entity).getVersion();
        if (version != null && !(versionType = persister.getVersionType()).isEqual(version, currentVersion = versionType.nullSafeGet(rs, this.getEntityAliases()[i].getSuffixedVersionAliases(), session, null))) {
            StatisticsImplementor statistics = session.getFactory().getStatistics();
            if (statistics.isStatisticsEnabled()) {
                statistics.optimisticFailure(persister.getEntityName());
            }
            throw new StaleObjectStateException(persister.getEntityName(), id);
        }
    }

    private Object[] getRow(ResultSet rs, Loadable[] persisters, EntityKey[] keys, Object optionalObject, EntityKey optionalObjectKey, LockMode[] lockModes, List hydratedObjects, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        int cols = persisters.length;
        EntityAliases[] entityAliases = this.getEntityAliases();
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Result row: %s", StringHelper.toString(keys));
        }
        Object[] rowResults = new Object[cols];
        for (int i = 0; i < cols; ++i) {
            Object object = null;
            EntityKey key = keys[i];
            if (keys[i] != null) {
                object = session.getEntityUsingInterceptor(key);
                if (object != null) {
                    this.instanceAlreadyLoaded(rs, i, persisters[i], key, object, lockModes[i], hydratedObjects, session);
                } else {
                    object = this.instanceNotYetLoaded(rs, i, persisters[i], entityAliases[i].getRowIdAlias(), key, lockModes[i], optionalObjectKey, optionalObject, hydratedObjects, session);
                }
            }
            rowResults[i] = object;
        }
        return rowResults;
    }

    protected void instanceAlreadyLoaded(ResultSet rs, int i, Loadable persister, EntityKey key, Object object, LockMode requestedLockMode, List hydratedObjects, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        EntityEntry entry;
        PersistentAttributeInterceptor interceptor;
        if (!persister.isInstance(object)) {
            throw new WrongClassException("loaded object was of wrong class " + object.getClass(), key.getIdentifier(), persister.getEntityName());
        }
        if (persister.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading() && (interceptor = ((PersistentAttributeInterceptable)object).$$_hibernate_getInterceptor()) instanceof EnhancementAsProxyLazinessInterceptor) {
            EntityEntry entry2 = session.getPersistenceContextInternal().getEntry(object);
            if (entry2 != null && entry2.getStatus() != Status.LOADING) {
                this.hydrateEntityState(rs, i, persister, this.getEntityAliases()[i].getRowIdAlias(), key, hydratedObjects, session, this.getInstanceClass(rs, i, persister, key.getIdentifier(), session), object, requestedLockMode);
            }
            return;
        }
        if (LockMode.NONE != requestedLockMode && this.upgradeLocks() && (entry = session.getPersistenceContextInternal().getEntry(object)).getLockMode().lessThan(requestedLockMode)) {
            if (persister.isVersioned()) {
                this.checkVersion(i, persister, key.getIdentifier(), object, rs, session);
            }
            entry.setLockMode(requestedLockMode);
        }
    }

    protected Object instanceNotYetLoaded(ResultSet rs, int i, Loadable persister, String rowIdAlias, EntityKey key, LockMode lockMode, EntityKey optionalObjectKey, Object optionalObject, List hydratedObjects, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object ck;
        Serializable cachedEntry;
        EntityDataAccess cache;
        String instanceClass = this.getInstanceClass(rs, i, persister, key.getIdentifier(), session);
        if (session.getCacheMode().isGetEnabled() && persister.canUseReferenceCacheEntries() && (cache = persister.getCacheAccessStrategy()) != null && (cachedEntry = CacheHelper.fromSharedCache(session, ck = cache.generateCacheKey(key.getIdentifier(), persister, session.getFactory(), session.getTenantIdentifier()), cache)) != null) {
            CacheEntry entry = (CacheEntry)persister.getCacheEntryStructure().destructure(cachedEntry, this.factory);
            return ((ReferenceCacheEntryImpl)entry).getReference();
        }
        Object object = optionalObjectKey != null && key.equals(optionalObjectKey) ? optionalObject : (persister.hasSubclasses() ? session.instantiate(instanceClass, key.getIdentifier()) : session.instantiate(persister, key.getIdentifier()));
        LockMode acquiredLockMode = lockMode == LockMode.NONE ? LockMode.READ : lockMode;
        this.hydrateEntityState(rs, i, persister, rowIdAlias, key, hydratedObjects, session, instanceClass, object, acquiredLockMode);
        return object;
    }

    private void hydrateEntityState(ResultSet rs, int i, Loadable persister, String rowIdAlias, EntityKey key, List hydratedObjects, SharedSessionContractImplementor session, String instanceClass, Object object, LockMode acquiredLockMode) throws SQLException {
        this.loadFromResultSet(rs, i, object, instanceClass, key, rowIdAlias, acquiredLockMode, persister, session);
        hydratedObjects.add(object);
    }

    private boolean isAllPropertyEagerFetchEnabled(int i) {
        boolean[] array = this.getEntityEagerPropertyFetches();
        return array != null && array[i];
    }

    private boolean[] getPerPropertyEagerFetchEnabled(int i) {
        boolean[][] array = this.getEntityEagerPerPropertyFetches();
        return array != null ? array[i] : null;
    }

    private void loadFromResultSet(ResultSet rs, int i, Object object, String instanceEntityName, EntityKey key, String rowIdAlias, LockMode lockMode, Loadable rootPersister, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        String ukName;
        Serializable id = key.getIdentifier();
        Loadable persister = (Loadable)this.getFactory().getEntityPersister(instanceEntityName);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Initializing object from ResultSet: %s", MessageHelper.infoString((EntityPersister)persister, id, this.getFactory()));
        }
        boolean fetchAllPropertiesRequested = this.isAllPropertyEagerFetchEnabled(i);
        TwoPhaseLoad.addUninitializedEntity(key, object, persister, lockMode, session);
        String[][] cols = persister == rootPersister ? this.getEntityAliases()[i].getSuffixedPropertyAliases() : this.getEntityAliases()[i].getSuffixedPropertyAliases(persister);
        Object[] values = persister.hydrate(rs, id, object, rootPersister, cols, fetchAllPropertiesRequested, this.getPerPropertyEagerFetchEnabled(i), session);
        Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
        EntityType[] ownerAssociationTypes = this.getOwnerAssociationTypes();
        if (ownerAssociationTypes != null && ownerAssociationTypes[i] != null && (ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()) != null) {
            int index = ((UniqueKeyLoadable)persister).getPropertyIndex(ukName);
            Type type = persister.getPropertyTypes()[index];
            EntityUniqueKey euk = new EntityUniqueKey(rootPersister.getEntityName(), ukName, type.semiResolve(values[index], session, object), type, persister.getEntityMode(), session.getFactory());
            session.getPersistenceContextInternal().addEntity(euk, object);
        }
        TwoPhaseLoad.postHydrate(persister, id, values, rowId, object, lockMode, session);
    }

    private String getInstanceClass(ResultSet rs, int i, Loadable persister, Serializable id, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (persister.hasSubclasses()) {
            Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(rs, this.getEntityAliases()[i].getSuffixedDiscriminatorAlias(), session, null);
            String result = persister.getSubclassForDiscriminatorValue(discriminatorValue);
            if (result == null) {
                throw new WrongClassException("Discriminator: " + discriminatorValue, id, persister.getEntityName());
            }
            return result;
        }
        return persister.getEntityName();
    }

    private void advance(ResultSet rs, RowSelection selection) throws SQLException {
        int firstRow = LimitHelper.getFirstRow(selection);
        if (firstRow != 0) {
            if (this.getFactory().getSessionFactoryOptions().isScrollableResultSetsEnabled()) {
                rs.absolute(firstRow);
            } else {
                for (int m = 0; m < firstRow; ++m) {
                    rs.next();
                }
            }
        }
    }

    protected LimitHandler getLimitHandler(RowSelection selection) {
        LimitHandler limitHandler = this.getFactory().getDialect().getLimitHandler();
        return LimitHelper.useLimit(limitHandler, selection) ? limitHandler : NoopLimitHandler.INSTANCE;
    }

    private ScrollMode getScrollMode(boolean scroll, LimitHandler limitHandler, QueryParameters queryParameters) {
        boolean canScroll = this.getFactory().getSessionFactoryOptions().isScrollableResultSetsEnabled();
        if (canScroll) {
            boolean useLimitOffset;
            if (scroll) {
                return queryParameters.getScrollMode();
            }
            RowSelection selection = queryParameters.getRowSelection();
            boolean useLimit = LimitHelper.useLimit(limitHandler, selection);
            boolean hasFirstRow = LimitHelper.hasFirstRow(selection);
            boolean bl = useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
            if (hasFirstRow && !useLimitOffset) {
                return ScrollMode.SCROLL_INSENSITIVE;
            }
        }
        return null;
    }

    protected SqlStatementWrapper executeQueryStatement(QueryParameters queryParameters, boolean scroll, List<AfterLoadAction> afterLoadActions, SharedSessionContractImplementor session) throws SQLException {
        return this.executeQueryStatement(this.getSQLString(), queryParameters, scroll, afterLoadActions, session);
    }

    protected SqlStatementWrapper executeQueryStatement(String sqlStatement, QueryParameters queryParameters, boolean scroll, List<AfterLoadAction> afterLoadActions, SharedSessionContractImplementor session) throws SQLException {
        ResultSet rs;
        queryParameters.processFilters(sqlStatement, session);
        LimitHandler limitHandler = this.getLimitHandler(queryParameters.getRowSelection());
        String sql = limitHandler.processSql(queryParameters.getFilteredSQL(), queryParameters);
        sql = this.preprocessSQL(sql, queryParameters, this.getFactory(), afterLoadActions);
        PreparedStatement st = this.prepareQueryStatement(sql, queryParameters, limitHandler, scroll, session);
        if (queryParameters.isCallable() && this.isTypeOf(st, CallableStatement.class)) {
            CallableStatement cs = st.unwrap(CallableStatement.class);
            rs = this.getResultSet(cs, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session);
        } else {
            rs = this.getResultSet(st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session);
        }
        return new SqlStatementWrapper(st, rs);
    }

    private boolean isTypeOf(Statement statement, Class<? extends Statement> type) {
        if (this.isJdbc4) {
            try {
                return statement.isWrapperFor(type);
            }
            catch (SQLException sQLException) {
            }
            catch (Throwable e) {
                this.isJdbc4 = false;
            }
        }
        return type.isInstance(statement);
    }

    protected final PreparedStatement prepareQueryStatement(String sql, QueryParameters queryParameters, LimitHandler limitHandler, boolean scroll, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        PreparedStatement preparedStatement = session.getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(sql, queryParameters.isCallable(), this.getScrollMode(scroll, limitHandler, queryParameters));
        return this.bindPreparedStatement(preparedStatement, queryParameters, limitHandler, session);
    }

    protected final PreparedStatement bindPreparedStatement(PreparedStatement st, QueryParameters queryParameters, LimitHandler limitHandler, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Dialect dialect = this.getFactory().getDialect();
        RowSelection selection = queryParameters.getRowSelection();
        boolean callable = queryParameters.isCallable();
        try {
            LockOptions lockOptions;
            int col = 1;
            col += limitHandler.bindLimitParametersAtStartOfQuery(selection, st, col);
            if (callable) {
                col = dialect.registerResultSetOutParameter((CallableStatement)st, col);
            }
            col += this.bindParameterValues(st, queryParameters, col, session);
            col += limitHandler.bindLimitParametersAtEndOfQuery(selection, st, col);
            limitHandler.setMaxRows(selection, st);
            if (selection != null) {
                if (selection.getTimeout() != null) {
                    st.setQueryTimeout(selection.getTimeout());
                }
                if (selection.getFetchSize() != null) {
                    st.setFetchSize(selection.getFetchSize());
                }
            }
            if ((lockOptions = queryParameters.getLockOptions()) != null && lockOptions.getTimeOut() != -1) {
                if (!dialect.supportsLockTimeouts()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debugf("Lock timeout [%s] requested but dialect reported to not support lock timeouts", lockOptions.getTimeOut());
                    }
                } else if (dialect.isLockTimeoutParameterized()) {
                    st.setInt(col++, lockOptions.getTimeOut());
                }
            }
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Bound [{0}] parameters total", col);
            }
        }
        catch (SQLException | HibernateException e) {
            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
            session.getJdbcCoordinator().afterStatementExecution();
            throw e;
        }
        return st;
    }

    protected int bindParameterValues(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        int span = 0;
        span += this.bindPositionalParameters(statement, queryParameters, startIndex, session);
        span += this.bindNamedParameters(statement, queryParameters.getNamedParameters(), startIndex + span, session);
        return span;
    }

    protected int bindPositionalParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] values = queryParameters.getFilteredPositionalParameterValues();
        Type[] types = queryParameters.getFilteredPositionalParameterTypes();
        int span = 0;
        for (int i = 0; i < values.length; ++i) {
            types[i].nullSafeSet(statement, values[i], startIndex + span, session);
            span += types[i].getColumnSpan(this.getFactory());
        }
        return span;
    }

    protected int bindNamedParameters(PreparedStatement statement, Map<String, TypedValue> namedParams, int startIndex, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        int result = 0;
        if (CollectionHelper.isEmpty(namedParams)) {
            return result;
        }
        boolean debugEnabled = LOG.isDebugEnabled();
        SessionFactoryImplementor factory = this.getFactory();
        for (Map.Entry<String, TypedValue> entry : namedParams.entrySet()) {
            int[] locs;
            String name = entry.getKey();
            TypedValue typedValue = entry.getValue();
            Type type = typedValue.getType();
            int columnSpan = type.getColumnSpan(factory);
            for (int loc : locs = this.getNamedParameterLocs(name)) {
                if (debugEnabled) {
                    LOG.debugf("bindNamedParameters() %s -> %s [%s]", typedValue.getValue(), name, loc + startIndex);
                }
                int start = loc * columnSpan + startIndex;
                type.nullSafeSet(statement, typedValue.getValue(), start, session);
            }
            result += locs.length;
        }
        return result;
    }

    public int[] getNamedParameterLocs(String name) {
        throw new AssertionFailure("no named parameters");
    }

    protected final ResultSet getResultSet(PreparedStatement st, RowSelection selection, LimitHandler limitHandler, boolean autodiscovertypes, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        try {
            ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
            return this.preprocessResultSet(rs, selection, limitHandler, autodiscovertypes, session);
        }
        catch (SQLException | HibernateException e) {
            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
            session.getJdbcCoordinator().afterStatementExecution();
            throw e;
        }
    }

    protected final ResultSet getResultSet(CallableStatement st, RowSelection selection, LimitHandler limitHandler, boolean autodiscovertypes, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        try {
            ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
            return this.preprocessResultSet(rs, selection, limitHandler, autodiscovertypes, session);
        }
        catch (SQLException | HibernateException e) {
            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
            session.getJdbcCoordinator().afterStatementExecution();
            throw e;
        }
    }

    protected ResultSet preprocessResultSet(ResultSet rs, RowSelection selection, LimitHandler limitHandler, boolean autodiscovertypes, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        rs = this.wrapResultSetIfEnabled(rs, session);
        if (!limitHandler.supportsLimitOffset() || !LimitHelper.useLimit(limitHandler, selection)) {
            this.advance(rs, selection);
        }
        if (autodiscovertypes) {
            this.autoDiscoverTypes(rs);
        }
        return rs;
    }

    protected void autoDiscoverTypes(ResultSet rs) {
        throw new AssertionFailure("Auto discover types not supported in this loader");
    }

    private ResultSet wrapResultSetIfEnabled(ResultSet rs, SharedSessionContractImplementor session) {
        if (session.getFactory().getSessionFactoryOptions().isWrapResultSetsEnabled()) {
            try {
                LOG.debugf("Wrapping result set [%s]", rs);
                return session.getFactory().getServiceRegistry().getService(JdbcServices.class).getResultSetWrapper().wrap(rs, this.retrieveColumnNameToIndexCache(rs));
            }
            catch (SQLException e) {
                LOG.unableToWrapResultSet(e);
                return rs;
            }
        }
        return rs;
    }

    private ColumnNameCache retrieveColumnNameToIndexCache(ResultSet rs) throws SQLException {
        ColumnNameCache cache = this.columnNameCache;
        if (cache == null) {
            LOG.trace("Building columnName -> columnIndex cache");
            this.columnNameCache = new ColumnNameCache(rs.getMetaData().getColumnCount());
            return this.columnNameCache;
        }
        return cache;
    }

    protected final List loadEntity(SharedSessionContractImplementor session, Object id, Type identifierType, Object optionalObject, String optionalEntityName, Serializable optionalIdentifier, EntityPersister persister, LockOptions lockOptions, Boolean readOnly) throws HibernateException {
        List result;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Loading entity: %s", MessageHelper.infoString(persister, id, identifierType, this.getFactory()));
        }
        try {
            QueryParameters qp = new QueryParameters();
            qp.setPositionalParameterTypes(new Type[]{identifierType});
            qp.setPositionalParameterValues(new Object[]{id});
            qp.setOptionalObject(optionalObject);
            qp.setOptionalEntityName(optionalEntityName);
            qp.setOptionalId(optionalIdentifier);
            qp.setLockOptions(lockOptions);
            if (readOnly != null) {
                qp.setReadOnly(readOnly);
            }
            result = this.doQueryAndInitializeNonLazyCollections(session, qp, false);
        }
        catch (SQLException sqle) {
            Loadable[] persisters = this.getEntityPersisters();
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not load an entity: " + MessageHelper.infoString(persisters[persisters.length - 1], id, identifierType, this.getFactory()), this.getSQLString());
        }
        LOG.debug("Done entity load");
        return result;
    }

    protected final List loadEntity(SharedSessionContractImplementor session, Object key, Object index, Type keyType, Type indexType, EntityPersister persister) throws HibernateException {
        List result;
        LOG.debug("Loading collection element by index");
        try {
            result = this.doQueryAndInitializeNonLazyCollections(session, new QueryParameters(new Type[]{keyType, indexType}, new Object[]{key, index}), false);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not load collection element by index", this.getSQLString());
        }
        LOG.debug("Done entity load");
        return result;
    }

    public final List loadEntityBatch(SharedSessionContractImplementor session, Serializable[] ids, Type idType, Object optionalObject, String optionalEntityName, Serializable optionalId, EntityPersister persister, LockOptions lockOptions) throws HibernateException {
        return this.loadEntityBatch(session, ids, idType, optionalObject, optionalEntityName, optionalId, persister, lockOptions, null);
    }

    public final List loadEntityBatch(SharedSessionContractImplementor session, Serializable[] ids, Type idType, Object optionalObject, String optionalEntityName, Serializable optionalId, EntityPersister persister, LockOptions lockOptions, Boolean readOnly) throws HibernateException {
        List result;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Batch loading entity: %s", MessageHelper.infoString(persister, ids, this.getFactory()));
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
            result = this.doQueryAndInitializeNonLazyCollections(session, qp, false);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not load an entity batch: " + MessageHelper.infoString((EntityPersister)this.getEntityPersisters()[0], ids, this.getFactory()), this.getSQLString());
        }
        LOG.debug("Done entity batch load");
        return result;
    }

    public final void loadCollection(SharedSessionContractImplementor session, Serializable id, Type type) throws HibernateException {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Loading collection: %s", MessageHelper.collectionInfoString(this.getCollectionPersisters()[0], id, this.getFactory()));
        }
        Object[] ids = new Serializable[]{id};
        try {
            this.doQueryAndInitializeNonLazyCollections(session, new QueryParameters(new Type[]{type}, ids, (Serializable[])ids), true);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not initialize a collection: " + MessageHelper.collectionInfoString(this.getCollectionPersisters()[0], id, this.getFactory()), this.getSQLString());
        }
        LOG.debug("Done loading collection");
    }

    public final void loadCollectionBatch(SharedSessionContractImplementor session, Serializable[] ids, Type type) throws HibernateException {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Batch loading collection: %s", MessageHelper.collectionInfoString(this.getCollectionPersisters()[0], ids, this.getFactory()));
        }
        Object[] idTypes = new Type[ids.length];
        Arrays.fill(idTypes, type);
        try {
            this.doQueryAndInitializeNonLazyCollections(session, new QueryParameters((Type[])idTypes, ids, ids), true);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not initialize a collection batch: " + MessageHelper.collectionInfoString(this.getCollectionPersisters()[0], ids, this.getFactory()), this.getSQLString());
        }
        LOG.debug("Done batch load");
    }

    protected final void loadCollectionSubselect(SharedSessionContractImplementor session, Serializable[] ids, Object[] parameterValues, Type[] parameterTypes, Map<String, TypedValue> namedParameters, Type type) throws HibernateException {
        try {
            this.doQueryAndInitializeNonLazyCollections(session, new QueryParameters(parameterTypes, parameterValues, namedParameters, ids), true);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not load collection by subselect: " + MessageHelper.collectionInfoString(this.getCollectionPersisters()[0], ids, this.getFactory()), this.getSQLString());
        }
    }

    protected List list(SharedSessionContractImplementor session, QueryParameters queryParameters, Set<Serializable> querySpaces, Type[] resultTypes) throws HibernateException {
        boolean cacheable;
        boolean bl = cacheable = this.factory.getSessionFactoryOptions().isQueryCacheEnabled() && queryParameters.isCacheable();
        if (cacheable) {
            return this.listUsingQueryCache(session, queryParameters, querySpaces, resultTypes);
        }
        return this.listIgnoreQueryCache(session, queryParameters);
    }

    private List listIgnoreQueryCache(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        return this.getResultList(this.doList(session, queryParameters), queryParameters.getResultTransformer());
    }

    private List listUsingQueryCache(SharedSessionContractImplementor session, QueryParameters queryParameters, Set<Serializable> querySpaces, Type[] resultTypes) {
        ResultTransformer resolvedTransformer;
        QueryResultsCache queryCache = this.factory.getCache().getQueryResultsCache(queryParameters.getCacheRegion());
        QueryKey key = this.generateQueryKey(session, queryParameters);
        if (querySpaces == null || querySpaces.size() == 0) {
            LOG.tracev("Unexpected querySpaces is {0}", querySpaces == null ? querySpaces : "empty");
        } else {
            LOG.tracev("querySpaces is {0}", querySpaces);
        }
        List result = this.getResultFromQueryCache(session, queryParameters, querySpaces, resultTypes, queryCache, key);
        if (result == null) {
            result = this.doList(session, queryParameters, key.getResultTransformer());
            this.putResultInQueryCache(session, queryParameters, resultTypes, queryCache, key, result);
        }
        if ((resolvedTransformer = this.resolveResultTransformer(queryParameters.getResultTransformer())) != null) {
            result = this.areResultSetRowsTransformedImmediately() ? key.getResultTransformer().retransformResults(result, this.getResultRowAliases(), queryParameters.getResultTransformer(), this.includeInResultRow()) : key.getResultTransformer().untransformToTuples(result);
        }
        return this.getResultList(result, queryParameters.getResultTransformer());
    }

    protected QueryKey generateQueryKey(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        return QueryKey.generateQueryKey(this.getSQLString(), queryParameters, FilterKey.createFilterKeys(session.getLoadQueryInfluencers().getEnabledFilters()), session, this.createCacheableResultTransformer(queryParameters));
    }

    protected CacheableResultTransformer createCacheableResultTransformer(QueryParameters queryParameters) {
        return CacheableResultTransformer.create(queryParameters.getResultTransformer(), this.getResultRowAliases(), this.includeInResultRow());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected List getResultFromQueryCache(SharedSessionContractImplementor session, QueryParameters queryParameters, Set<Serializable> querySpaces, Type[] resultTypes, QueryResultsCache queryCache, QueryKey key) {
        List result = null;
        if (session.getCacheMode().isGetEnabled()) {
            boolean isImmutableNaturalKeyLookup = queryParameters.isNaturalKeyLookup() && resultTypes.length == 1 && resultTypes[0].isEntityType() && this.getEntityPersister((EntityType)EntityType.class.cast(resultTypes[0])).getEntityMetamodel().hasImmutableNaturalId();
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
            if (queryParameters.isReadOnlyInitialized()) {
                persistenceContext.setDefaultReadOnly(queryParameters.isReadOnly());
            } else {
                queryParameters.setReadOnly(persistenceContext.isDefaultReadOnly());
            }
            try {
                result = queryCache.get(key, querySpaces, key.getResultTransformer().getCachedResultTypes(resultTypes), session);
            }
            finally {
                persistenceContext.setDefaultReadOnly(defaultReadOnlyOrig);
            }
            StatisticsImplementor statistics = this.factory.getStatistics();
            if (statistics.isStatisticsEnabled()) {
                if (result == null) {
                    statistics.queryCacheMiss(this.getQueryIdentifier(), queryCache.getRegion().getName());
                } else {
                    statistics.queryCacheHit(this.getQueryIdentifier(), queryCache.getRegion().getName());
                }
            }
        }
        return result;
    }

    protected EntityPersister getEntityPersister(EntityType entityType) {
        return this.factory.getMetamodel().entityPersister(entityType.getAssociatedEntityName());
    }

    protected void putResultInQueryCache(SharedSessionContractImplementor session, QueryParameters queryParameters, Type[] resultTypes, QueryResultsCache queryCache, QueryKey key, List result) {
        if (session.getCacheMode().isPutEnabled()) {
            boolean put = queryCache.put(key, result, key.getResultTransformer().getCachedResultTypes(resultTypes), session);
            StatisticsImplementor statistics = this.factory.getStatistics();
            if (put && statistics.isStatisticsEnabled()) {
                statistics.queryCachePut(this.getQueryIdentifier(), queryCache.getRegion().getName());
            }
        }
    }

    protected List doList(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
        return this.doList(session, queryParameters, null);
    }

    private List doList(SharedSessionContractImplementor session, QueryParameters queryParameters, ResultTransformer forcedResultTransformer) throws HibernateException {
        List result;
        StatisticsImplementor statistics = this.getFactory().getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        long startTime = 0L;
        if (stats) {
            startTime = System.nanoTime();
        }
        try {
            result = this.doQueryAndInitializeNonLazyCollections(session, queryParameters, true, forcedResultTransformer);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not execute query", this.getSQLString());
        }
        if (stats) {
            long endTime = System.nanoTime();
            long milliseconds = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            statistics.queryExecuted(this.getQueryIdentifier(), result.size(), milliseconds);
        }
        return result;
    }

    protected void checkScrollability() throws HibernateException {
    }

    protected boolean needsFetchingScroll() {
        return false;
    }

    protected ScrollableResultsImplementor scroll(QueryParameters queryParameters, Type[] returnTypes, HolderInstantiator holderInstantiator, SharedSessionContractImplementor session) throws HibernateException {
        this.checkScrollability();
        StatisticsImplementor statistics = this.getFactory().getStatistics();
        boolean stats = this.getQueryIdentifier() != null && statistics.isStatisticsEnabled();
        long startTime = 0L;
        if (stats) {
            startTime = System.nanoTime();
        }
        try {
            SqlStatementWrapper wrapper = this.executeQueryStatement(queryParameters, true, new ArrayList<AfterLoadAction>(), session);
            ResultSet rs = wrapper.getResultSet();
            PreparedStatement st = (PreparedStatement)wrapper.getStatement();
            if (stats) {
                long endTime = System.nanoTime();
                long milliseconds = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
                statistics.queryExecuted(this.getQueryIdentifier(), 0, milliseconds);
            }
            if (this.needsFetchingScroll()) {
                return new FetchingScrollableResultsImpl(rs, st, session, this, queryParameters, returnTypes, holderInstantiator);
            }
            return new ScrollableResultsImpl(rs, st, session, this, queryParameters, returnTypes, holderInstantiator);
        }
        catch (SQLException sqle) {
            throw this.factory.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not execute query using scroll", this.getSQLString());
        }
    }

    protected void postInstantiate() {
    }

    protected abstract EntityAliases[] getEntityAliases();

    protected abstract CollectionAliases[] getCollectionAliases();

    protected String getQueryIdentifier() {
        return null;
    }

    public final SessionFactoryImplementor getFactory() {
        return this.factory;
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.getSQLString() + ')';
    }

    protected String processDistinctKeyword(String sql, QueryParameters parameters) {
        if (!parameters.isPassDistinctThrough() && sql.startsWith(SELECT_DISTINCT)) {
            return SELECT + sql.substring(SELECT_DISTINCT.length());
        }
        return sql;
    }

    protected static class SqlStatementWrapper {
        private final Statement statement;
        private final ResultSet resultSet;

        private SqlStatementWrapper(Statement statement, ResultSet resultSet) {
            this.resultSet = resultSet;
            this.statement = statement;
        }

        public ResultSet getResultSet() {
            return this.resultSet;
        }

        public Statement getStatement() {
            return this.statement;
        }
    }
}

