/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import org.jdom2.Content;
import org.jdom2.filter.AndFilter;
import org.jdom2.filter.Filter;
import org.jdom2.filter.NegateFilter;
import org.jdom2.filter.OrFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractFilter<T>
implements Filter<T> {
    private static final long serialVersionUID = 200L;

    @Override
    public final boolean matches(Object content) {
        return this.filter(content) != null;
    }

    @Override
    public List<T> filter(List<?> content) {
        if (content == null) {
            return Collections.emptyList();
        }
        if (content instanceof RandomAccess) {
            int sz = content.size();
            ArrayList ret = new ArrayList(sz);
            for (int i = 0; i < sz; ++i) {
                Object c = this.filter(content.get(i));
                if (c == null) continue;
                ret.add(c);
            }
            if (ret.isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(ret);
        }
        ArrayList ret = new ArrayList(10);
        Iterator<?> it = content.iterator();
        while (it.hasNext()) {
            Object c = this.filter(it.next());
            if (c == null) continue;
            ret.add(c);
        }
        if (ret.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(ret);
    }

    @Override
    public final Filter<?> negate() {
        if (this instanceof NegateFilter) {
            return ((NegateFilter)this).getBaseFilter();
        }
        return new NegateFilter(this);
    }

    @Override
    public final Filter<? extends Content> or(Filter<?> filter) {
        return new OrFilter(this, filter);
    }

    @Override
    public final Filter<T> and(Filter<?> filter) {
        return new AndFilter(filter, this);
    }

    @Override
    public <R> Filter<R> refine(Filter<R> filter) {
        return new AndFilter<R>(this, filter);
    }
}

