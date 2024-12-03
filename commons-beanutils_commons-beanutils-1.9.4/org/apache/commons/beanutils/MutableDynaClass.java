/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.DynaClass;

public interface MutableDynaClass
extends DynaClass {
    public void add(String var1);

    public void add(String var1, Class<?> var2);

    public void add(String var1, Class<?> var2, boolean var3, boolean var4);

    public boolean isRestricted();

    public void remove(String var1);

    public void setRestricted(boolean var1);
}

