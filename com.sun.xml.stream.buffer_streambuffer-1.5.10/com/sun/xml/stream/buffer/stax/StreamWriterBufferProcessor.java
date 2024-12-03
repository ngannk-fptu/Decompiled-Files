/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.staxex.Base64Data
 *  org.jvnet.staxex.XMLStreamWriterEx
 */
package com.sun.xml.stream.buffer.stax;

import com.sun.xml.stream.buffer.AbstractProcessor;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.Base64Data;
import org.jvnet.staxex.XMLStreamWriterEx;

public class StreamWriterBufferProcessor
extends AbstractProcessor {
    public StreamWriterBufferProcessor() {
    }

    public StreamWriterBufferProcessor(XMLStreamBuffer buffer) {
        this.setXMLStreamBuffer(buffer, buffer.isFragment());
    }

    public StreamWriterBufferProcessor(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
        this.setXMLStreamBuffer(buffer, produceFragmentEvent);
    }

    public final void process(XMLStreamBuffer buffer, XMLStreamWriter writer) throws XMLStreamException {
        this.setXMLStreamBuffer(buffer, buffer.isFragment());
        this.process(writer);
    }

    public void process(XMLStreamWriter writer) throws XMLStreamException {
        if (this._fragmentMode) {
            this.writeFragment(writer);
        } else {
            this.write(writer);
        }
    }

    public void setXMLStreamBuffer(XMLStreamBuffer buffer) {
        this.setBuffer(buffer);
    }

    public void setXMLStreamBuffer(XMLStreamBuffer buffer, boolean produceFragmentEvent) {
        this.setBuffer(buffer, produceFragmentEvent);
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        int item;
        if (!this._fragmentMode) {
            if (this._treeCount > 1) {
                throw new IllegalStateException("forest cannot be written as a full infoset");
            }
            writer.writeStartDocument();
        }
        block9: while (true) {
            item = StreamWriterBufferProcessor.getEIIState(this.peekStructure());
            writer.flush();
            switch (item) {
                case 1: {
                    this.readStructure();
                    continue block9;
                }
                case 3: 
                case 4: 
                case 5: 
                case 6: {
                    this.writeFragment(writer);
                    continue block9;
                }
                case 12: {
                    this.readStructure();
                    int length = this.readStructure();
                    int start = this.readContentCharactersBuffer(length);
                    String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue block9;
                }
                case 13: {
                    this.readStructure();
                    int length = this.readStructure16();
                    int start = this.readContentCharactersBuffer(length);
                    String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue block9;
                }
                case 14: {
                    this.readStructure();
                    char[] ch = this.readContentCharactersCopy();
                    writer.writeComment(new String(ch));
                    continue block9;
                }
                case 16: {
                    this.readStructure();
                    writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue block9;
                }
                case 17: {
                    this.readStructure();
                    writer.writeEndDocument();
                    return;
                }
            }
            break;
        }
        throw new XMLStreamException("Invalid State " + item);
    }

    public void writeFragment(XMLStreamWriter writer) throws XMLStreamException {
        if (writer instanceof XMLStreamWriterEx) {
            this.writeFragmentEx((XMLStreamWriterEx)writer);
        } else {
            this.writeFragmentNoEx(writer);
        }
    }

    public void writeFragmentEx(XMLStreamWriterEx writer) throws XMLStreamException {
        int depth = 0;
        int item = StreamWriterBufferProcessor.getEIIState(this.peekStructure());
        if (item == 1) {
            this.readStructure();
        }
        block17: do {
            item = this.readEiiState();
            switch (item) {
                case 1: {
                    throw new AssertionError();
                }
                case 3: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    String prefix = this.getPrefixFromQName(this.readStructureString());
                    writer.writeStartElement(prefix, localName, uri);
                    this.writeAttributes((XMLStreamWriter)writer, this.isInscope(++depth));
                    break;
                }
                case 4: {
                    String prefix = this.readStructureString();
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    writer.writeStartElement(prefix, localName, uri);
                    this.writeAttributes((XMLStreamWriter)writer, this.isInscope(++depth));
                    break;
                }
                case 5: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    writer.writeStartElement("", localName, uri);
                    this.writeAttributes((XMLStreamWriter)writer, this.isInscope(++depth));
                    break;
                }
                case 6: {
                    String localName = this.readStructureString();
                    writer.writeStartElement(localName);
                    this.writeAttributes((XMLStreamWriter)writer, this.isInscope(++depth));
                    break;
                }
                case 7: {
                    int length = this.readStructure();
                    int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    break;
                }
                case 8: {
                    int length = this.readStructure16();
                    int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    break;
                }
                case 9: {
                    char[] c = this.readContentCharactersCopy();
                    writer.writeCharacters(c, 0, c.length);
                    break;
                }
                case 10: {
                    String s = this.readContentString();
                    writer.writeCharacters(s);
                    break;
                }
                case 11: {
                    CharSequence c = (CharSequence)this.readContentObject();
                    writer.writePCDATA(c);
                    break;
                }
                case 12: {
                    int length = this.readStructure();
                    int start = this.readContentCharactersBuffer(length);
                    String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    break;
                }
                case 13: {
                    int length = this.readStructure16();
                    int start = this.readContentCharactersBuffer(length);
                    String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    break;
                }
                case 14: {
                    char[] ch = this.readContentCharactersCopy();
                    writer.writeComment(new String(ch));
                    break;
                }
                case 16: {
                    writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
                    break;
                }
                case 17: {
                    writer.writeEndElement();
                    if (--depth != 0) continue block17;
                    --this._treeCount;
                    break;
                }
                default: {
                    throw new XMLStreamException("Invalid State " + item);
                }
            }
        } while (depth > 0 || this._treeCount > 0);
    }

    public void writeFragmentNoEx(XMLStreamWriter writer) throws XMLStreamException {
        int depth = 0;
        int item = StreamWriterBufferProcessor.getEIIState(this.peekStructure());
        if (item == 1) {
            this.readStructure();
        }
        block19: do {
            item = this.readEiiState();
            switch (item) {
                case 1: {
                    throw new AssertionError();
                }
                case 3: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    String prefix = this.getPrefixFromQName(this.readStructureString());
                    writer.writeStartElement(prefix, localName, uri);
                    this.writeAttributes(writer, this.isInscope(++depth));
                    break;
                }
                case 4: {
                    String prefix = this.readStructureString();
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    writer.writeStartElement(prefix, localName, uri);
                    this.writeAttributes(writer, this.isInscope(++depth));
                    break;
                }
                case 5: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    writer.writeStartElement("", localName, uri);
                    this.writeAttributes(writer, this.isInscope(++depth));
                    break;
                }
                case 6: {
                    String localName = this.readStructureString();
                    writer.writeStartElement(localName);
                    this.writeAttributes(writer, this.isInscope(++depth));
                    break;
                }
                case 7: {
                    int length = this.readStructure();
                    int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    break;
                }
                case 8: {
                    int length = this.readStructure16();
                    int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    break;
                }
                case 9: {
                    char[] c = this.readContentCharactersCopy();
                    writer.writeCharacters(c, 0, c.length);
                    break;
                }
                case 10: {
                    String s = this.readContentString();
                    writer.writeCharacters(s);
                    break;
                }
                case 11: {
                    CharSequence c = (CharSequence)this.readContentObject();
                    if (c instanceof Base64Data) {
                        try {
                            Base64Data bd = (Base64Data)c;
                            bd.writeTo(writer);
                            break;
                        }
                        catch (IOException e) {
                            throw new XMLStreamException(e);
                        }
                    }
                    writer.writeCharacters(c.toString());
                    break;
                }
                case 12: {
                    int length = this.readStructure();
                    int start = this.readContentCharactersBuffer(length);
                    String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    break;
                }
                case 13: {
                    int length = this.readStructure16();
                    int start = this.readContentCharactersBuffer(length);
                    String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    break;
                }
                case 14: {
                    char[] ch = this.readContentCharactersCopy();
                    writer.writeComment(new String(ch));
                    break;
                }
                case 16: {
                    writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
                    break;
                }
                case 17: {
                    writer.writeEndElement();
                    if (--depth != 0) continue block19;
                    --this._treeCount;
                    break;
                }
                default: {
                    throw new XMLStreamException("Invalid State " + item);
                }
            }
        } while (depth > 0 || this._treeCount > 0);
    }

    private boolean isInscope(int depth) {
        return this._buffer.getInscopeNamespaces().size() > 0 && depth == 1;
    }

    private void writeAttributes(XMLStreamWriter writer, boolean inscope) throws XMLStreamException {
        HashSet<String> prefixSet = inscope ? new HashSet<String>() : Collections.emptySet();
        int item = this.peekStructure();
        if ((item & 0xF0) == 64) {
            item = this.writeNamespaceAttributes(item, writer, inscope, prefixSet);
        }
        if (inscope) {
            this.writeInscopeNamespaces(writer, prefixSet);
        }
        if ((item & 0xF0) == 48) {
            this.writeAttributes(item, writer);
        }
    }

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private void writeInscopeNamespaces(XMLStreamWriter writer, Set<String> prefixSet) throws XMLStreamException {
        for (Map.Entry<String, String> e : this._buffer.getInscopeNamespaces().entrySet()) {
            String key = StreamWriterBufferProcessor.fixNull(e.getKey());
            if (prefixSet.contains(key)) continue;
            writer.writeNamespace(key, e.getValue());
        }
    }

    private int writeNamespaceAttributes(int item, XMLStreamWriter writer, boolean collectPrefixes, Set<String> prefixSet) throws XMLStreamException {
        do {
            switch (StreamWriterBufferProcessor.getNIIState(item)) {
                case 1: {
                    writer.writeDefaultNamespace("");
                    if (!collectPrefixes) break;
                    prefixSet.add("");
                    break;
                }
                case 2: {
                    String prefix = this.readStructureString();
                    writer.writeNamespace(prefix, "");
                    if (!collectPrefixes) break;
                    prefixSet.add(prefix);
                    break;
                }
                case 3: {
                    String prefix = this.readStructureString();
                    writer.writeNamespace(prefix, this.readStructureString());
                    if (!collectPrefixes) break;
                    prefixSet.add(prefix);
                    break;
                }
                case 4: {
                    writer.writeDefaultNamespace(this.readStructureString());
                    if (!collectPrefixes) break;
                    prefixSet.add("");
                }
            }
            this.readStructure();
        } while (((item = this.peekStructure()) & 0xF0) == 64);
        return item;
    }

    private void writeAttributes(int item, XMLStreamWriter writer) throws XMLStreamException {
        do {
            switch (StreamWriterBufferProcessor.getAIIState(item)) {
                case 1: {
                    String uri = this.readStructureString();
                    String localName = this.readStructureString();
                    String prefix = this.getPrefixFromQName(this.readStructureString());
                    writer.writeAttribute(prefix, uri, localName, this.readContentString());
                    break;
                }
                case 2: {
                    writer.writeAttribute(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 3: {
                    writer.writeAttribute(this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 4: {
                    writer.writeAttribute(this.readStructureString(), this.readContentString());
                }
            }
            this.readStructureString();
            this.readStructure();
        } while (((item = this.peekStructure()) & 0xF0) == 48);
    }
}

