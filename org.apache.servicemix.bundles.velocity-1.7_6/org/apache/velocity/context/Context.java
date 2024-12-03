/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

public interface Context {
    public Object put(String var1, Object var2);

    public Object get(String var1);

    public boolean containsKey(Object var1);

    public Object[] getKeys();

    public Object remove(Object var1);
}

