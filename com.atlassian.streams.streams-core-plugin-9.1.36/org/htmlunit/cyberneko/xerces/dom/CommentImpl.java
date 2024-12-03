/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CharacterDataImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.Comment;

public class CommentImpl
extends CharacterDataImpl
implements Comment {
    public CommentImpl(CoreDocumentImpl ownerDoc, String data) {
        super(ownerDoc, data);
    }

    @Override
    public short getNodeType() {
        return 8;
    }

    @Override
    public String getNodeName() {
        return "#comment";
    }
}

