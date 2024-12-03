/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TransactionGroup<T extends Serializable, E extends Serializable>
implements Serializable {
    private final T primaryObject;
    private final Collection<? extends E> dependantObjects;

    private TransactionGroup(Builder<T, E> builder) {
        this.primaryObject = ((Builder)builder).primaryObject;
        this.dependantObjects = ((Builder)builder).dependantObjects;
    }

    public T getPrimaryObject() {
        return this.primaryObject;
    }

    public Collection<? extends E> getDependantObjects() {
        return this.dependantObjects;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TransactionGroup that = (TransactionGroup)o;
        return !(this.primaryObject != null ? !this.primaryObject.equals(that.primaryObject) : that.primaryObject != null);
    }

    public int hashCode() {
        return this.primaryObject != null ? this.primaryObject.hashCode() : 0;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("primaryObject", this.primaryObject).toString();
    }

    public static class Builder<T extends Serializable, E extends Serializable> {
        private T primaryObject;
        private Collection<? extends E> dependantObjects;

        public Builder(T primaryObject) {
            this.primaryObject = primaryObject;
        }

        public Builder<T, E> withDependantObjects(Collection<? extends E> dependantObjects) {
            this.dependantObjects = dependantObjects;
            return this;
        }

        public TransactionGroup<T, E> build() {
            return new TransactionGroup(this);
        }
    }
}

