/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.events.ToolkitNotificationEvent
 *  org.terracotta.toolkit.events.ToolkitNotificationListener
 *  org.terracotta.toolkit.events.ToolkitNotifier
 */
package org.terracotta.modules.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.event.CacheEventNotificationMsg;
import org.terracotta.modules.ehcache.event.ClusteredEventReplicatorFactory;
import org.terracotta.toolkit.events.ToolkitNotificationEvent;
import org.terracotta.toolkit.events.ToolkitNotificationListener;
import org.terracotta.toolkit.events.ToolkitNotifier;

public class ClusteredEventReplicator
implements CacheEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(ClusteredEventReplicator.class);
    private final ToolkitNotifier<CacheEventNotificationMsg> toolkitNotifier;
    private final String fullyQualifiedEhcacheName;
    private final Ehcache ecache;
    private final ClusteredEventReplicatorFactory factory;
    private final ToolkitListener toolkitListener;

    public ClusteredEventReplicator(Ehcache cache, String fullyQualifiedEhcacheName, ToolkitNotifier<CacheEventNotificationMsg> toolkitNotifier, ClusteredEventReplicatorFactory factory) {
        this.fullyQualifiedEhcacheName = fullyQualifiedEhcacheName;
        this.ecache = cache;
        this.toolkitNotifier = toolkitNotifier;
        this.toolkitListener = new ToolkitListener();
        this.toolkitNotifier.addNotificationListener((ToolkitNotificationListener)this.toolkitListener);
        this.factory = factory;
    }

    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        this.sendEvent(CacheEventNotificationMsg.EventType.ELEMENT_REMOVED, element);
    }

    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        this.sendEvent(CacheEventNotificationMsg.EventType.ELEMENT_PUT, element);
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        this.sendEvent(CacheEventNotificationMsg.EventType.ELEMENT_UPDATED, element);
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
        this.sendEvent(CacheEventNotificationMsg.EventType.ELEMENT_EXPIRED, element);
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
        this.sendEvent(CacheEventNotificationMsg.EventType.ELEMENT_EVICTED, element);
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
        this.sendEvent(CacheEventNotificationMsg.EventType.REMOVEALL, null);
    }

    @Override
    public void dispose() {
        this.toolkitNotifier.removeNotificationListener((ToolkitNotificationListener)this.toolkitListener);
        this.factory.disposeClusteredEventReplicator(this.fullyQualifiedEhcacheName);
    }

    @Override
    public ClusteredEventReplicator clone() throws CloneNotSupportedException {
        return (ClusteredEventReplicator)super.clone();
    }

    private void sendEvent(CacheEventNotificationMsg.EventType eventType, Element element) {
        this.toolkitNotifier.notifyListeners((Object)new CacheEventNotificationMsg(this.fullyQualifiedEhcacheName, eventType, element));
    }

    private class ToolkitListener
    implements ToolkitNotificationListener {
        private ToolkitListener() {
        }

        public void onNotification(ToolkitNotificationEvent event) {
            if (this.shouldProcessNotification(event)) {
                this.processEventNotification((CacheEventNotificationMsg)event.getMessage());
            } else {
                LOG.warn("Ignoring uninterested notification - " + event);
            }
        }

        private void processEventNotification(CacheEventNotificationMsg msg) {
            RegisteredEventListeners notificationService = ClusteredEventReplicator.this.ecache.getCacheEventNotificationService();
            switch (msg.getToolkitEventType()) {
                case ELEMENT_REMOVED: {
                    notificationService.notifyElementRemoved(msg.getElement(), true);
                    break;
                }
                case ELEMENT_PUT: {
                    notificationService.notifyElementPut(msg.getElement(), true);
                    break;
                }
                case ELEMENT_UPDATED: {
                    notificationService.notifyElementUpdated(msg.getElement(), true);
                    break;
                }
                case ELEMENT_EXPIRED: {
                    notificationService.notifyElementExpiry(msg.getElement(), true);
                    break;
                }
                case ELEMENT_EVICTED: {
                    notificationService.notifyElementEvicted(msg.getElement(), true);
                    break;
                }
                case REMOVEALL: {
                    notificationService.notifyRemoveAll(true);
                }
            }
        }

        private boolean shouldProcessNotification(ToolkitNotificationEvent event) {
            return event.getMessage() instanceof CacheEventNotificationMsg && ((CacheEventNotificationMsg)event.getMessage()).getFullyQualifiedEhcacheName().equals(ClusteredEventReplicator.this.fullyQualifiedEhcacheName);
        }
    }
}

