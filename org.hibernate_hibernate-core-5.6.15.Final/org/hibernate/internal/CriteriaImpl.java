/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.NaturalIdentifier;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;

public class CriteriaImpl
implements Criteria,
Serializable {
    private final String entityOrClassName;
    private transient SharedSessionContractImplementor session;
    private final String rootAlias;
    private List<CriterionEntry> criterionEntries = new ArrayList<CriterionEntry>();
    private List<OrderEntry> orderEntries = new ArrayList<OrderEntry>();
    private Projection projection;
    private Criteria projectionCriteria;
    private List<Subcriteria> subcriteriaList = new ArrayList<Subcriteria>();
    private Map<String, FetchMode> fetchModes = new HashMap<String, FetchMode>();
    private Map<String, LockMode> lockModes = new HashMap<String, LockMode>();
    private Integer maxResults;
    private Integer firstResult;
    private Integer timeout;
    private Integer fetchSize;
    private boolean cacheable;
    private String cacheRegion;
    private String comment;
    private final List<String> queryHints = new ArrayList<String>();
    private FlushMode flushMode;
    private CacheMode cacheMode;
    private FlushMode sessionFlushMode;
    private CacheMode sessionCacheMode;
    private Boolean readOnly;
    private ResultTransformer resultTransformer = Criteria.ROOT_ENTITY;

    public CriteriaImpl(String entityOrClassName, SharedSessionContractImplementor session) {
        this(entityOrClassName, "this", session);
    }

    public CriteriaImpl(String entityOrClassName, String alias, SharedSessionContractImplementor session) {
        this.session = session;
        this.entityOrClassName = entityOrClassName;
        this.cacheable = false;
        this.rootAlias = alias;
    }

    public String toString() {
        return "CriteriaImpl(" + this.entityOrClassName + ":" + (this.rootAlias == null ? "" : this.rootAlias) + this.subcriteriaList.toString() + this.criterionEntries.toString() + (this.projection == null ? "" : this.projection.toString()) + ')';
    }

    public SharedSessionContractImplementor getSession() {
        return this.session;
    }

    public void setSession(SharedSessionContractImplementor session) {
        this.session = session;
    }

    public String getEntityOrClassName() {
        return this.entityOrClassName;
    }

    public Map<String, LockMode> getLockModes() {
        return this.lockModes;
    }

    public Criteria getProjectionCriteria() {
        return this.projectionCriteria;
    }

    public Iterator<Subcriteria> iterateSubcriteria() {
        return this.subcriteriaList.iterator();
    }

    public Iterator<CriterionEntry> iterateExpressionEntries() {
        return this.criterionEntries.iterator();
    }

    public Iterator<OrderEntry> iterateOrderings() {
        return this.orderEntries.iterator();
    }

    public Criteria add(Criteria criteriaInst, Criterion expression) {
        this.criterionEntries.add(new CriterionEntry(expression, criteriaInst));
        return this;
    }

    @Override
    public String getAlias() {
        return this.rootAlias;
    }

    public Projection getProjection() {
        return this.projection;
    }

    @Override
    public Criteria setProjection(Projection projection) {
        this.projection = projection;
        this.projectionCriteria = this;
        this.setResultTransformer(PROJECTION);
        return this;
    }

    @Override
    public Criteria add(Criterion expression) {
        this.add(this, expression);
        return this;
    }

    @Override
    public Criteria addOrder(Order ordering) {
        this.orderEntries.add(new OrderEntry(ordering, this));
        return this;
    }

    public FetchMode getFetchMode(String path) {
        return this.fetchModes.get(path);
    }

    @Override
    public Criteria setFetchMode(String associationPath, FetchMode mode) {
        String rootAliasPathPrefix = this.rootAlias + ".";
        if (this.rootAlias != null && !associationPath.startsWith(rootAliasPathPrefix)) {
            associationPath = rootAliasPathPrefix + associationPath;
        }
        this.fetchModes.put(associationPath, mode);
        return this;
    }

    @Override
    public Criteria setLockMode(LockMode lockMode) {
        return this.setLockMode(this.getAlias(), lockMode);
    }

    @Override
    public Criteria setLockMode(String alias, LockMode lockMode) {
        this.lockModes.put(alias, lockMode);
        return this;
    }

    @Override
    public Criteria createAlias(String associationPath, String alias) {
        return this.createAlias(associationPath, alias, JoinType.INNER_JOIN);
    }

    @Override
    public Criteria createAlias(String associationPath, String alias, JoinType joinType) {
        new Subcriteria((Criteria)this, associationPath, alias, joinType);
        return this;
    }

    @Override
    public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
        return this.createAlias(associationPath, alias, JoinType.parse(joinType));
    }

    @Override
    public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) {
        new Subcriteria(this, associationPath, alias, joinType, withClause);
        return this;
    }

    @Override
    public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
        return this.createAlias(associationPath, alias, JoinType.parse(joinType), withClause);
    }

    @Override
    public Criteria createCriteria(String associationPath) {
        return this.createCriteria(associationPath, JoinType.INNER_JOIN);
    }

    @Override
    public Criteria createCriteria(String associationPath, JoinType joinType) {
        return new Subcriteria((Criteria)this, associationPath, joinType);
    }

    @Override
    public Criteria createCriteria(String associationPath, int joinType) throws HibernateException {
        return this.createCriteria(associationPath, JoinType.parse(joinType));
    }

    @Override
    public Criteria createCriteria(String associationPath, String alias) {
        return this.createCriteria(associationPath, alias, JoinType.INNER_JOIN);
    }

    @Override
    public Criteria createCriteria(String associationPath, String alias, JoinType joinType) {
        return new Subcriteria((Criteria)this, associationPath, alias, joinType);
    }

    @Override
    public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
        return this.createCriteria(associationPath, alias, JoinType.parse(joinType));
    }

    @Override
    public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) {
        return new Subcriteria(this, associationPath, alias, joinType, withClause);
    }

    @Override
    public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
        return this.createCriteria(associationPath, alias, JoinType.parse(joinType), withClause);
    }

    public ResultTransformer getResultTransformer() {
        return this.resultTransformer;
    }

    @Override
    public Criteria setResultTransformer(ResultTransformer tupleMapper) {
        this.resultTransformer = tupleMapper;
        return this;
    }

    public Integer getMaxResults() {
        return this.maxResults;
    }

    @Override
    public Criteria setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public Integer getFirstResult() {
        return this.firstResult;
    }

    @Override
    public Criteria setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    @Override
    public Criteria setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    @Override
    public Criteria setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public boolean isReadOnlyInitialized() {
        return this.readOnly != null;
    }

    @Override
    public boolean isReadOnly() {
        if (!this.isReadOnlyInitialized() && this.getSession() == null) {
            throw new IllegalStateException("cannot determine readOnly/modifiable setting when it is not initialized and is not initialized and getSession() == null");
        }
        return this.isReadOnlyInitialized() ? this.readOnly.booleanValue() : this.getSession().getPersistenceContextInternal().isDefaultReadOnly();
    }

    @Override
    public Criteria setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public boolean getCacheable() {
        return this.cacheable;
    }

    @Override
    public Criteria setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }

    public String getCacheRegion() {
        return this.cacheRegion;
    }

    @Override
    public Criteria setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion.trim();
        return this;
    }

    public String getComment() {
        return this.comment;
    }

    @Override
    public Criteria setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public List<String> getQueryHints() {
        return this.queryHints;
    }

    @Override
    public Criteria addQueryHint(String queryHint) {
        this.queryHints.add(queryHint);
        return this;
    }

    @Override
    public Criteria setFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    @Override
    public Criteria setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    @Override
    public List list() throws HibernateException {
        this.before();
        try {
            List list = this.session.list(this);
            return list;
        }
        finally {
            this.after();
        }
    }

    @Override
    public ScrollableResults scroll() {
        return this.scroll(this.session.getFactory().getDialect().defaultScrollMode());
    }

    @Override
    public ScrollableResults scroll(ScrollMode scrollMode) {
        this.before();
        try {
            ScrollableResultsImplementor scrollableResultsImplementor = this.session.scroll(this, scrollMode);
            return scrollableResultsImplementor;
        }
        finally {
            this.after();
        }
    }

    @Override
    public Object uniqueResult() throws HibernateException {
        return AbstractProducedQuery.uniqueElement(this.list());
    }

    protected void before() {
        if (this.flushMode != null) {
            this.sessionFlushMode = this.getSession().getHibernateFlushMode();
            this.getSession().setHibernateFlushMode(this.flushMode);
        }
        if (this.cacheMode != null) {
            this.sessionCacheMode = this.getSession().getCacheMode();
            this.getSession().setCacheMode(this.cacheMode);
        }
    }

    protected void after() {
        if (this.sessionFlushMode != null) {
            this.getSession().setHibernateFlushMode(this.sessionFlushMode);
            this.sessionFlushMode = null;
        }
        if (this.sessionCacheMode != null) {
            this.getSession().setCacheMode(this.sessionCacheMode);
            this.sessionCacheMode = null;
        }
    }

    public boolean isLookupByNaturalKey() {
        if (this.projection != null) {
            return false;
        }
        if (this.subcriteriaList.size() > 0) {
            return false;
        }
        if (this.criterionEntries.size() != 1) {
            return false;
        }
        CriterionEntry ce = this.criterionEntries.get(0);
        return ce.getCriterion() instanceof NaturalIdentifier;
    }

    public static final class OrderEntry
    implements Serializable {
        private final Order order;
        private final Criteria criteria;

        private OrderEntry(Order order, Criteria criteria) {
            this.criteria = criteria;
            this.order = order;
        }

        public Order getOrder() {
            return this.order;
        }

        public Criteria getCriteria() {
            return this.criteria;
        }

        public String toString() {
            return this.order.toString();
        }
    }

    public static final class CriterionEntry
    implements Serializable {
        private final Criterion criterion;
        private final Criteria criteria;

        private CriterionEntry(Criterion criterion, Criteria criteria) {
            this.criteria = criteria;
            this.criterion = criterion;
        }

        public Criterion getCriterion() {
            return this.criterion;
        }

        public Criteria getCriteria() {
            return this.criteria;
        }

        public String toString() {
            return this.criterion.toString();
        }
    }

    public final class Subcriteria
    implements Criteria,
    Serializable {
        private String alias;
        private String path;
        private Criteria parent;
        private LockMode lockMode;
        private JoinType joinType = JoinType.INNER_JOIN;
        private Criterion withClause;
        private boolean hasRestriction;

        private Subcriteria(Criteria parent, String path, String alias, JoinType joinType, Criterion withClause) {
            this.alias = alias;
            this.path = path;
            this.parent = parent;
            this.joinType = joinType;
            this.withClause = withClause;
            this.hasRestriction = withClause != null;
            CriteriaImpl.this.subcriteriaList.add(this);
        }

        private Subcriteria(Criteria parent, String path, String alias, JoinType joinType) {
            this(parent, path, alias, joinType, (Criterion)null);
        }

        private Subcriteria(Criteria parent, String path, JoinType joinType) {
            this(parent, path, null, joinType);
        }

        public String toString() {
            return "Subcriteria(" + this.path + ":" + (this.alias == null ? "" : this.alias) + ')';
        }

        @Override
        public String getAlias() {
            return this.alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getPath() {
            return this.path;
        }

        public Criteria getParent() {
            return this.parent;
        }

        public LockMode getLockMode() {
            return this.lockMode;
        }

        @Override
        public Criteria setLockMode(LockMode lockMode) {
            this.lockMode = lockMode;
            return this;
        }

        public JoinType getJoinType() {
            return this.joinType;
        }

        public Criterion getWithClause() {
            return this.withClause;
        }

        public boolean hasRestriction() {
            return this.hasRestriction;
        }

        @Override
        public Criteria add(Criterion expression) {
            this.hasRestriction = true;
            CriteriaImpl.this.add(this, expression);
            return this;
        }

        @Override
        public Criteria addOrder(Order order) {
            CriteriaImpl.this.orderEntries.add(new OrderEntry(order, this));
            return this;
        }

        @Override
        public Criteria createAlias(String associationPath, String alias) {
            return this.createAlias(associationPath, alias, JoinType.INNER_JOIN);
        }

        @Override
        public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException {
            new Subcriteria((Criteria)this, associationPath, alias, joinType);
            return this;
        }

        @Override
        public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
            return this.createAlias(associationPath, alias, JoinType.parse(joinType));
        }

        @Override
        public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
            new Subcriteria((Criteria)this, associationPath, alias, joinType, withClause);
            return this;
        }

        @Override
        public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
            return this.createAlias(associationPath, alias, JoinType.parse(joinType), withClause);
        }

        @Override
        public Criteria createCriteria(String associationPath) {
            return this.createCriteria(associationPath, JoinType.INNER_JOIN);
        }

        @Override
        public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException {
            return new Subcriteria(this, associationPath, joinType);
        }

        @Override
        public Criteria createCriteria(String associationPath, int joinType) throws HibernateException {
            return this.createCriteria(associationPath, JoinType.parse(joinType));
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias) {
            return this.createCriteria(associationPath, alias, JoinType.INNER_JOIN);
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException {
            return new Subcriteria((Criteria)this, associationPath, alias, joinType);
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
            return this.createCriteria(associationPath, alias, JoinType.parse(joinType));
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
            return new Subcriteria((Criteria)this, associationPath, alias, joinType, withClause);
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
            return this.createCriteria(associationPath, alias, JoinType.parse(joinType), withClause);
        }

        @Override
        public boolean isReadOnly() {
            return CriteriaImpl.this.isReadOnly();
        }

        @Override
        public boolean isReadOnlyInitialized() {
            return CriteriaImpl.this.isReadOnlyInitialized();
        }

        @Override
        public Criteria setReadOnly(boolean readOnly) {
            CriteriaImpl.this.setReadOnly(readOnly);
            return this;
        }

        @Override
        public Criteria setCacheable(boolean cacheable) {
            CriteriaImpl.this.setCacheable(cacheable);
            return this;
        }

        @Override
        public Criteria setCacheRegion(String cacheRegion) {
            CriteriaImpl.this.setCacheRegion(cacheRegion);
            return this;
        }

        @Override
        public List list() throws HibernateException {
            return CriteriaImpl.this.list();
        }

        @Override
        public ScrollableResults scroll() throws HibernateException {
            return CriteriaImpl.this.scroll();
        }

        @Override
        public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
            return CriteriaImpl.this.scroll(scrollMode);
        }

        @Override
        public Object uniqueResult() throws HibernateException {
            return CriteriaImpl.this.uniqueResult();
        }

        @Override
        public Criteria setFetchMode(String associationPath, FetchMode mode) {
            CriteriaImpl.this.setFetchMode(StringHelper.qualify(this.path, associationPath), mode);
            return this;
        }

        @Override
        public Criteria setFlushMode(FlushMode flushMode) {
            CriteriaImpl.this.setFlushMode(flushMode);
            return this;
        }

        @Override
        public Criteria setCacheMode(CacheMode cacheMode) {
            CriteriaImpl.this.setCacheMode(cacheMode);
            return this;
        }

        @Override
        public Criteria setFirstResult(int firstResult) {
            CriteriaImpl.this.setFirstResult(firstResult);
            return this;
        }

        @Override
        public Criteria setMaxResults(int maxResults) {
            CriteriaImpl.this.setMaxResults(maxResults);
            return this;
        }

        @Override
        public Criteria setTimeout(int timeout) {
            CriteriaImpl.this.setTimeout(timeout);
            return this;
        }

        @Override
        public Criteria setFetchSize(int fetchSize) {
            CriteriaImpl.this.setFetchSize(fetchSize);
            return this;
        }

        @Override
        public Criteria setLockMode(String alias, LockMode lockMode) {
            CriteriaImpl.this.setLockMode(alias, lockMode);
            return this;
        }

        @Override
        public Criteria setResultTransformer(ResultTransformer resultProcessor) {
            CriteriaImpl.this.setResultTransformer(resultProcessor);
            return this;
        }

        @Override
        public Criteria setComment(String comment) {
            CriteriaImpl.this.setComment(comment);
            return this;
        }

        @Override
        public Criteria addQueryHint(String queryHint) {
            CriteriaImpl.this.addQueryHint(queryHint);
            return this;
        }

        @Override
        public Criteria setProjection(Projection projection) {
            CriteriaImpl.this.projection = projection;
            CriteriaImpl.this.projectionCriteria = this;
            this.setResultTransformer(PROJECTION);
            return this;
        }
    }
}

