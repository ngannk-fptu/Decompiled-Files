/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import java.util.ArrayList;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import org.apache.tomcat.util.modeler.BaseAttributeFilter;
import org.apache.tomcat.util.modeler.BaseNotificationBroadcasterEntry;

public class BaseNotificationBroadcaster
implements NotificationBroadcaster {
    protected ArrayList<BaseNotificationBroadcasterEntry> entries = new ArrayList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
        ArrayList<BaseNotificationBroadcasterEntry> arrayList = this.entries;
        synchronized (arrayList) {
            if (filter instanceof BaseAttributeFilter) {
                BaseAttributeFilter newFilter = (BaseAttributeFilter)filter;
                for (BaseNotificationBroadcasterEntry item : this.entries) {
                    if (item.listener != listener || item.filter == null || !(item.filter instanceof BaseAttributeFilter) || item.handback != handback) continue;
                    BaseAttributeFilter oldFilter = (BaseAttributeFilter)item.filter;
                    String[] newNames = newFilter.getNames();
                    String[] oldNames = oldFilter.getNames();
                    if (newNames.length == 0) {
                        oldFilter.clear();
                    } else if (oldNames.length != 0) {
                        for (String newName : newNames) {
                            oldFilter.addAttribute(newName);
                        }
                    }
                    return;
                }
            }
            this.entries.add(new BaseNotificationBroadcasterEntry(listener, filter, handback));
        }
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        ArrayList<BaseNotificationBroadcasterEntry> arrayList = this.entries;
        synchronized (arrayList) {
            this.entries.removeIf(item -> item.listener == listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendNotification(Notification notification) {
        ArrayList<BaseNotificationBroadcasterEntry> arrayList = this.entries;
        synchronized (arrayList) {
            for (BaseNotificationBroadcasterEntry item : this.entries) {
                if (item.filter != null && !item.filter.isNotificationEnabled(notification)) continue;
                item.listener.handleNotification(notification, item.handback);
            }
        }
    }
}

