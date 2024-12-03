/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermBracketedIdents;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TermBracketedIdentsImpl
extends AbstractList<TermIdent>
implements TermBracketedIdents {
    protected List<TermIdent> value;
    protected Term.Operator operator;

    protected TermBracketedIdentsImpl() {
        this.value = new ArrayList<TermIdent>();
    }

    protected TermBracketedIdentsImpl(int initialSize) {
        this.value = new ArrayList<TermIdent>(initialSize);
    }

    @Override
    public List<TermIdent> getValue() {
        return this.value;
    }

    public TermBracketedIdents setValue(List<TermIdent> value) {
        this.value = value;
        return this;
    }

    @Override
    public Term.Operator getOperator() {
        return this.operator;
    }

    public TermBracketedIdents setOperator(Term.Operator operator) {
        this.operator = operator;
        return this;
    }

    @Override
    public TermIdent get(int arg0) {
        return this.value.get(arg0);
    }

    @Override
    public void add(int index, TermIdent element) {
        this.value.add(index, element);
    }

    @Override
    public TermIdent remove(int index) {
        return this.value.remove(index);
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public Iterator<TermIdent> iterator() {
        return this.value.iterator();
    }

    @Override
    public boolean add(TermIdent o) {
        return this.value.add(o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        sb.append('[');
        OutputUtil.appendList(sb, this.value, " ");
        sb.append(']');
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
        if (!(obj instanceof TermBracketedIdentsImpl)) {
            return false;
        }
        TermBracketedIdentsImpl other = (TermBracketedIdentsImpl)obj;
        if (this.operator == null ? other.operator != null : !this.operator.equals((Object)other.operator)) {
            return false;
        }
        return !(this.value == null ? other.value != null : !this.value.equals(other.value));
    }

    public TermBracketedIdents shallowClone() {
        try {
            return (TermBracketedIdents)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

