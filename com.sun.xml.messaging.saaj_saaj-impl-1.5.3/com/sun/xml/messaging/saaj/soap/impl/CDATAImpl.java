/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.TextImpl;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class CDATAImpl
extends TextImpl<CDATASection>
implements CDATASection {
    static final String cdataUC = "<![CDATA[";
    static final String cdataLC = "<![cdata[";

    public CDATAImpl(SOAPDocumentImpl ownerDoc, String text) {
        super(ownerDoc, text);
    }

    public CDATAImpl(SOAPDocumentImpl ownerDoc, CharacterData data) {
        super(ownerDoc, data);
    }

    @Override
    protected CDATASection createN(SOAPDocumentImpl ownerDoc, String text) {
        return ownerDoc.getDomDocument().createCDATASection(text);
    }

    @Override
    protected CDATASection createN(SOAPDocumentImpl ownerDoc, CharacterData data) {
        return (CDATASection)data;
    }

    protected CDATAImpl doClone() {
        return new CDATAImpl(this.getSoapDocument(), this.getTextContent());
    }

    @Override
    public Text splitText(int offset) throws DOMException {
        Text text = ((CDATASection)this.getDomElement()).splitText(offset);
        this.getSoapDocument().registerChildNodes(text, true);
        return text;
    }

    @Override
    public boolean isElementContentWhitespace() {
        return ((CDATASection)this.getDomElement()).isElementContentWhitespace();
    }

    @Override
    public String getWholeText() {
        return ((CDATASection)this.getDomElement()).getWholeText();
    }

    @Override
    public Text replaceWholeText(String content) throws DOMException {
        Text text = ((CDATASection)this.getDomElement()).replaceWholeText(content);
        this.getSoapDocument().registerChildNodes(text, true);
        return text;
    }

    public boolean isComment() {
        return false;
    }
}

