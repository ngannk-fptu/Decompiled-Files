/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import java.util.HashSet;
import java.util.Set;
import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;

public class BaseAttributeFilter
implements NotificationFilter {
    private static final long serialVersionUID = 1L;
    private Set<String> names = new HashSet<String>();

    public BaseAttributeFilter(String name) {
        if (name != null) {
            this.addAttribute(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addAttribute(String name) {
        Set<String> set = this.names;
        synchronized (set) {
            this.names.add(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Set<String> set = this.names;
        synchronized (set) {
            this.names.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getNames() {
        Set<String> set = this.names;
        synchronized (set) {
            return this.names.toArray(new String[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isNotificationEnabled(Notification notification) {
        if (notification == null) {
            return false;
        }
        if (!(notification instanceof AttributeChangeNotification)) {
            return false;
        }
        AttributeChangeNotification acn = (AttributeChangeNotification)notification;
        if (!"jmx.attribute.change".equals(acn.getType())) {
            return false;
        }
        Set<String> set = this.names;
        synchronized (set) {
            if (this.names.size() < 1) {
                return true;
            }
            return this.names.contains(acn.getAttributeName());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAttribute(String name) {
        Set<String> set = this.names;
        synchronized (set) {
            this.names.remove(name);
        }
    }
}

