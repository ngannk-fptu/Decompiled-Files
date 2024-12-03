/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.Action;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.struts2.util.IteratorFilterSupport;

public class MergeIteratorFilter
extends IteratorFilterSupport
implements Iterator,
Action {
    List iterators = new ArrayList();
    List sources = new ArrayList();
    int idx = 0;

    public void setSource(Object anIterator) {
        this.sources.add(anIterator);
    }

    @Override
    public String execute() {
        for (int i = 0; i < this.sources.size(); ++i) {
            Object source = this.sources.get(i);
            this.iterators.add(this.getIterator(source));
        }
        return "success";
    }

    @Override
    public boolean hasNext() {
        while (this.iterators.size() > 0) {
            if (((Iterator)this.iterators.get(this.idx)).hasNext()) {
                return true;
            }
            this.iterators.remove(this.idx);
            if (this.iterators.size() <= 0) continue;
            this.idx %= this.iterators.size();
        }
        return false;
    }

    public Object next() {
        try {
            Object e = ((Iterator)this.iterators.get(this.idx)).next();
            return e;
        }
        finally {
            this.idx = (this.idx + 1) % this.iterators.size();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in MergeIteratorFilter.");
    }
}

