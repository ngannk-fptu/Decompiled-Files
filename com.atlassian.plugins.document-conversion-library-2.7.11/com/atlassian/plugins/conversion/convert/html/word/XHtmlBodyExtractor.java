/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.links.WebLink
 */
package com.atlassian.plugins.conversion.convert.html.word;

import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.plugins.conversion.convert.html.word.AbstractStringExtractor;
import com.atlassian.plugins.conversion.convert.html.word.StringExtractor;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XHtmlBodyExtractor
extends AbstractStringExtractor
implements StringExtractor {
    @Override
    protected ContentHandler wrapContentHandler(ContentHandler contentHandler) {
        return new CustomContentHandler(contentHandler);
    }

    private static class CustomContentHandler
    implements ContentHandler {
        private final ContentHandler parent;
        private static final String BODY_ELEMENT_NAME = "body";
        private boolean isInBody = false;

        public CustomContentHandler(ContentHandler parent) {
            this.parent = parent;
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
            if (this.isInBody) {
                String url;
                if (qualifiedName.equalsIgnoreCase("a") && (url = attrs.getValue("href")) != null && !WebLink.isValidURL((String)url)) {
                    AttributesImpl newAttrs = new AttributesImpl(attrs);
                    newAttrs.removeAttribute(newAttrs.getIndex("href"));
                    attrs = newAttrs;
                }
                this.parent.startElement(namespaceURI, localName, qualifiedName, attrs);
            }
            if (this.isBodyElement(qualifiedName)) {
                this.parent.startElement(namespaceURI, localName, "div", attrs);
                this.isInBody = true;
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
            if (this.isBodyElement(qualifiedName)) {
                this.parent.endElement(namespaceURI, localName, "div");
                this.isInBody = false;
            }
            if (this.isInBody) {
                this.parent.endElement(namespaceURI, localName, qualifiedName);
            }
        }

        @Override
        public void startDocument() throws SAXException {
            if (this.isInBody) {
                this.parent.startDocument();
            }
        }

        @Override
        public void endDocument() throws SAXException {
            if (this.isInBody) {
                this.parent.endDocument();
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            if (this.isInBody) {
                this.parent.startPrefixMapping(prefix, uri);
            }
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            if (this.isInBody) {
                this.parent.endPrefixMapping(prefix);
            }
        }

        @Override
        public void characters(char[] text, int start, int length) throws SAXException {
            if (this.isInBody) {
                this.parent.characters(text, start, length);
            }
        }

        @Override
        public void ignorableWhitespace(char[] text, int start, int length) throws SAXException {
            if (this.isInBody) {
                this.parent.ignorableWhitespace(text, start, length);
            }
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            if (this.isInBody) {
                this.parent.processingInstruction(target, data);
            }
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            if (this.isInBody) {
                this.parent.skippedEntity(name);
            }
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            if (this.isInBody) {
                this.parent.setDocumentLocator(locator);
            }
        }

        private boolean isBodyElement(String qualifiedName) {
            return qualifiedName.equalsIgnoreCase(BODY_ELEMENT_NAME);
        }
    }
}

