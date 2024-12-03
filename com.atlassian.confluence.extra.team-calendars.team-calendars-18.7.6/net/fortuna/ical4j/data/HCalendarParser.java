/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.CalendarException;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class HCalendarParser
implements CalendarParser {
    private static final Logger LOG = LoggerFactory.getLogger(HCalendarParser.class);
    private static final DocumentBuilderFactory BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();
    private static final XPathExpression XPATH_METHOD;
    private static final XPathExpression XPATH_VEVENTS;
    private static final XPathExpression XPATH_DTSTART;
    private static final XPathExpression XPATH_DTEND;
    private static final XPathExpression XPATH_DURATION;
    private static final XPathExpression XPATH_SUMMARY;
    private static final XPathExpression XPATH_UID;
    private static final XPathExpression XPATH_DTSTAMP;
    private static final XPathExpression XPATH_CATEGORY;
    private static final XPathExpression XPATH_LOCATION;
    private static final XPathExpression XPATH_URL;
    private static final XPathExpression XPATH_DESCRIPTION;
    private static final XPathExpression XPATH_LAST_MODIFIED;
    private static final XPathExpression XPATH_STATUS;
    private static final XPathExpression XPATH_CLASS;
    private static final XPathExpression XPATH_ATTENDEE;
    private static final XPathExpression XPATH_CONTACT;
    private static final XPathExpression XPATH_ORGANIZER;
    private static final XPathExpression XPATH_SEQUENCE;
    private static final XPathExpression XPATH_ATTACH;
    private static final String HCAL_DATE_PATTERN = "yyyy-MM-dd";
    private static final SimpleDateFormat HCAL_DATE_FORMAT;
    private static final String HCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";
    private static final SimpleDateFormat HCAL_DATE_TIME_FORMAT;

    private static XPathExpression compileExpression(String expr) {
        try {
            return XPATH.compile(expr);
        }
        catch (XPathException e) {
            throw new CalendarException(e);
        }
    }

    @Override
    public void parse(InputStream in, ContentHandler handler) throws IOException, ParserException {
        this.parse(new InputSource(in), handler);
    }

    @Override
    public void parse(Reader in, ContentHandler handler) throws IOException, ParserException {
        this.parse(new InputSource(in), handler);
    }

    private void parse(InputSource in, ContentHandler handler) throws IOException, ParserException {
        try {
            Document d = BUILDER_FACTORY.newDocumentBuilder().parse(in);
            this.buildCalendar(d, handler);
        }
        catch (ParserConfigurationException e) {
            throw new CalendarException(e);
        }
        catch (SAXException e) {
            if (e instanceof SAXParseException) {
                SAXParseException pe = (SAXParseException)e;
                throw new ParserException("Could not parse XML", pe.getLineNumber(), e);
            }
            throw new ParserException(e.getMessage(), -1, e);
        }
    }

    private static NodeList findNodes(XPathExpression expr, Object context) throws ParserException {
        try {
            return (NodeList)expr.evaluate(context, XPathConstants.NODESET);
        }
        catch (XPathException e) {
            throw new ParserException("Unable to find nodes", -1, e);
        }
    }

    private static Node findNode(XPathExpression expr, Object context) throws ParserException {
        try {
            return (Node)expr.evaluate(context, XPathConstants.NODE);
        }
        catch (XPathException e) {
            throw new ParserException("Unable to find node", -1, e);
        }
    }

    private static List<Element> findElements(XPathExpression expr, Object context) throws ParserException {
        NodeList nodes = HCalendarParser.findNodes(expr, context);
        ArrayList<Element> elements = new ArrayList<Element>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node n = nodes.item(i);
            if (!(n instanceof Element)) continue;
            elements.add((Element)n);
        }
        return elements;
    }

    private static Element findElement(XPathExpression expr, Object context) throws ParserException {
        Node n = HCalendarParser.findNode(expr, context);
        if (!(n instanceof Element)) {
            return null;
        }
        return (Element)n;
    }

    private static String getTextContent(Element element) throws ParserException {
        try {
            String content = element.getFirstChild().getNodeValue();
            if (content != null) {
                return content.trim().replaceAll("\\s+", " ");
            }
            return null;
        }
        catch (DOMException e) {
            throw new ParserException("Unable to get text content for element " + element.getNodeName(), -1, e);
        }
    }

    private void buildCalendar(Document d, ContentHandler handler) throws ParserException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Building calendar");
        }
        handler.startCalendar();
        handler.startProperty("VERSION");
        try {
            handler.propertyValue(Version.VERSION_2_0.getValue());
            handler.endProperty("VERSION");
        }
        catch (IOException | URISyntaxException | ParseException e) {
            LOG.warn("Caught exception", (Throwable)e);
        }
        Element method = HCalendarParser.findElement(XPATH_METHOD, d);
        if (method != null) {
            this.buildProperty(method, "METHOD", handler);
        }
        List<Element> vevents = HCalendarParser.findElements(XPATH_VEVENTS, d);
        for (Element vevent : vevents) {
            this.buildEvent(vevent, handler);
        }
        handler.endCalendar();
    }

    private void buildEvent(Element element, ContentHandler handler) throws ParserException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Building event");
        }
        handler.startComponent("VEVENT");
        this.buildProperty(HCalendarParser.findElement(XPATH_DTSTART, element), "DTSTART", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_DTEND, element), "DTEND", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_DURATION, element), "DURATION", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_SUMMARY, element), "SUMMARY", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_UID, element), "UID", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_DTSTAMP, element), "DTSTAMP", handler);
        List<Element> categories = HCalendarParser.findElements(XPATH_CATEGORY, element);
        for (Element category : categories) {
            this.buildProperty(category, "CATEGORIES", handler);
        }
        this.buildProperty(HCalendarParser.findElement(XPATH_LOCATION, element), "LOCATION", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_URL, element), "URL", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_DESCRIPTION, element), "DESCRIPTION", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_LAST_MODIFIED, element), "LAST-MODIFIED", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_STATUS, element), "STATUS", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_CLASS, element), "CLASS", handler);
        List<Element> attendees = HCalendarParser.findElements(XPATH_ATTENDEE, element);
        for (Element attendee : attendees) {
            this.buildProperty(attendee, "ATTENDEE", handler);
        }
        this.buildProperty(HCalendarParser.findElement(XPATH_CONTACT, element), "CONTACT", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_ORGANIZER, element), "ORGANIZER", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_SEQUENCE, element), "SEQUENCE", handler);
        this.buildProperty(HCalendarParser.findElement(XPATH_ATTACH, element), "ATTACH", handler);
        handler.endComponent("VEVENT");
    }

    private void buildProperty(Element element, String propName, ContentHandler handler) throws ParserException {
        String lang;
        String value;
        if (element == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Building property " + propName);
        }
        String className = HCalendarParser.className(propName);
        String elementName = element.getLocalName().toLowerCase();
        if (elementName.equals("abbr")) {
            value = element.getAttribute("title");
            if (StringUtils.isBlank((CharSequence)value)) {
                throw new ParserException("Abbr element '" + className + "' requires a non-empty title", -1);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting value '" + value + "' from title attribute");
            }
        } else if (HCalendarParser.isHeaderElement(elementName)) {
            value = element.getAttribute("title");
            if (!StringUtils.isBlank((CharSequence)value)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting value '" + value + "' from title attribute");
                }
            } else {
                value = HCalendarParser.getTextContent(element);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting value '" + value + "' from text content");
                }
            }
        } else if (elementName.equals("a") && HCalendarParser.isUrlProperty(propName)) {
            value = element.getAttribute("href");
            if (StringUtils.isBlank((CharSequence)value)) {
                throw new ParserException("A element '" + className + "' requires a non-empty href", -1);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting value '" + value + "' from href attribute");
            }
        } else if (elementName.equals("img")) {
            if (HCalendarParser.isUrlProperty(propName)) {
                value = element.getAttribute("src");
                if (StringUtils.isBlank((CharSequence)value)) {
                    throw new ParserException("Img element '" + className + "' requires a non-empty src", -1);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting value '" + value + "' from src attribute");
                }
            } else {
                value = element.getAttribute("alt");
                if (StringUtils.isBlank((CharSequence)value)) {
                    throw new ParserException("Img element '" + className + "' requires a non-empty alt", -1);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting value '" + value + "' from alt attribute");
                }
            }
        } else {
            value = HCalendarParser.getTextContent(element);
            if (!StringUtils.isBlank((CharSequence)value) && LOG.isDebugEnabled()) {
                LOG.debug("Setting value '" + value + "' from text content");
            }
        }
        if (StringUtils.isBlank((CharSequence)value)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skipping property with empty value");
            }
            return;
        }
        handler.startProperty(propName);
        if (HCalendarParser.isDateProperty(propName)) {
            try {
                Date date = HCalendarParser.icalDate(value);
                value = date.toString();
                if (!(date instanceof DateTime)) {
                    try {
                        handler.parameter("VALUE", Value.DATE.getValue());
                    }
                    catch (URISyntaxException e) {
                        LOG.warn("Caught exception", (Throwable)e);
                    }
                }
            }
            catch (ParseException e) {
                throw new ParserException("Malformed date value for element '" + className + "'", -1, e);
            }
        }
        if (HCalendarParser.isTextProperty(propName) && !StringUtils.isBlank((CharSequence)(lang = element.getAttributeNS("http://www.w3.org/XML/1998/namespace", "lang")))) {
            try {
                handler.parameter("LANGUAGE", lang);
            }
            catch (URISyntaxException e) {
                LOG.warn("Caught exception", (Throwable)e);
            }
        }
        try {
            handler.propertyValue(value);
            handler.endProperty(propName);
        }
        catch (URISyntaxException e) {
            throw new ParserException("Malformed URI value for element '" + className + "'", -1, e);
        }
        catch (ParseException e) {
            throw new ParserException("Malformed value for element '" + className + "'", -1, e);
        }
        catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    private static String className(String propName) {
        return propName.toLowerCase();
    }

    private static boolean isHeaderElement(String name) {
        return name.equals("h1") || name.equals("h2") || name.equals("h3") || name.equals("h4") || name.equals("h5") || name.equals("h6");
    }

    private static boolean isDateProperty(String name) {
        return name.equals("DTSTART") || name.equals("DTEND") || name.equals("DTSTAMP") || name.equals("LAST-MODIFIED");
    }

    private static boolean isUrlProperty(String name) {
        return name.equals("URL");
    }

    private static boolean isTextProperty(String name) {
        return name.equals("SUMMARY") || name.equals("LOCATION") || name.equals("CATEGORIES") || name.equals("DESCRIPTION") || name.equals("ATTENDEE") || name.equals("CONTACT") || name.equals("ORGANIZER");
    }

    private static Date icalDate(String original) throws ParseException {
        if (original.indexOf(84) == -1) {
            try {
                if (original.indexOf(45) == -1) {
                    return new Date(original);
                }
            }
            catch (ParseException e) {
                LOG.warn("Caught exception", (Throwable)e);
            }
            return new Date(HCAL_DATE_FORMAT.parse(original));
        }
        try {
            return new DateTime(original);
        }
        catch (ParseException e) {
            String normalized;
            LOG.warn("Caught exception", (Throwable)e);
            if (LOG.isDebugEnabled()) {
                LOG.debug("normalizing date-time " + original);
            }
            if (original.charAt(original.length() - 1) == 'Z') {
                normalized = original.replaceAll("Z", "GMT-00:00");
            } else if (!(original.contains("GMT") || original.charAt(original.length() - 6) != '+' && original.charAt(original.length() - 6) != '-')) {
                String tzId = "GMT" + original.substring(original.length() - 6);
                normalized = original.substring(0, original.length() - 6) + tzId;
            } else {
                normalized = original;
            }
            DateTime dt = new DateTime(HCAL_DATE_TIME_FORMAT.parse(normalized));
            dt.setUtc(true);
            return dt;
        }
    }

    static {
        HCAL_DATE_FORMAT = new SimpleDateFormat(HCAL_DATE_PATTERN);
        HCAL_DATE_TIME_FORMAT = new SimpleDateFormat(HCAL_DATE_TIME_PATTERN);
        BUILDER_FACTORY.setNamespaceAware(true);
        BUILDER_FACTORY.setIgnoringComments(true);
        XPATH_METHOD = HCalendarParser.compileExpression("//*[contains(@class, 'method')]");
        XPATH_VEVENTS = HCalendarParser.compileExpression("//*[contains(@class, 'vevent')]");
        XPATH_DTSTART = HCalendarParser.compileExpression(".//*[contains(@class, 'dtstart')]");
        XPATH_DTEND = HCalendarParser.compileExpression(".//*[contains(@class, 'dtend')]");
        XPATH_DURATION = HCalendarParser.compileExpression(".//*[contains(@class, 'duration')]");
        XPATH_SUMMARY = HCalendarParser.compileExpression(".//*[contains(@class, 'summary')]");
        XPATH_UID = HCalendarParser.compileExpression(".//*[contains(@class, 'uid')]");
        XPATH_DTSTAMP = HCalendarParser.compileExpression(".//*[contains(@class, 'dtstamp')]");
        XPATH_CATEGORY = HCalendarParser.compileExpression(".//*[contains(@class, 'category')]");
        XPATH_LOCATION = HCalendarParser.compileExpression(".//*[contains(@class, 'location')]");
        XPATH_URL = HCalendarParser.compileExpression(".//*[contains(@class, 'url')]");
        XPATH_DESCRIPTION = HCalendarParser.compileExpression(".//*[contains(@class, 'description')]");
        XPATH_LAST_MODIFIED = HCalendarParser.compileExpression(".//*[contains(@class, 'last-modified')]");
        XPATH_STATUS = HCalendarParser.compileExpression(".//*[contains(@class, 'status')]");
        XPATH_CLASS = HCalendarParser.compileExpression(".//*[contains(@class, 'class')]");
        XPATH_ATTENDEE = HCalendarParser.compileExpression(".//*[contains(@class, 'attendee')]");
        XPATH_CONTACT = HCalendarParser.compileExpression(".//*[contains(@class, 'contact')]");
        XPATH_ORGANIZER = HCalendarParser.compileExpression(".//*[contains(@class, 'organizer')]");
        XPATH_SEQUENCE = HCalendarParser.compileExpression(".//*[contains(@class, 'sequence')]");
        XPATH_ATTACH = HCalendarParser.compileExpression(".//*[contains(@class, 'attach')]");
    }
}

