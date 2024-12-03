/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.writer;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.json.impl.writer.A2EXmlStreamWriterProxy;
import com.sun.jersey.json.impl.writer.DefaultXmlStreamWriter;
import com.sun.jersey.json.impl.writer.JsonEncoder;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class JsonXmlStreamWriter
extends DefaultXmlStreamWriter
implements XMLStreamWriter {
    Writer mainWriter;
    boolean stripRoot;
    char nsSeparator;
    final List<ProcessingState> processingStack = new ArrayList<ProcessingState>();
    int depth;
    final Collection<String> arrayElementNames = new LinkedList<String>();
    final Collection<String> nonStringElementNames = new LinkedList<String>();
    final Map<String, String> xml2JsonNs = new HashMap<String, String>();
    private final String rootName;

    private JsonXmlStreamWriter(Writer writer, JSONConfiguration config, String rootName) {
        this.mainWriter = writer;
        this.stripRoot = config.isRootUnwrapping();
        this.rootName = rootName;
        this.nsSeparator = config.getNsSeparator().charValue();
        if (null != config.getArrays()) {
            this.arrayElementNames.addAll(config.getArrays());
        }
        if (null != config.getNonStrings()) {
            this.nonStringElementNames.addAll(config.getNonStrings());
        }
        if (null != config.getXml2JsonNs()) {
            this.xml2JsonNs.putAll(config.getXml2JsonNs());
        }
        this.processingStack.add(this.createProcessingState());
        this.depth = 0;
    }

    public static XMLStreamWriter createWriter(Writer writer, JSONConfiguration config, String rootName) {
        Collection<String> attrsAsElems = config.getAttributeAsElements();
        if (attrsAsElems != null && !attrsAsElems.isEmpty()) {
            return new A2EXmlStreamWriterProxy(new JsonXmlStreamWriter(writer, config, rootName), attrsAsElems);
        }
        return new JsonXmlStreamWriter(writer, config, rootName);
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            this.mainWriter.close();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void flush() throws XMLStreamException {
        try {
            this.mainWriter.flush();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            if (null != this.processingStack.get((int)this.depth).lastElementWriter) {
                this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
            }
            if (null == this.processingStack.get((int)this.depth).lastWasPrimitive || !this.processingStack.get((int)this.depth).lastWasPrimitive.booleanValue()) {
                this.processingStack.get((int)this.depth).writer.write("}");
            }
            this.pollStack();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        try {
            if (null != this.processingStack.get((int)this.depth).lastElementWriter) {
                if (this.processingStack.get((int)this.depth).lastIsArray) {
                    this.processingStack.get((int)this.depth).writer.write(",");
                    this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
                    this.processingStack.get((int)this.depth).writer.write("]");
                } else if (this.isArrayElement(this.processingStack.get((int)this.depth).lastName)) {
                    this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastIsArray ? "," : "[");
                    this.processingStack.get((int)this.depth).lastIsArray = true;
                    this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
                    this.processingStack.get((int)this.depth).writer.write("]");
                } else {
                    this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
                }
            }
            if (this.processingStack.get((int)this.depth).writer.isEmpty) {
                String currentName = this.processingStack.get((int)this.depth).currentName;
                String string = currentName = currentName == null ? this.processingStack.get((int)(this.depth - 1)).currentName : currentName;
                if (this.arrayElementNames.contains(currentName) || this.nonStringElementNames.contains(currentName) || this.rootName.equals(currentName)) {
                    this.processingStack.get((int)this.depth).writer.write("{}");
                } else {
                    this.processingStack.get((int)this.depth).writer.write("null");
                }
            } else if (null == this.processingStack.get((int)this.depth).lastWasPrimitive || !this.processingStack.get((int)this.depth).lastWasPrimitive.booleanValue()) {
                this.processingStack.get((int)this.depth).writer.write("}");
            }
            this.processingStack.get((int)(this.depth - 1)).lastName = this.processingStack.get((int)(this.depth - 1)).currentName;
            this.processingStack.get((int)(this.depth - 1)).lastWasPrimitive = false;
            this.processingStack.get((int)(this.depth - 1)).lastElementWriter = this.processingStack.get((int)this.depth).writer;
            this.pollStack();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    private QName getQName(String s) {
        String[] currentName = s.split(Character.toString(this.nsSeparator));
        QName name = new QName(s);
        if (currentName.length > 1) {
            name = new QName(currentName[0], currentName[1]);
        }
        return name;
    }

    @Override
    public void writeCharacters(char[] text, int start, int length) throws XMLStreamException {
        this.writeCharacters(new String(text, start, length));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        if (this.processingStack.get((int)this.depth).isNotEmpty) {
            this.writeStartElement(null, "$", null);
            this._writeCharacters(text);
            this.writeEndElement();
        } else {
            this._writeCharacters(text);
        }
    }

    private void _writeCharacters(String text) throws XMLStreamException {
        try {
            if (this.isNonString(this.processingStack.get((int)(this.depth - 1)).currentName)) {
                this.processingStack.get((int)this.depth).writer.write(JsonEncoder.encode(text));
            } else {
                this.processingStack.get((int)this.depth).writer.write("\"" + JsonEncoder.encode(text) + "\"");
            }
            this.processingStack.get((int)this.depth).lastWasPrimitive = true;
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, null);
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, null);
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.writeAttribute(null, null, localName, value);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, null);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, namespaceURI);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writeAttribute(null, namespaceURI, localName, value);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writeStartElement(localName);
        this.writeEndElement();
    }

    private void pollStack() throws IOException {
        this.processingStack.remove(this.depth--);
    }

    private void printStack(String localName) {
        try {
            for (int d = 0; d <= this.depth; ++d) {
                this.mainWriter.write("\n**" + d + ":" + this.processingStack.get(d));
            }
            this.mainWriter.write("\n*** [" + localName + "]");
        }
        catch (IOException ex) {
            Logger.getLogger(JsonXmlStreamWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isArrayElement(String name) {
        if (null == name) {
            return false;
        }
        return this.arrayElementNames.contains(name);
    }

    private boolean isNonString(String name) {
        if (null == name) {
            return false;
        }
        return this.nonStringElementNames.contains(name);
    }

    private String getEffectiveName(String namespaceURI, String localName) {
        if (namespaceURI != null && this.xml2JsonNs.containsKey(namespaceURI)) {
            return String.format("%s%c%s", this.xml2JsonNs.get(namespaceURI), Character.valueOf(this.nsSeparator), localName);
        }
        return localName;
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        String effectiveName = this.getEffectiveName(namespaceURI, localName);
        this.processingStack.get((int)this.depth).isNotEmpty = true;
        this.processingStack.get((int)this.depth).currentName = effectiveName;
        try {
            boolean isNextArrayElement = this.processingStack.get((int)this.depth).currentName.equals(this.processingStack.get((int)this.depth).lastName);
            if (!isNextArrayElement) {
                if (this.isArrayElement(this.processingStack.get((int)this.depth).lastName)) {
                    this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastIsArray ? "," : "[");
                    this.processingStack.get((int)this.depth).lastIsArray = true;
                    this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
                } else {
                    if (null != this.processingStack.get((int)this.depth).lastElementWriter) {
                        if (this.processingStack.get((int)this.depth).lastIsArray) {
                            this.processingStack.get((int)this.depth).writer.write(",");
                            this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
                            this.processingStack.get((int)this.depth).writer.write("]");
                        } else {
                            this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
                        }
                    }
                    this.processingStack.get((int)this.depth).lastIsArray = false;
                }
                if (null != this.processingStack.get((int)this.depth).lastName) {
                    if (this.processingStack.get((int)this.depth).lastIsArray) {
                        this.processingStack.get((int)this.depth).writer.write("]");
                        this.processingStack.get((int)this.depth).lastIsArray = false;
                    }
                    this.processingStack.get((int)this.depth).writer.write(",");
                }
                if (null == this.processingStack.get((int)this.depth).lastWasPrimitive) {
                    this.processingStack.get((int)this.depth).writer.write("{");
                }
                this.processingStack.get((int)this.depth).writer.write("\"" + effectiveName + "\":");
            } else {
                this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastIsArray ? "," : "[");
                this.processingStack.get((int)this.depth).lastIsArray = true;
                this.processingStack.get((int)this.depth).writer.write(this.processingStack.get((int)this.depth).lastElementWriter.getContent());
            }
            ++this.depth;
            this.processingStack.add(this.depth, this.createProcessingState());
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writeStartElement(prefix, "@" + this.getEffectiveName(namespaceURI, localName), null);
        this.writeCharacters(value);
        this.writeEndElement();
    }

    private ProcessingState createProcessingState() {
        switch (this.depth) {
            case 0: {
                return new ProcessingState(this.stripRoot ? new DummyWriterAdapter() : new WriterAdapter(this.mainWriter));
            }
            case 1: {
                return this.stripRoot ? new ProcessingState(new WriterAdapter(this.mainWriter)) : new ProcessingState();
            }
        }
        return new ProcessingState();
    }

    private static final class ProcessingState {
        String lastName;
        String currentName;
        WriterAdapter lastElementWriter;
        Boolean lastWasPrimitive;
        boolean lastIsArray;
        boolean isNotEmpty = false;
        WriterAdapter writer;

        ProcessingState() {
            this.writer = new StringWriterAdapter();
        }

        ProcessingState(WriterAdapter w) {
            this.writer = w;
        }

        public String toString() {
            return String.format("{currentName:%s, writer: \"%s\", lastName:%s, lastWriter: %s}", this.currentName, this.writer != null ? this.writer.getContent() : null, this.lastName, this.lastElementWriter != null ? this.lastElementWriter.getContent() : null);
        }
    }

    private static final class DummyWriterAdapter
    extends WriterAdapter {
        DummyWriterAdapter() {
        }

        @Override
        void write(String s) throws IOException {
        }

        @Override
        String getContent() {
            return null;
        }
    }

    private static final class StringWriterAdapter
    extends WriterAdapter {
        StringWriterAdapter() {
            this.writer = new StringWriter();
        }

        @Override
        String getContent() {
            return this.writer.toString();
        }
    }

    private static class WriterAdapter {
        Writer writer;
        boolean isEmpty = true;

        WriterAdapter() {
        }

        WriterAdapter(Writer w) {
            this.writer = w;
        }

        void write(String s) throws IOException {
            assert (null != this.writer);
            this.writer.write(s);
            this.isEmpty = false;
        }

        String getContent() {
            return null;
        }
    }
}

