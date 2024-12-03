/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

public interface CacheStorage {
    public Object get(Object var1);

    public void put(Object var1, Object var2);

    public void remove(Object var1);

    public void clear();
}

