/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import java.util.List;

public interface Registry {
    public <T> List<T> getPlugins(Class<T> var1);

    public <T> T getPlugin(Class<T> var1);
}

