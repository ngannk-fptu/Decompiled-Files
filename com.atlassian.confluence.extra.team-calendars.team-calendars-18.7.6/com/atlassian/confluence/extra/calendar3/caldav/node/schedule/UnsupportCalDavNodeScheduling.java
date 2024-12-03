/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node.schedule;

import com.atlassian.confluence.extra.calendar3.caldav.node.schedule.CalDavNodeSchedulingSupport;
import java.util.Set;
import org.bedework.caldav.server.Organizer;
import org.bedework.webdav.servlet.shared.WebdavException;

public class UnsupportCalDavNodeScheduling
implements CalDavNodeSchedulingSupport {
    @Override
    public String getScheduleTag() throws WebdavException {
        return null;
    }

    @Override
    public boolean getOrganizerSchedulingObject() throws WebdavException {
        return false;
    }

    @Override
    public boolean getAttendeeSchedulingObject() throws WebdavException {
        return false;
    }

    @Override
    public String getPrevScheduleTag() throws WebdavException {
        return null;
    }

    @Override
    public void setOrganizer(Organizer val) throws WebdavException {
    }

    @Override
    public Organizer getOrganizer() throws WebdavException {
        return null;
    }

    @Override
    public void setOriginator(String val) throws WebdavException {
    }

    @Override
    public void setRecipients(Set<String> val) throws WebdavException {
    }

    @Override
    public Set<String> getRecipients() throws WebdavException {
        return null;
    }

    @Override
    public void addRecipient(String val) throws WebdavException {
    }

    @Override
    public void setScheduleMethod(int val) throws WebdavException {
    }

    @Override
    public int getScheduleMethod() throws WebdavException {
        return 0;
    }
}

