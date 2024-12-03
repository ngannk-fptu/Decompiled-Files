/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAXEventRecorder
implements ContentHandler {
    private List _events = new LinkedList();

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.characters(ch, start, length);
            }
        });
    }

    @Override
    public void endDocument() throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.endDocument();
            }
        });
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.endElement(uri, localName, qName);
            }
        });
    }

    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.endPrefixMapping(prefix);
            }
        });
    }

    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.ignorableWhitespace(ch, start, length);
            }
        });
    }

    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.processingInstruction(target, data);
            }
        });
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(final String name) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.skippedEntity(name);
            }
        });
    }

    @Override
    public void startDocument() throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.startDocument();
            }
        });
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.startElement(uri, localName, qName, atts);
            }
        });
    }

    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this._events.add(new Event(){

            @Override
            public void replay(ContentHandler handler) throws SAXException {
                handler.startPrefixMapping(prefix, uri);
            }
        });
    }

    public void replay(ContentHandler handler) throws SAXException {
        for (Event e : this._events) {
            e.replay(handler);
        }
    }

    private static interface Event {
        public void replay(ContentHandler var1) throws SAXException;
    }
}

