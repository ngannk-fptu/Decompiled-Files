/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

public interface XMLEventWriter
extends XMLEventConsumer {
    public void add(XMLEvent var1) throws XMLStreamException;

    public void add(XMLEventReader var1) throws XMLStreamException;

    public void close() throws XMLStreamException;

    public void flush() throws XMLStreamException;

    public NamespaceContext getNamespaceContext();

    public String getPrefix(String var1) throws XMLStreamException;

    public void setDefaultNamespace(String var1) throws XMLStreamException;

    public void setNamespaceContext(NamespaceContext var1) throws XMLStreamException;

    public void setPrefix(String var1, String var2) throws XMLStreamException;
}

