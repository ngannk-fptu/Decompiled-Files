/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DOMLocatorImpl;
import org.apache.xerces.xni.parser.XMLParseException;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;

public class DOMErrorImpl
implements DOMError {
    public short fSeverity = 1;
    public String fMessage = null;
    public DOMLocatorImpl fLocator = new DOMLocatorImpl();
    public Exception fException = null;
    public String fType;
    public Object fRelatedData;

    public DOMErrorImpl() {
    }

    public DOMErrorImpl(short s, XMLParseException xMLParseException) {
        this.fSeverity = s;
        this.fException = xMLParseException;
        this.fLocator = this.createDOMLocator(xMLParseException);
    }

    @Override
    public short getSeverity() {
        return this.fSeverity;
    }

    @Override
    public String getMessage() {
        return this.fMessage;
    }

    @Override
    public DOMLocator getLocation() {
        return this.fLocator;
    }

    private DOMLocatorImpl createDOMLocator(XMLParseException xMLParseException) {
        return new DOMLocatorImpl(xMLParseException.getLineNumber(), xMLParseException.getColumnNumber(), xMLParseException.getCharacterOffset(), xMLParseException.getExpandedSystemId());
    }

    @Override
    public Object getRelatedException() {
        return this.fException;
    }

    public void reset() {
        this.fSeverity = 1;
        this.fException = null;
    }

    @Override
    public String getType() {
        return this.fType;
    }

    @Override
    public Object getRelatedData() {
        return this.fRelatedData;
    }
}

