/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import java.lang.reflect.Member;
import javax.persistence.metamodel.ManagedType;

public interface Attribute<X, Y> {
    public String getName();

    public PersistentAttributeType getPersistentAttributeType();

    public ManagedType<X> getDeclaringType();

    public Class<Y> getJavaType();

    public Member getJavaMember();

    public boolean isAssociation();

    public boolean isCollection();

    public static enum PersistentAttributeType {
        MANY_TO_ONE,
        ONE_TO_ONE,
        BASIC,
        EMBEDDED,
        MANY_TO_MANY,
        ONE_TO_MANY,
        ELEMENT_COLLECTION;

    }
}

