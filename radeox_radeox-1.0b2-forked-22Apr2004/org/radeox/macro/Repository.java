/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.util.List;

public interface Repository {
    public boolean containsKey(String var1);

    public Object get(String var1);

    public List getPlugins();

    public void put(String var1, Object var2);
}

