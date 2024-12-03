/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.sax.filters;

import aQute.libg.sax.ContentFilterImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class ElementSelectionFilter
extends ContentFilterImpl {
    int depth = 0;
    int hiddenDepth = -1;

    protected abstract boolean select(int var1, String var2, String var3, String var4, Attributes var5);

    @Override
    public final void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (this.hiddenDepth < 0) {
            boolean allow = this.select(this.depth, uri, localName, qName, atts);
            if (allow) {
                super.startElement(uri, localName, qName, atts);
            } else {
                this.hiddenDepth = 0;
            }
        } else {
            ++this.hiddenDepth;
        }
        ++this.depth;
    }

    @Override
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.hiddenDepth < 0) {
            super.endElement(uri, localName, qName);
        } else {
            --this.hiddenDepth;
        }
        --this.depth;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.hiddenDepth < 0) {
            super.characters(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.hiddenDepth < 0) {
            super.ignorableWhitespace(ch, start, length);
        }
    }
}

