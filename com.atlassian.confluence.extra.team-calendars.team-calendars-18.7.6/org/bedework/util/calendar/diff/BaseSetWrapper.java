/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;
import org.bedework.util.calendar.diff.BaseWrapper;

abstract class BaseSetWrapper<T extends BaseWrapper, ParentT extends BaseWrapper, ListT>
extends BaseWrapper<ParentT> {
    private Set<T> els = new TreeSet<T>();
    private T[] tarray;

    BaseSetWrapper(ParentT parent, QName name, List<ListT> elsList) {
        super(parent, name);
        Set<T> t;
        if (elsList == null) {
            return;
        }
        for (ListT el : elsList) {
            t = this.getWrapped(el);
            if (t == null) continue;
            this.els.addAll(t);
        }
        this.tarray = this.getTarray(this.els.size());
        int i = 0;
        Iterator<T> iterator = this.els.iterator();
        while (iterator.hasNext()) {
            this.getTarray()[i] = t = (BaseWrapper)iterator.next();
            ++i;
        }
    }

    abstract Set<T> getWrapped(ListT var1);

    abstract T[] getTarray(int var1);

    Set<T> getEls() {
        return this.els;
    }

    int size() {
        return this.els.size();
    }

    public T[] getTarray() {
        return this.tarray;
    }

    T find(QName nm) {
        for (BaseWrapper t : this.els) {
            if (!t.getName().equals(nm)) continue;
            return (T)t;
        }
        return null;
    }

    List<T> findAll(QName nm) {
        ArrayList<BaseWrapper> found = new ArrayList<BaseWrapper>();
        for (BaseWrapper t : this.els) {
            if (!t.getName().equals(nm)) continue;
            found.add(t);
        }
        return found;
    }

    @Override
    protected void toStringSegment(StringBuilder sb) {
        sb.append("size=");
        sb.append(this.size());
        for (BaseWrapper t : this.els) {
            sb.append(",\n   ");
            sb.append(t.toString());
        }
    }
}

