/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search;

import java.util.Collections;
import java.util.List;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;

public class NullResults
implements Results {
    public static final NullResults INSTANCE = new NullResults();

    @Override
    public void discard() {
    }

    @Override
    public List<Result> all() throws SearchException {
        return Collections.emptyList();
    }

    @Override
    public List<Result> range(int start, int count) throws SearchException, IndexOutOfBoundsException {
        return Collections.emptyList();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean hasKeys() {
        return false;
    }

    @Override
    public boolean hasValues() {
        return false;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public boolean hasAggregators() {
        return false;
    }
}

