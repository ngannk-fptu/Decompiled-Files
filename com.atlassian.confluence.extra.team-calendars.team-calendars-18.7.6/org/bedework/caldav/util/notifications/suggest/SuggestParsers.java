/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.suggest;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.parse.BaseNotificationParser;
import org.bedework.caldav.util.notifications.parse.Parser;
import org.bedework.caldav.util.notifications.suggest.SuggestNotificationType;
import org.bedework.caldav.util.notifications.suggest.SuggestResponseNotificationType;
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

public class SuggestParsers {
    public static final QName acceptedTag = BedeworkServerTags.accepted;
    public static final QName commentTag = BedeworkServerTags.comment;
    public static final QName hrefTag = WebdavTags.href;
    public static final QName nameTag = BedeworkServerTags.name;
    public static final QName suggesteeHrefTag = BedeworkServerTags.suggesteeHref;
    public static final QName suggesterHrefTag = BedeworkServerTags.suggesterHref;
    public static final QName suggestTag = BedeworkServerTags.suggest;
    public static final QName suggestReplyTag = BedeworkServerTags.suggestReply;
    public static final QName uidTag = AppleServerTags.uid;

    public static Document parseXmlString(String val) throws WebdavException {
        if (val == null || val.length() == 0) {
            return null;
        }
        return SuggestParsers.parseXml(new StringReader(val));
    }

    public static Document parseXml(Reader val) throws WebdavException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(val));
        }
        catch (SAXException e) {
            throw SuggestParsers.parseException(e);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public SuggestNotificationType parseSuggest(String val) throws WebdavException {
        Document d = SuggestParsers.parseXmlString(val);
        return this.parseSuggest(d.getDocumentElement());
    }

    public SuggestNotificationType parseSuggest(Node nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, suggestTag)) {
                throw new WebdavBadRequest("Expected " + suggestTag);
            }
            SuggestNotificationType snt = new SuggestNotificationType();
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (XmlUtil.nodeMatches(curnode, nameTag)) {
                    snt.setName(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, uidTag)) {
                    snt.setUid(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                    snt.setHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, suggesteeHrefTag)) {
                    snt.setSuggesteeHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, suggesterHrefTag)) {
                    snt.setSuggesterHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, commentTag)) {
                    snt.setComment(XmlUtil.getElementContent(curnode));
                    continue;
                }
                throw new WebdavBadRequest("Unexpected element " + curnode);
            }
            return snt;
        }
        catch (SAXException e) {
            throw SuggestParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public SuggestResponseNotificationType parseSuggestReply(Node nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, suggestReplyTag)) {
                throw new WebdavBadRequest("Expected " + suggestReplyTag);
            }
            SuggestResponseNotificationType srnt = new SuggestResponseNotificationType();
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (XmlUtil.nodeMatches(curnode, nameTag)) {
                    srnt.setName(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, uidTag)) {
                    srnt.setUid(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, hrefTag)) {
                    srnt.setHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, suggesteeHrefTag)) {
                    srnt.setSuggesteeHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, suggesterHrefTag)) {
                    srnt.setSuggesterHref(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, commentTag)) {
                    srnt.setComment(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, acceptedTag)) {
                    srnt.setAccepted(Boolean.parseBoolean(XmlUtil.getElementContent(curnode)));
                    continue;
                }
                throw new WebdavBadRequest("Unexpected element " + curnode);
            }
            return srnt;
        }
        catch (SAXException e) {
            throw SuggestParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private static WebdavException parseException(SAXException e) throws WebdavException {
        Logger log = SuggestParsers.getLog();
        if (log.isDebugEnabled()) {
            log.error("Parse error:", e);
        }
        return new WebdavBadRequest();
    }

    private static Logger getLog() {
        return Logger.getLogger(SuggestParsers.class);
    }

    static {
        Parser.register(new SuggestParser());
        Parser.register(new SuggestReplyParser());
    }

    static class SuggestReplyParser
    extends SuggestionParser {
        SuggestReplyParser() {
            super(suggestReplyTag);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                SuggestResponseNotificationType suggestResponseNotificationType = this.getParser().parseSuggestReply(nd);
                return suggestResponseNotificationType;
            }
            finally {
                this.putParser();
            }
        }
    }

    static class SuggestParser
    extends SuggestionParser {
        SuggestParser() {
            super(suggestTag);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                SuggestNotificationType suggestNotificationType = this.getParser().parseSuggest(nd);
                return suggestNotificationType;
            }
            finally {
                this.putParser();
            }
        }
    }

    private static abstract class SuggestionParser
    implements BaseNotificationParser {
        private static final int maxPoolSize = 10;
        private final List<SuggestParsers> parsers = new ArrayList<SuggestParsers>();
        protected SuggestParsers parser;
        protected QName element;

        protected SuggestionParser(QName element) {
            this.element = element;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected SuggestParsers getParser() {
            if (this.parser != null) {
                return this.parser;
            }
            List<SuggestParsers> list = this.parsers;
            synchronized (list) {
                if (this.parsers.size() > 0) {
                    this.parser = this.parsers.remove(0);
                    return this.parser;
                }
                this.parser = new SuggestParsers();
                this.parsers.add(this.parser);
                return this.parser;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void putParser() {
            List<SuggestParsers> list = this.parsers;
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

