/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.parser.MAggregate;
import net.sf.ehcache.search.parser.MAttribute;
import net.sf.ehcache.search.parser.MCriteria;
import net.sf.ehcache.search.parser.MOrderBy;
import net.sf.ehcache.search.parser.MTarget;
import net.sf.ehcache.store.StoreQuery;

public class ParseModel {
    private MCriteria criteria = null;
    private List<MTarget> targets = new ArrayList<MTarget>();
    private int limit = 0;
    private boolean isLimited = false;
    private List<MOrderBy> orderBy = new LinkedList<MOrderBy>();
    private List<MAttribute> groupBy = new LinkedList<MAttribute>();
    private boolean includeKeys = false;
    private boolean includeValues = false;
    private List<MAttribute> includedAttributes = new LinkedList<MAttribute>();
    private List<MAggregate> includedAggregators = new LinkedList<MAggregate>();
    private boolean includeStar = false;
    private boolean isCountStar = false;
    private String cacheName;
    private String cacheManagerName;
    private boolean cacheManagerNameWasAttempted = false;

    public void includeTargetKeys() {
        this.includeKeys = true;
    }

    public void includeTargetValues() {
        this.includeValues = true;
    }

    public void includeCountStar() {
        this.isCountStar = true;
    }

    public void includeTargetAttribute(MAttribute ma) {
        if (ma.isKey()) {
            this.includeTargetKeys();
        } else if (ma.isValue()) {
            this.includeTargetValues();
        } else {
            this.includedAttributes.add(ma);
        }
        this.targets.add(new MTarget(ma));
    }

    public void includeTargetAggregator(MAggregate ma) {
        this.includedAggregators.add(ma);
        this.targets.add(new MTarget(ma));
    }

    public void includeTargetStar() {
        this.includeStar = true;
        this.targets.add(new MTarget());
    }

    public void setCriteria(MCriteria crit) {
        this.criteria = crit;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        boolean first = true;
        for (MTarget ma : this.targets) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(ma.toString());
        }
        if (this.criteria != null) {
            sb.append(" where " + this.criteria);
        }
        for (MAttribute m : this.groupBy) {
            sb.append(" group by " + m);
        }
        for (MOrderBy ord : this.orderBy) {
            sb.append(" " + ord);
        }
        if (this.isLimited) {
            sb.append(" limit " + this.limit);
        }
        return sb.toString();
    }

    public void addOrderBy(MAttribute attr, boolean asc) {
        this.orderBy.add(new MOrderBy(attr, asc));
    }

    public void setLimit(int lim) {
        this.isLimited = true;
        this.limit = lim;
    }

    public void addGroupBy(MAttribute attr) {
        this.groupBy.add(attr);
    }

    public MCriteria getCriteria() {
        return this.criteria;
    }

    public MTarget[] getTargets() {
        return this.targets.toArray(new MTarget[0]);
    }

    public boolean isIncludedTargetKeys() {
        return this.includeKeys;
    }

    public boolean isIncludedTargetValues() {
        return this.includeValues;
    }

    public List<MAttribute> getIncludedTargetAttributes() {
        return Collections.unmodifiableList(this.includedAttributes);
    }

    public List<MAggregate> getIncludedTargetAgregators() {
        return Collections.unmodifiableList(this.includedAggregators);
    }

    public boolean isIncludedTargetStar() {
        return this.includeStar;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean isLimited() {
        return this.isLimited;
    }

    public List<MOrderBy> getOrderBy() {
        return this.orderBy;
    }

    public List<MAttribute> getGroupBy() {
        return this.groupBy;
    }

    public Query getQuery(Ehcache ehcache) {
        ClassLoader loader = ehcache.getCacheConfiguration().getClassLoader();
        Query q = ehcache.createQuery();
        if (this.criteria != null) {
            q.addCriteria((Criteria)this.criteria.asEhcacheObject(loader));
        }
        if (this.isLimited) {
            q.maxResults(this.limit);
        }
        ArrayList<Object> targetList = new ArrayList<Object>();
        for (MTarget mTarget : this.targets) {
            if (mTarget.isAttribute()) {
                targetList.add(mTarget.getAttribute().getName());
                continue;
            }
            if (mTarget.isAggregate()) {
                MAggregate agg = mTarget.getAggregate();
                MAggregate.AggOp op = agg.getOp();
                MAttribute ma = agg.getAttribute();
                targetList.add(op.toString().toLowerCase() + "(" + ma.getName() + ")");
                continue;
            }
            for (Attribute attr : this.getAttributesImpliedByStar(ehcache)) {
                if (Query.KEY.equals(attr) || Query.VALUE.equals(attr)) continue;
                targetList.add(attr.getAttributeName());
            }
        }
        ((StoreQuery)((Object)q)).targets(targetList.toArray(new String[0]));
        for (MAttribute mAttribute : this.getIncludedTargetAttributes()) {
            q.includeAttribute(new Attribute[]{mAttribute.asEhcacheObject(loader)});
        }
        for (MAggregate mAggregate : this.getIncludedTargetAgregators()) {
            q.includeAggregator(mAggregate.asEhcacheObject(loader));
        }
        if (this.isIncludedTargetKeys()) {
            q.includeKeys();
        }
        if (this.isIncludedTargetValues()) {
            q.includeValues();
        }
        if (this.isIncludedTargetStar()) {
            for (Attribute attribute : this.getAttributesImpliedByStar(ehcache)) {
                if (Query.KEY.equals(attribute) || Query.VALUE.equals(attribute)) continue;
                q.includeAttribute(attribute);
            }
        }
        for (MAttribute mAttribute : this.groupBy) {
            q.addGroupBy(new Attribute[]{mAttribute.asEhcacheObject(loader)});
        }
        for (MOrderBy mOrderBy : this.orderBy) {
            q.addOrderBy((Attribute<?>)mOrderBy.getAttribute().asEhcacheObject(loader), mOrderBy.isOrderAscending() ? Direction.ASCENDING : Direction.DESCENDING);
        }
        return q;
    }

    private Collection<Attribute> getAttributesImpliedByStar(Ehcache cache) {
        return this.isIncludedTargetStar() ? cache.getSearchAttributes() : Collections.emptySet();
    }

    public void setCacheName(String cacheName) {
        String[] tokens = cacheName.split("\\.");
        if (tokens.length > 2) {
            throw new SearchException("Cache manager name not specified.");
        }
        if (tokens.length == 2) {
            this.cacheManagerName = tokens[0];
            this.cacheName = tokens[1];
            this.cacheManagerNameWasAttempted = true;
        } else {
            this.cacheName = cacheName;
        }
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public String getCacheManagerName() {
        return this.cacheManagerName;
    }
}

