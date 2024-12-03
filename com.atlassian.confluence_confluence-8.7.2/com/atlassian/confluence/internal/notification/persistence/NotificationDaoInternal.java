/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.notification.persistence;

import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.persistence.NotificationDao;

public interface NotificationDaoInternal
extends NotificationDao,
ObjectDaoInternal<Notification> {
}

