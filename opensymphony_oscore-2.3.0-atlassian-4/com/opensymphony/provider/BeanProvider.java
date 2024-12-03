/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

import com.opensymphony.provider.Provider;

public interface BeanProvider
extends Provider {
    public boolean setProperty(Object var1, String var2, Object var3);

    public Object getProperty(Object var1, String var2);
}

