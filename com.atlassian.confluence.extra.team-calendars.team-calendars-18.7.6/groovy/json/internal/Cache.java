/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

public interface Cache<KEY, VALUE> {
    public void put(KEY var1, VALUE var2);

    public VALUE get(KEY var1);

    public VALUE getSilent(KEY var1);

    public void remove(KEY var1);

    public int size();
}

