/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.MBeanNotificationInfo;
import org.apache.tomcat.util.modeler.FeatureInfo;

public class NotificationInfo
extends FeatureInfo {
    private static final long serialVersionUID = -6319885418912650856L;
    transient MBeanNotificationInfo info = null;
    protected String[] notifTypes = new String[0];
    protected final ReadWriteLock notifTypesLock = new ReentrantReadWriteLock();

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        this.info = null;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.info = null;
    }

    public String[] getNotifTypes() {
        Lock readLock = this.notifTypesLock.readLock();
        readLock.lock();
        try {
            String[] stringArray = this.notifTypes;
            return stringArray;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addNotifType(String notifType) {
        Lock writeLock = this.notifTypesLock.writeLock();
        writeLock.lock();
        try {
            String[] results = new String[this.notifTypes.length + 1];
            System.arraycopy(this.notifTypes, 0, results, 0, this.notifTypes.length);
            results[this.notifTypes.length] = notifType;
            this.notifTypes = results;
            this.info = null;
        }
        finally {
            writeLock.unlock();
        }
    }

    public MBeanNotificationInfo createNotificationInfo() {
        if (this.info != null) {
            return this.info;
        }
        this.info = new MBeanNotificationInfo(this.getNotifTypes(), this.getName(), this.getDescription());
        return this.info;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NotificationInfo[");
        sb.append("name=");
        sb.append(this.name);
        sb.append(", description=");
        sb.append(this.description);
        sb.append(", notifTypes=");
        Lock readLock = this.notifTypesLock.readLock();
        readLock.lock();
        try {
            sb.append(this.notifTypes.length);
        }
        finally {
            readLock.unlock();
        }
        sb.append(']');
        return sb.toString();
    }
}

