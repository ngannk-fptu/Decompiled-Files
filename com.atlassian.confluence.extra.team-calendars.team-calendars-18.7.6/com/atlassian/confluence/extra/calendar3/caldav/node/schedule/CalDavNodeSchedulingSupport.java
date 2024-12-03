/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node.schedule;

import java.util.Set;
import org.bedework.caldav.server.Organizer;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface CalDavNodeSchedulingSupport {
    public String getScheduleTag() throws WebdavException;

    public boolean getOrganizerSchedulingObject() throws WebdavException;

    public boolean getAttendeeSchedulingObject() throws WebdavException;

    public String getPrevScheduleTag() throws WebdavException;

    public void setOrganizer(Organizer var1) throws WebdavException;

    public Organizer getOrganizer() throws WebdavException;

    public void setOriginator(String var1) throws WebdavException;

    public void setRecipients(Set<String> var1) throws WebdavException;

    public Set<String> getRecipients() throws WebdavException;

    public void addRecipient(String var1) throws WebdavException;

    public void setScheduleMethod(int var1) throws WebdavException;

    public int getScheduleMethod() throws WebdavException;
}

