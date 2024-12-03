/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.jmx.export.notification;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanException;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBeanNotificationBroadcaster;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;
import org.springframework.util.Assert;

public class ModelMBeanNotificationPublisher
implements NotificationPublisher {
    private final ModelMBeanNotificationBroadcaster modelMBean;
    private final ObjectName objectName;
    private final Object managedResource;

    public ModelMBeanNotificationPublisher(ModelMBeanNotificationBroadcaster modelMBean, ObjectName objectName, Object managedResource) {
        Assert.notNull((Object)modelMBean, (String)"'modelMBean' must not be null");
        Assert.notNull((Object)objectName, (String)"'objectName' must not be null");
        Assert.notNull((Object)managedResource, (String)"'managedResource' must not be null");
        this.modelMBean = modelMBean;
        this.objectName = objectName;
        this.managedResource = managedResource;
    }

    @Override
    public void sendNotification(Notification notification) {
        Assert.notNull((Object)notification, (String)"Notification must not be null");
        this.replaceNotificationSourceIfNecessary(notification);
        try {
            if (notification instanceof AttributeChangeNotification) {
                this.modelMBean.sendAttributeChangeNotification((AttributeChangeNotification)notification);
            } else {
                this.modelMBean.sendNotification(notification);
            }
        }
        catch (MBeanException ex) {
            throw new UnableToSendNotificationException("Unable to send notification [" + notification + "]", ex);
        }
    }

    private void replaceNotificationSourceIfNecessary(Notification notification) {
        if (notification.getSource() == null || notification.getSource().equals(this.managedResource)) {
            notification.setSource(this.objectName);
        }
    }
}

