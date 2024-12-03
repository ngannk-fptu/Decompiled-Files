/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer.dom3;

import org.apache.xml.serializer.dom3.DOMLocatorImpl;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;

final class DOMErrorImpl
implements DOMError {
    private short fSeverity = 1;
    private String fMessage = null;
    private String fType;
    private Exception fException = null;
    private Object fRelatedData;
    private DOMLocatorImpl fLocation = new DOMLocatorImpl();

    DOMErrorImpl() {
    }

    DOMErrorImpl(short severity, String message, String type) {
        this.fSeverity = severity;
        this.fMessage = message;
        this.fType = type;
    }

    DOMErrorImpl(short severity, String message, String type, Exception exception) {
        this.fSeverity = severity;
        this.fMessage = message;
        this.fType = type;
        this.fException = exception;
    }

    DOMErrorImpl(short severity, String message, String type, Exception exception, Object relatedData, DOMLocatorImpl location) {
        this.fSeverity = severity;
        this.fMessage = message;
        this.fType = type;
        this.fException = exception;
        this.fRelatedData = relatedData;
        this.fLocation = location;
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
        return this.fLocation;
    }

    @Override
    public Object getRelatedException() {
        return this.fException;
    }

    @Override
    public String getType() {
        return this.fType;
    }

    @Override
    public Object getRelatedData() {
        return this.fRelatedData;
    }

    public void reset() {
        this.fSeverity = 1;
        this.fException = null;
        this.fMessage = null;
        this.fType = null;
        this.fRelatedData = null;
        this.fLocation = null;
    }
}

