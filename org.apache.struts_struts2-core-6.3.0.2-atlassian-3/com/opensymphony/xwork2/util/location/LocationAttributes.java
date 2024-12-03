/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class LocationAttributes {
    public static final String PREFIX = "loc";
    public static final String URI = "http://struts.apache.org/xwork/location";
    public static final String SRC_ATTR = "src";
    public static final String LINE_ATTR = "line";
    public static final String COL_ATTR = "column";
    public static final String Q_SRC_ATTR = "loc:src";
    public static final String Q_LINE_ATTR = "loc:line";
    public static final String Q_COL_ATTR = "loc:column";

    private LocationAttributes() {
    }

    public static Attributes addLocationAttributes(Locator locator, Attributes attrs) {
        if (locator == null || attrs.getIndex(URI, SRC_ATTR) != -1) {
            return attrs;
        }
        AttributesImpl newAttrs = attrs instanceof AttributesImpl ? (AttributesImpl)attrs : new AttributesImpl(attrs);
        newAttrs.addAttribute(URI, SRC_ATTR, Q_SRC_ATTR, "CDATA", locator.getSystemId());
        newAttrs.addAttribute(URI, LINE_ATTR, Q_LINE_ATTR, "CDATA", Integer.toString(locator.getLineNumber()));
        newAttrs.addAttribute(URI, COL_ATTR, Q_COL_ATTR, "CDATA", Integer.toString(locator.getColumnNumber()));
        return newAttrs;
    }

    public static Location getLocation(Attributes attrs, String description) {
        String src = attrs.getValue(URI, SRC_ATTR);
        if (src == null) {
            return Location.UNKNOWN;
        }
        return new LocationImpl(description, src, LocationAttributes.getLine(attrs), LocationAttributes.getColumn(attrs));
    }

    public static String getLocationString(Attributes attrs) {
        String src = attrs.getValue(URI, SRC_ATTR);
        if (src == null) {
            return "[unknown location]";
        }
        return src + ":" + attrs.getValue(URI, LINE_ATTR) + ":" + attrs.getValue(URI, COL_ATTR);
    }

    public static String getURI(Attributes attrs) {
        String src = attrs.getValue(URI, SRC_ATTR);
        return src != null ? src : "[unknown location]";
    }

    public static int getLine(Attributes attrs) {
        String line = attrs.getValue(URI, LINE_ATTR);
        return line != null ? Integer.parseInt(line) : -1;
    }

    public static int getColumn(Attributes attrs) {
        String col = attrs.getValue(URI, COL_ATTR);
        return col != null ? Integer.parseInt(col) : -1;
    }

    public static Location getLocation(Element elem, String description) {
        Attr srcAttr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        if (srcAttr == null) {
            return Location.UNKNOWN;
        }
        return new LocationImpl(description == null ? elem.getNodeName() : description, srcAttr.getValue(), LocationAttributes.getLine(elem), LocationAttributes.getColumn(elem));
    }

    public static Location getLocation(Element elem) {
        return LocationAttributes.getLocation(elem, null);
    }

    public static String getLocationString(Element elem) {
        Attr srcAttr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        if (srcAttr == null) {
            return "[unknown location]";
        }
        return srcAttr.getValue() + ":" + elem.getAttributeNS(URI, LINE_ATTR) + ":" + elem.getAttributeNS(URI, COL_ATTR);
    }

    public static String getURI(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        return attr != null ? attr.getValue() : "[unknown location]";
    }

    public static int getLine(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, LINE_ATTR);
        return attr != null ? Integer.parseInt(attr.getValue()) : -1;
    }

    public static int getColumn(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, COL_ATTR);
        return attr != null ? Integer.parseInt(attr.getValue()) : -1;
    }

    public static void remove(Element elem, boolean recurse) {
        elem.removeAttributeNS(URI, SRC_ATTR);
        elem.removeAttributeNS(URI, LINE_ATTR);
        elem.removeAttributeNS(URI, COL_ATTR);
        if (recurse) {
            NodeList children = elem.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child.getNodeType() != 1) continue;
                LocationAttributes.remove((Element)child, recurse);
            }
        }
    }

    public static class Pipe
    implements ContentHandler {
        private Locator locator;
        private ContentHandler nextHandler;

        public Pipe() {
        }

        public Pipe(ContentHandler next) {
            this.nextHandler = next;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
            this.nextHandler.setDocumentLocator(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            this.nextHandler.startDocument();
            this.nextHandler.startPrefixMapping(LocationAttributes.PREFIX, LocationAttributes.URI);
        }

        @Override
        public void endDocument() throws SAXException {
            this.endPrefixMapping(LocationAttributes.PREFIX);
            this.nextHandler.endDocument();
        }

        @Override
        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            this.nextHandler.startElement(uri, loc, raw, LocationAttributes.addLocationAttributes(this.locator, attrs));
        }

        @Override
        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            this.nextHandler.endElement(arg0, arg1, arg2);
        }

        @Override
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            this.nextHandler.startPrefixMapping(arg0, arg1);
        }

        @Override
        public void endPrefixMapping(String arg0) throws SAXException {
            this.nextHandler.endPrefixMapping(arg0);
        }

        @Override
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            this.nextHandler.characters(arg0, arg1, arg2);
        }

        @Override
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            this.nextHandler.ignorableWhitespace(arg0, arg1, arg2);
        }

        @Override
        public void processingInstruction(String arg0, String arg1) throws SAXException {
            this.nextHandler.processingInstruction(arg0, arg1);
        }

        @Override
        public void skippedEntity(String arg0) throws SAXException {
            this.nextHandler.skippedEntity(arg0);
        }
    }
}

