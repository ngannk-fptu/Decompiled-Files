/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.module.Module;
import java.util.List;

public interface Extendable {
    public Module getModule(String var1);

    public List<Module> getModules();

    public void setModules(List<Module> var1);
}

