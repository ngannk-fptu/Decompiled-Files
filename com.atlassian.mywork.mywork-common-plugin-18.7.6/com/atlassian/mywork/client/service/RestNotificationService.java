/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.node.ObjectNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.mywork.client.service.ReliableRestService;
import com.atlassian.mywork.client.service.RemoteNotificationService;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.model.UpdateMetadata;
import com.atlassian.sal.api.user.UserKey;
import java.util.concurrent.Future;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestNotificationService
implements RemoteNotificationService {
    private static final Logger log = LoggerFactory.getLogger(RestNotificationService.class);
    private final ReliableRestService restService;

    public RestNotificationService(ReliableRestService restService) {
        this.restService = restService;
    }

    @Override
    public Future<Notification> createOrUpdate(String username, Notification notification) {
        Notification newNotification = new NotificationBuilder(notification).user(username).createNotification();
        return this.restService.post(username, "/rest/mywork/1/notification", newNotification);
    }

    @Override
    public int getCount(String username) {
        try {
            return Integer.parseInt(this.restService.get(username, "/rest/mywork/1/status/notification/new"));
        }
        catch (CredentialsRequiredException e) {
            return 1;
        }
    }

    @Override
    public void updateMetadata(String username, String globalId, ObjectNode condition, ObjectNode metadata) {
        this.restService.post(username, "/rest/mywork/1/notification/metadata", new UpdateMetadata(globalId, condition, metadata));
    }

    @Override
    public void setRead(UserKey userKey, String globalId, String action, ObjectNode condition) {
        log.warn("setRead via REST call is currently unsupported");
    }
}

