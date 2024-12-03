/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.MessageElement;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class SAX2EventRecorder {
    private static final Integer Z = new Integer(0);
    private static final Integer STATE_SET_DOCUMENT_LOCATOR = new Integer(0);
    private static final Integer STATE_START_DOCUMENT = new Integer(1);
    private static final Integer STATE_END_DOCUMENT = new Integer(2);
    private static final Integer STATE_START_PREFIX_MAPPING = new Integer(3);
    private static final Integer STATE_END_PREFIX_MAPPING = new Integer(4);
    private static final Integer STATE_START_ELEMENT = new Integer(5);
    private static final Integer STATE_END_ELEMENT = new Integer(6);
    private static final Integer STATE_CHARACTERS = new Integer(7);
    private static final Integer STATE_IGNORABLE_WHITESPACE = new Integer(8);
    private static final Integer STATE_PROCESSING_INSTRUCTION = new Integer(9);
    private static final Integer STATE_SKIPPED_ENTITY = new Integer(10);
    private static final Integer STATE_NEWELEMENT = new Integer(11);
    private static final Integer STATE_START_DTD = new Integer(12);
    private static final Integer STATE_END_DTD = new Integer(13);
    private static final Integer STATE_START_ENTITY = new Integer(14);
    private static final Integer STATE_END_ENTITY = new Integer(15);
    private static final Integer STATE_START_CDATA = new Integer(16);
    private static final Integer STATE_END_CDATA = new Integer(17);
    private static final Integer STATE_COMMENT = new Integer(18);
    Locator locator;
    objArrayVector events = new objArrayVector();

    public void clear() {
        this.locator = null;
        this.events = new objArrayVector();
    }

    public int getLength() {
        return this.events.getLength();
    }

    public int setDocumentLocator(Locator p1) {
        this.locator = p1;
        return this.events.add(STATE_SET_DOCUMENT_LOCATOR, Z, Z, Z, Z);
    }

    public int startDocument() {
        return this.events.add(STATE_START_DOCUMENT, Z, Z, Z, Z);
    }

    public int endDocument() {
        return this.events.add(STATE_END_DOCUMENT, Z, Z, Z, Z);
    }

    public int startPrefixMapping(String p1, String p2) {
        return this.events.add(STATE_START_PREFIX_MAPPING, p1, p2, Z, Z);
    }

    public int endPrefixMapping(String p1) {
        return this.events.add(STATE_END_PREFIX_MAPPING, p1, Z, Z, Z);
    }

    public int startElement(String p1, String p2, String p3, Attributes p4) {
        return this.events.add(STATE_START_ELEMENT, p1, p2, p3, p4);
    }

    public int endElement(String p1, String p2, String p3) {
        return this.events.add(STATE_END_ELEMENT, p1, p2, p3, Z);
    }

    public int characters(char[] p1, int p2, int p3) {
        return this.events.add(STATE_CHARACTERS, SAX2EventRecorder.clone(p1, p2, p3), Z, Z, Z);
    }

    public int ignorableWhitespace(char[] p1, int p2, int p3) {
        return this.events.add(STATE_IGNORABLE_WHITESPACE, SAX2EventRecorder.clone(p1, p2, p3), Z, Z, Z);
    }

    public int processingInstruction(String p1, String p2) {
        return this.events.add(STATE_PROCESSING_INSTRUCTION, p1, p2, Z, Z);
    }

    public int skippedEntity(String p1) {
        return this.events.add(STATE_SKIPPED_ENTITY, p1, Z, Z, Z);
    }

    public void startDTD(String name, String publicId, String systemId) {
        this.events.add(STATE_START_DTD, name, publicId, systemId, Z);
    }

    public void endDTD() {
        this.events.add(STATE_END_DTD, Z, Z, Z, Z);
    }

    public void startEntity(String name) {
        this.events.add(STATE_START_ENTITY, name, Z, Z, Z);
    }

    public void endEntity(String name) {
        this.events.add(STATE_END_ENTITY, name, Z, Z, Z);
    }

    public void startCDATA() {
        this.events.add(STATE_START_CDATA, Z, Z, Z, Z);
    }

    public void endCDATA() {
        this.events.add(STATE_END_CDATA, Z, Z, Z, Z);
    }

    public void comment(char[] ch, int start, int length) {
        this.events.add(STATE_COMMENT, SAX2EventRecorder.clone(ch, start, length), Z, Z, Z);
    }

    public int newElement(MessageElement elem) {
        return this.events.add(STATE_NEWELEMENT, elem, Z, Z, Z);
    }

    public void replay(ContentHandler handler) throws SAXException {
        if (this.events.getLength() > 0) {
            this.replay(0, this.events.getLength() - 1, handler);
        }
    }

    public void replay(int start, int stop, ContentHandler handler) throws SAXException {
        if (start == 0 && stop == -1) {
            this.replay(handler);
            return;
        }
        if (stop + 1 > this.events.getLength() || stop < start) {
            return;
        }
        LexicalHandler lexicalHandler = null;
        if (handler instanceof LexicalHandler) {
            lexicalHandler = (LexicalHandler)((Object)handler);
        }
        for (int n = start; n <= stop; ++n) {
            char[] data;
            Object event = this.events.get(n, 0);
            if (event == STATE_START_ELEMENT) {
                handler.startElement((String)this.events.get(n, 1), (String)this.events.get(n, 2), (String)this.events.get(n, 3), (Attributes)this.events.get(n, 4));
                continue;
            }
            if (event == STATE_END_ELEMENT) {
                handler.endElement((String)this.events.get(n, 1), (String)this.events.get(n, 2), (String)this.events.get(n, 3));
                continue;
            }
            if (event == STATE_CHARACTERS) {
                data = (char[])this.events.get(n, 1);
                handler.characters(data, 0, data.length);
                continue;
            }
            if (event == STATE_IGNORABLE_WHITESPACE) {
                data = (char[])this.events.get(n, 1);
                handler.ignorableWhitespace(data, 0, data.length);
                continue;
            }
            if (event == STATE_PROCESSING_INSTRUCTION) {
                handler.processingInstruction((String)this.events.get(n, 1), (String)this.events.get(n, 2));
                continue;
            }
            if (event == STATE_SKIPPED_ENTITY) {
                handler.skippedEntity((String)this.events.get(n, 1));
                continue;
            }
            if (event == STATE_SET_DOCUMENT_LOCATOR) {
                handler.setDocumentLocator(this.locator);
                continue;
            }
            if (event == STATE_START_DOCUMENT) {
                handler.startDocument();
                continue;
            }
            if (event == STATE_END_DOCUMENT) {
                handler.endDocument();
                continue;
            }
            if (event == STATE_START_PREFIX_MAPPING) {
                handler.startPrefixMapping((String)this.events.get(n, 1), (String)this.events.get(n, 2));
                continue;
            }
            if (event == STATE_END_PREFIX_MAPPING) {
                handler.endPrefixMapping((String)this.events.get(n, 1));
                continue;
            }
            if (event == STATE_START_DTD && lexicalHandler != null) {
                lexicalHandler.startDTD((String)this.events.get(n, 1), (String)this.events.get(n, 2), (String)this.events.get(n, 3));
                continue;
            }
            if (event == STATE_END_DTD && lexicalHandler != null) {
                lexicalHandler.endDTD();
                continue;
            }
            if (event == STATE_START_ENTITY && lexicalHandler != null) {
                lexicalHandler.startEntity((String)this.events.get(n, 1));
                continue;
            }
            if (event == STATE_END_ENTITY && lexicalHandler != null) {
                lexicalHandler.endEntity((String)this.events.get(n, 1));
                continue;
            }
            if (event == STATE_START_CDATA && lexicalHandler != null) {
                lexicalHandler.startCDATA();
                continue;
            }
            if (event == STATE_END_CDATA && lexicalHandler != null) {
                lexicalHandler.endCDATA();
                continue;
            }
            if (event == STATE_COMMENT && lexicalHandler != null) {
                data = (char[])this.events.get(n, 1);
                lexicalHandler.comment(data, 0, data.length);
                continue;
            }
            if (event != STATE_NEWELEMENT || !(handler instanceof DeserializationContext)) continue;
            DeserializationContext context = (DeserializationContext)handler;
            context.setCurElement((MessageElement)this.events.get(n, 1));
        }
    }

    private static char[] clone(char[] in, int off, int len) {
        char[] out = new char[len];
        System.arraycopy(in, off, out, 0, len);
        return out;
    }

    class objArrayVector {
        private int RECORD_SIZE = 5;
        private int currentSize = 0;
        private Object[] objarray = new Object[50 * this.RECORD_SIZE];

        objArrayVector() {
        }

        public int add(Object p1, Object p2, Object p3, Object p4, Object p5) {
            if (this.currentSize == this.objarray.length) {
                Object[] newarray = new Object[this.currentSize * 2];
                System.arraycopy(this.objarray, 0, newarray, 0, this.currentSize);
                this.objarray = newarray;
            }
            int pos = this.currentSize / this.RECORD_SIZE;
            this.objarray[this.currentSize++] = p1;
            this.objarray[this.currentSize++] = p2;
            this.objarray[this.currentSize++] = p3;
            this.objarray[this.currentSize++] = p4;
            this.objarray[this.currentSize++] = p5;
            return pos;
        }

        public Object get(int pos, int fld) {
            return this.objarray[pos * this.RECORD_SIZE + fld];
        }

        public int getLength() {
            return this.currentSize / this.RECORD_SIZE;
        }
    }
}

