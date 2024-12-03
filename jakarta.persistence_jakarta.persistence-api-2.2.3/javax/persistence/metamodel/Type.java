/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

public interface Type<X> {
    public PersistenceType getPersistenceType();

    public Class<X> getJavaType();

    public static enum PersistenceType {
        ENTITY,
        EMBEDDABLE,
        MAPPED_SUPERCLASS,
        BASIC;

    }
}

