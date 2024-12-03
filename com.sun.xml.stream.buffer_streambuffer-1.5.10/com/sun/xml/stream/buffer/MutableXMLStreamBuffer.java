/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.stream.buffer;

import com.sun.xml.stream.buffer.FragmentedArray;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.sax.SAXBufferCreator;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class MutableXMLStreamBuffer
extends XMLStreamBuffer {
    public static final int DEFAULT_ARRAY_SIZE = 512;

    public MutableXMLStreamBuffer() {
        this(512);
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public MutableXMLStreamBuffer(int size) {
        this._structure = new FragmentedArray<byte[]>(new byte[size]);
        this._structureStrings = new FragmentedArray<String[]>(new String[size]);
        this._contentCharactersBuffer = new FragmentedArray<char[]>(new char[4096]);
        this._contentObjects = new FragmentedArray<Object[]>(new Object[size]);
        ((byte[])this._structure.getArray())[0] = -112;
    }

    public void createFromXMLStreamReader(XMLStreamReader reader) throws XMLStreamException {
        this.reset();
        StreamReaderBufferCreator c = new StreamReaderBufferCreator(this);
        c.create(reader);
    }

    public XMLStreamWriter createFromXMLStreamWriter() {
        this.reset();
        return new StreamWriterBufferCreator(this);
    }

    public SAXBufferCreator createFromSAXBufferCreator() {
        this.reset();
        SAXBufferCreator c = new SAXBufferCreator();
        c.setBuffer(this);
        return c;
    }

    public void createFromXMLReader(XMLReader reader, InputStream in) throws SAXException, IOException {
        this.createFromXMLReader(reader, in, null);
    }

    public void createFromXMLReader(XMLReader reader, InputStream in, String systemId) throws SAXException, IOException {
        this.reset();
        SAXBufferCreator c = new SAXBufferCreator(this);
        reader.setContentHandler(c);
        reader.setDTDHandler(c);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", c);
        c.create(reader, in, systemId);
    }

    public void reset() {
        this._contentObjectsPtr = 0;
        this._contentCharactersBufferPtr = 0;
        this._structureStringsPtr = 0;
        this._structurePtr = 0;
        ((byte[])this._structure.getArray())[0] = -112;
        this._contentObjects.setNext(null);
        Object[] o = (Object[])this._contentObjects.getArray();
        for (int i = 0; i < o.length && o[i] != null; ++i) {
            o[i] = null;
        }
        this.treeCount = 0;
    }

    protected void setHasInternedStrings(boolean hasInternedStrings) {
        this._hasInternedStrings = hasInternedStrings;
    }
}

