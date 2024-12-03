/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package org.bedework.caldav.server;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.RequestParsExt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.get.FreeBusyGetHandler;
import org.bedework.caldav.server.get.GetHandler;
import org.bedework.caldav.server.get.IscheduleGetHandler;
import org.bedework.caldav.server.get.ServerInfoGetHandler;
import org.bedework.caldav.server.get.WebcalGetHandler;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.common.WebdavServlet;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="calendarCaldavBWIntf")
public final class CalendarCalDavBWIntf
extends CaldavBWIntf {
    private final SysIntf calendarSysIntfImpl;
    private final CalendarManager calendarManager;

    @Autowired
    public CalendarCalDavBWIntf(@Qualifier(value="calendarSysIntfImpl") SysIntf sysIntf, CalendarManager calendarManager) {
        this.calendarSysIntfImpl = sysIntf;
        this.calendarManager = calendarManager;
    }

    public CalendarManager getCalendarManager() {
        return this.calendarManager;
    }

    @Override
    public void init(WebdavServlet servlet, HttpServletRequest req, HashMap<String, MethodBase.MethodInfo> methods, boolean dumpContent) throws WebdavException {
        super.init(servlet, req, methods, dumpContent);
        boolean calWs = Boolean.parseBoolean(servlet.getInitParameter("calws"));
        boolean synchWs = Boolean.parseBoolean(servlet.getInitParameter("synchws"));
        boolean notifyWs = Boolean.parseBoolean(servlet.getInitParameter("notifyws"));
        boolean socketWs = Boolean.parseBoolean(servlet.getInitParameter("notifyws"));
        this.sysi = this.calendarSysIntfImpl;
        this.account = this.sysi.init(req, this.account, false, calWs, synchWs, notifyWs, socketWs, null);
    }

    @Override
    public String getResourceUri(HttpServletRequest req) throws WebdavException {
        String resourceUri = super.getResourceUri(req);
        return this.normalize(resourceUri);
    }

    @Override
    public String normalizeUri(String uri) throws WebdavException {
        String normalizeUri = super.normalizeUri(uri);
        return this.normalize(normalizeUri);
    }

    @Override
    public String getDavHeader(WebdavNsNode node) throws WebdavException {
        ArrayList<String> davHeaders = new ArrayList<String>(Arrays.asList("1", "3", "calendar-access"));
        if (this.account != null) {
            davHeaders.add("access-control");
            davHeaders.add("extended-mkcol");
        }
        return String.join((CharSequence)", ", davHeaders);
    }

    @Override
    public boolean specialUri(HttpServletRequest request, HttpServletResponse response, String resourceUri) throws WebdavException {
        RequestParsExt parameters = new RequestParsExt(request, this, resourceUri);
        GetHandler handler = null;
        if (parameters.isiSchedule()) {
            handler = new IscheduleGetHandler(this);
        } else if (parameters.isServerInfo()) {
            handler = new ServerInfoGetHandler(this);
        } else if (parameters.isFreeBusy()) {
            handler = new FreeBusyGetHandler(this);
        } else if (parameters.isWebcal()) {
            handler = new WebcalGetHandler(this);
        }
        if (handler == null) {
            return false;
        }
        ((GetHandler)handler).process(request, response, parameters);
        return true;
    }

    private String normalize(String resourceUri) {
        String icsExtention;
        String servletPath = this.getRequest().getServletPath();
        String normalizePath = resourceUri.replace("/+", "/");
        if (normalizePath.startsWith(servletPath)) {
            normalizePath = normalizePath.substring(servletPath.length());
        }
        if (normalizePath.endsWith(icsExtention = ".ics")) {
            normalizePath = normalizePath.substring(0, normalizePath.lastIndexOf(icsExtention));
        }
        return normalizePath;
    }
}

