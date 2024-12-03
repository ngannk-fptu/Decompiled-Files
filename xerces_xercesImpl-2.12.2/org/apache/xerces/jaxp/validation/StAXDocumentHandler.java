/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.transform.stax.StAXResult;
import org.apache.xerces.xni.XMLDocumentHandler;

interface StAXDocumentHandler
extends XMLDocumentHandler {
    public void setStAXResult(StAXResult var1);

    public void startDocument(XMLStreamReader var1) throws XMLStreamException;

    public void endDocument(XMLStreamReader var1) throws XMLStreamException;

    public void comment(XMLStreamReader var1) throws XMLStreamException;

    public void processingInstruction(XMLStreamReader var1) throws XMLStreamException;

    public void entityReference(XMLStreamReader var1) throws XMLStreamException;

    public void startDocument(StartDocument var1) throws XMLStreamException;

    public void endDocument(EndDocument var1) throws XMLStreamException;

    public void doctypeDecl(DTD var1) throws XMLStreamException;

    public void characters(Characters var1) throws XMLStreamException;

    public void cdata(Characters var1) throws XMLStreamException;

    public void comment(Comment var1) throws XMLStreamException;

    public void processingInstruction(ProcessingInstruction var1) throws XMLStreamException;

    public void entityReference(EntityReference var1) throws XMLStreamException;

    public void setIgnoringCharacters(boolean var1);
}

