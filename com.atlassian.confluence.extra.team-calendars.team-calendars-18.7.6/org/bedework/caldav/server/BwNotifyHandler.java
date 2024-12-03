/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.caldav.util.notifications.eventreg.EventregCancelledNotificationType;
import org.bedework.caldav.util.notifications.eventreg.EventregRegisteredNotificationType;
import org.bedework.util.misc.Logged;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BwNotifyHandler
extends Logged {
    public void doNotify(CaldavBWIntf intf, RequestPars pars, HttpServletResponse resp) throws WebdavException {
        if (!pars.processXml()) {
            resp.setStatus(400);
            return;
        }
        try {
            SysIntf sysi = intf.getSysi();
            Element root = pars.getXmlDoc().getDocumentElement();
            if (XmlUtil.nodeMatches(root, BedeworkServerTags.eventregCancelled)) {
                this.doEventregCancel(root, sysi, resp);
                return;
            }
            if (XmlUtil.nodeMatches(root, BedeworkServerTags.eventregRegistered)) {
                this.doEventregReg(root, sysi, resp);
                return;
            }
            if (XmlUtil.nodeMatches(root, BedeworkServerTags.notifySubscribe)) {
                this.doNotifySubscribe(root, sysi, resp);
                return;
            }
            resp.setStatus(400);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void doEventregCancel(Node root, SysIntf sysi, HttpServletResponse resp) throws WebdavException {
        try {
            List<Element> els = XmlUtil.getElements(root);
            if (els.size() < 2) {
                resp.setStatus(400);
                return;
            }
            String href = this.mustHref(els.get(0), resp);
            if (href == null) {
                return;
            }
            String uid = this.mustUid(els.get(1), resp);
            if (uid == null) {
                return;
            }
            for (int index = 2; index < els.size(); ++index) {
                String principalHref = this.mustPrincipalHref(els.get(index), resp);
                if (principalHref == null) {
                    return;
                }
                EventregCancelledNotificationType ecnt = new EventregCancelledNotificationType();
                ecnt.setUid(uid);
                ecnt.setHref(href);
                ecnt.setPrincipalHref(principalHref);
                NotificationType note = new NotificationType();
                note.setNotification(ecnt);
                sysi.sendNotification(ecnt.getPrincipalHref(), note);
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void doEventregReg(Node root, SysIntf sysi, HttpServletResponse resp) throws WebdavException {
        try {
            List<Element> els;
            if (this.debug) {
                this.debug("enter doEventregReg");
            }
            if ((els = XmlUtil.getElements(root)).size() < 2) {
                resp.setStatus(400);
                return;
            }
            String href = this.mustHref(els.get(0), resp);
            if (href == null) {
                if (this.debug) {
                    this.debug("No href");
                }
                return;
            }
            String uid = this.mustUid(els.get(1), resp);
            if (uid == null) {
                if (this.debug) {
                    this.debug("No uid");
                }
                return;
            }
            Integer numTicketsRequested = this.mustInt(els.get(2), BedeworkServerTags.eventregNumTicketsRequested, resp);
            Integer numTickets = this.mustInt(els.get(3), BedeworkServerTags.eventregNumTickets, resp);
            if (numTickets == null) {
                if (this.debug) {
                    this.debug("No num tickets");
                }
                return;
            }
            String principalHref = this.mustPrincipalHref(els.get(4), resp);
            if (principalHref == null) {
                if (this.debug) {
                    this.debug("No principal href");
                }
                return;
            }
            if (this.debug) {
                this.debug("principal href=" + principalHref);
            }
            EventregRegisteredNotificationType ereg = new EventregRegisteredNotificationType();
            ereg.setUid(uid);
            ereg.setHref(href);
            ereg.setNumTicketsRequested(numTicketsRequested);
            ereg.setNumTickets(numTickets);
            ereg.setPrincipalHref(principalHref);
            NotificationType note = new NotificationType();
            note.setNotification(ereg);
            sysi.sendNotification(ereg.getPrincipalHref(), note);
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void doNotifySubscribe(Node root, SysIntf sysi, HttpServletResponse resp) throws WebdavException {
        try {
            List<Element> els = XmlUtil.getElements(root);
            if (els.size() < 3) {
                resp.setStatus(400);
                return;
            }
            String principalHref = this.mustPrincipalHref(els.get(0), resp);
            if (principalHref == null) {
                return;
            }
            String action = this.must(els.get(1), BedeworkServerTags.action, resp);
            if (action == null) {
                return;
            }
            ArrayList<String> emails = new ArrayList<String>();
            for (int i = 2; i < els.size(); ++i) {
                String email = this.must(els.get(i), BedeworkServerTags.email, resp);
                if (email == null) {
                    return;
                }
                emails.add(email);
            }
            if (!sysi.subscribeNotification(principalHref, action, emails)) {
                resp.setStatus(417);
            }
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private String mustHref(Element el, HttpServletResponse resp) throws WebdavException {
        return this.must(el, WebdavTags.href, resp);
    }

    private String mustUid(Element el, HttpServletResponse resp) throws WebdavException {
        return this.must(el, AppleServerTags.uid, resp);
    }

    private String must(Element el, QName tag, HttpServletResponse resp) throws WebdavException {
        try {
            if (!this.isElement(el, tag, resp)) {
                return null;
            }
            return XmlUtil.getElementContent(el);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private Integer mustInt(Element el, QName tag, HttpServletResponse resp) throws WebdavException {
        String val = this.must(el, tag, resp);
        if (val == null) {
            return null;
        }
        return Integer.parseInt(val);
    }

    private boolean isElement(Element el, QName tag, HttpServletResponse resp) throws WebdavException {
        try {
            if (!XmlUtil.nodeMatches(el, tag)) {
                resp.setStatus(400);
                return false;
            }
            return true;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private String mustPrincipalHref(Element el, HttpServletResponse resp) throws WebdavException {
        try {
            if (!XmlUtil.nodeMatches(el, WebdavTags.principalURL)) {
                resp.setStatus(400);
                return null;
            }
            Element chEl = XmlUtil.getOnlyElement(el);
            if (!XmlUtil.nodeMatches(chEl, WebdavTags.href)) {
                resp.setStatus(400);
                return null;
            }
            return XmlUtil.getElementContent(chEl);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

