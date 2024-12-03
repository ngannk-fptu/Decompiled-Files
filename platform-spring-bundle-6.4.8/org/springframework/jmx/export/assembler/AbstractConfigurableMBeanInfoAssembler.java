/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler;
import org.springframework.jmx.export.metadata.JmxMetadataUtils;
import org.springframework.jmx.export.metadata.ManagedNotification;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public abstract class AbstractConfigurableMBeanInfoAssembler
extends AbstractReflectiveMBeanInfoAssembler {
    @Nullable
    private ModelMBeanNotificationInfo[] notificationInfos;
    private final Map<String, ModelMBeanNotificationInfo[]> notificationInfoMappings = new HashMap<String, ModelMBeanNotificationInfo[]>();

    public void setNotificationInfos(ManagedNotification[] notificationInfos) {
        ModelMBeanNotificationInfo[] infos = new ModelMBeanNotificationInfo[notificationInfos.length];
        for (int i2 = 0; i2 < notificationInfos.length; ++i2) {
            ManagedNotification notificationInfo = notificationInfos[i2];
            infos[i2] = JmxMetadataUtils.convertToModelMBeanNotificationInfo(notificationInfo);
        }
        this.notificationInfos = infos;
    }

    public void setNotificationInfoMappings(Map<String, Object> notificationInfoMappings) {
        notificationInfoMappings.forEach((beanKey, result) -> this.notificationInfoMappings.put((String)beanKey, this.extractNotificationMetadata(result)));
    }

    @Override
    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) {
        ModelMBeanNotificationInfo[] result = null;
        if (StringUtils.hasText(beanKey)) {
            result = this.notificationInfoMappings.get(beanKey);
        }
        if (result == null) {
            result = this.notificationInfos;
        }
        return result != null ? result : new ModelMBeanNotificationInfo[]{};
    }

    private ModelMBeanNotificationInfo[] extractNotificationMetadata(Object mapValue) {
        if (mapValue instanceof ManagedNotification) {
            ManagedNotification mn = (ManagedNotification)mapValue;
            return new ModelMBeanNotificationInfo[]{JmxMetadataUtils.convertToModelMBeanNotificationInfo(mn)};
        }
        if (mapValue instanceof Collection) {
            Collection col = (Collection)mapValue;
            ArrayList<ModelMBeanNotificationInfo> result = new ArrayList<ModelMBeanNotificationInfo>();
            for (Object colValue : col) {
                if (!(colValue instanceof ManagedNotification)) {
                    throw new IllegalArgumentException("Property 'notificationInfoMappings' only accepts ManagedNotifications for Map values");
                }
                ManagedNotification mn = (ManagedNotification)colValue;
                result.add(JmxMetadataUtils.convertToModelMBeanNotificationInfo(mn));
            }
            return result.toArray(new ModelMBeanNotificationInfo[0]);
        }
        throw new IllegalArgumentException("Property 'notificationInfoMappings' only accepts ManagedNotifications for Map values");
    }
}

