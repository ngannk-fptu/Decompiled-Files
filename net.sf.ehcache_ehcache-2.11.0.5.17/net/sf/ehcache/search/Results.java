/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search;

import java.util.List;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.SearchException;

public interface Results {
    public void discard();

    public List<Result> all() throws SearchException;

    public List<Result> range(int var1, int var2) throws SearchException, IndexOutOfBoundsException;

    public int size();

    public boolean hasKeys();

    public boolean hasValues();

    public boolean hasAttributes();

    public boolean hasAggregators();
}

