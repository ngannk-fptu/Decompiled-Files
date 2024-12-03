/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationFilter
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.host.dao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationFilter;
import com.atlassian.mywork.model.Status;
import com.atlassian.sal.api.user.UserKey;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import org.codehaus.jackson.node.ObjectNode;

@Transactional
public interface NotificationDao {
    public Notification get(long var1);

    public Notification create(Notification var1);

    public Notification update(Notification var1);

    public void updateMetadata(String var1, String var2, ObjectNode var3, ObjectNode var4);

    public Notification delete(long var1);

    public Iterable<Notification> deleteByGlobalId(String var1);

    public Iterable<Notification> findAll(String var1);

    public Iterable<Notification> findAll(String var1, String var2, List<String> var3, Date var4);

    public List<Notification> findAll(String var1, boolean var2, int var3, int var4);

    public List<Notification> findAll(NotificationFilter var1, int var2, int var3);

    public Iterable<Notification> findAllUnread(String var1);

    public Iterable<Notification> findAllUnread(String var1, String var2, String var3);

    public Iterable<Notification> findAllAfter(String var1, long var2, long var4, int var6);

    public int countAllUnreadAfterOnlyIdsAction(String var1, long var2, int var4);

    public Iterable<Notification> findByGlobalId(String var1, String var2);

    public int countByGlobalId(String var1, String var2);

    public void markAllRead(String var1, long var2);

    public void setRead(String var1, List<Long> var2);

    public List<Long> setRead(UserKey var1, String var2, String var3, ObjectNode var4);

    public void setRead(NotificationFilter var1);

    public void setStatusByGlobalId(String var1, String var2, Status var3);

    public int deleteOldNotifications(int var1, boolean var2);

    public int deleteAll(@Nonnull com.atlassian.sal.usercompatibility.UserKey var1);

    public void delete(NotificationFilter var1);
}

