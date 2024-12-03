/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

public interface XMLStreamWriter {
    public void writeStartElement(String var1) throws XMLStreamException;

    public void writeStartElement(String var1, String var2) throws XMLStreamException;

    public void writeStartElement(String var1, String var2, String var3) throws XMLStreamException;

    public void writeEmptyElement(String var1, String var2) throws XMLStreamException;

    public void writeEmptyElement(String var1, String var2, String var3) throws XMLStreamException;

    public void writeEmptyElement(String var1) throws XMLStreamException;

    public void writeEndElement() throws XMLStreamException;

    public void writeEndDocument() throws XMLStreamException;

    public void close() throws XMLStreamException;

    public void flush() throws XMLStreamException;

    public void writeAttribute(String var1, String var2) throws XMLStreamException;

    public void writeAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public void writeAttribute(String var1, String var2, String var3) throws XMLStreamException;

    public void writeNamespace(String var1, String var2) throws XMLStreamException;

    public void writeDefaultNamespace(String var1) throws XMLStreamException;

    public void writeComment(String var1) throws XMLStreamException;

    public void writeProcessingInstruction(String var1) throws XMLStreamException;

    public void writeProcessingInstruction(String var1, String var2) throws XMLStreamException;

    public void writeCData(String var1) throws XMLStreamException;

    public void writeDTD(String var1) throws XMLStreamException;

    public void writeEntityRef(String var1) throws XMLStreamException;

    public void writeStartDocument() throws XMLStreamException;

    public void writeStartDocument(String var1) throws XMLStreamException;

    public void writeStartDocument(String var1, String var2) throws XMLStreamException;

    public void writeCharacters(String var1) throws XMLStreamException;

    public void writeCharacters(char[] var1, int var2, int var3) throws XMLStreamException;

    public String getPrefix(String var1) throws XMLStreamException;

    public void setPrefix(String var1, String var2) throws XMLStreamException;

    public void setDefaultNamespace(String var1) throws XMLStreamException;

    public void setNamespaceContext(NamespaceContext var1) throws XMLStreamException;

    public NamespaceContext getNamespaceContext();

    public Object getProperty(String var1) throws IllegalArgumentException;
}

