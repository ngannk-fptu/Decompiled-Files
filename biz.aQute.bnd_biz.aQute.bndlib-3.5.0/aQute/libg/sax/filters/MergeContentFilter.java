/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.sax.filters;

import aQute.libg.sax.ContentFilterImpl;
import aQute.libg.sax.SAXElement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MergeContentFilter
extends ContentFilterImpl {
    private int elementDepth = 0;
    private final List<SAXElement> rootElements = new LinkedList<SAXElement>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (this.elementDepth++ == 0) {
            if (this.rootElements.isEmpty()) {
                super.startElement(uri, localName, qName, atts);
            } else if (!this.rootElements.get(0).getqName().equals(qName)) {
                throw new SAXException(String.format("Documents have inconsistent root element names: first was %s, current is %s.", this.rootElements.get(0).getqName(), qName));
            }
            this.rootElements.add(new SAXElement(uri, localName, qName, atts));
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (--this.elementDepth > 0) {
            super.endElement(uri, localName, qName);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if (this.rootElements.isEmpty()) {
            super.processingInstruction(target, data);
        }
    }

    public void closeRootAndDocument() throws SAXException {
        if (!this.rootElements.isEmpty()) {
            SAXElement root = this.rootElements.get(0);
            super.endElement(root.getUri(), root.getLocalName(), root.getqName());
        }
        super.endDocument();
    }

    public List<SAXElement> getRootElements() {
        return Collections.unmodifiableList(this.rootElements);
    }
}

