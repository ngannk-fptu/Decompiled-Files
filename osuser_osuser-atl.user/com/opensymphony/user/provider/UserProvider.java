/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider;

import com.opensymphony.user.Entity;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public interface UserProvider
extends Serializable {
    public boolean create(String var1);

    public void flushCaches();

    public boolean handles(String var1);

    public boolean init(Properties var1);

    public List list();

    public boolean load(String var1, Entity.Accessor var2);

    public boolean remove(String var1);

    public boolean store(String var1, Entity.Accessor var2);
}

