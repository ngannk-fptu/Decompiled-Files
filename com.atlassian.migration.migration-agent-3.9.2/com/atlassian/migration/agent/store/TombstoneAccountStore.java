/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.TombstoneAccount;
import java.util.List;

public interface TombstoneAccountStore {
    public void save(TombstoneAccount var1);

    public List<TombstoneAccount> loadByUserkeys(List<String> var1);
}

