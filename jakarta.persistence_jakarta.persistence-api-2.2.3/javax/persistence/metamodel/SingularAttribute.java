/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Type;

public interface SingularAttribute<X, T>
extends Attribute<X, T>,
Bindable<T> {
    public boolean isId();

    public boolean isVersion();

    public boolean isOptional();

    public Type<T> getType();
}

