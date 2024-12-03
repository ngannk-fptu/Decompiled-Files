/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import javax.servlet.http.HttpServletRequest;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WebdavException;

public class CalendarUrlHandler
extends UrlHandler {
    public String CALDAL_URL = "/plugins/servlet/team-calendars/caldav/";

    public CalendarUrlHandler(HttpServletRequest req, boolean relative) throws WebdavException {
        super(req, relative);
    }
}

