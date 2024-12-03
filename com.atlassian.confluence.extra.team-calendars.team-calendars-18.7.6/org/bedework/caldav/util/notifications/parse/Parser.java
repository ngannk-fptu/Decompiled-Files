/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.parse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.bedework.caldav.util.notifications.BaseEntityChangeType;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.CalendarChangesType;
import org.bedework.caldav.util.notifications.ChangedByType;
import org.bedework.caldav.util.notifications.ChangedParameterType;
import org.bedework.caldav.util.notifications.ChangedPropertyType;
import org.bedework.caldav.util.notifications.ChangesType;
import org.bedework.caldav.util.notifications.ChildCreatedType;
import org.bedework.caldav.util.notifications.ChildDeletedType;
import org.bedework.caldav.util.notifications.ChildUpdatedType;
import org.bedework.caldav.util.notifications.CollectionChangesType;
import org.bedework.caldav.util.notifications.CreatedType;
import org.bedework.caldav.util.notifications.DeletedDetailsType;
import org.bedework.caldav.util.notifications.DeletedType;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.caldav.util.notifications.ProcessorType;
import org.bedework.caldav.util.notifications.ProcessorsType;
import org.bedework.caldav.util.notifications.PropType;
import org.bedework.caldav.util.notifications.RecurrenceType;
import org.bedework.caldav.util.notifications.ResourceChangeType;
import org.bedework.caldav.util.notifications.UpdatedType;
import org.bedework.caldav.util.notifications.parse.BaseNotificationParser;
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

public class Parser {
    private static final Map<QName, BaseNotificationParser> parsers = new HashMap<QName, BaseNotificationParser>();
    private static final QName dtstampTag;
    private static final QName notificationTag;

    public static boolean register(BaseNotificationParser bnp) {
        QName key = bnp.getElement();
        if (parsers.get(key) != null) {
            return false;
        }
        parsers.put(key, bnp);
        return true;
    }

    public static NotificationType fromXml(String val) throws WebdavException {
        ByteArrayInputStream bais = new ByteArrayInputStream(val.getBytes());
        return Parser.fromXml(bais);
    }

    public static NotificationType fromXml(InputStream is) throws WebdavException {
        Document doc = Parser.parseXmlString(is);
        if (doc == null) {
            return null;
        }
        NotificationType note = new Parser().parseNotification(doc.getDocumentElement());
        if (note != null) {
            note.setParsed(doc);
        }
        return note;
    }

    public static Document parseXmlString(InputStream is) throws WebdavException {
        if (is == null) {
            return null;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(is));
        }
        catch (SAXException e) {
            throw new WebdavBadRequest();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public NotificationType parseNotification(Node nd) throws WebdavException {
        try {
            if (!XmlUtil.nodeMatches(nd, notificationTag)) {
                throw new WebdavBadRequest("Expected " + notificationTag);
            }
            NotificationType n = new NotificationType();
            Element[] els = XmlUtil.getElementsArray(nd);
            for (int pos = this.parseCommonElements(n, nd); pos < els.length; ++pos) {
                Element curnode = els[pos];
                if (XmlUtil.nodeMatches(curnode, dtstampTag)) {
                    n.setDtstamp(XmlUtil.getElementContent(curnode));
                    continue;
                }
                BaseNotificationParser bnp = parsers.get(XmlUtil.fromNode(curnode));
                if (bnp == null || n.getNotification() != null) {
                    this.error("No parser to handle " + curnode);
                    return null;
                }
                n.setNotification(bnp.parse(curnode));
            }
            return n;
        }
        catch (SAXException e) {
            Parser.dumpXml(nd);
            throw new WebdavBadRequest();
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public int parseCommonElements(NotificationType note, Node nd) throws Throwable {
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length == 0) {
            return 0;
        }
        if (!XmlUtil.nodeMatches(els[0], BedeworkServerTags.processors)) {
            return 0;
        }
        note.setProcessors(this.parseProcessors(els[0]));
        return 1;
    }

    ProcessorsType parseProcessors(Element nd) throws Throwable {
        Element[] els;
        ProcessorsType pt = new ProcessorsType();
        for (Element curnode : els = XmlUtil.getElementsArray(nd)) {
            if (!XmlUtil.nodeMatches(curnode, BedeworkServerTags.processor)) {
                throw new WebdavBadRequest("Expected " + BedeworkServerTags.processor);
            }
            pt.getProcessor().add(this.parseProcessor(curnode));
        }
        return pt;
    }

    ProcessorType parseProcessor(Element nd) throws Throwable {
        int pos;
        ProcessorType pt = new ProcessorType();
        Element[] els = XmlUtil.getElementsArray(nd);
        int len = els.length;
        if (len > (pos = 0) && XmlUtil.nodeMatches(els[pos], BedeworkServerTags.type)) {
            pt.setType(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], dtstampTag)) {
            pt.setDtstamp(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        if (len > pos && XmlUtil.nodeMatches(els[pos], WebdavTags.status)) {
            pt.setStatus(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        return pt;
    }

    public ResourceChangeType parseResourceChangeNotification(Element nd) throws WebdavException {
        try {
            if (!XmlUtil.nodeMatches(nd, AppleServerTags.resourceChange)) {
                throw new WebdavBadRequest("Expected " + AppleServerTags.resourceChange);
            }
            ResourceChangeType rc = new ResourceChangeType();
            Element[] els = XmlUtil.getElementsArray(nd);
            BaseEntityChangeType parsed = null;
            for (Element curnode : els) {
                if (XmlUtil.nodeMatches(curnode, BedeworkServerTags.name)) {
                    rc.setName(XmlUtil.getElementContent(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, AppleServerTags.created)) {
                    if (parsed != null) {
                        throw this.badNotification(curnode);
                    }
                    CreatedType c = this.parseCreated(curnode);
                    rc.setCreated(c);
                    parsed = c;
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, AppleServerTags.updated)) {
                    if (parsed != null && !(parsed instanceof UpdatedType)) {
                        throw this.badNotification(curnode);
                    }
                    UpdatedType u = this.parseUpdated(curnode);
                    rc.addUpdate(u);
                    parsed = u;
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, AppleServerTags.deleted)) {
                    if (parsed != null) {
                        throw this.badNotification(curnode);
                    }
                    DeletedType d = this.parseDeleted(curnode);
                    rc.setDeleted(d);
                    parsed = d;
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, AppleServerTags.collectionChanges)) {
                    if (parsed != null) {
                        throw this.badNotification(curnode);
                    }
                    CollectionChangesType cc = this.parseCollectionChanges(curnode);
                    rc.setCollectionChanges(cc);
                    parsed = cc;
                    continue;
                }
                throw this.badNotification(curnode);
            }
            return rc;
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

    private CreatedType parseCreated(Element nd) throws Throwable {
        CreatedType c = new CreatedType();
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length < 1) {
            throw this.badNotification("No elements for create");
        }
        c.setHref(this.parseHref(els[0]));
        if (els.length > 1 && XmlUtil.nodeMatches(els[1], AppleServerTags.changedBy)) {
            c.setChangedBy(this.parseChangedBy(els[1]));
        }
        return c;
    }

    private UpdatedType parseUpdated(Element nd) throws Throwable {
        UpdatedType u = new UpdatedType();
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length < 1) {
            throw this.badNotification("No elements for update");
        }
        int pos = 0;
        u.setHref(this.parseHref(els[pos]));
        if (els.length > ++pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.changedBy)) {
            u.setChangedBy(this.parseChangedBy(els[1]));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.content)) {
            u.setContent(true);
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], WebdavTags.prop)) {
            u.setProp(this.parseProps(els[pos]));
            ++pos;
        }
        while (els.length > pos) {
            this.expect(els[pos], AppleServerTags.calendarChanges);
            u.getCalendarChanges().add(this.parseCalendarChange(els[pos]));
            ++pos;
        }
        return u;
    }

    private DeletedType parseDeleted(Element nd) throws Throwable {
        DeletedType d = new DeletedType();
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length < 1) {
            throw this.badNotification("No elements for delete");
        }
        int pos = 0;
        d.setHref(this.parseHref(els[pos]));
        if (els.length > ++pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.changedBy)) {
            d.setChangedBy(this.parseChangedBy(els[pos]));
            ++pos;
        }
        this.expect(els[pos], AppleServerTags.deletedDetails);
        d.setDeletedDetails(this.parseDeletedDetails(els[pos]));
        return d;
    }

    private CollectionChangesType parseCollectionChanges(Element nd) throws Throwable {
        CollectionChangesType cc = new CollectionChangesType();
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length < 1) {
            throw this.badNotification("No elements for collection-changes");
        }
        int pos = 0;
        cc.setHref(this.parseHref(els[pos]));
        ++pos;
        while (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.changedBy)) {
            cc.getChangedByList().add(this.parseChangedBy(els[pos]));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], WebdavTags.prop)) {
            cc.setProp(this.parseProps(els[pos]));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.childCreated)) {
            ChildCreatedType chc = new ChildCreatedType();
            chc.setCount(this.getIntContent(els[pos]));
            cc.setChildCreated(chc);
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.childUpdated)) {
            ChildUpdatedType chu = new ChildUpdatedType();
            chu.setCount(this.getIntContent(els[pos]));
            cc.setChildUpdated(chu);
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.childDeleted)) {
            ChildDeletedType chd = new ChildDeletedType();
            chd.setCount(this.getIntContent(els[pos]));
            cc.setChildDeleted(chd);
            ++pos;
        }
        if (els.length > pos) {
            throw this.badNotification(els[pos]);
        }
        return cc;
    }

    private int getIntContent(Element nd) throws Throwable {
        String val = XmlUtil.getElementContent(nd);
        return Integer.valueOf(val);
    }

    private DeletedDetailsType parseDeletedDetails(Element nd) throws Throwable {
        DeletedDetailsType dd = new DeletedDetailsType();
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length < 1) {
            throw this.badNotification("No elements for deleted-details");
        }
        int pos = 0;
        if (XmlUtil.nodeMatches(els[pos], AppleServerTags.deletedDisplayname)) {
            dd.setDeletedDisplayname(XmlUtil.getElementContent(els[pos]));
            if (els.length > ++pos) {
                throw this.badNotification(els[pos]);
            }
            return dd;
        }
        this.expect(els[pos], AppleServerTags.deletedComponent);
        dd.setDeletedComponent(XmlUtil.getElementContent(els[pos]));
        this.expect(els[++pos], AppleServerTags.deletedSummary);
        dd.setDeletedSummary(XmlUtil.getElementContent(els[pos]));
        if (els.length > ++pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.deletedNextInstance)) {
            dd.setDeletedNextInstance(XmlUtil.getElementContent(els[pos]));
            dd.setDeletedNextInstanceTzid(XmlUtil.getAttrVal(els[pos], "tzid"));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.deletedHadMoreInstances)) {
            dd.setDeletedHadMoreInstances(true);
            ++pos;
        }
        while (els.length > pos) {
            dd.getDeletedProps().add(this.parseChangedProperty(els[pos]));
            ++pos;
        }
        if (els.length > pos) {
            throw this.badNotification(els[pos]);
        }
        return dd;
    }

    private PropType parseProps(Element nd) throws Throwable {
        PropType p = new PropType();
        for (Element curnode : XmlUtil.getElementsArray(nd)) {
            p.getQnames().add(XmlUtil.fromNode(curnode));
        }
        return p;
    }

    private CalendarChangesType parseCalendarChange(Element nd) throws Throwable {
        CalendarChangesType cc = new CalendarChangesType();
        for (Element curnode : XmlUtil.getElementsArray(nd)) {
            this.expect(curnode, AppleServerTags.recurrence);
            cc.getRecurrence().add(this.parseRecurrence(curnode));
        }
        return cc;
    }

    private RecurrenceType parseRecurrence(Element nd) throws Throwable {
        RecurrenceType r = new RecurrenceType();
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length < 1) {
            throw this.badNotification("No elements for recurrence");
        }
        int pos = 0;
        if (XmlUtil.nodeMatches(els[pos], AppleServerTags.master)) {
            ++pos;
        } else {
            this.expect(els[pos], AppleServerTags.recurrenceid);
            r.setRecurrenceid(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.added)) {
            r.setAdded(true);
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.removed)) {
            r.setRemoved(true);
            ++pos;
        }
        while (els.length > pos) {
            this.expect(els[pos], AppleServerTags.changes);
            r.getChanges().add(this.parseChanges(els[pos]));
            ++pos;
        }
        return r;
    }

    private ChangesType parseChanges(Element nd) throws Throwable {
        ChangesType c = new ChangesType();
        for (Element curnode : XmlUtil.getElementsArray(nd)) {
            this.expect(curnode, AppleServerTags.changedProperty);
            c.getChangedProperty().add(this.parseChangedProperty(curnode));
        }
        return c;
    }

    private ChangedPropertyType parseChangedProperty(Element nd) throws Throwable {
        int pos;
        ChangedPropertyType cp = new ChangedPropertyType();
        cp.setName(XmlUtil.getAttrVal(nd, "name"));
        Element[] els = XmlUtil.getElementsArray(nd);
        for (pos = 0; els.length > pos && XmlUtil.nodeMatches(els[pos], AppleServerTags.changedParameter); ++pos) {
            cp.getChangedParameter().add(this.parseChangedParameter(els[pos]));
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], BedeworkServerTags.dataFrom)) {
            cp.setDataFrom(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], BedeworkServerTags.dataTo)) {
            cp.setDataTo(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        if (els.length > pos) {
            throw this.badNotification(els[pos]);
        }
        return cp;
    }

    private ChangedParameterType parseChangedParameter(Element nd) throws Throwable {
        ChangedParameterType cp = new ChangedParameterType();
        cp.setName(XmlUtil.getAttrVal(nd, "name"));
        Element[] els = XmlUtil.getElementsArray(nd);
        int pos = 0;
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], BedeworkServerTags.dataFrom)) {
            cp.setDataFrom(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        if (els.length > pos && XmlUtil.nodeMatches(els[pos], BedeworkServerTags.dataTo)) {
            cp.setDataTo(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        if (els.length > pos) {
            throw this.badNotification(els[pos]);
        }
        return cp;
    }

    private ChangedByType parseChangedBy(Element nd) throws Throwable {
        int pos;
        ChangedByType cb = new ChangedByType();
        Element[] els = XmlUtil.getElementsArray(nd);
        if (XmlUtil.nodeMatches(els[0], AppleServerTags.commonName)) {
            cb.setCommonName(XmlUtil.getElementContent(els[0]));
            pos = 1;
        } else {
            this.expect(els[0], AppleServerTags.firstName);
            cb.setFirstName(XmlUtil.getElementContent(els[0]));
            this.expect(els[1], AppleServerTags.lastName);
            cb.setLastName(XmlUtil.getElementContent(els[1]));
            pos = 2;
        }
        if (XmlUtil.nodeMatches(els[pos], AppleServerTags.dtstamp)) {
            cb.setDtstamp(XmlUtil.getElementContent(els[pos]));
            ++pos;
        }
        cb.setHref(this.parseHref(els[pos]));
        return cb;
    }

    private String parseHref(Element nd) throws Throwable {
        this.expect(nd, WebdavTags.href);
        return XmlUtil.getElementContent(nd);
    }

    private static void dumpXml(Node nd) {
        Logger log = Parser.getLog();
        if (!log.isDebugEnabled()) {
            return;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer = tfactory.newTransformer();
            serializer.setOutputProperty("indent", "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            serializer.transform(new DOMSource(nd), new StreamResult(out));
            log.debug(out.toString());
        }
        catch (Throwable t) {
            log.error("Unable to dump XML");
        }
    }

    private void expect(Element nd, QName expected) throws Throwable {
        if (!XmlUtil.nodeMatches(nd, expected)) {
            throw this.badNotification(nd, expected);
        }
    }

    private WebdavBadRequest badNotification(String msg) {
        return new WebdavBadRequest(msg);
    }

    private WebdavBadRequest badNotification(Element curnode, QName expected) {
        return new WebdavBadRequest("Unexpected element " + curnode + " expected " + expected);
    }

    private WebdavBadRequest badNotification(Element curnode) {
        return new WebdavBadRequest("Unexpected element " + curnode);
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

    private void error(String msg) {
        Parser.getLog().error(msg);
    }

    static {
        for (BaseNotificationParser bnp : org.bedework.caldav.util.sharing.parse.Parser.getParsers()) {
            parsers.put(bnp.getElement(), bnp);
        }
        ResourceChangeParser bnp = new ResourceChangeParser();
        parsers.put(bnp.getElement(), bnp);
        dtstampTag = AppleServerTags.dtstamp;
        notificationTag = AppleServerTags.notification;
    }

    static class ResourceChangeParser
    extends AbstractNotificationParser {
        ResourceChangeParser() {
            super(AppleServerTags.resourceChange);
        }

        @Override
        public BaseNotificationType parse(Element nd) throws WebdavException {
            try {
                ResourceChangeType resourceChangeType = this.getParser().parseResourceChangeNotification(nd);
                return resourceChangeType;
            }
            finally {
                this.putParser();
            }
        }
    }

    private static abstract class AbstractNotificationParser
    implements BaseNotificationParser {
        private static final int maxPoolSize = 10;
        private final List<Parser> parsers = new ArrayList<Parser>();
        protected Parser parser;
        protected QName element;

        protected AbstractNotificationParser(QName element) {
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

