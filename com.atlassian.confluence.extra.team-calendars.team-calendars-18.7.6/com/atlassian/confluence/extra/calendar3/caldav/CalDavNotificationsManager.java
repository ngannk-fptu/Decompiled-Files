/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.caldav.util.sharing.InviteReplyType;
import org.bedework.caldav.util.sharing.InviteType;
import org.bedework.caldav.util.sharing.ShareResultType;
import org.bedework.caldav.util.sharing.ShareType;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavNotificationsManager {
    public String getNotificationURL() throws WebdavException;

    public boolean subscribeNotification(String var1, String var2, List<String> var3) throws WebdavException;

    public boolean sendNotification(String var1, NotificationType var2) throws WebdavException;

    public void removeNotification(String var1, NotificationType var2) throws WebdavException;

    public List<NotificationType> getNotifications() throws WebdavException;

    public List<NotificationType> getNotifications(String var1, QName var2) throws WebdavException;

    public ShareResultType share(CalDAVCollection var1, ShareType var2) throws WebdavException;

    public String sharingReply(CalDAVCollection var1, InviteReplyType var2) throws WebdavException;

    public InviteType getInviteStatus(CalDAVCollection var1) throws WebdavException;
}

