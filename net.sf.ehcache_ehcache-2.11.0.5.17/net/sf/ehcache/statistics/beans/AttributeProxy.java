/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.beans;

public abstract class AttributeProxy<T> {
    private final String name;
    private final Class<T> clazz;
    private final boolean isWrite;
    private final boolean isRead;
    private final String description;

    public AttributeProxy(Class<T> clazz, String name, String description, boolean isRead, boolean isWrite) {
        this.name = name;
        this.description = description;
        this.clazz = clazz;
        this.isWrite = isWrite;
        this.isRead = isRead;
    }

    public String getDescription() {
        return this.description;
    }

    public Class<?> getTypeClass() {
        return this.clazz;
    }

    public String getName() {
        return this.name;
    }

    public T get(String name) {
        throw new UnsupportedOperationException();
    }

    public void set(String name, T t) {
        throw new UnsupportedOperationException();
    }

    public boolean isRead() {
        return this.isRead;
    }

    public boolean isWrite() {
        return this.isWrite;
    }
}

