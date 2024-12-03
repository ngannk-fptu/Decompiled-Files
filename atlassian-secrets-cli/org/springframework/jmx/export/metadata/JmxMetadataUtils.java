/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.metadata;

import javax.management.modelmbean.ModelMBeanNotificationInfo;
import org.springframework.jmx.export.metadata.ManagedNotification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class JmxMetadataUtils {
    public static ModelMBeanNotificationInfo convertToModelMBeanNotificationInfo(ManagedNotification notificationInfo) {
        Object[] notifTypes = notificationInfo.getNotificationTypes();
        if (ObjectUtils.isEmpty(notifTypes)) {
            throw new IllegalArgumentException("Must specify at least one notification type");
        }
        String name = notificationInfo.getName();
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Must specify notification name");
        }
        String description = notificationInfo.getDescription();
        return new ModelMBeanNotificationInfo((String[])notifTypes, name, description);
    }
}

