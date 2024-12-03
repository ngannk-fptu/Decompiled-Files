/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;

public class CommentImpl
extends CharacterDataImpl
implements CharacterData,
Comment {
    static final long serialVersionUID = -2685736833408134044L;

    public CommentImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl, string);
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

