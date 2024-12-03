/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.jmx.export;

import javax.management.NotificationListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.support.NotificationListenerHolder;
import org.springframework.util.Assert;

public class NotificationListenerBean
extends NotificationListenerHolder
implements InitializingBean {
    public NotificationListenerBean() {
    }

    public NotificationListenerBean(NotificationListener notificationListener) {
        Assert.notNull((Object)notificationListener, (String)"NotificationListener must not be null");
        this.setNotificationListener(notificationListener);
    }

    public void afterPropertiesSet() {
        if (this.getNotificationListener() == null) {
            throw new IllegalArgumentException("Property 'notificationListener' is required");
        }
    }

    void replaceObjectName(Object originalName, Object newName) {
        if (this.mappedObjectNames != null && this.mappedObjectNames.contains(originalName)) {
            this.mappedObjectNames.remove(originalName);
            this.mappedObjectNames.add(newName);
        }
    }
}

