/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.sax.SafeContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XHTMLContentHandler
extends SafeContentHandler {
    public static final String XHTML = "http://www.w3.org/1999/xhtml";
    private static final char[] NL = new char[]{'\n'};
    private static final char[] TAB = new char[]{'\t'};
    private static final Set<String> HEAD = XHTMLContentHandler.unmodifiableSet("title", "link", "base", "meta", "script");
    private static final Set<String> AUTO = XHTMLContentHandler.unmodifiableSet("head", "frameset");
    private static final Set<String> INDENT = XHTMLContentHandler.unmodifiableSet("li", "dd", "dt", "td", "th", "frame");
    public static final Set<String> ENDLINE = XHTMLContentHandler.unmodifiableSet("p", "h1", "h2", "h3", "h4", "h5", "h6", "div", "ul", "ol", "dl", "pre", "hr", "blockquote", "address", "fieldset", "table", "form", "noscript", "li", "dt", "dd", "noframes", "br", "tr", "select", "option", "link", "script");
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
    private final Metadata metadata;
    private boolean documentStarted = false;
    private boolean headStarted = false;
    private boolean headEnded = false;
    private boolean useFrameset = false;

    private static Set<String> unmodifiableSet(String ... elements) {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(elements)));
    }

    public XHTMLContentHandler(ContentHandler handler, Metadata metadata) {
        super(handler);
        this.metadata = metadata;
    }

    @Override
    public void startDocument() throws SAXException {
        if (!this.documentStarted) {
            this.documentStarted = true;
            super.startDocument();
            this.startPrefixMapping("", XHTML);
        }
    }

    private void lazyStartHead() throws SAXException {
        if (!this.headStarted) {
            this.headStarted = true;
            AttributesImpl htmlAttrs = new AttributesImpl();
            String lang = this.metadata.get("Content-Language");
            if (lang != null) {
                htmlAttrs.addAttribute("", "lang", "lang", "CDATA", lang);
            }
            super.startElement(XHTML, "html", "html", htmlAttrs);
            this.newline();
            super.startElement(XHTML, "head", "head", EMPTY_ATTRIBUTES);
            this.newline();
        }
    }

    private void lazyEndHead(boolean isFrameset) throws SAXException {
        this.lazyStartHead();
        if (!this.headEnded) {
            this.headEnded = true;
            this.useFrameset = isFrameset;
            for (String name : this.metadata.names()) {
                if (name.equals("title")) continue;
                for (String value : this.metadata.getValues(name)) {
                    if (value == null) continue;
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute("", "name", "name", "CDATA", name);
                    attributes.addAttribute("", "content", "content", "CDATA", value);
                    super.startElement(XHTML, "meta", "meta", attributes);
                    super.endElement(XHTML, "meta", "meta");
                    this.newline();
                }
            }
            super.startElement(XHTML, "title", "title", EMPTY_ATTRIBUTES);
            String title = this.metadata.get(TikaCoreProperties.TITLE);
            if (title != null && title.length() > 0) {
                char[] titleChars = title.toCharArray();
                super.characters(titleChars, 0, titleChars.length);
            } else {
                super.characters(new char[0], 0, 0);
            }
            super.endElement(XHTML, "title", "title");
            this.newline();
            super.endElement(XHTML, "head", "head");
            this.newline();
            if (this.useFrameset) {
                super.startElement(XHTML, "frameset", "frameset", EMPTY_ATTRIBUTES);
            } else {
                super.startElement(XHTML, "body", "body", EMPTY_ATTRIBUTES);
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {
        this.lazyEndHead(this.useFrameset);
        if (this.useFrameset) {
            super.endElement(XHTML, "frameset", "frameset");
        } else {
            super.endElement(XHTML, "body", "body");
        }
        super.endElement(XHTML, "html", "html");
        this.endPrefixMapping("");
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String local, String name, Attributes attributes) throws SAXException {
        if (name.equals("frameset")) {
            this.lazyEndHead(true);
        } else if (!AUTO.contains(name)) {
            if (HEAD.contains(name)) {
                this.lazyStartHead();
            } else {
                this.lazyEndHead(false);
            }
            if (XHTML.equals(uri) && INDENT.contains(name)) {
                this.ignorableWhitespace(TAB, 0, TAB.length);
            }
            super.startElement(uri, local, name, attributes);
        }
    }

    @Override
    public void endElement(String uri, String local, String name) throws SAXException {
        if (!AUTO.contains(name)) {
            super.endElement(uri, local, name);
            if (XHTML.equals(uri) && ENDLINE.contains(name)) {
                this.newline();
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.lazyEndHead(this.useFrameset);
        super.characters(ch, start, length);
    }

    public void startElement(String name) throws SAXException {
        this.startElement(XHTML, name, name, EMPTY_ATTRIBUTES);
    }

    public void startElement(String name, String attribute, String value) throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", attribute, attribute, "CDATA", value);
        this.startElement(XHTML, name, name, attributes);
    }

    public void startElement(String name, AttributesImpl attributes) throws SAXException {
        this.startElement(XHTML, name, name, attributes);
    }

    public void endElement(String name) throws SAXException {
        this.endElement(XHTML, name, name);
    }

    public void characters(String characters) throws SAXException {
        if (characters != null && characters.length() > 0) {
            this.characters(characters.toCharArray(), 0, characters.length());
        }
    }

    public void newline() throws SAXException {
        this.ignorableWhitespace(NL, 0, NL.length);
    }

    public void element(String name, String value) throws SAXException {
        if (value != null && value.length() > 0) {
            this.startElement(name);
            this.characters(value);
            this.endElement(name);
        }
    }

    @Override
    protected boolean isInvalid(int ch) {
        if (super.isInvalid(ch)) {
            return true;
        }
        return 127 <= ch && ch <= 159;
    }
}

