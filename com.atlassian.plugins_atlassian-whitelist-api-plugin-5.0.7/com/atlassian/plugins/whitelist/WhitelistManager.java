/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.whitelist.WhitelistRule;
import java.util.Collection;
import javax.annotation.Nullable;

@PublicApi
public interface WhitelistManager {
    public WhitelistRule add(WhitelistRule var1);

    public Collection<WhitelistRule> addAll(Iterable<WhitelistRule> var1);

    public WhitelistRule update(WhitelistRule var1);

    public void remove(WhitelistRule var1);

    public void removeAll(Iterable<WhitelistRule> var1);

    public Collection<WhitelistRule> getAll();

    @Nullable
    public WhitelistRule get(int var1);
}

