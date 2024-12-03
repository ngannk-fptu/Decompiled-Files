/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.manager.PluginPersistentState
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

@Deprecated
public class RestorePluginStateStoreTransactionCallbackDecorator<T>
implements TransactionCallback<T> {
    private static final Logger log = LoggerFactory.getLogger(RestorePluginStateStoreTransactionCallbackDecorator.class);
    private final TransactionCallback<T> delegate;
    private final PluginPersistentStateStore pluginStateStore;

    public RestorePluginStateStoreTransactionCallbackDecorator(PluginPersistentStateStore pluginStateStore, TransactionCallback<T> delegate) {
        this.pluginStateStore = pluginStateStore;
        this.delegate = delegate;
    }

    public T doInTransaction(TransactionStatus status) {
        log.info("Recording plugin states");
        PluginPersistentState before = this.pluginStateStore.load();
        Object result = this.delegate.doInTransaction(status);
        log.info("Restoring plugin states");
        PluginPersistentState after = this.pluginStateStore.load();
        this.pluginStateStore.save(before);
        if (log.isInfoEnabled()) {
            log.info("Dropped persisted plugin states {} and restored {}.", (Object)ToStringBuilder.reflectionToString((Object)after.getStatesMap().entrySet().toArray(), (ToStringStyle)ToStringStyle.SIMPLE_STYLE), (Object)ToStringBuilder.reflectionToString((Object)before.getStatesMap().entrySet().toArray(), (ToStringStyle)ToStringStyle.SIMPLE_STYLE));
        }
        return (T)result;
    }
}

