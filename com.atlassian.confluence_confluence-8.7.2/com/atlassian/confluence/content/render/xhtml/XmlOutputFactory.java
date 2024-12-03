/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public interface XmlOutputFactory {
    public XMLStreamWriter createXMLStreamWriter(Writer var1) throws XMLStreamException;

    public XMLStreamWriter createXMLStreamWriter(OutputStream var1) throws XMLStreamException;

    public XMLStreamWriter createXMLStreamWriter(OutputStream var1, String var2) throws XMLStreamException;

    public XMLStreamWriter createXMLStreamWriter(Result var1) throws XMLStreamException;

    public XMLEventWriter createXMLEventWriter(Result var1) throws XMLStreamException;

    public XMLEventWriter createXMLEventWriter(OutputStream var1) throws XMLStreamException;

    public XMLEventWriter createXMLEventWriter(OutputStream var1, String var2) throws XMLStreamException;

    public XMLEventWriter createXMLEventWriter(Writer var1) throws XMLStreamException;

    public void setProperty(String var1, Object var2) throws IllegalArgumentException;

    public Object getProperty(String var1) throws IllegalArgumentException;

    public boolean isPropertySupported(String var1);
}

