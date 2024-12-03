/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.mobile.service.executor;

import com.atlassian.confluence.plugins.mobile.activeobject.dao.PushNotificationDao;
import com.atlassian.confluence.plugins.mobile.activeobject.entity.PushNotificationAO;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationContent;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationResult;
import com.atlassian.confluence.plugins.mobile.remoteservice.PushNotificationClient;
import com.atlassian.confluence.plugins.mobile.util.MobileUtil;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushNotificationTask
implements Runnable {
    private Logger LOG = LoggerFactory.getLogger(PushNotificationTask.class);
    private final PushNotificationClient client;
    private final PushNotificationDao pushNotificationDao;
    private final List<PushNotificationContent> contents;

    public PushNotificationTask(PushNotificationClient client, PushNotificationDao pushNotificationDao, List<PushNotificationContent> contents) {
        this.client = client;
        this.pushNotificationDao = pushNotificationDao;
        this.contents = contents;
    }

    @Override
    public void run() {
        try {
            this.LOG.debug("Sending push notification");
            PushNotificationResult result = this.client.push(this.contents);
            List<PushNotificationResult.ResultItem> invalidPushes = result.getInvalidPushes();
            List<PushNotificationResult.ResultItem> newPushes = result.getNewPushes();
            if (!MobileUtil.isNullOrEmpty(invalidPushes)) {
                Set<String> ids = invalidPushes.stream().map(PushNotificationResult.ResultItem::getRegistrationId).collect(Collectors.toSet());
                this.pushNotificationDao.deleteByIds(ids);
                this.LOG.debug("Delete invalid notifications success");
            }
            if (!MobileUtil.isNullOrEmpty(newPushes)) {
                Map<String, String> newPushMap = newPushes.stream().collect(Collectors.toMap(PushNotificationResult.ResultItem::getRegistrationId, PushNotificationResult.ResultItem::getEndpoint));
                List<PushNotificationAO> notificationAOS = this.pushNotificationDao.findById(newPushMap.keySet());
                notificationAOS.stream().filter(Objects::nonNull).forEach(ao -> ao.setEndpoint((String)newPushMap.get(ao.getId())));
                this.pushNotificationDao.update(notificationAOS);
            }
        }
        catch (Exception e) {
            this.LOG.warn("Sending push notification is unsuccessful", (Throwable)e);
        }
    }
}

