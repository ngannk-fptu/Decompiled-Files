/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

public abstract class SimpleState
extends State {
    protected boolean isGrammarElement(StartTagInfo tag) {
        return this.reader.isGrammarElement(tag);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        StartTagInfo tag = new StartTagInfo(namespaceURI, localName, qName, new AttributesImpl(atts));
        if (this.isGrammarElement(tag)) {
            State nextState = this.createChildState(tag);
            if (nextState != null) {
                this.reader.pushState(nextState, this, tag);
                return;
            }
            this.reader.reportError("GrammarReader.MalplacedElement", (Object)tag.qName);
        } else if (this.parentState == null) {
            this.reader.reportError("GrammarReader.MalplacedElement", (Object)tag.qName);
            this.reader.reportError("GrammarReader.Warning.MaybeWrongNamespace", (Object)tag.namespaceURI);
        }
        this.reader.pushState(new IgnoreState(), this, tag);
    }

    protected abstract State createChildState(StartTagInfo var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void endElement(String namespaceURI, String localName, String qName) {
        Locator prevLoc = this.reader.getLocator();
        try {
            this.reader.setLocator(this.location);
            this.endSelf();
        }
        finally {
            this.reader.setLocator(prevLoc);
        }
        this.reader.popState();
    }

    public final void endDocument() {
        this.endSelf();
        this.reader.popState();
    }

    protected void endSelf() {
    }
}

