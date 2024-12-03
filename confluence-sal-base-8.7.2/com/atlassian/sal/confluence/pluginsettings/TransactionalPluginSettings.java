/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Permission
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 */
package com.atlassian.sal.confluence.pluginsettings;

import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettings;

public class TransactionalPluginSettings
implements PluginSettings {
    private final PluginSettings delegate;
    private final TransactionalHostContextAccessor hostContextAccessor;

    public TransactionalPluginSettings(PluginSettings delegate, TransactionalHostContextAccessor hostContextAccessor) {
        this.delegate = delegate;
        this.hostContextAccessor = hostContextAccessor;
    }

    public Object get(String key) {
        return this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Permission.READ_ONLY, () -> this.delegate.get(key));
    }

    public Object put(String key, Object value) {
        return this.hostContextAccessor.doInTransaction(() -> this.delegate.put(key, value));
    }

    public Object remove(String key) {
        return this.hostContextAccessor.doInTransaction(() -> this.delegate.remove(key));
    }
}

