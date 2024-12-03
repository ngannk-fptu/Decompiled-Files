/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.admin;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.admin.AdminNotificationType;
import org.bedework.caldav.util.notifications.admin.ApprovalResponseNotificationType;
import org.bedework.caldav.util.notifications.admin.AwaitingApprovalNotificationType;
import org.bedework.caldav.util.notifications.parse.BaseNotificationParser;
import org.bedework.caldav.util.notifications.parse.Parser;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AdminNoteParsers {
    public static final QName awaitingApprovalTag = BedeworkServerTags.awaitingApproval;
    public static final QName approvalResponseTag = BedeworkServerTags.approvalResponse;
    public static final QName acceptedTag = BedeworkServerTags.accepted;
    public static final QName calsuiteURLTag = BedeworkServerTags.calsuiteURL;
    public static final QName commentTag = BedeworkServerTags.comment;
    public static final QName hrefTag = WebdavTags.href;
    public static final QName nameTag = BedeworkServerTags.name;
    public static final QName principalURLTag = WebdavTags.principalURL;
    public static final QName uidTag = AppleServerTags.uid;

    public static Document parseXmlString(String val) throws WebdavException {
        if (val == null || val.length() == 0) {
            return null;
        }
        return AdminNoteParsers.parseXml(new StringReader(val));
    }

    public static Document parseXml(Reader val) throws WebdavException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(val));
        }
        catch (SAXException e) {
            throw AdminNoteParsers.parseException(e);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public AwaitingApprovalNotificationType parseparseAwaitingApproval(String val) throws WebdavException {
        Document d = AdminNoteParsers.parseXmlString(val);
        return this.parseAwaitingApproval(d.getDocumentElement());
    }

    public AwaitingApprovalNotificationType parseAwaitingApproval(Node nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, awaitingApprovalTag)) {
                throw new WebdavBadRequest("Expected " + awaitingApprovalTag);
            }
            AwaitingApprovalNotificationType note = new AwaitingApprovalNotificationType();
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (this.adminBaseNode(note, curnode)) continue;
                throw new WebdavBadRequest("Unexpected element " + curnode);
            }
            return note;
        }
        catch (SAXException e) {
            throw AdminNoteParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public ApprovalResponseNotificationType parseApprovalResponse(Node nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, approvalResponseTag)) {
                throw new WebdavBadRequest("Expected " + approvalResponseTag);
            }
            ApprovalResponseNotificationType note = new ApprovalResponseNotificationType();
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (this.adminBaseNode(note, curnode)) continue;
                if (XmlUtil.nodeMatches(curnode, acceptedTag)) {
                    note.setAccepted(Boolean.parseBoolean(XmlUtil.getElementContent(curnode)));
                    continue;
                }
                throw new WebdavBadRequest("Unexpected element " + curnode);
            }
            return note;
        }
        catch (SAXException e) {
            throw AdminNoteParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private boolean adminBaseNode(AdminNotificationType base, Element curnode) throws WebdavException {
        try {
            if (XmlUtil.nodeMatches(curnode, nameTag)) {
                base.setName(XmlUtil.getElementContent(curnode));
                return true;
            }
            if (XmlUtil.nodeMatches(curnode, uidTag)) {
                base.setUid(XmlUtil.getElementContent(curnode));
                return true;
            }
            if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                base.setHref(XmlUtil.getElementContent(curnode));
                return true;
            }
            if (XmlUtil.nodeMatches(curnode, principalURLTag)) {
                Element href = XmlUtil.getOnlyElement(curnode);
                if (href == null || !XmlUtil.nodeMatches(href, hrefTag)) {
                    throw new WebdavBadRequest("Expected " + hrefTag);
                }
                base.setPrincipalHref(XmlUtil.getElementContent(href));
                return true;
            }
            if (XmlUtil.nodeMatches(curnode, commentTag)) {
                base.setComment(XmlUtil.getElementContent(curnode));
                return true;
            }
            if (XmlUtil.nodeMatches(curnode, calsuiteURLTag)) {
                Element href = XmlUtil.getOnlyElement(curnode);
                if (href == null || !XmlUtil.nodeMatches(href, hrefTag)) {
                    throw new WebdavBadRequest("Expected " + hrefTag);
                }
                base.setCalsuiteHref(XmlUtil.getElementContent(href));
                return true;
            }
            return false;
        }
        catch (SAXException e) {
            throw AdminNoteParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private static WebdavException parseException(SAXException e) throws WebdavException {
        Logger log = AdminNoteParsers.getLog();
        if (log.isDebugEnabled()) {
            log.error("Parse error:", e);
        }
        return new WebdavBadRequest();
    }

    private static Logger getLog() {
        return Logger.getLogger(AdminNoteParsers.class);
    }

    static {
        Parser.register(new AwaitingApprovalParser());
        Parser.register(new ApprovalResponseParser());
    }

    static class ApprovalResponseParser
    extends AdmParser {
        ApprovalResponseParser() {
            super(approvalResponseTag);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                ApprovalResponseNotificationType approvalResponseNotificationType = this.getParser().parseApprovalResponse(nd);
                return approvalResponseNotificationType;
            }
            finally {
                this.putParser();
            }
        }
    }

    static class AwaitingApprovalParser
    extends AdmParser {
        AwaitingApprovalParser() {
            super(awaitingApprovalTag);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                AwaitingApprovalNotificationType awaitingApprovalNotificationType = this.getParser().parseAwaitingApproval(nd);
                return awaitingApprovalNotificationType;
            }
            finally {
                this.putParser();
            }
        }
    }

    private static abstract class AdmParser
    implements BaseNotificationParser {
        private static final int maxPoolSize = 10;
        private final List<AdminNoteParsers> parsers = new ArrayList<AdminNoteParsers>();
        protected AdminNoteParsers parser;
        protected QName element;

        protected AdmParser(QName element) {
            this.element = element;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected AdminNoteParsers getParser() {
            if (this.parser != null) {
                return this.parser;
            }
            List<AdminNoteParsers> list = this.parsers;
            synchronized (list) {
                if (this.parsers.size() > 0) {
                    this.parser = this.parsers.remove(0);
                    return this.parser;
                }
                this.parser = new AdminNoteParsers();
                this.parsers.add(this.parser);
                return this.parser;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void putParser() {
            List<AdminNoteParsers> list = this.parsers;
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

