/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server.get;

import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.get.GetHandler;
import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.tagdefs.IscheduleTags;
import org.bedework.webdav.servlet.shared.WebdavException;

public class IscheduleGetHandler
extends GetHandler {
    public IscheduleGetHandler(CaldavBWIntf intf) {
        super(intf);
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp, RequestPars pars) throws WebdavException {
        try {
            if (pars.getNoPrefixResourceUri().length() == 0) {
                String query = req.getParameter("action");
                if (Util.equalsString(query, "capabilities")) {
                    this.doCapabilities(resp);
                    return;
                }
                resp.sendError(400, "Bad request parameters");
            }
            if (pars.getNoPrefixResourceUri().startsWith("/domainkey/")) {
                String[] pe = pars.getNoPrefixResourceUri().split("/");
                if (pe.length < 3) {
                    resp.sendError(400, "Bad request parameters");
                    return;
                }
                this.makeDomainKey(resp, pe[1], pe[2]);
                return;
            }
            resp.sendError(403);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void makeDomainKey(HttpServletResponse resp, String domain, String service) throws WebdavException {
        try {
            byte[] key = this.intf.getSysi().getPublicKey(domain, service);
            if (key == null || key.length == 0) {
                resp.sendError(404);
                return;
            }
            resp.setContentType("text/plain");
            PrintWriter wtr = resp.getWriter();
            ((Writer)wtr).write("v=DKIM1;p=");
            ((Writer)wtr).write(new String(new Base64().encode(key)));
            ((Writer)wtr).close();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void doCapabilities(HttpServletResponse resp) throws WebdavException {
        try {
            this.startEmit(resp);
            this.openTag(IscheduleTags.queryResult);
            this.openTag(IscheduleTags.capabilities);
            this.property(IscheduleTags.serialNumber, String.valueOf(2));
            this.openTag(IscheduleTags.versions);
            this.property(IscheduleTags.version, "1");
            this.closeTag(IscheduleTags.versions);
            this.openTag(IscheduleTags.schedulingMessages);
            this.openTag(IscheduleTags.component, "name", "VEVENT");
            this.supportedMethod("REQUEST");
            this.supportedMethod("ADD");
            this.supportedMethod("REPLY");
            this.supportedMethod("CANCEL");
            this.closeTag(IscheduleTags.component);
            this.openTag(IscheduleTags.component, "name", "VTODO");
            this.supportedMethod("REQUEST");
            this.supportedMethod("ADD");
            this.supportedMethod("REPLY");
            this.supportedMethod("CANCEL");
            this.closeTag(IscheduleTags.component);
            this.openTag(IscheduleTags.component, "name", "VPOLL");
            this.supportedMethod("POLLSTATUS");
            this.supportedMethod("REQUEST");
            this.supportedMethod("ADD");
            this.supportedMethod("REPLY");
            this.supportedMethod("CANCEL");
            this.closeTag(IscheduleTags.component);
            this.openTag(IscheduleTags.component, "name", "VFREEBUSY");
            this.supportedMethod("REQUEST");
            this.closeTag(IscheduleTags.component);
            this.closeTag(IscheduleTags.schedulingMessages);
            this.openTag(IscheduleTags.calendarDataTypes);
            String[] calDataNames = new String[]{"content-type", "version"};
            String[] calDataVals = new String[]{"text/calendar", "2.0"};
            this.attrTag(IscheduleTags.calendarData, calDataNames, calDataVals);
            this.closeTag(IscheduleTags.calendarDataTypes);
            this.openTag(IscheduleTags.attachments);
            this.emptyTag(IscheduleTags.inline);
            this.emptyTag(IscheduleTags.external);
            this.closeTag(IscheduleTags.attachments);
            CalDAVAuthProperties authp = this.intf.getSysi().getAuthProperties();
            this.prop(IscheduleTags.maxContentLength, authp.getMaxUserEntitySize());
            this.prop(IscheduleTags.minDateTime, authp.getMinDateTime());
            this.prop(IscheduleTags.maxDateTime, authp.getMaxDateTime());
            this.prop(IscheduleTags.maxInstances, authp.getMaxInstances());
            this.prop(IscheduleTags.maxRecipients, authp.getMaxAttendeesPerInstance());
            this.prop(IscheduleTags.administrator, this.intf.getSysi().getSystemProperties().getAdminContact());
            this.closeTag(IscheduleTags.capabilities);
            this.closeTag(IscheduleTags.queryResult);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void prop(QName tag, Object val) throws WebdavException {
        if (val == null) {
            return;
        }
        this.property(tag, String.valueOf(val));
    }

    private void supportedMethod(String val) throws WebdavException {
        try {
            this.attrTag(IscheduleTags.method, "name", val);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void attrTag(QName tag, String attrName, String attrVal) throws WebdavException {
        try {
            this.xml.startTag(tag);
            this.xml.attribute(attrName, attrVal);
            this.xml.endEmptyTag();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void attrTag(QName tag, String[] attrNames, String[] attrVals) throws WebdavException {
        try {
            this.xml.startTag(tag);
            for (int i = 0; i < attrNames.length; ++i) {
                this.xml.attribute(attrNames[i], attrVals[i]);
            }
            this.xml.endEmptyTag();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

