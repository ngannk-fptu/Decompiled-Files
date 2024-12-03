/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CalendarException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import org.bedework.util.calendar.BuildState;
import org.bedework.util.calendar.ContentHandlerImpl;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlCalendarBuilder {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final TimeZoneRegistry tzRegistry;

    public XmlCalendarBuilder(TimeZoneRegistry tzRegistry) {
        this.tzRegistry = tzRegistry;
    }

    public Calendar build(InputStream in) throws IOException, ParserException {
        return this.build(new InputStreamReader(in, DEFAULT_CHARSET));
    }

    public Calendar build(Reader in) throws IOException, ParserException {
        BuildState bs = new BuildState(this.tzRegistry);
        bs.setContentHandler(new ContentHandlerImpl(bs));
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(in));
            this.process(doc, bs);
        }
        catch (SAXException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        catch (Throwable t) {
            throw new ParserException(t.getMessage(), 0, t);
        }
        if (bs.getDatesMissingTimezones().size() > 0 && this.tzRegistry != null) {
            this.resolveTimezones(bs);
        }
        return bs.getCalendars().iterator().next();
    }

    private void process(Document doc, BuildState bs) throws ParserException {
        Element root = doc.getDocumentElement();
        if (!XmlUtil.nodeMatches(root, XcalTags.icalendar)) {
            throw new ParserException("Expected " + XcalTags.icalendar + " found " + root, 0);
        }
        for (Element el : this.getChildren(root)) {
            if (!XmlUtil.nodeMatches(el, XcalTags.vcalendar)) {
                throw new ParserException("Expected " + XcalTags.vcalendar + " found " + el, 0);
            }
            bs.setCalendar(null);
            this.processVcalendar(el, bs);
            if (bs.getCalendar() == null) continue;
            bs.getCalendars().add(bs.getCalendar());
        }
    }

    private void processVcalendar(Element el, BuildState bs) throws ParserException {
        bs.getContentHandler().startCalendar();
        try {
            List<Element> els = XmlUtil.getElements(el);
            Iterator elit = els.iterator();
            Element vcel = null;
            if (elit.hasNext()) {
                vcel = (Element)elit.next();
            }
            if (XmlUtil.nodeMatches(vcel, XcalTags.properties)) {
                this.processProperties(vcel, bs);
                vcel = elit.hasNext() ? (Element)elit.next() : null;
            }
            if (XmlUtil.nodeMatches(vcel, XcalTags.components)) {
                this.processCalcomps(vcel, bs);
                vcel = elit.hasNext() ? (Element)elit.next() : null;
            }
            if (vcel != null) {
                throw new ParserException("Unexpected element: found " + vcel, 0);
            }
        }
        catch (SAXException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
    }

    private void processProperties(Element el, BuildState bs) throws ParserException {
        try {
            for (Element e : XmlUtil.getElements(el)) {
                this.processProperty(e, bs);
            }
        }
        catch (SAXException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
    }

    private void processCalcomps(Element el, BuildState bs) throws ParserException {
        try {
            for (Element e : XmlUtil.getElements(el)) {
                this.processComponent(e, bs);
            }
        }
        catch (SAXException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
    }

    private void processComponent(Element el, BuildState bs) throws ParserException {
        try {
            bs.getContentHandler().startComponent(el.getLocalName().toUpperCase());
            for (Element e : XmlUtil.getElements(el)) {
                if (XmlUtil.nodeMatches(e, XcalTags.properties)) {
                    this.processProperties(e, bs);
                    continue;
                }
                if (XmlUtil.nodeMatches(e, XcalTags.components)) {
                    for (Element ce : XmlUtil.getElements(e)) {
                        this.processComponent(ce, bs);
                    }
                    continue;
                }
                throw new ParserException("Unexpected element: found " + e, 0);
            }
            bs.getContentHandler().endComponent(el.getLocalName().toUpperCase());
        }
        catch (SAXException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
    }

    private void processProperty(Element el, BuildState bs) throws ParserException {
        try {
            bs.getContentHandler().startProperty(el.getLocalName());
            for (Element e : XmlUtil.getElements(el)) {
                if (XmlUtil.nodeMatches(e, XcalTags.parameters)) {
                    for (Element par : XmlUtil.getElements(e)) {
                        bs.getContentHandler().parameter(par.getLocalName(), XmlUtil.getElementContent(par));
                    }
                }
                if (this.processValue(e, bs)) continue;
                throw new ParserException("Bad property " + el, 0);
            }
            bs.getContentHandler().endProperty(el.getLocalName());
        }
        catch (SAXException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        catch (URISyntaxException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
    }

    private boolean processValue(Element el, BuildState bs) throws ParserException {
        try {
            if (XmlUtil.nodeMatches(el, XcalTags.recurVal)) {
                StringBuilder sb = new StringBuilder();
                String delim = "";
                for (Element re : XmlUtil.getElements(el)) {
                    sb.append(delim);
                    delim = ";";
                    sb.append(re.getLocalName().toUpperCase());
                    sb.append("=");
                    sb.append(XmlUtil.getElementContent(re));
                }
                bs.getContentHandler().propertyValue(sb.toString());
                return true;
            }
            if (XmlUtil.nodeMatches(el, XcalTags.binaryVal) || XmlUtil.nodeMatches(el, XcalTags.booleanVal) || XmlUtil.nodeMatches(el, XcalTags.calAddressVal) || XmlUtil.nodeMatches(el, XcalTags.dateVal) || XmlUtil.nodeMatches(el, XcalTags.dateTimeVal) || XmlUtil.nodeMatches(el, XcalTags.durationVal) || XmlUtil.nodeMatches(el, XcalTags.floatVal) || XmlUtil.nodeMatches(el, XcalTags.integerVal) || XmlUtil.nodeMatches(el, XcalTags.periodVal) || XmlUtil.nodeMatches(el, XcalTags.textVal) || XmlUtil.nodeMatches(el, XcalTags.timeVal) || XmlUtil.nodeMatches(el, XcalTags.uriVal) || XmlUtil.nodeMatches(el, XcalTags.utcOffsetVal)) {
                bs.getContentHandler().propertyValue(XmlUtil.getElementContent(el));
                return true;
            }
            return false;
        }
        catch (SAXException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        catch (URISyntaxException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        catch (ParseException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
        catch (IOException e) {
            throw new ParserException(e.getMessage(), 0, e);
        }
    }

    public final TimeZoneRegistry getRegistry() {
        return this.tzRegistry;
    }

    private void resolveTimezones(BuildState bs) throws IOException {
        for (Property property : bs.getDatesMissingTimezones()) {
            TimeZone timezone;
            Object tzParam = property.getParameter("TZID");
            if (tzParam == null || (timezone = this.tzRegistry.getTimeZone(((Content)tzParam).getValue())) == null) continue;
            String strDate = property.getValue();
            if (property instanceof DateProperty) {
                ((DateProperty)property).setTimeZone(timezone);
            } else if (property instanceof DateListProperty) {
                ((DateListProperty)property).setTimeZone(timezone);
            }
            try {
                property.setValue(strDate);
            }
            catch (ParseException e) {
                throw new CalendarException(e);
            }
            catch (URISyntaxException e) {
                throw new CalendarException(e);
            }
        }
    }

    boolean icalElement(Element el) {
        if (el == null) {
            return false;
        }
        String ns = el.getNamespaceURI();
        return ns != null && ns.equals("urn:ietf:params:xml:ns:icalendar-2.0");
    }

    boolean icalElement(Element el, String name) {
        if (!this.icalElement(el)) {
            return false;
        }
        String ln = el.getLocalName();
        if (ln == null) {
            return false;
        }
        return ln.equals(name);
    }

    protected Collection<Element> getChildren(Node nd) throws ParserException {
        try {
            return XmlUtil.getElements(nd);
        }
        catch (Throwable t) {
            throw new ParserException(t.getMessage(), 0);
        }
    }

    protected Element[] getChildrenArray(Node nd) throws ParserException {
        try {
            return XmlUtil.getElementsArray(nd);
        }
        catch (Throwable t) {
            throw new ParserException(t.getMessage(), 0);
        }
    }

    protected Element getOnlyChild(Node nd) throws ParserException {
        try {
            return XmlUtil.getOnlyElement(nd);
        }
        catch (Throwable t) {
            throw new ParserException(t.getMessage(), 0);
        }
    }

    protected String getElementContent(Element el) throws ParserException {
        try {
            return XmlUtil.getElementContent(el);
        }
        catch (Throwable t) {
            throw new ParserException(t.getMessage(), 0);
        }
    }

    protected boolean isEmpty(Element el) throws ParserException {
        try {
            return XmlUtil.isEmpty(el);
        }
        catch (Throwable t) {
            throw new ParserException(t.getMessage(), 0);
        }
    }
}

