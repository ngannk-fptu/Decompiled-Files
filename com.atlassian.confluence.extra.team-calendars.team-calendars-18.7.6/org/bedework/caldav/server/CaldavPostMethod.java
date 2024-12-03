/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.james.jdkim.IscheduleDKIMVerifier
 *  org.apache.james.jdkim.api.BodyHasher
 *  org.apache.james.jdkim.api.Headers
 *  org.apache.james.jdkim.api.SignatureRecord
 *  org.apache.james.jdkim.exceptions.FailException
 */
package org.bedework.caldav.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.apache.james.jdkim.IscheduleDKIMVerifier;
import org.apache.james.jdkim.api.BodyHasher;
import org.apache.james.jdkim.api.Headers;
import org.apache.james.jdkim.api.SignatureRecord;
import org.apache.james.jdkim.exceptions.FailException;
import org.bedework.caldav.server.BwNotifyHandler;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.CaldavCalNode;
import org.bedework.caldav.server.CaldavReportMethod;
import org.bedework.caldav.server.IscheduleIn;
import org.bedework.caldav.server.Organizer;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.soap.calws.CalwsHandler;
import org.bedework.caldav.server.soap.synch.SynchwsHandler;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.sharing.InviteReplyType;
import org.bedework.caldav.util.sharing.ShareResultType;
import org.bedework.caldav.util.sharing.ShareType;
import org.bedework.caldav.util.sharing.SharedAsType;
import org.bedework.caldav.util.sharing.parse.Parser;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.IscheduleTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.common.PostMethod;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.w3c.dom.Element;

public class CaldavPostMethod
extends PostMethod {
    @Override
    public void init() {
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        CaldavBWIntf intf;
        RequestPars pars;
        if (this.debug) {
            this.debug("PostMethod: doMethod");
        }
        if ((pars = new RequestPars(req, intf = (CaldavBWIntf)this.getNsIntf(), this.getResourceUri(req))).isAddMember()) {
            this.handleAddMember(pars, resp);
            return;
        }
        if (pars.isSynchws()) {
            new SynchwsHandler(intf).processPost(req, resp, pars);
            return;
        }
        if (pars.isCalwsSoap()) {
            new CalwsHandler(intf).processPost(req, resp, pars);
            return;
        }
        if (pars.isNotifyws()) {
            new BwNotifyHandler().doNotify(intf, pars, resp);
            return;
        }
        if (!pars.isiSchedule()) {
            if (intf.getCalWS()) {
                this.doWsQuery(intf, pars, resp);
                return;
            }
            this.doCalDav(intf, pars, resp);
            return;
        }
        try {
            this.xml.addNs(new XmlEmit.NameSpace("urn:ietf:params:xml:ns:ischedule", "IS"), true);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        if (intf.getSysi().getPrincipal() == null) {
            intf.reAuth(req, "isched01", true, null);
        }
        this.doISchedule(intf, pars, resp);
    }

    private void doWsQuery(CaldavBWIntf intf, RequestPars pars, HttpServletResponse resp) throws WebdavException {
        if (!pars.getContentTypePars()[0].equals("text/xml")) {
            resp.setStatus(404);
            return;
        }
        CaldavReportMethod method = new CaldavReportMethod();
        method.init(intf, true);
        method.doMethod(pars.getReq(), resp);
    }

    private void doCalDav(CaldavBWIntf intf, RequestPars pars, HttpServletResponse resp) throws WebdavException {
        if (!pars.isAppXml()) {
            this.doSchedule(intf, pars, resp);
            return;
        }
        WebdavNsNode node = intf.getNode(pars.getResourceUri(), 1, 0, false);
        if (node == null) {
            resp.setStatus(404);
            return;
        }
        if (!node.isCollection()) {
            throw new WebdavForbidden("Not a collection");
        }
        CaldavCalNode calnode = (CaldavCalNode)node;
        CalDAVCollection col = (CalDAVCollection)calnode.getCollection(false);
        Element root = pars.getXmlDoc().getDocumentElement();
        SysIntf sysi = intf.getSysi();
        Parser parse = new Parser();
        if (XmlUtil.nodeMatches(root, AppleServerTags.inviteReply)) {
            InviteReplyType reply = parse.parseInviteReply(root);
            reply.setHostUrl(intf.getUri(reply.getHostUrl()));
            String newUri = sysi.sharingReply(col, reply);
            if (newUri == null) {
                resp.setStatus(200);
                return;
            }
            SharedAsType sa = new SharedAsType(newUri);
            resp.setStatus(200);
            resp.setContentType("text/xml;charset=utf-8");
            this.startEmit(resp);
            XmlEmit xml = intf.getXmlEmit();
            try {
                sa.toXml(xml);
            }
            catch (Throwable t) {
                throw new WebdavException(t);
            }
            return;
        }
        if (XmlUtil.nodeMatches(root, AppleServerTags.share)) {
            ShareType share = parse.parseShare(root);
            ShareResultType sr = sysi.share(col, share);
            if (sr.getBadSharees().isEmpty()) {
                resp.setStatus(200);
                return;
            }
            resp.setStatus(207);
            resp.setContentType("text/xml;charset=utf-8");
            this.startEmit(resp);
            XmlEmit xml = intf.getXmlEmit();
            try {
                xml.openTag(WebdavTags.multistatus);
                for (String s : sr.getGoodSharees()) {
                    xml.openTag(WebdavTags.response);
                    xml.property(WebdavTags.href, s);
                    this.addStatus(200, null);
                    xml.closeTag(WebdavTags.response);
                }
                for (String s : sr.getBadSharees()) {
                    xml.openTag(WebdavTags.response);
                    xml.property(WebdavTags.href, s);
                    this.addStatus(403, null);
                    xml.closeTag(WebdavTags.response);
                }
                xml.closeTag(WebdavTags.multistatus);
            }
            catch (Throwable t) {
                throw new WebdavException(t);
            }
        }
    }

    public void doSchedule(CaldavBWIntf intf, RequestPars pars, HttpServletResponse resp) throws WebdavException {
        SysIntf sysi = intf.getSysi();
        WebdavNsNode node = intf.getNode(pars.getResourceUri(), 1, 0, false);
        if (node == null) {
            resp.setStatus(404);
            return;
        }
        try {
            if (!(node instanceof CaldavCalNode)) {
                throw new WebdavException(403);
            }
            pars.setCol((CalDAVCollection)node.getCollection(false));
            if (pars.getCol().getCalType() != 3) {
                if (this.debug) {
                    this.debug("Not targetted at Outbox");
                }
                throw new WebdavException(405, "Not targetted at Outbox");
            }
            pars.setIcalendar(intf.getSysi().fromIcal(pars.getCol(), pars.getReader(), pars.getContentTypePars()[0], SysIntf.IcalResultType.OneComponent, false));
            if (!pars.getIcalendar().validItipMethodType()) {
                if (this.debug) {
                    this.debug("Bad method: " + String.valueOf(pars.getIcalendar().getMethodType()));
                }
                throw new WebdavForbidden(CaldavTags.validCalendarData, "Bad METHOD");
            }
            if (pars.getIcalendar().requestMethodType()) {
                Organizer organizer = pars.getIcalendar().getOrganizer();
                if (organizer == null) {
                    throw new WebdavForbidden(CaldavTags.organizerAllowed, "No access for scheduling");
                }
                String cn = organizer.getOrganizerUri();
                organizer.setOrganizerUri(sysi.getUrlHandler().unprefix(cn));
                CalPrincipalInfo organizerInfo = sysi.getCalPrincipalInfo(sysi.caladdrToPrincipal(cn));
                if (this.debug) {
                    if (organizerInfo == null) {
                        this.debug("organizerInfo for " + cn + " is NULL");
                    } else {
                        this.debug("organizer cn = " + cn + ", resourceUri = " + pars.getResourceUri() + ", outBoxPath = " + organizerInfo.outboxPath);
                    }
                }
                if (organizerInfo == null) {
                    throw new WebdavForbidden(CaldavTags.organizerAllowed, "No access for scheduling");
                }
                if (!pars.getResourceUri().equals(organizerInfo.outboxPath)) {
                    throw new WebdavForbidden(CaldavTags.organizerAllowed, "No access for scheduling");
                }
            }
            if (pars.getIcalendar().getComponentType() != IcalDefs.IcalComponentType.freebusy) {
                if (this.debug) {
                    this.debug("Unsupported component type: " + (Object)((Object)pars.getIcalendar().getComponentType()));
                }
                throw new WebdavForbidden("org.bedework.caldav.unsupported.component " + (Object)((Object)pars.getIcalendar().getComponentType()));
            }
            this.handleFreeBusy(sysi, pars, resp);
            this.flush();
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void doISchedule(CaldavBWIntf intf, RequestPars pars, HttpServletResponse resp) throws WebdavException {
        SysIntf sysi = intf.getSysi();
        try {
            if (!pars.getContentTypePars()[0].equals("text/calendar") && !pars.getContentTypePars()[0].equals("application/calendar+xml")) {
                if (this.debug) {
                    this.debug("Bad content type: " + pars.getContentType());
                }
                throw new WebdavForbidden(IscheduleTags.invalidCalendarDataType, "Bad content type: " + pars.getContentType());
            }
            IscheduleIn isi = pars.getIschedRequest();
            if (isi.getOriginator() == null) {
                if (this.debug) {
                    this.debug("No originator");
                }
                throw new WebdavForbidden(IscheduleTags.originatorMissing, "No originator");
            }
            if (isi.getRecipients().isEmpty()) {
                if (this.debug) {
                    this.debug("No recipient(s)");
                }
                throw new WebdavForbidden(IscheduleTags.recipientMissing, "No recipient(s)");
            }
            if (isi.getIScheduleMessageId() == null) {
                if (this.debug) {
                    this.debug("No message id");
                }
                throw new WebdavForbidden(IscheduleTags.recipientMissing, "No message id");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.streamCopy((InputStream)pars.getReq().getInputStream(), baos);
            this.validateHost(pars, new ByteArrayInputStream(baos.toByteArray()));
            pars.setIcalendar(sysi.fromIcal(pars.getCol(), new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())), pars.getContentTypePars()[0], SysIntf.IcalResultType.OneComponent, false));
            if (!pars.getIcalendar().validItipMethodType()) {
                if (this.debug) {
                    this.debug("Bad method: " + String.valueOf(pars.getIcalendar().getMethodType()));
                }
                throw new WebdavForbidden(IscheduleTags.invalidCalendarData, "Bad METHOD");
            }
            IcalDefs.IcalComponentType ctype = pars.getIcalendar().getComponentType();
            if (ctype == IcalDefs.IcalComponentType.event || ctype == IcalDefs.IcalComponentType.vpoll) {
                this.handleEvent(sysi, pars, resp);
            } else if (ctype == IcalDefs.IcalComponentType.freebusy) {
                this.handleFreeBusy(sysi, pars, resp);
            } else {
                if (this.debug) {
                    this.debug("Unsupported component type: " + (Object)((Object)ctype));
                }
                throw new WebdavForbidden("org.bedework.caldav.unsupported.component " + (Object)((Object)ctype));
            }
            this.flush();
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void validateHost(RequestPars pars, InputStream content) throws WebdavException {
        IscheduleIn isi = pars.getIschedRequest();
        SignatureRecord sig = isi.getDkimSignature();
        if (sig == null) {
            this.warn("Unchecked host - no dkim signature:");
            return;
        }
        try {
            IscheduleDKIMVerifier verifier = new IscheduleDKIMVerifier();
            BodyHasher bh = verifier.newBodyHasher((Headers)isi);
            if (bh != null) {
                OutputStream os = bh.getOutputStream();
                this.streamCopy(content, os);
            }
            verifier.verify(bh);
        }
        catch (IOException e) {
            throw new WebdavException(e);
        }
        catch (FailException e) {
            if (this.debug) {
                this.error(e);
            }
            throw new WebdavForbidden(IscheduleTags.verificationFailed);
        }
    }

    private void handleEvent(SysIntf intf, RequestPars pars, HttpServletResponse resp) throws WebdavException {
        CalDAVEvent ev = pars.getIcalendar().getEvent();
        if (pars.getIschedRequest().getRecipients() != null) {
            for (String r : pars.getIschedRequest().getRecipients()) {
                ev.addRecipient(r);
            }
        }
        ev.setScheduleMethod(pars.getIcalendar().getMethodType());
        this.validateOriginator(pars, ev);
        Collection<SysIntf.SchedRecipientResult> srrs = intf.schedule(ev);
        resp.setStatus(200);
        resp.setContentType("text/xml;charset=utf-8");
        resp.addHeader("iSchedule-Capabilities", String.valueOf(2));
        this.startEmit(resp);
        this.openTag(IscheduleTags.scheduleResponse);
        for (SysIntf.SchedRecipientResult srr : srrs) {
            this.openTag(IscheduleTags.response);
            this.property(IscheduleTags.recipient, srr.recipient);
            this.setReqstat(srr.status, true);
            this.closeTag(IscheduleTags.response);
        }
        this.closeTag(IscheduleTags.scheduleResponse);
    }

    private void handleFreeBusy(SysIntf intf, RequestPars pars, HttpServletResponse resp) throws WebdavException {
        QName calendarDataTag;
        QName recipientTag;
        QName responseTag;
        QName sresponseTag;
        CalDAVEvent ev = pars.getIcalendar().getEvent();
        if (pars.isiSchedule()) {
            ev.setRecipients(pars.getIschedRequest().getRecipients());
        } else {
            ev.setRecipients(ev.getAttendeeUris());
        }
        ev.setScheduleMethod(pars.getIcalendar().getMethodType());
        this.validateOriginator(pars, ev);
        Collection<SysIntf.SchedRecipientResult> srrs = intf.requestFreeBusy(ev, true);
        resp.setStatus(200);
        resp.setContentType("application/xml;charset=utf-8");
        resp.addHeader("iSchedule-Capabilities", String.valueOf(2));
        this.startEmit(resp);
        if (pars.isiSchedule()) {
            sresponseTag = IscheduleTags.scheduleResponse;
            responseTag = IscheduleTags.response;
            recipientTag = IscheduleTags.recipient;
            calendarDataTag = IscheduleTags.calendarData;
        } else {
            sresponseTag = CaldavTags.scheduleResponse;
            responseTag = CaldavTags.response;
            recipientTag = CaldavTags.recipient;
            calendarDataTag = CaldavTags.calendarData;
        }
        this.openTag(sresponseTag);
        for (SysIntf.SchedRecipientResult srr : srrs) {
            this.openTag(responseTag);
            if (pars.isiSchedule()) {
                this.property(recipientTag, srr.recipient);
            } else {
                this.openTag(recipientTag);
                this.property(WebdavTags.href, srr.recipient);
                this.closeTag(recipientTag);
            }
            this.setReqstat(srr.status, pars.isiSchedule());
            CalDAVEvent rfb = srr.freeBusy;
            if (rfb != null) {
                rfb.setOrganizer(pars.getIcalendar().getOrganizer());
                try {
                    this.cdataProperty(calendarDataTag, "content-type", pars.getContentType(), rfb.toIcalString(3, pars.getContentTypePars()[0]));
                }
                catch (Throwable t) {
                    if (this.debug) {
                        this.error(t);
                    }
                    throw new WebdavException(t);
                }
            }
            this.closeTag(responseTag);
        }
        this.closeTag(sresponseTag);
    }

    private void validateOriginator(RequestPars pars, CalDAVEvent ev) throws WebdavException {
        int meth = ev.getScheduleMethod();
        if (meth == 1) {
            return;
        }
        boolean matchOrganizer = meth == 4 || meth == 5 || meth == 9 || meth == 8;
        boolean request = meth == 2;
        Organizer org = ev.getOrganizer();
        if (org == null) {
            throw new WebdavBadRequest(IscheduleTags.invalidCalendarData, "Missing organizer");
        }
        if (pars.isiSchedule()) {
            String origUrl = pars.getIschedRequest().getOriginator();
            boolean matchAttendee = true;
            if (matchOrganizer || request) {
                if (!origUrl.equals(org.getOrganizerUri())) {
                    if (!request) {
                        throw new WebdavBadRequest(IscheduleTags.invalidCalendarData, "Organizer/originator mismatch");
                    }
                } else {
                    matchAttendee = false;
                }
            }
            ev.setOriginator(origUrl);
            if (matchAttendee) {
                Set<String> attUris = ev.getAttendeeUris();
                if (attUris.size() != 1) {
                    throw new WebdavBadRequest(IscheduleTags.invalidCalendarData, "Attendee/originator mismatch");
                }
                if (!attUris.contains(origUrl)) {
                    throw new WebdavBadRequest(IscheduleTags.invalidCalendarData, "Attendee/originator mismatch");
                }
            }
        } else {
            ev.setOriginator(org.getOrganizerUri());
        }
    }

    private void setReqstat(int status, boolean iSchedule) throws WebdavException {
        String reqstat;
        if (status == 2) {
            reqstat = "1.0;Deferred";
        } else if (status == 1) {
            if (iSchedule) {
                this.propertyTagVal(WebdavTags.error, IscheduleTags.recipientPermissions);
            } else {
                this.propertyTagVal(WebdavTags.error, CaldavTags.recipientPermissions);
            }
            reqstat = "4.2;No Access";
        } else {
            reqstat = status == -1 ? "3.7;Invalid User:" : (status == 4 ? "5.1;Unavailable" : "2.0;Success");
        }
        if (iSchedule) {
            this.property(IscheduleTags.requestStatus, reqstat);
        } else {
            this.property(CaldavTags.requestStatus, reqstat);
        }
    }

    private void streamCopy(InputStream in, OutputStream out) throws IOException {
        int read;
        byte[] buffer = new byte[2048];
        while ((read = in.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }
}

