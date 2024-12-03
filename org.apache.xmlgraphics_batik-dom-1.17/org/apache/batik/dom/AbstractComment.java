/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractCharacterData;
import org.w3c.dom.Comment;

public abstract class AbstractComment
extends AbstractCharacterData
implements Comment {
    @Override
    public String getNodeName() {
        return "#comment";
    }

    @Override
    public short getNodeType() {
        return 8;
    }

    @Override
    public String getTextContent() {
        return this.getNodeValue();
    }
}

