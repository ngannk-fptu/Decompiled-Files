/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.cyberneko.html.parsers.SAXParser
 */
package org.outerj.daisy.diff.helper;

import java.io.IOException;
import org.cyberneko.html.parsers.SAXParser;
import org.outerj.daisy.diff.helper.CleanBrokenAttributeQNamesHandler;
import org.outerj.daisy.diff.helper.MergeCharacterEventsHandler;
import org.outerj.daisy.diff.helper.SaxBuffer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class NekoHtmlParser {
    public SaxBuffer parse(InputSource is) throws IOException, SAXException {
        SaxBuffer buffer = new SaxBuffer();
        this.parse(is, buffer);
        return buffer;
    }

    public void parse(InputSource is, ContentHandler consumer) throws IOException, SAXException {
        if (is == null) {
            throw new NullPointerException("is argument is required.");
        }
        SAXParser parser = new SAXParser();
        parser.setFeature("http://xml.org/sax/features/namespaces", true);
        parser.setFeature("http://cyberneko.org/html/features/override-namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/insert-namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
        parser.setProperty("http://cyberneko.org/html/properties/default-encoding", (Object)"UTF-8");
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"lower");
        parser.setProperty("http://cyberneko.org/html/properties/names/attrs", (Object)"lower");
        parser.setContentHandler((ContentHandler)new RemoveNamespacesHandler(new MergeCharacterEventsHandler(new CleanBrokenAttributeQNamesHandler(consumer))));
        parser.parse(is);
    }

    static class RemoveNamespacesHandler
    implements ContentHandler {
        private ContentHandler consumer;

        public RemoveNamespacesHandler(ContentHandler consumer) {
            this.consumer = consumer;
        }

        @Override
        public void endDocument() throws SAXException {
            this.consumer.endDocument();
        }

        @Override
        public void startDocument() throws SAXException {
            this.consumer.startDocument();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.consumer.characters(ch, start, length);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            this.consumer.ignorableWhitespace(ch, start, length);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.consumer.setDocumentLocator(locator);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            this.consumer.endElement("", localName, localName);
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            AttributesImpl newAtts = new AttributesImpl(atts);
            for (int i = 0; i < atts.getLength(); ++i) {
                newAtts.setURI(i, "");
                newAtts.setQName(i, newAtts.getLocalName(i));
            }
            this.consumer.startElement("", localName, localName, atts);
        }
    }
}

