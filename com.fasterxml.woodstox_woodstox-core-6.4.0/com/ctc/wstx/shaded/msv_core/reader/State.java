/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public abstract class State
implements ContentHandler {
    protected State parentState;
    public GrammarReader reader;
    protected StartTagInfo startTag;
    protected Locator location;
    protected String baseURI;

    public final State getParentState() {
        return this.parentState;
    }

    public StartTagInfo getStartTag() {
        return this.startTag;
    }

    public Locator getLocation() {
        return this.location;
    }

    public String getBaseURI() {
        return this.baseURI;
    }

    protected final void init(GrammarReader reader, State parentState, StartTagInfo startTag) {
        this.reader = reader;
        this.parentState = parentState;
        this.startTag = startTag;
        if (reader.getLocator() != null) {
            this.location = new LocatorImpl(reader.getLocator());
        }
        String base = null;
        if (startTag != null) {
            base = startTag.getAttribute("http://www.w3.org/XML/1998/namespace", "base");
        }
        if (parentState == null) {
            this.baseURI = null;
        } else {
            this.baseURI = parentState.baseURI;
            if (this.baseURI == null) {
                this.baseURI = reader.getLocator().getSystemId();
            }
        }
        if (base != null) {
            this.baseURI = reader.combineURI(this.baseURI, base);
        }
        this.startSelf();
    }

    protected void startSelf() {
    }

    public static void _assert(boolean b) {
        if (!b) {
            throw new InternalError();
        }
    }

    public void characters(char[] buffer, int from, int len) throws SAXException {
        block3: for (int i = from; i < len; ++i) {
            switch (buffer[i]) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    continue block3;
                }
                default: {
                    this.reader.reportError("GrammarReader.Characters", (Object)new String(buffer, from, len).trim());
                    return;
                }
            }
        }
    }

    protected final Expression callInterceptExpression(Expression exp) {
        return this.reader.interceptExpression(this, exp);
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void ignorableWhitespace(char[] buffer, int from, int len) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public final void startDocument() throws SAXException {
    }

    public void setDocumentLocator(Locator loc) {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }
}

