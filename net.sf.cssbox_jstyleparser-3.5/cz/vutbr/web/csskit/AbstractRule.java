/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Rule;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AbstractRule<T>
extends AbstractList<T>
implements Rule<T> {
    protected List<T> list = Collections.emptyList();
    protected int hash = 0;

    @Override
    public List<T> asList() {
        return this.list;
    }

    @Override
    public Rule<T> replaceAll(List<T> replacement) {
        this.hash = 0;
        this.list = replacement;
        return this;
    }

    @Override
    public Rule<T> unlock() {
        this.hash = 0;
        this.list = new ArrayList<T>();
        return this;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public T get(int index) {
        return this.list.get(index);
    }

    @Override
    public T set(int index, T element) {
        this.hash = 0;
        return this.list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.hash = 0;
        this.list.add(index, element);
    }

    @Override
    public T remove(int index) {
        this.hash = 0;
        return this.list.remove(index);
    }

    @Override
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    @Override
    public boolean add(T o) {
        this.hash = 0;
        return this.list.add(o);
    }

    @Override
    public int hashCode() {
        if (this.hash == 0) {
            int prime = 31;
            int result = super.hashCode();
            this.hash = result = 31 * result + (this.list == null ? 0 : this.list.hashCode());
        }
        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof AbstractRule)) {
            return false;
        }
        AbstractRule other = (AbstractRule)obj;
        return !(this.list == null ? other.list != null : !this.list.equals(other.list));
    }
}

