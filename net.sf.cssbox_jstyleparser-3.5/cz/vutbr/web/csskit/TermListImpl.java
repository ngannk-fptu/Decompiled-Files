/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TermListImpl
extends AbstractList<Term<?>>
implements TermList {
    protected List<Term<?>> value;
    protected Term.Operator operator;

    protected TermListImpl() {
        this.value = new ArrayList();
    }

    protected TermListImpl(int initialSize) {
        this.value = new ArrayList(initialSize);
    }

    @Override
    public List<Term<?>> getValue() {
        return this.value;
    }

    public TermList setValue(List<Term<?>> value) {
        this.value = value;
        return this;
    }

    @Override
    public Term.Operator getOperator() {
        return this.operator;
    }

    public TermList setOperator(Term.Operator operator) {
        this.operator = operator;
        return this;
    }

    @Override
    public Term<?> get(int arg0) {
        return this.value.get(arg0);
    }

    @Override
    public void add(int index, Term<?> element) {
        this.value.add(index, element);
    }

    @Override
    public Term<?> remove(int index) {
        return this.value.remove(index);
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public Iterator<Term<?>> iterator() {
        return this.value.iterator();
    }

    @Override
    public boolean add(Term<?> o) {
        return this.value.add(o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        OutputUtil.appendList(sb, this.value, " ");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.operator == null ? 0 : this.operator.hashCode());
        result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TermListImpl)) {
            return false;
        }
        TermListImpl other = (TermListImpl)obj;
        if (this.operator == null ? other.operator != null : !this.operator.equals((Object)other.operator)) {
            return false;
        }
        return !(this.value == null ? other.value != null : !this.value.equals(other.value));
    }

    public TermList shallowClone() {
        try {
            return (TermList)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

