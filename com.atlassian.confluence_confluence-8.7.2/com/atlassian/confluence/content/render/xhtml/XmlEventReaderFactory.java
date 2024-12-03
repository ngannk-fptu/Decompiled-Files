/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Namespace;
import java.io.Reader;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public interface XmlEventReaderFactory {
    public XMLEventReader createXmlEventReader(Reader var1) throws XMLStreamException;

    public XMLEventReader createXmlFragmentEventReader(Reader var1) throws XMLStreamException;

    public XMLEventReader createStorageXmlEventReader(Reader var1) throws XMLStreamException;

    public XMLEventReader createStorageXmlEventReader(Reader var1, boolean var2) throws XMLStreamException;

    public XMLEventReader createEditorXmlEventReader(Reader var1) throws XMLStreamException;

    public XMLEventReader createXMLEventReader(Reader var1, List<Namespace> var2, boolean var3) throws XMLStreamException;

    public XMLEventReader createXmlFragmentEventReader(XMLEventReader var1) throws XMLStreamException;

    public XMLEventReader createXmlFragmentBodyEventReader(XMLEventReader var1) throws XMLStreamException;
}

