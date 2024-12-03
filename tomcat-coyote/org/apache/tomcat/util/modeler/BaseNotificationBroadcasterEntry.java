/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;

class BaseNotificationBroadcasterEntry {
    public NotificationFilter filter = null;
    public Object handback = null;
    public NotificationListener listener = null;

    BaseNotificationBroadcasterEntry(NotificationListener listener, NotificationFilter filter, Object handback) {
        this.listener = listener;
        this.filter = filter;
        this.handback = handback;
    }
}

