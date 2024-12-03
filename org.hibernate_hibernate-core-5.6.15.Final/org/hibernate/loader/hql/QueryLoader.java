/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.hql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.hql.spi.ParameterInformation;
import org.hibernate.internal.IteratorImpl;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.BasicLoader;
import org.hibernate.loader.Loader;
import org.hibernate.loader.internal.AliasConstantsHelper;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class QueryLoader
extends BasicLoader {
    private QueryTranslatorImpl queryTranslator;
    private Queryable[] entityPersisters;
    private String[] entityAliases;
    private String[] sqlAliases;
    private String[] sqlAliasSuffixes;
    private boolean[] includeInSelect;
    private String[] collectionSuffixes;
    private boolean hasScalars;
    private String[][] scalarColumnNames;
    private Type[] queryReturnTypes;
    private final Map<String, String> sqlAliasByEntityAlias = new HashMap<String, String>(8);
    private EntityType[] ownerAssociationTypes;
    private int[] owners;
    private boolean[] entityEagerPropertyFetches;
    private boolean[][] entityEagerPerPropertyFetches;
    private int[] collectionOwners;
    private QueryableCollection[] collectionPersisters;
    private int selectLength;
    private AggregatedSelectExpression aggregatedSelectExpression;
    private String[] queryReturnAliases;
    private LockMode[] defaultLockModes;

    public QueryLoader(QueryTranslatorImpl queryTranslator, SessionFactoryImplementor factory, SelectClause selectClause) {
        super(factory);
        this.queryTranslator = queryTranslator;
        this.initialize(selectClause);
        this.postInstantiate();
    }

    private void initialize(SelectClause selectClause) {
        int i;
        List fromElementList = selectClause.getFromElementsForLoad();
        this.hasScalars = selectClause.isScalarSelect();
        this.scalarColumnNames = selectClause.getColumnNames();
        this.queryReturnTypes = selectClause.getQueryReturnTypes();
        this.aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
        this.queryReturnAliases = selectClause.getQueryReturnAliases();
        List collectionFromElements = selectClause.getCollectionFromElements();
        if (collectionFromElements != null && collectionFromElements.size() != 0) {
            int length = collectionFromElements.size();
            this.collectionPersisters = new QueryableCollection[length];
            this.collectionOwners = new int[length];
            this.collectionSuffixes = new String[length];
            for (i = 0; i < length; ++i) {
                FromElement collectionFromElement = (FromElement)collectionFromElements.get(i);
                this.collectionPersisters[i] = collectionFromElement.getQueryableCollection();
                this.collectionOwners[i] = fromElementList.indexOf(collectionFromElement.getOrigin());
                this.collectionSuffixes[i] = collectionFromElement.getCollectionSuffix();
            }
        }
        int size = fromElementList.size();
        this.entityPersisters = new Queryable[size];
        this.entityEagerPropertyFetches = new boolean[size];
        this.entityEagerPerPropertyFetches = new boolean[size][];
        this.entityAliases = new String[size];
        this.sqlAliases = new String[size];
        this.sqlAliasSuffixes = new String[size];
        this.includeInSelect = new boolean[size];
        this.owners = new int[size];
        this.ownerAssociationTypes = new EntityType[size];
        for (i = 0; i < size; ++i) {
            FromElement element = (FromElement)fromElementList.get(i);
            this.entityPersisters[i] = (Queryable)element.getEntityPersister();
            if (this.entityPersisters[i] == null) {
                throw new IllegalStateException("No entity persister for " + element.toString());
            }
            this.entityEagerPropertyFetches[i] = element.isAllPropertyFetch();
            this.entityEagerPerPropertyFetches[i] = null;
            this.sqlAliases[i] = element.getTableAlias();
            this.entityAliases[i] = element.getClassAlias();
            this.sqlAliasByEntityAlias.put(this.entityAliases[i], this.sqlAliases[i]);
            this.sqlAliasSuffixes[i] = size == 1 ? "" : AliasConstantsHelper.get(i);
            boolean bl = this.includeInSelect[i] = !element.isFetch();
            if (this.includeInSelect[i]) {
                ++this.selectLength;
            }
            this.owners[i] = -1;
            if (!element.isFetch() || element.isCollectionJoin() || element.getQueryableCollection() != null || !element.getDataType().isEntityType()) continue;
            EntityType entityType = (EntityType)element.getDataType();
            if (entityType.isOneToOne()) {
                int originIndex;
                this.owners[i] = originIndex = fromElementList.indexOf(element.getOrigin());
                Integer propertyIndex = QueryLoader.propertyIndexInOrigin(element, this.entityPersisters[originIndex]);
                if (propertyIndex != null) {
                    if (this.entityEagerPerPropertyFetches[originIndex] == null) {
                        this.entityEagerPerPropertyFetches[originIndex] = new boolean[this.entityPersisters[originIndex].getPropertyNames().length];
                    }
                    this.entityEagerPerPropertyFetches[originIndex][propertyIndex.intValue()] = true;
                }
            }
            this.ownerAssociationTypes[i] = entityType;
        }
        this.defaultLockModes = ArrayHelper.fillArray(LockMode.NONE, size);
    }

    private static Integer propertyIndexInOrigin(FromElement element, Queryable originPersister) {
        String role;
        int attributeStartIndex = element.getOrigin().getClassName().length() + 1;
        if (attributeStartIndex >= (role = element.getRole()).length()) {
            return null;
        }
        String propertyName = role.substring(attributeStartIndex);
        return originPersister.getEntityMetamodel().getPropertyIndexOrNull(propertyName);
    }

    public AggregatedSelectExpression getAggregatedSelectExpression() {
        return this.aggregatedSelectExpression;
    }

    public final void validateScrollability() throws HibernateException {
        this.queryTranslator.validateScrollability();
    }

    @Override
    protected boolean needsFetchingScroll() {
        return this.queryTranslator.containsCollectionFetches();
    }

    @Override
    public Loadable[] getEntityPersisters() {
        return this.entityPersisters;
    }

    @Override
    public String[] getAliases() {
        return this.sqlAliases;
    }

    public String[] getSqlAliasSuffixes() {
        return this.sqlAliasSuffixes;
    }

    @Override
    public String[] getSuffixes() {
        return this.getSqlAliasSuffixes();
    }

    @Override
    public String[] getCollectionSuffixes() {
        return this.collectionSuffixes;
    }

    @Override
    protected String getQueryIdentifier() {
        return this.queryTranslator.getQueryIdentifier();
    }

    @Override
    public String getSQLString() {
        return this.queryTranslator.getSQLString();
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
    protected boolean[] getEntityEagerPropertyFetches() {
        return this.entityEagerPropertyFetches;
    }

    @Override
    public boolean[][] getEntityEagerPerPropertyFetches() {
        return this.entityEagerPerPropertyFetches;
    }

    @Override
    protected int[] getOwners() {
        return this.owners;
    }

    @Override
    protected EntityType[] getOwnerAssociationTypes() {
        return this.ownerAssociationTypes;
    }

    @Override
    protected boolean isSubselectLoadingEnabled() {
        return this.hasSubselectLoadableCollections();
    }

    @Override
    protected LockMode[] getLockModes(LockOptions lockOptions) {
        if (lockOptions == null) {
            return this.defaultLockModes;
        }
        if (lockOptions.getAliasLockCount() == 0 && (lockOptions.getLockMode() == null || LockMode.NONE.equals((Object)lockOptions.getLockMode()))) {
            return this.defaultLockModes;
        }
        LockMode[] lockModesArray = new LockMode[this.entityAliases.length];
        for (int i = 0; i < this.entityAliases.length; ++i) {
            LockMode lockMode = lockOptions.getEffectiveLockMode(this.entityAliases[i]);
            if (lockMode == null) {
                lockMode = LockMode.NONE;
            }
            lockModesArray[i] = lockMode;
        }
        return lockModesArray;
    }

    @Override
    protected String applyLocks(String sql, QueryParameters parameters, Dialect dialect, List<AfterLoadAction> afterLoadActions) throws QueryException {
        LockOptions lockOptions = parameters.getLockOptions();
        if (lockOptions == null || lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0) {
            return sql;
        }
        if (this.shouldUseFollowOnLocking(parameters, dialect, afterLoadActions)) {
            return sql;
        }
        LockOptions locks = new LockOptions(lockOptions.getLockMode());
        HashMap<String, String[]> keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap<String, String[]>() : null;
        locks.setScope(lockOptions.getScope());
        locks.setTimeOut(lockOptions.getTimeOut());
        for (Map.Entry<String, String> entry : this.sqlAliasByEntityAlias.entrySet()) {
            String userAlias = entry.getKey();
            String drivingSqlAlias = entry.getValue();
            if (drivingSqlAlias == null) {
                throw new IllegalArgumentException("could not locate alias to apply lock mode : " + userAlias);
            }
            QueryNode select = (QueryNode)this.queryTranslator.getSqlAST();
            Lockable drivingPersister = (Lockable)((Object)select.getFromClause().findFromElementByUserOrSqlAlias(userAlias, drivingSqlAlias).getQueryable());
            String sqlAlias = drivingPersister.getRootTableAlias(drivingSqlAlias);
            LockMode effectiveLockMode = lockOptions.getEffectiveLockMode(userAlias);
            locks.setAliasSpecificLockMode(sqlAlias, effectiveLockMode);
            if (keyColumnNames == null) continue;
            keyColumnNames.put(sqlAlias, drivingPersister.getRootTableIdentifierColumnNames());
        }
        return dialect.applyLocksToSql(sql, locks, keyColumnNames);
    }

    @Override
    protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SharedSessionContractImplementor session) {
    }

    @Override
    protected boolean upgradeLocks() {
        return true;
    }

    protected boolean hasSelectNew() {
        return this.aggregatedSelectExpression != null && this.aggregatedSelectExpression.getResultTransformer() != null;
    }

    @Override
    protected String[] getResultRowAliases() {
        return this.queryReturnAliases;
    }

    @Override
    protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
        ResultTransformer implicitResultTransformer = this.aggregatedSelectExpression == null ? null : this.aggregatedSelectExpression.getResultTransformer();
        return HolderInstantiator.resolveResultTransformer(implicitResultTransformer, resultTransformer);
    }

    @Override
    protected boolean[] includeInResultRow() {
        boolean[] includeInResultTuple = this.includeInSelect;
        if (this.hasScalars) {
            includeInResultTuple = new boolean[this.queryReturnTypes.length];
            Arrays.fill(includeInResultTuple, true);
        }
        return includeInResultTuple;
    }

    @Override
    protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] resultRow = this.getResultRow(row, rs, session);
        boolean hasTransform = this.hasSelectNew() || transformer != null;
        return !hasTransform && resultRow.length == 1 ? resultRow[0] : resultRow;
    }

    @Override
    protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] resultRow;
        if (this.hasScalars) {
            String[][] scalarColumns = this.scalarColumnNames;
            int queryCols = this.queryReturnTypes.length;
            resultRow = new Object[queryCols];
            for (int i = 0; i < queryCols; ++i) {
                resultRow[i] = this.queryReturnTypes[i].nullSafeGet(rs, scalarColumns[i], session, null);
            }
        } else {
            resultRow = this.toResultRow(row);
        }
        return resultRow;
    }

    @Override
    protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
        HolderInstantiator holderInstantiator = this.buildHolderInstantiator(resultTransformer);
        if (holderInstantiator.isRequired()) {
            for (int i = 0; i < results.size(); ++i) {
                Object[] row = (Object[])results.get(i);
                Object result = holderInstantiator.instantiate(row);
                results.set(i, result);
            }
            if (!this.hasSelectNew() && resultTransformer != null) {
                return resultTransformer.transformList(results);
            }
            return results;
        }
        return results;
    }

    protected HolderInstantiator buildHolderInstantiator(ResultTransformer queryLocalResultTransformer) {
        ResultTransformer implicitResultTransformer = this.aggregatedSelectExpression == null ? null : this.aggregatedSelectExpression.getResultTransformer();
        return HolderInstantiator.getHolderInstantiator(implicitResultTransformer, queryLocalResultTransformer, this.queryReturnAliases);
    }

    public List list(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
        this.checkQuery(queryParameters);
        return this.list(session, queryParameters, this.queryTranslator.getQuerySpaces(), this.queryReturnTypes);
    }

    protected void checkQuery(QueryParameters queryParameters) {
        if (this.hasSelectNew() && queryParameters.getResultTransformer() != null) {
            throw new QueryException("ResultTransformer is not allowed for 'select new' queries.");
        }
    }

    public Iterator iterate(QueryParameters queryParameters, EventSource session) throws HibernateException {
        this.checkQuery(queryParameters);
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        long startTime = 0L;
        if (stats) {
            startTime = System.nanoTime();
        }
        try {
            if (queryParameters.isCallable()) {
                throw new QueryException("iterate() not supported for callable statements");
            }
            Loader.SqlStatementWrapper wrapper = this.executeQueryStatement(queryParameters, false, Collections.emptyList(), session);
            ResultSet rs = wrapper.getResultSet();
            PreparedStatement st = (PreparedStatement)wrapper.getStatement();
            IteratorImpl result = new IteratorImpl(rs, st, session, queryParameters.isReadOnly(session), this.queryReturnTypes, this.queryTranslator.getColumnNames(), this.buildHolderInstantiator(queryParameters.getResultTransformer()));
            if (stats) {
                long endTime = System.nanoTime();
                long milliseconds = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
                statistics.queryExecuted(this.getQueryIdentifier(), 0, milliseconds);
            }
            return result;
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not execute query using iterate", this.getSQLString());
        }
    }

    public ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        this.checkQuery(queryParameters);
        return this.scroll(queryParameters, this.queryReturnTypes, this.buildHolderInstantiator(queryParameters.getResultTransformer()), session);
    }

    private Object[] toResultRow(Object[] row) {
        if (this.selectLength == row.length) {
            return row;
        }
        Object[] result = new Object[this.selectLength];
        int j = 0;
        for (int i = 0; i < row.length; ++i) {
            if (!this.includeInSelect[i]) continue;
            result[j++] = row[i];
        }
        return result;
    }

    @Override
    public int[] getNamedParameterLocs(String name) throws QueryException {
        ParameterInformation info = this.queryTranslator.getParameterTranslations().getNamedParameterInformation(name);
        if (info == null) {
            try {
                info = this.queryTranslator.getParameterTranslations().getPositionalParameterInformation(Integer.parseInt(name));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (info == null) {
            throw new QueryException("Unrecognized parameter label : " + name);
        }
        return info.getSourceLocations();
    }

    @Override
    protected int bindParameterValues(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        int position = startIndex;
        List<ParameterSpecification> parameterSpecs = this.queryTranslator.getCollectedParameterSpecifications();
        for (ParameterSpecification spec : parameterSpecs) {
            position += spec.bind(statement, queryParameters, session, position);
        }
        return position - startIndex;
    }
}

