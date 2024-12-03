/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.caldav.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.CaldavPostMethod;
import org.bedework.caldav.server.CaldavReportMethod;
import org.bedework.caldav.server.MkcalendarMethod;
import org.bedework.webdav.servlet.common.DeleteMethod;
import org.bedework.webdav.servlet.common.GetMethod;
import org.bedework.webdav.servlet.common.HeadMethod;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.common.OptionsMethod;
import org.bedework.webdav.servlet.common.PropFindMethod;
import org.bedework.webdav.servlet.common.PutMethod;
import org.bedework.webdav.servlet.common.WebdavServlet;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;

public class CaldavBWServlet
extends WebdavServlet
implements ServletContextListener {
    private boolean calWs;
    private boolean notifyWs;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.calWs = Boolean.parseBoolean(config.getInitParameter("calws"));
        this.notifyWs = Boolean.parseBoolean(config.getInitParameter("notifyws"));
        super.init(config);
    }

    @Override
    protected void addMethods() {
        if (this.notifyWs) {
            this.methods.clear();
            this.methods.put("DELETE", new MethodBase.MethodInfo(DeleteMethod.class, false));
            this.methods.put("GET", new MethodBase.MethodInfo(GetMethod.class, false));
            this.methods.put("HEAD", new MethodBase.MethodInfo(HeadMethod.class, false));
            this.methods.put("OPTIONS", new MethodBase.MethodInfo(OptionsMethod.class, false));
            this.methods.put("POST", new MethodBase.MethodInfo(CaldavPostMethod.class, false));
            this.methods.put("PROPFIND", new MethodBase.MethodInfo(PropFindMethod.class, false));
            this.methods.put("PUT", new MethodBase.MethodInfo(PutMethod.class, false));
            this.methods.put("REPORT", new MethodBase.MethodInfo(CaldavReportMethod.class, false));
            return;
        }
        if (this.calWs) {
            this.methods.clear();
            this.methods.put("DELETE", new MethodBase.MethodInfo(DeleteMethod.class, true));
            this.methods.put("GET", new MethodBase.MethodInfo(GetMethod.class, false));
            this.methods.put("HEAD", new MethodBase.MethodInfo(HeadMethod.class, false));
            this.methods.put("OPTIONS", new MethodBase.MethodInfo(OptionsMethod.class, false));
            this.methods.put("POST", new MethodBase.MethodInfo(CaldavPostMethod.class, false));
            this.methods.put("PUT", new MethodBase.MethodInfo(PutMethod.class, true));
            return;
        }
        super.addMethods();
        this.methods.put("MKCALENDAR", new MethodBase.MethodInfo(MkcalendarMethod.class, true));
        this.methods.put("POST", new MethodBase.MethodInfo(CaldavPostMethod.class, false));
        this.methods.put("REPORT", new MethodBase.MethodInfo(CaldavReportMethod.class, false));
    }

    @Override
    public WebdavNsIntf getNsIntf(HttpServletRequest req) throws WebdavException {
        CaldavBWIntf wi = new CaldavBWIntf();
        wi.init(this, req, this.methods, this.dumpContent);
        return wi;
    }

    public void contextInitialized(ServletContextEvent sce) {
        try {
            CaldavBWIntf.contextInitialized(sce);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            CaldavBWIntf.contextDestroyed(sce);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

