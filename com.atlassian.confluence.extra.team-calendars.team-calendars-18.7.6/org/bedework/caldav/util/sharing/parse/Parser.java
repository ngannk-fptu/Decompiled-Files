/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing.parse;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.parse.BaseNotificationParser;
import org.bedework.caldav.util.sharing.AccessType;
import org.bedework.caldav.util.sharing.InviteNotificationType;
import org.bedework.caldav.util.sharing.InviteReplyType;
import org.bedework.caldav.util.sharing.InviteType;
import org.bedework.caldav.util.sharing.OrganizerType;
import org.bedework.caldav.util.sharing.RemoveType;
import org.bedework.caldav.util.sharing.SetType;
import org.bedework.caldav.util.sharing.ShareType;
import org.bedework.caldav.util.sharing.UserType;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Parser {
    public static final QName accessTag = AppleServerTags.access;
    public static final QName commonNameTag = AppleServerTags.commonName;
    public static final QName compTag = CaldavTags.comp;
    public static final QName firstNameTag = AppleServerTags.firstName;
    public static final QName lastNameTag = AppleServerTags.lastName;
    public static final QName hosturlTag = AppleServerTags.hosturl;
    public static final QName hrefTag = WebdavTags.href;
    public static final QName inReplyToTag = AppleServerTags.inReplyTo;
    public static final QName inviteTag = AppleServerTags.invite;
    public static final QName inviteAcceptedTag = AppleServerTags.inviteAccepted;
    public static final QName inviteDeclinedTag = AppleServerTags.inviteDeclined;
    public static final QName inviteDeletedTag = AppleServerTags.inviteDeleted;
    public static final QName inviteInvalidTag = AppleServerTags.inviteInvalid;
    public static final QName inviteNoresponseTag = AppleServerTags.inviteNoresponse;
    public static final QName inviteNotificationTag = AppleServerTags.inviteNotification;
    public static final QName inviteReplyTag = AppleServerTags.inviteReply;
    public static final QName organizerTag = AppleServerTags.organizer;
    public static final QName readTag = AppleServerTags.read;
    public static final QName readWriteTag = AppleServerTags.readWrite;
    public static final QName removeTag = AppleServerTags.remove;
    public static final QName setTag = AppleServerTags.set;
    public static final QName shareTag = AppleServerTags.share;
    public static final QName summaryTag = AppleServerTags.summary;
    public static final QName bwnameTag = BedeworkServerTags.name;
    public static final QName externalUserTag = BedeworkServerTags.externalUser;
    public static final QName supportedComponentsTag = CaldavTags.supportedCalendarComponentSet;
    public static final QName uidTag = AppleServerTags.uid;
    public static final QName userTag = AppleServerTags.user;
    private static Map<String, QName> statusToInviteStatus = new HashMap<String, QName>();
    private static Map<QName, String> inviteStatusToStatus = new HashMap<QName, String>();
    private static final List<BaseNotificationParser> parsers;

    private static void setStatusMaps(QName val) {
        statusToInviteStatus.put(val.getLocalPart(), val);
        inviteStatusToStatus.put(val, val.getLocalPart());
    }

    public static List<BaseNotificationParser> getParsers() {
        return Collections.unmodifiableList(parsers);
    }

    public static String getInviteStatusToStatus(QName val) {
        return inviteStatusToStatus.get(val);
    }

    public static Document parseXmlString(String val) throws WebdavException {
        if (val == null || val.length() == 0) {
            return null;
        }
        return Parser.parseXml(new StringReader(val));
    }

    public static Document parseXml(Reader val) throws WebdavException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(val));
        }
        catch (SAXException e) {
            throw Parser.parseException(e);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public InviteType parseInvite(String val) throws WebdavException {
        Document d = Parser.parseXmlString(val);
        return this.parseInvite(d.getDocumentElement());
    }

    public InviteType parseInvite(Node nd) throws WebdavException {
        try {
            Element[] shareEls;
            if (!XmlUtil.nodeMatches(nd, inviteTag)) {
                throw new WebdavBadRequest("Expected " + inviteTag);
            }
            InviteType in = new InviteType();
            for (Element curnode : shareEls = XmlUtil.getElementsArray(nd)) {
                if (XmlUtil.nodeMatches(curnode, organizerTag)) {
                    in.setOrganizer(this.parseOrganizer(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, userTag)) {
                    in.getUsers().add(this.parseUser(curnode));
                    continue;
                }
                throw new WebdavBadRequest("Expected " + userTag);
            }
            return in;
        }
        catch (SAXException e) {
            throw Parser.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public ShareType parseShare(Node nd) throws WebdavException {
        try {
            Element[] shareEls;
            if (!XmlUtil.nodeMatches(nd, shareTag)) {
                throw new WebdavBadRequest("Expected " + shareTag);
            }
            ShareType sh = new ShareType();
            for (Element curnode : shareEls = XmlUtil.getElementsArray(nd)) {
                if (XmlUtil.nodeMatches(curnode, setTag)) {
                    sh.getSet().add(this.parseSet(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, removeTag)) {
                    sh.getRemove().add(this.parseRemove(curnode));
                    continue;
                }
                throw new WebdavBadRequest("Expected " + setTag + " or " + removeTag);
            }
            return sh;
        }
        catch (SAXException e) {
            throw Parser.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public InviteReplyType parseInviteReply(String s) throws WebdavException {
        Document d = Parser.parseXmlString(s);
        return this.parseInviteReply(d.getDocumentElement());
    }

    public InviteReplyType parseInviteReply(Element nd) throws WebdavException {
        try {
            Element[] shareEls;
            if (!XmlUtil.nodeMatches(nd, inviteReplyTag)) {
                throw new WebdavBadRequest("Expected " + inviteReplyTag);
            }
            InviteReplyType ir = new InviteReplyType();
            ir.setSharedType(XmlUtil.getAttrVal(nd, "shared-type"));
            for (Element curnode : shareEls = XmlUtil.getElementsArray(nd)) {
                if (XmlUtil.nodeMatches(curnode, bwnameTag)) {
                    ir.setName(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, commonNameTag)) {
                    if (ir.getCommonName() != null) {
                        throw this.badInviteReply();
                    }
                    ir.setCommonName(XmlUtil.getElementContent(curnode));
                }
                if (XmlUtil.nodeMatches(curnode, firstNameTag) || XmlUtil.nodeMatches(curnode, lastNameTag)) continue;
                if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                    if (ir.getHref() != null) {
                        throw this.badInviteReply();
                    }
                    ir.setHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, inviteAcceptedTag)) {
                    if (ir.getAccepted() != null) {
                        throw this.badInviteReply();
                    }
                    ir.setAccepted(true);
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, inviteDeclinedTag)) {
                    if (ir.getAccepted() != null) {
                        throw this.badInviteReply();
                    }
                    ir.setAccepted(false);
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, hosturlTag)) {
                    if (ir.getHostUrl() != null) {
                        throw this.badInviteReply();
                    }
                    ir.setHostUrl(this.parseHostUrl(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, inReplyToTag)) {
                    if (ir.getInReplyTo() != null) {
                        throw this.badInviteReply();
                    }
                    ir.setInReplyTo(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, summaryTag)) {
                    if (ir.getSummary() != null) {
                        throw this.badInviteReply();
                    }
                    ir.setSummary(XmlUtil.getElementContent(curnode));
                    continue;
                }
                throw this.badInviteReply();
            }
            if (ir.getHref() == null || ir.getAccepted() == null || ir.getHostUrl() == null || ir.getInReplyTo() == null) {
                throw this.badInviteReply();
            }
            return ir;
        }
        catch (SAXException e) {
            throw Parser.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public InviteNotificationType parseInviteNotification(Element nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, inviteNotificationTag)) {
                throw new WebdavBadRequest("Expected " + inviteNotificationTag);
            }
            InviteNotificationType in = new InviteNotificationType();
            in.setSharedType(XmlUtil.getAttrVal(nd, "shared-type"));
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (XmlUtil.nodeMatches(curnode, bwnameTag)) {
                    in.setName(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, uidTag)) {
                    if (in.getUid() != null) {
                        throw this.badInviteNotification();
                    }
                    in.setUid(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                    if (in.getHref() != null) {
                        throw this.badInviteNotification();
                    }
                    in.setHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, inviteAcceptedTag) || XmlUtil.nodeMatches(curnode, inviteDeclinedTag) || XmlUtil.nodeMatches(curnode, inviteNoresponseTag) || XmlUtil.nodeMatches(curnode, inviteDeletedTag)) {
                    if (in.getInviteStatus() != null) {
                        throw this.badAccess();
                    }
                    in.setInviteStatus(new QName("http://calendarserver.org/ns/", curnode.getLocalName()));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, accessTag)) {
                    if (in.getAccess() != null) {
                        throw this.badInviteNotification();
                    }
                    in.setAccess(this.parseAccess(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, hosturlTag)) {
                    if (in.getHostUrl() != null) {
                        throw this.badInviteNotification();
                    }
                    in.setHostUrl(this.parseHostUrl(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, organizerTag)) {
                    if (in.getOrganizer() != null) {
                        throw this.badInviteNotification();
                    }
                    in.setOrganizer(this.parseOrganizer(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, summaryTag)) {
                    if (in.getSummary() != null) {
                        throw this.badInviteNotification();
                    }
                    in.setSummary(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, supportedComponentsTag)) {
                    if (!in.getSupportedComponents().isEmpty()) {
                        throw this.badInviteNotification();
                    }
                    this.parseSupportedComponents(curnode, in.getSupportedComponents());
                    continue;
                }
                throw this.badInviteNotification();
            }
            if (in.getUid() == null || in.getHref() == null || in.getHostUrl() == null || in.getOrganizer() == null) {
                throw this.badInviteNotification();
            }
            if (!in.getInviteStatus().equals(AppleServerTags.inviteDeleted) && in.getAccess() == null) {
                throw this.badInviteNotification();
            }
            return in;
        }
        catch (SAXException e) {
            throw Parser.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public UserType parseUser(Node nd) throws WebdavException {
        try {
            if (!XmlUtil.nodeMatches(nd, userTag)) {
                throw new WebdavBadRequest("Expected " + userTag);
            }
            UserType u = new UserType();
            Element[] shareEls = XmlUtil.getElementsArray(nd);
            boolean parsedExternalElement = false;
            for (Element curnode : shareEls) {
                if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                    if (u.getHref() != null) {
                        throw this.badUser();
                    }
                    u.setHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, commonNameTag)) {
                    if (u.getCommonName() != null) {
                        throw this.badUser();
                    }
                    u.setCommonName(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, inviteAcceptedTag) || XmlUtil.nodeMatches(curnode, inviteDeclinedTag) || XmlUtil.nodeMatches(curnode, inviteNoresponseTag) || XmlUtil.nodeMatches(curnode, inviteDeletedTag)) {
                    if (u.getInviteStatus() != null) {
                        throw this.badAccess();
                    }
                    u.setInviteStatus(new QName("http://calendarserver.org/ns/", curnode.getLocalName()));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, accessTag)) {
                    if (u.getAccess() != null) {
                        throw this.badUser();
                    }
                    u.setAccess(this.parseAccess(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, summaryTag)) {
                    if (u.getSummary() != null) {
                        throw this.badUser();
                    }
                    u.setSummary(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, externalUserTag)) {
                    if (parsedExternalElement) {
                        throw this.badUser();
                    }
                    parsedExternalElement = true;
                    u.setExternalUser(Boolean.valueOf(XmlUtil.getElementContent(curnode)));
                    continue;
                }
                throw this.badInviteNotification();
            }
            if (u.getHref() == null || u.getInviteStatus() == null || u.getAccess() == null) {
                throw this.badUser();
            }
            return u;
        }
        catch (SAXException e) {
            throw Parser.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void parseSupportedComponents(Node nd, List<String> comps) throws Throwable {
        for (Element curnode : XmlUtil.getElementsArray(nd)) {
            if (!XmlUtil.nodeMatches(curnode, compTag)) {
                throw this.badComps();
            }
            comps.add(XmlUtil.getAttrVal(curnode, "name"));
        }
    }

    private AccessType parseAccess(Node nd) throws Throwable {
        Element[] els;
        AccessType a = new AccessType();
        for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
            if (XmlUtil.nodeMatches(curnode, readTag) || XmlUtil.nodeMatches(curnode, readWriteTag)) {
                if (a.getRead() != null || a.getReadWrite() != null) {
                    throw this.badAccess();
                }
                if (XmlUtil.nodeMatches(curnode, readTag)) {
                    a.setRead(true);
                    continue;
                }
                a.setReadWrite(true);
                continue;
            }
            throw this.badAccess();
        }
        if (a.getRead() == null && a.getReadWrite() == null) {
            throw this.badAccess();
        }
        return a;
    }

    private SetType parseSet(Node nd) throws Throwable {
        Element[] els;
        SetType s = new SetType();
        for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
            if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                if (s.getHref() != null) {
                    throw this.badSet();
                }
                s.setHref(XmlUtil.getElementContent(curnode));
                continue;
            }
            if (XmlUtil.nodeMatches(curnode, commonNameTag)) {
                if (s.getCommonName() != null) {
                    throw this.badSet();
                }
                s.setCommonName(XmlUtil.getElementContent(curnode));
                continue;
            }
            if (XmlUtil.nodeMatches(curnode, summaryTag)) {
                if (s.getSummary() != null) {
                    throw this.badSet();
                }
                s.setSummary(XmlUtil.getElementContent(curnode));
                continue;
            }
            if (XmlUtil.nodeMatches(curnode, readTag) || XmlUtil.nodeMatches(curnode, readWriteTag)) {
                if (s.getAccess() != null) {
                    throw this.badSet();
                }
                AccessType a = new AccessType();
                if (XmlUtil.nodeMatches(curnode, readTag)) {
                    a.setRead(true);
                } else {
                    a.setReadWrite(true);
                }
                s.setAccess(a);
                continue;
            }
            throw this.badSet();
        }
        if (s.getHref() == null) {
            throw this.badSet();
        }
        if (s.getAccess() == null) {
            throw this.badSet();
        }
        return s;
    }

    private RemoveType parseRemove(Node nd) throws Throwable {
        Element[] els;
        RemoveType r = new RemoveType();
        for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
            if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                if (r.getHref() != null) {
                    throw this.badRemove();
                }
            } else {
                throw this.badRemove();
            }
            r.setHref(XmlUtil.getElementContent(curnode));
        }
        if (r.getHref() == null) {
            throw this.badRemove();
        }
        return r;
    }

    private OrganizerType parseOrganizer(Node nd) throws Throwable {
        Element[] els;
        OrganizerType o = new OrganizerType();
        for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
            if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                if (o.getHref() != null) {
                    throw this.badOrganizer();
                }
                o.setHref(XmlUtil.getElementContent(curnode));
                continue;
            }
            if (XmlUtil.nodeMatches(curnode, commonNameTag)) {
                if (o.getCommonName() != null) {
                    throw this.badOrganizer();
                }
                o.setCommonName(XmlUtil.getElementContent(curnode));
                continue;
            }
            throw this.badOrganizer();
        }
        return o;
    }

    private String parseHostUrl(Node nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, hosturlTag)) {
                throw new WebdavBadRequest("Expected " + hosturlTag);
            }
            String href = null;
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                    if (href != null) {
                        throw this.badHostUrl();
                    }
                } else {
                    throw this.badHostUrl();
                }
                href = XmlUtil.getElementContent(curnode);
            }
            if (href == null) {
                throw this.badHostUrl();
            }
            return href;
        }
        catch (SAXException e) {
            throw Parser.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private WebdavBadRequest badHostUrl() {
        return new WebdavBadRequest("Expected " + hrefTag);
    }

    private WebdavBadRequest badAccess() {
        return new WebdavBadRequest("Expected " + readTag + " or " + readWriteTag);
    }

    private WebdavBadRequest badSet() {
        return new WebdavBadRequest("Expected " + hrefTag + ", " + commonNameTag + "(optional), " + summaryTag + "(optional), (" + readTag + " or " + readWriteTag + ")");
    }

    private WebdavBadRequest badRemove() {
        return new WebdavBadRequest("Expected " + hrefTag);
    }

    private WebdavBadRequest badOrganizer() {
        return new WebdavBadRequest("Expected " + hrefTag + ", " + commonNameTag);
    }

    private WebdavBadRequest badComps() {
        return new WebdavBadRequest("Expected " + compTag);
    }

    private WebdavBadRequest badInviteNotification() {
        return new WebdavBadRequest("Expected " + uidTag + ", " + hrefTag + ", (" + inviteNoresponseTag + " or " + inviteDeclinedTag + " or " + inviteDeletedTag + " or " + inviteAcceptedTag + "), " + hosturlTag + ", " + organizerTag + ", " + summaryTag + "(optional)");
    }

    private WebdavBadRequest badInviteReply() {
        return new WebdavBadRequest("Expected " + hrefTag + ", (" + inviteAcceptedTag + " or " + inviteDeclinedTag + "), " + hosturlTag + ", " + inReplyToTag + ", " + summaryTag + "(optional)");
    }

    private WebdavBadRequest badUser() {
        return new WebdavBadRequest("Expected " + hrefTag + ", " + commonNameTag + "(optional), (" + inviteNoresponseTag + " or " + inviteDeclinedTag + " or " + inviteDeletedTag + " or " + inviteAcceptedTag + "), , " + summaryTag + "(optional)");
    }

    private static WebdavException parseException(SAXException e) throws WebdavException {
        Logger log = Parser.getLog();
        if (log.isDebugEnabled()) {
            log.error("Parse error:", e);
        }
        return new WebdavBadRequest();
    }

    private static Logger getLog() {
        return Logger.getLogger(Parser.class);
    }

    static {
        Parser.setStatusMaps(inviteAcceptedTag);
        Parser.setStatusMaps(inviteDeclinedTag);
        Parser.setStatusMaps(inviteNoresponseTag);
        Parser.setStatusMaps(inviteDeletedTag);
        Parser.setStatusMaps(inviteInvalidTag);
        parsers = new ArrayList<BaseNotificationParser>();
        parsers.add(new InviteParser());
        parsers.add(new InviteReplyParser());
    }

    static class InviteReplyParser
    extends SharingNotificationParser {
        InviteReplyParser() {
            super(AppleServerTags.inviteReply);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                InviteReplyType inviteReplyType = this.getParser().parseInviteReply(nd);
                return inviteReplyType;
            }
            finally {
                this.putParser();
            }
        }
    }

    static class InviteParser
    extends SharingNotificationParser {
        InviteParser() {
            super(AppleServerTags.inviteNotification);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                InviteNotificationType inviteNotificationType = this.getParser().parseInviteNotification(nd);
                return inviteNotificationType;
            }
            finally {
                this.putParser();
            }
        }
    }

    private static abstract class SharingNotificationParser
    implements BaseNotificationParser {
        private static final int maxPoolSize = 10;
        private final List<Parser> parsers = new ArrayList<Parser>();
        protected Parser parser;
        protected QName element;

        protected SharingNotificationParser(QName element) {
            this.element = element;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected Parser getParser() {
            if (this.parser != null) {
                return this.parser;
            }
            List<Parser> list = this.parsers;
            synchronized (list) {
                if (this.parsers.size() > 0) {
                    this.parser = this.parsers.remove(0);
                    return this.parser;
                }
                this.parser = new Parser();
                this.parsers.add(this.parser);
                return this.parser;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void putParser() {
            List<Parser> list = this.parsers;
            synchronized (list) {
                if (this.parsers.size() >= 10) {
                    return;
                }
                this.parsers.add(this.parser);
            }
        }

        @Override
        public QName getElement() {
            return this.element;
        }
    }
}

