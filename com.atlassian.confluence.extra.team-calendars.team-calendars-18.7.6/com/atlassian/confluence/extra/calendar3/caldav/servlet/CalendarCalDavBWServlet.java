/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.servlet;

import com.atlassian.confluence.extra.calendar3.caldav.servlet.AclMethodExt;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.CaldavDeleteMethodExt;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.CaldavProfindMethodExt;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.CaldavPutMethodExt;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.CaldavReportMethodExt;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.MkcolMethodExt;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.PropPatchMethodExt;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.CaldavBWServlet;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public final class CalendarCalDavBWServlet
extends CaldavBWServlet {
    private static final String DUMP_CONTENT_KEY = "teamcal.caldav.dumpContent";
    private final CaldavBWIntf caldavBWIntf;
    private final DarkFeatureManager darkFeatureManager;

    @Autowired
    public CalendarCalDavBWServlet(@Qualifier(value="calendarCaldavBWIntf") CaldavBWIntf caldavBWIntf, @ComponentImport DarkFeatureManager darkFeatureManager) {
        this.caldavBWIntf = caldavBWIntf;
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.dumpContent = this.darkFeatureManager.isFeatureEnabledForCurrentUser(DUMP_CONTENT_KEY);
    }

    @Override
    public WebdavNsIntf getNsIntf(HttpServletRequest req) throws WebdavException {
        this.caldavBWIntf.init(this, req, this.methods, this.dumpContent);
        return this.caldavBWIntf;
    }

    @Override
    protected void addMethods() {
        super.addMethods();
        this.methods.put("REPORT", new MethodBase.MethodInfo(CaldavReportMethodExt.class, false));
        this.methods.put("PROPFIND", new MethodBase.MethodInfo(CaldavProfindMethodExt.class, false));
        this.methods.put("PUT", new MethodBase.MethodInfo(CaldavPutMethodExt.class, false));
        this.methods.put("DELETE", new MethodBase.MethodInfo(CaldavDeleteMethodExt.class, false));
        this.methods.put("PROPPATCH", new MethodBase.MethodInfo(PropPatchMethodExt.class, false));
        this.methods.put("ACL", new MethodBase.MethodInfo(AclMethodExt.class, false));
        this.methods.put("MKCOL", new MethodBase.MethodInfo(MkcolMethodExt.class, true));
        this.methods.remove("POST");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String subCalendarId;
        String pathInfo = req.getPathInfo();
        String[] calendarIds = pathInfo.split("/");
        if (calendarIds.length >= 1 && StringUtils.countMatches((CharSequence)pathInfo, (CharSequence)(subCalendarId = calendarIds[calendarIds.length - 1])) > 1) {
            resp.sendError(500, "Incorrect request URL. Duplicate segment detected");
            return;
        }
        super.service(req, resp);
    }
}

