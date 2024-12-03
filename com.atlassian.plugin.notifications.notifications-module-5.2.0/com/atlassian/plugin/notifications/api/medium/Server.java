/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.medium.Group;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.NotificationException;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerConnectionException;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.List;

public interface Server {
    public ServerConfiguration getConfig();

    public ErrorCollection testConnection(I18nResolver var1);

    public void sendIndividualNotification(NotificationAddress var1, Message var2) throws NotificationException;

    public void sendGroupNotification(NotificationAddress var1, Message var2) throws NotificationException;

    public List<Group> getAvailableGroups(String var1) throws ServerConnectionException;

    public ErrorCollection validateGroup(I18nResolver var1, String var2);

    public void terminate();
}

