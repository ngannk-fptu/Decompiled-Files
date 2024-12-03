/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.model.Notification
 */
package com.atlassian.mywork.host.service;

import com.atlassian.mywork.model.Notification;

public interface NotificationRendererService {
    public Notification renderDescription(Notification var1);

    public Iterable<Notification> renderDescriptions(Iterable<Notification> var1);
}

