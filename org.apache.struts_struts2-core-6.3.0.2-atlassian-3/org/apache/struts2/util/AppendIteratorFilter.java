/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.Action;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.struts2.util.IteratorFilterSupport;

public class AppendIteratorFilter
extends IteratorFilterSupport
implements Iterator,
Action {
    List iterators = new ArrayList();
    List sources = new ArrayList();

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
        if (this.iterators.size() > 0) {
            return ((Iterator)this.iterators.get(0)).hasNext();
        }
        return false;
    }

    public Object next() {
        try {
            Object e = ((Iterator)this.iterators.get(0)).next();
            return e;
        }
        finally {
            if (this.iterators.size() > 0 && !((Iterator)this.iterators.get(0)).hasNext()) {
                this.iterators.remove(0);
            }
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

