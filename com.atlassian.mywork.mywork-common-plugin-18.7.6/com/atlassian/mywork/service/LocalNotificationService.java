/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.service;

import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationFilter;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.service.NotificationService;
import java.util.Date;
import java.util.List;

public interface LocalNotificationService
extends NotificationService {
    public Iterable<Notification> findAll(String var1);

    public Iterable<Notification> findAllWithCurrentUser(String var1, List<String> var2, Date var3);

    public List<Notification> findAllWithCurrentUser(boolean var1, int var2, int var3);

    public List<Notification> findAllWithCurrentUser(NotificationFilter var1, int var2, int var3);

    public Iterable<Notification> findAllAfter(String var1, long var2, long var4, int var6);

    public Iterable<Notification> findAllUnread(String var1);

    public Iterable<Notification> findAllUnread(String var1, String var2, String var3);

    public Notification find(String var1, long var2);

    public Iterable<Notification> find(String var1, String var2);

    public int count(String var1, String var2);

    public void invalidateCachedCounts();

    public void delete(String var1, long var2);

    public void deleteByGlobalId(String var1, String var2);

    public void deleteByGlobalId(String var1);

    public void deleteWithCurrentUser(NotificationFilter var1);

    public Task setStatus(String var1, long var2, Status var4);

    public void setLastRead(String var1, Long var2);

    public void setRead(String var1, List<Long> var2);

    public void setReadWithCurrentUser(NotificationFilter var1);
}

