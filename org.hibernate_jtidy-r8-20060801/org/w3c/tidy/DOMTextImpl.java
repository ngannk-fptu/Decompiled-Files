/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;
import org.w3c.tidy.DOMCharacterDataImpl;
import org.w3c.tidy.Node;

public class DOMTextImpl
extends DOMCharacterDataImpl
implements Text {
    protected DOMTextImpl(Node adaptee) {
        super(adaptee);
    }

    public String getNodeName() {
        return "#text";
    }

    public short getNodeType() {
        return 3;
    }

    public Text splitText(int offset) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public String getWholeText() {
        return null;
    }

    public boolean isElementContentWhitespace() {
        return false;
    }

    public Text replaceWholeText(String content) throws DOMException {
        return this;
    }
}

