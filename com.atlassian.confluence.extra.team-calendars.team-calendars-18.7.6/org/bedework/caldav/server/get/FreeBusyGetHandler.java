/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server.get;

import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.get.GetHandler;
import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.bedework.caldav.util.ParseUtil;
import org.bedework.caldav.util.TimeRange;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;

public class FreeBusyGetHandler
extends GetHandler {
    public FreeBusyGetHandler(CaldavBWIntf intf) {
        super(intf);
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp, RequestPars pars) throws WebdavException {
        try {
            String cua;
            String originator = null;
            if (this.getAccount() != null) {
                originator = this.getSysi().principalToCaladdr(this.getSysi().getPrincipal());
            }
            if ((cua = req.getParameter("cua")) == null) {
                String user = req.getParameter("user");
                if (user == null) {
                    if (this.getAccount() == null) {
                        resp.sendError(400, "Missing user/cua");
                        return;
                    }
                    user = this.getAccount();
                }
                cua = this.getSysi().principalToCaladdr(this.getSysi().getPrincipalForUser(user));
            }
            pars.setContentType("text/calendar;charset=utf-8");
            CalDAVAuthProperties authp = this.getSysi().getAuthProperties();
            TimeRange tr = ParseUtil.getPeriod(req.getParameter("start"), req.getParameter("end"), 5, authp.getDefaultFBPeriod(), 5, authp.getMaxFBPeriod());
            if (tr == null) {
                resp.sendError(400, "Date/times");
                return;
            }
            TreeSet<String> recipients = new TreeSet<String>();
            resp.setHeader("Content-Disposition", "Attachment; Filename=\"freebusy.ics\"");
            resp.setContentType("text/calendar;charset=utf-8");
            recipients.add(cua);
            this.getSysi().getSpecialFreeBusy(cua, recipients, originator, tr, resp.getWriter());
        }
        catch (WebdavForbidden wdf) {
            resp.setStatus(403);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

