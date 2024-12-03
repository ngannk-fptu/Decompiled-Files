/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.eventreg;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.eventreg.EventregBaseNotificationType;
import org.bedework.caldav.util.notifications.eventreg.EventregCancelledNotificationType;
import org.bedework.caldav.util.notifications.eventreg.EventregRegisteredNotificationType;
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

public class EventregParsers {
    public static final QName cancelledTag = BedeworkServerTags.eventregCancelled;
    public static final QName registeredTag = BedeworkServerTags.eventregRegistered;
    public static final QName numTicketsRequestedTag = BedeworkServerTags.eventregNumTicketsRequested;
    public static final QName numTicketsTag = BedeworkServerTags.eventregNumTickets;
    public static final QName commentTag = BedeworkServerTags.comment;
    public static final QName hrefTag = WebdavTags.href;
    public static final QName nameTag = BedeworkServerTags.name;
    public static final QName principalURLTag = WebdavTags.principalURL;
    public static final QName uidTag = AppleServerTags.uid;

    public static Document parseXmlString(String val) throws WebdavException {
        if (val == null || val.length() == 0) {
            return null;
        }
        return EventregParsers.parseXml(new StringReader(val));
    }

    public static Document parseXml(Reader val) throws WebdavException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(val));
        }
        catch (SAXException e) {
            throw EventregParsers.parseException(e);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public EventregCancelledNotificationType parseEventCancelled(String val) throws WebdavException {
        Document d = EventregParsers.parseXmlString(val);
        return this.parseEventCancelled(d.getDocumentElement());
    }

    public EventregCancelledNotificationType parseEventCancelled(Node nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, cancelledTag)) {
                throw new WebdavBadRequest("Expected " + cancelledTag);
            }
            EventregCancelledNotificationType note = new EventregCancelledNotificationType();
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (this.eventregBaseNode(note, curnode)) continue;
                throw new WebdavBadRequest("Unexpected element " + curnode);
            }
            return note;
        }
        catch (SAXException e) {
            throw EventregParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public EventregRegisteredNotificationType parseEventRegistered(Node nd) throws WebdavException {
        try {
            Element[] els;
            if (!XmlUtil.nodeMatches(nd, registeredTag)) {
                throw new WebdavBadRequest("Expected " + registeredTag);
            }
            EventregRegisteredNotificationType note = new EventregRegisteredNotificationType();
            for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
                if (this.eventregBaseNode(note, curnode)) continue;
                if (XmlUtil.nodeMatches(curnode, numTicketsRequestedTag)) {
                    note.setNumTicketsRequested(Integer.valueOf(XmlUtil.getElementContent(curnode)));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, numTicketsTag)) {
                    note.setNumTickets(Integer.valueOf(XmlUtil.getElementContent(curnode)));
                    continue;
                }
                throw new WebdavBadRequest("Unexpected element " + curnode);
            }
            return note;
        }
        catch (SAXException e) {
            throw EventregParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private boolean eventregBaseNode(EventregBaseNotificationType base, Element curnode) throws WebdavException {
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
            return false;
        }
        catch (SAXException e) {
            throw EventregParsers.parseException(e);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private static WebdavException parseException(SAXException e) throws WebdavException {
        Logger log = EventregParsers.getLog();
        if (log.isDebugEnabled()) {
            log.error("Parse error:", e);
        }
        return new WebdavBadRequest();
    }

    private static Logger getLog() {
        return Logger.getLogger(EventregParsers.class);
    }

    static {
        Parser.register(new EventCancelledParser());
        Parser.register(new EventRegisteredParser());
    }

    static class EventRegisteredParser
    extends EvRegParser {
        EventRegisteredParser() {
            super(registeredTag);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                EventregRegisteredNotificationType eventregRegisteredNotificationType = this.getParser().parseEventRegistered(nd);
                return eventregRegisteredNotificationType;
            }
            finally {
                this.putParser();
            }
        }
    }

    static class EventCancelledParser
    extends EvRegParser {
        EventCancelledParser() {
            super(cancelledTag);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                EventregCancelledNotificationType eventregCancelledNotificationType = this.getParser().parseEventCancelled(nd);
                return eventregCancelledNotificationType;
            }
            finally {
                this.putParser();
            }
        }
    }

    private static abstract class EvRegParser
    implements BaseNotificationParser {
        private static final int maxPoolSize = 10;
        private final List<EventregParsers> parsers = new ArrayList<EventregParsers>();
        protected EventregParsers parser;
        protected QName element;

        protected EvRegParser(QName element) {
            this.element = element;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected EventregParsers getParser() {
            if (this.parser != null) {
                return this.parser;
            }
            List<EventregParsers> list = this.parsers;
            synchronized (list) {
                if (this.parsers.size() > 0) {
                    this.parser = this.parsers.remove(0);
                    return this.parser;
                }
                this.parser = new EventregParsers();
                this.parsers.add(this.parser);
                return this.parser;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void putParser() {
            List<EventregParsers> list = this.parsers;
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

