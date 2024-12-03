/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.DynaClass;

public interface DynaBean {
    public boolean contains(String var1, String var2);

    public Object get(String var1);

    public Object get(String var1, int var2);

    public Object get(String var1, String var2);

    public DynaClass getDynaClass();

    public void remove(String var1, String var2);

    public void set(String var1, Object var2);

    public void set(String var1, int var2, Object var3);

    public void set(String var1, String var2, Object var3);
}

