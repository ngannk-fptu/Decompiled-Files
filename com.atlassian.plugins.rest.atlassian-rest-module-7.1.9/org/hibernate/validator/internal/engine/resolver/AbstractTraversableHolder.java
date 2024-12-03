/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Path$Node
 */
package org.hibernate.validator.internal.engine.resolver;

import javax.validation.Path;

abstract class AbstractTraversableHolder {
    private final Object traversableObject;
    private final Path.Node traversableProperty;
    private final int hashCode;

    protected AbstractTraversableHolder(Object traversableObject, Path.Node traversableProperty) {
        this.traversableObject = traversableObject;
        this.traversableProperty = traversableProperty;
        this.hashCode = this.buildHashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof AbstractTraversableHolder)) {
            return false;
        }
        AbstractTraversableHolder that = (AbstractTraversableHolder)o;
        if (this.traversableObject != null ? this.traversableObject != that.traversableObject : that.traversableObject != null) {
            return false;
        }
        return this.traversableProperty.equals(that.traversableProperty);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public int buildHashCode() {
        int result = this.traversableObject != null ? System.identityHashCode(this.traversableObject) : 0;
        result = 31 * result + this.traversableProperty.hashCode();
        return result;
    }
}

