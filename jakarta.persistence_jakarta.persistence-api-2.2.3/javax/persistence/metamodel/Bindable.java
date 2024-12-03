/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

public interface Bindable<T> {
    public BindableType getBindableType();

    public Class<T> getBindableJavaType();

    public static enum BindableType {
        SINGULAR_ATTRIBUTE,
        PLURAL_ATTRIBUTE,
        ENTITY_TYPE;

    }
}

