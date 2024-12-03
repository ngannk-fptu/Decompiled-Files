/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathDiagnostic;
import org.jdom2.xpath.XPathExpression;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XPathDiagnosticImpl<T>
implements XPathDiagnostic<T> {
    private final Object dcontext;
    private final XPathExpression<T> dxpath;
    private final List<Object> draw;
    private final List<Object> dfiltered;
    private final List<T> dresult;
    private final boolean dfirstonly;

    public XPathDiagnosticImpl(Object dcontext, XPathExpression<T> dxpath, List<?> inraw, boolean dfirstonly) {
        int sz = inraw.size();
        ArrayList raw = new ArrayList(sz);
        ArrayList filtered = new ArrayList(sz);
        ArrayList<T> result = new ArrayList<T>(sz);
        Filter<T> filter = dxpath.getFilter();
        for (Object o : inraw) {
            raw.add(o);
            T t = filter.filter(o);
            if (t == null) {
                filtered.add(o);
                continue;
            }
            result.add(t);
        }
        this.dcontext = dcontext;
        this.dxpath = dxpath;
        this.dfirstonly = dfirstonly;
        this.dfiltered = Collections.unmodifiableList(filtered);
        this.draw = Collections.unmodifiableList(raw);
        this.dresult = Collections.unmodifiableList(result);
    }

    @Override
    public Object getContext() {
        return this.dcontext;
    }

    @Override
    public XPathExpression<T> getXPathExpression() {
        return this.dxpath;
    }

    @Override
    public List<T> getResult() {
        return this.dresult;
    }

    @Override
    public List<Object> getFilteredResults() {
        return this.dfiltered;
    }

    @Override
    public List<Object> getRawResults() {
        return this.draw;
    }

    @Override
    public boolean isFirstOnly() {
        return this.dfirstonly;
    }

    public String toString() {
        return String.format("[XPathDiagnostic: '%s' evaluated (%s) against %s produced  raw=%d discarded=%d returned=%d]", this.dxpath.getExpression(), this.dfirstonly ? "first" : "all", this.dcontext.getClass().getName(), this.draw.size(), this.dfiltered.size(), this.dresult.size());
    }
}

