/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Term;

public class TermImpl<T>
implements Term<T> {
    protected T value;
    protected Term.Operator operator = null;

    protected TermImpl() {
    }

    @Override
    public Term.Operator getOperator() {
        return this.operator;
    }

    @Override
    public Term<T> setOperator(Term.Operator operator) {
        this.operator = operator;
        return this;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public Term<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        if (this.value != null) {
            sb.append(this.value.toString());
        }
        return sb.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.operator == null ? 0 : this.operator.hashCode());
        result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TermImpl)) {
            return false;
        }
        TermImpl other = (TermImpl)obj;
        if (this.operator == null ? other.operator != null : !this.operator.equals((Object)other.operator)) {
            return false;
        }
        return !(this.value == null ? other.value != null : !this.value.equals(other.value));
    }

    @Override
    public Term<T> shallowClone() {
        try {
            return (Term)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

