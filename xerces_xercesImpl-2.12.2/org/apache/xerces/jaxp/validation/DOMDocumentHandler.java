/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import javax.xml.transform.dom.DOMResult;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

interface DOMDocumentHandler
extends XMLDocumentHandler {
    public void setDOMResult(DOMResult var1);

    public void doctypeDecl(DocumentType var1) throws XNIException;

    public void characters(Text var1) throws XNIException;

    public void cdata(CDATASection var1) throws XNIException;

    public void comment(Comment var1) throws XNIException;

    public void processingInstruction(ProcessingInstruction var1) throws XNIException;

    public void setIgnoringCharacters(boolean var1);
}

