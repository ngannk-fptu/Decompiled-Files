/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.TextImpl;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class SOAPTextImpl
extends TextImpl<Text>
implements Text {
    public SOAPTextImpl(SOAPDocumentImpl ownerDoc, String text) {
        super(ownerDoc, text);
    }

    public SOAPTextImpl(SOAPDocumentImpl ownerDoc, CharacterData data) {
        super(ownerDoc, data);
    }

    protected SOAPTextImpl doClone() {
        return new SOAPTextImpl(this.getSoapDocument(), this.getTextContent());
    }

    @Override
    protected Text createN(SOAPDocumentImpl ownerDoc, String text) {
        return ownerDoc.getDomDocument().createTextNode(text);
    }

    @Override
    protected Text createN(SOAPDocumentImpl ownerDoc, CharacterData data) {
        return (Text)data;
    }

    @Override
    public Text splitText(int offset) throws DOMException {
        Text text = ((Text)this.getDomElement()).splitText(offset);
        this.getSoapDocument().registerChildNodes(text, true);
        return text;
    }

    @Override
    public boolean isElementContentWhitespace() {
        return ((Text)this.getDomElement()).isElementContentWhitespace();
    }

    @Override
    public String getWholeText() {
        return ((Text)this.getDomElement()).getWholeText();
    }

    @Override
    public Text replaceWholeText(String content) throws DOMException {
        Text text = ((Text)this.getDomElement()).replaceWholeText(content);
        this.getSoapDocument().registerChildNodes(text, true);
        return text;
    }

    public boolean isComment() {
        String txt = this.getNodeValue();
        if (txt == null) {
            return false;
        }
        return txt.startsWith("<!--") && txt.endsWith("-->");
    }
}

