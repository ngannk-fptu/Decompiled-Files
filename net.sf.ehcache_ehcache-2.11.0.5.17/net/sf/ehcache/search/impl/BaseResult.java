/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.store.StoreQuery;

public abstract class BaseResult
implements Result {
    private final StoreQuery query;
    private volatile List<Object> aggregateResults = Collections.emptyList();

    public BaseResult(StoreQuery query) {
        this.query = query;
    }

    public void setAggregateResults(List<Object> aggregateResults) {
        this.aggregateResults = Collections.unmodifiableList(aggregateResults);
    }

    @Override
    public Object getKey() {
        if (this.query.requestsKeys()) {
            return this.basicGetKey();
        }
        throw new SearchException("keys not included in query. Use includeKeys() to add keys to results.");
    }

    protected abstract Object basicGetKey();

    @Override
    public List<Object> getAggregatorResults() throws SearchException {
        if (this.aggregateResults.isEmpty()) {
            throw new SearchException("No aggregators present in query");
        }
        return this.aggregateResults;
    }

    @Override
    public Object getValue() throws SearchException {
        if (this.query.requestsValues()) {
            return this.basicGetValue();
        }
        throw new SearchException("values not included in query. Use includeValues() to add values to results.");
    }

    protected abstract Object basicGetValue();

    @Override
    public <T> T getAttribute(Attribute<T> attribute) {
        String name = attribute.getAttributeName();
        if (!this.query.requestedAttributes().contains(attribute)) {
            throw new SearchException("Attribute [" + name + "] not included in query");
        }
        return (T)this.basicGetAttribute(name);
    }

    protected abstract Object basicGetAttribute(String var1);

    public String toString() {
        StringBuilder sb = new StringBuilder("Result(");
        if (this.query.requestsKeys()) {
            sb.append("key=");
            sb.append(this.getKey());
        } else {
            sb.append("[no key]");
        }
        sb.append(", ");
        if (this.query.requestsValues()) {
            sb.append("value=");
            sb.append(this.getValue());
        } else {
            sb.append("[no value]");
        }
        sb.append(", ");
        if (!this.query.requestedAttributes().isEmpty()) {
            HashMap<String, String> attrs = new HashMap<String, String>();
            for (Attribute<?> a : this.query.requestedAttributes()) {
                attrs.put(a.getAttributeName(), String.valueOf(this.getAttribute(a)));
            }
            sb.append("attributes=" + attrs);
        } else {
            sb.append("[no attributes]");
        }
        sb.append(", ");
        if (!this.aggregateResults.isEmpty()) {
            sb.append("aggregateResults=" + this.getAggregatorResults());
        } else {
            sb.append("[no aggregateResults]");
        }
        sb.append(")");
        return sb.toString();
    }

    abstract Object getSortAttribute(int var1);
}

