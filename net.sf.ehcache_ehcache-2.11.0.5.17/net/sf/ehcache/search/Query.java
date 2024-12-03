/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.ExecutionHints;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.aggregator.Aggregator;
import net.sf.ehcache.search.aggregator.AggregatorException;
import net.sf.ehcache.search.expression.Criteria;

public interface Query {
    public static final Attribute KEY = new Attribute("key");
    public static final Attribute VALUE = new Attribute("value");

    public Query includeKeys();

    public Query includeValues();

    public Query includeAttribute(Attribute<?> ... var1);

    public Query includeAggregator(Aggregator ... var1) throws SearchException, AggregatorException;

    public Query addOrderBy(Attribute<?> var1, Direction var2);

    public Query addGroupBy(Attribute<?> ... var1);

    public Query maxResults(int var1);

    public Query addCriteria(Criteria var1);

    public Results execute() throws SearchException;

    public Results execute(ExecutionHints var1) throws SearchException;

    public Query end();
}

