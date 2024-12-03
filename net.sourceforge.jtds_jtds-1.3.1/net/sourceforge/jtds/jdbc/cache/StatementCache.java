/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc.cache;

import java.util.Collection;

public interface StatementCache {
    public Object get(String var1);

    public void put(String var1, Object var2);

    public void remove(String var1);

    public Collection getObsoleteHandles(Collection var1);
}

