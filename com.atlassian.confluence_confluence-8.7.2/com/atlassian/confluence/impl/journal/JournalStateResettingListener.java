/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.event.events.admin.ImportFinishedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;

public class JournalStateResettingListener
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(JournalStateResettingListener.class);
    private final EventListenerRegistrar eventListenerRegistrar;
    private final JournalStateStore journalStateStore;

    public JournalStateResettingListener(EventListenerRegistrar eventListenerRegistrar, JournalStateStore journalStateStore) {
        this.eventListenerRegistrar = (EventListenerRegistrar)Preconditions.checkNotNull((Object)eventListenerRegistrar);
        this.journalStateStore = (JournalStateStore)Preconditions.checkNotNull((Object)journalStateStore);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventListenerRegistrar.register((Object)this);
    }

    @EventListener
    public void onEvent(ClusterEventWrapper eventWrapper) {
        ImportFinishedEvent event;
        if (eventWrapper.getEvent() instanceof ImportFinishedEvent && (event = (ImportFinishedEvent)eventWrapper.getEvent()).isSiteImport()) {
            log.info("Reset journal states in response to import event");
            try {
                this.journalStateStore.resetAllJournalStates();
            }
            catch (DataAccessException e) {
                log.error("Import failed: " + e.getMessage(), (Throwable)e);
            }
        }
    }

    public void destroy() throws Exception {
        this.eventListenerRegistrar.unregister((Object)this);
    }
}

