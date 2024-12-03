/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.module;

import com.sun.syndication.feed.module.Module;
import java.util.List;

public interface Extendable {
    public Module getModule(String var1);

    public List getModules();

    public void setModules(List var1);
}

