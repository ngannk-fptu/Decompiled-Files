/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.event;

import java.io.Serializable;
import net.sf.ehcache.Element;

public class CacheEventNotificationMsg
implements Serializable {
    private final String fullyQualifiedEhcacheName;
    private final EventType toolkitEventType;
    private final Element element;

    public CacheEventNotificationMsg(String fullyQualifiedEhcacheName, EventType toolkitEventType, Element element) {
        this.fullyQualifiedEhcacheName = fullyQualifiedEhcacheName;
        this.toolkitEventType = toolkitEventType;
        this.element = element;
    }

    public String getFullyQualifiedEhcacheName() {
        return this.fullyQualifiedEhcacheName;
    }

    public EventType getToolkitEventType() {
        return this.toolkitEventType;
    }

    public Element getElement() {
        return this.element;
    }

    public String toString() {
        return "CacheEventNotificationMsg [fullyQualifiedEhcacheName=" + this.fullyQualifiedEhcacheName + ", toolkitEventtype=" + this.toolkitEventType + ", element=" + this.element + "]";
    }

    public static enum EventType {
        ELEMENT_REMOVED,
        ELEMENT_PUT,
        ELEMENT_UPDATED,
        ELEMENT_EXPIRED,
        ELEMENT_EVICTED,
        REMOVEALL;

    }
}

