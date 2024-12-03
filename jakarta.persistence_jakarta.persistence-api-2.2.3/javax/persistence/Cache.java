/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

public interface Cache {
    public boolean contains(Class var1, Object var2);

    public void evict(Class var1, Object var2);

    public void evict(Class var1);

    public void evictAll();

    public <T> T unwrap(Class<T> var1);
}

