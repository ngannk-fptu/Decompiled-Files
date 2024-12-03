/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.hibernate.management.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.StandardMBean;

public abstract class BaseEmitterBean
extends StandardMBean
implements NotificationEmitter {
    protected final Emitter emitter = new Emitter();
    protected final AtomicLong sequenceNumber = new AtomicLong();
    private final List<NotificationListener> notificationListeners = new CopyOnWriteArrayList<NotificationListener>();

    protected <T> BaseEmitterBean(Class<T> mbeanInterface) throws NotCompliantMBeanException {
        super(mbeanInterface);
    }

    public void sendNotification(String eventType) {
        this.sendNotification(eventType, null, null);
    }

    public void sendNotification(String eventType, Object data) {
        this.sendNotification(eventType, data, null);
    }

    public void sendNotification(String eventType, Object data, String msg) {
        Notification notif = new Notification(eventType, this, this.sequenceNumber.incrementAndGet(), System.currentTimeMillis(), msg);
        if (data != null) {
            notif.setUserData(data);
        }
        this.emitter.sendNotification(notif);
    }

    public final void dispose() {
        this.doDispose();
        this.removeAllNotificationListeners();
    }

    protected abstract void doDispose();

    @Override
    public void addNotificationListener(NotificationListener notif, NotificationFilter filter, Object callBack) {
        this.emitter.addNotificationListener(notif, filter, callBack);
        this.notificationListeners.add(notif);
    }

    private void removeAllNotificationListeners() {
        for (NotificationListener listener : this.notificationListeners) {
            try {
                this.emitter.removeNotificationListener(listener);
            }
            catch (ListenerNotFoundException listenerNotFoundException) {}
        }
        this.notificationListeners.clear();
    }

    @Override
    public abstract MBeanNotificationInfo[] getNotificationInfo();

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        this.emitter.removeNotificationListener(listener);
        this.notificationListeners.remove(listener);
    }

    @Override
    public void removeNotificationListener(NotificationListener notif, NotificationFilter filter, Object callBack) throws ListenerNotFoundException {
        this.emitter.removeNotificationListener(notif, filter, callBack);
        this.notificationListeners.remove(notif);
    }

    private class Emitter
    extends NotificationBroadcasterSupport {
        private Emitter() {
        }

        @Override
        public MBeanNotificationInfo[] getNotificationInfo() {
            return BaseEmitterBean.this.getNotificationInfo();
        }
    }
}

