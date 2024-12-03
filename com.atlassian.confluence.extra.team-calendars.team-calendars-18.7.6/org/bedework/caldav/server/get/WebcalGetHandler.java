/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server.get;

import java.util.ArrayList;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.CaldavComponentNode;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.get.GetHandler;
import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.ParseUtil;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.EntityTimeRangeFilter;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public class WebcalGetHandler
extends GetHandler {
    public WebcalGetHandler(CaldavBWIntf intf) {
        super(intf);
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp, RequestPars pars) throws WebdavException {
        try {
            String suffix;
            String calPath;
            CalDAVAuthProperties authp = this.getSysi().getAuthProperties();
            TimeRange tr = ParseUtil.getPeriod(req.getParameter("start"), req.getParameter("end"), 5, authp.getDefaultWebCalPeriod(), 5, authp.getMaxWebCalPeriod());
            if (tr == null) {
                resp.sendError(400, "Date/times");
                return;
            }
            if (pars.isWebcalGetAccept()) {
                calPath = pars.getResourceUri();
            } else {
                calPath = req.getParameter("calPath");
                if (calPath == null) {
                    resp.sendError(400, "No calPath");
                    return;
                }
                calPath = WebdavNsIntf.fixPath(calPath);
            }
            WebdavNsNode node = this.getNode(calPath, 1, 3);
            if (node == null || !node.getExists()) {
                resp.setStatus(404);
                return;
            }
            if (!node.isCollection()) {
                resp.sendError(400, "Not collection");
                return;
            }
            ArrayList<CalDAVEvent> evs = new ArrayList<CalDAVEvent>();
            EntityTimeRangeFilter etrf = new EntityTimeRangeFilter(null, 0, tr);
            Supplier<Object> filters = () -> etrf;
            for (WebdavNsNode child : this.getChildren(node, filters)) {
                if (!(child instanceof CaldavComponentNode)) continue;
                evs.add(((CaldavComponentNode)child).getEvent());
            }
            String acceptType = pars.getAcceptType();
            if (acceptType == null) {
                acceptType = this.getSysi().getDefaultContentType();
            }
            if (acceptType.equals("application/calendar+xml")) {
                resp.setContentType(acceptType);
                suffix = ".xcs";
            } else {
                resp.setContentType(acceptType + ";charset=utf-8");
                suffix = ".ics";
            }
            resp.setHeader("Content-Disposition", "Attachment; Filename=\"" + node.getDisplayname() + suffix + "\"");
            this.getSysi().writeCalendar(evs, SysIntf.MethodEmitted.publish, null, resp.getWriter(), acceptType);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

