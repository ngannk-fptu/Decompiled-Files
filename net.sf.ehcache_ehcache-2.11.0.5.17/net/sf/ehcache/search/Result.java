/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search;

import java.util.List;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.SearchException;

public interface Result {
    public Object getKey() throws SearchException;

    public Object getValue() throws SearchException;

    public <T> T getAttribute(Attribute<T> var1) throws SearchException;

    public List<Object> getAggregatorResults() throws SearchException;
}

