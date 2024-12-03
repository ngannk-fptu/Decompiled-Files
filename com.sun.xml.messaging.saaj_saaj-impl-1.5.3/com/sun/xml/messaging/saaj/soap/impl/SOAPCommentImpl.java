/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.TextImpl;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class SOAPCommentImpl
extends TextImpl<Comment>
implements Comment {
    public SOAPCommentImpl(SOAPDocumentImpl ownerDoc, String text) {
        super(ownerDoc, text);
    }

    public SOAPCommentImpl(SOAPDocumentImpl ownerDoc, CharacterData data) {
        super(ownerDoc, data);
    }

    @Override
    protected Comment createN(SOAPDocumentImpl ownerDoc, String text) {
        return ownerDoc.getDomDocument().createComment(text);
    }

    @Override
    protected Comment createN(SOAPDocumentImpl ownerDoc, CharacterData data) {
        return (Comment)data;
    }

    protected SOAPCommentImpl doClone() {
        return new SOAPCommentImpl(this.getSoapDocument(), this.getTextContent());
    }

    public boolean isComment() {
        return true;
    }

    public Text splitText(int offset) throws DOMException {
        log.severe("SAAJ0113.impl.cannot.split.text.from.comment");
        throw new UnsupportedOperationException("Cannot split text from a Comment Node.");
    }

    public Text replaceWholeText(String content) throws DOMException {
        log.severe("SAAJ0114.impl.cannot.replace.wholetext.from.comment");
        throw new UnsupportedOperationException("Cannot replace Whole Text from a Comment Node.");
    }

    public String getWholeText() {
        throw new UnsupportedOperationException("Not Supported");
    }

    public boolean isElementContentWhitespace() {
        throw new UnsupportedOperationException("Not Supported");
    }
}

