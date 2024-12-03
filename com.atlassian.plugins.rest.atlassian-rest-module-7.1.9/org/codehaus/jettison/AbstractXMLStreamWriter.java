/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class AbstractXMLStreamWriter
implements XMLStreamWriter {
    private ArrayList<String> serializedAsArrays = new ArrayList();
    private boolean arrayKeysWithSlashAvailable;

    @Override
    public void writeCData(String text) throws XMLStreamException {
        this.writeCharacters(text);
    }

    @Override
    public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
        this.writeCharacters(new String(arg0, arg1, arg2));
    }

    @Override
    public void writeEmptyElement(String prefix, String local, String ns) throws XMLStreamException {
        this.writeStartElement(prefix, local, ns);
        this.writeEndElement();
    }

    @Override
    public void writeEmptyElement(String ns, String local) throws XMLStreamException {
        this.writeStartElement(local, ns);
        this.writeEndElement();
    }

    @Override
    public void writeEmptyElement(String local) throws XMLStreamException {
        this.writeStartElement(local);
        this.writeEndElement();
    }

    @Override
    public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
        this.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String arg0) throws XMLStreamException {
        this.writeStartDocument();
    }

    @Override
    public void writeStartElement(String ns, String local) throws XMLStreamException {
        this.writeStartElement("", local, ns);
    }

    @Override
    public void writeStartElement(String local) throws XMLStreamException {
        this.writeStartElement("", local, "");
    }

    @Override
    public void writeComment(String arg0) throws XMLStreamException {
    }

    @Override
    public void writeDTD(String arg0) throws XMLStreamException {
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
    }

    public void serializeAsArray(String name) {
        this.serializedAsArrays.add(name);
        if (!this.arrayKeysWithSlashAvailable) {
            this.arrayKeysWithSlashAvailable = name.contains("/");
        }
    }

    @Deprecated
    public void seriliazeAsArray(String name) {
        this.serializedAsArrays.add(name);
    }

    public ArrayList<String> getSerializedAsArrays() {
        return this.serializedAsArrays;
    }

    public boolean isArrayKeysWithSlashAvailable() {
        return this.arrayKeysWithSlashAvailable;
    }
}

