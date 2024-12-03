/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.CharNode;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.w3c.dom.Text;

class TextNode
extends CharNode
implements Text {
    TextNode(Locale l) {
        super(l);
    }

    @Override
    public int nodeType() {
        return 3;
    }

    public String name() {
        return "#text";
    }

    @Override
    public Text splitText(int offset) {
        return DomImpl._text_splitText(this, offset);
    }

    @Override
    public String getWholeText() {
        return DomImpl._text_getWholeText(this);
    }

    @Override
    public boolean isElementContentWhitespace() {
        return DomImpl._text_isElementContentWhitespace(this);
    }

    @Override
    public Text replaceWholeText(String content) {
        return DomImpl._text_replaceWholeText(this, content);
    }
}

