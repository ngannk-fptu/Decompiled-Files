/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavNotificationsManager;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.caldav.util.sharing.InviteReplyType;
import org.bedework.caldav.util.sharing.InviteType;
import org.bedework.caldav.util.sharing.ShareResultType;
import org.bedework.caldav.util.sharing.ShareType;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.springframework.stereotype.Component;

@Component(value="calDavNotificationsManager")
public class DefaultCalDavNotificationsManager
implements CalDavNotificationsManager {
    @Override
    public String getNotificationURL() {
        return null;
    }

    @Override
    public boolean subscribeNotification(String principalHref, String action, List<String> emails) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean sendNotification(String href, NotificationType val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNotification(String href, NotificationType val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NotificationType> getNotifications() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NotificationType> getNotifications(String href, QName type) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShareResultType share(CalDAVCollection col, ShareType share) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String sharingReply(CalDAVCollection col, InviteReplyType reply) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InviteType getInviteStatus(CalDAVCollection collection) {
        return null;
    }
}

