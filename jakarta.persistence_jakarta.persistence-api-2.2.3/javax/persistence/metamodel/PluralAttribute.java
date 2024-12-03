/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Type;

public interface PluralAttribute<X, C, E>
extends Attribute<X, C>,
Bindable<E> {
    public CollectionType getCollectionType();

    public Type<E> getElementType();

    public static enum CollectionType {
        COLLECTION,
        SET,
        LIST,
        MAP;

    }
}

