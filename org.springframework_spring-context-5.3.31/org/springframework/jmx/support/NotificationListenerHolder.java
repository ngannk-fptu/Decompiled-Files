/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.jmx.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class NotificationListenerHolder {
    @Nullable
    private NotificationListener notificationListener;
    @Nullable
    private NotificationFilter notificationFilter;
    @Nullable
    private Object handback;
    @Nullable
    protected Set<Object> mappedObjectNames;

    public void setNotificationListener(@Nullable NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    @Nullable
    public NotificationListener getNotificationListener() {
        return this.notificationListener;
    }

    public void setNotificationFilter(@Nullable NotificationFilter notificationFilter) {
        this.notificationFilter = notificationFilter;
    }

    @Nullable
    public NotificationFilter getNotificationFilter() {
        return this.notificationFilter;
    }

    public void setHandback(@Nullable Object handback) {
        this.handback = handback;
    }

    @Nullable
    public Object getHandback() {
        return this.handback;
    }

    public void setMappedObjectName(@Nullable Object mappedObjectName) {
        this.mappedObjectNames = mappedObjectName != null ? new LinkedHashSet<Object>(Collections.singleton(mappedObjectName)) : null;
    }

    public void setMappedObjectNames(Object ... mappedObjectNames) {
        this.mappedObjectNames = new LinkedHashSet<Object>(Arrays.asList(mappedObjectNames));
    }

    @Nullable
    public ObjectName[] getResolvedObjectNames() throws MalformedObjectNameException {
        if (this.mappedObjectNames == null) {
            return null;
        }
        ObjectName[] resolved = new ObjectName[this.mappedObjectNames.size()];
        int i = 0;
        for (Object objectName : this.mappedObjectNames) {
            resolved[i] = ObjectNameManager.getInstance(objectName);
            ++i;
        }
        return resolved;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NotificationListenerHolder)) {
            return false;
        }
        NotificationListenerHolder otherNlh = (NotificationListenerHolder)other;
        return ObjectUtils.nullSafeEquals((Object)this.notificationListener, (Object)otherNlh.notificationListener) && ObjectUtils.nullSafeEquals((Object)this.notificationFilter, (Object)otherNlh.notificationFilter) && ObjectUtils.nullSafeEquals((Object)this.handback, (Object)otherNlh.handback) && ObjectUtils.nullSafeEquals(this.mappedObjectNames, otherNlh.mappedObjectNames);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode((Object)this.notificationListener);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object)this.notificationFilter);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object)this.handback);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.mappedObjectNames);
        return hashCode;
    }
}

