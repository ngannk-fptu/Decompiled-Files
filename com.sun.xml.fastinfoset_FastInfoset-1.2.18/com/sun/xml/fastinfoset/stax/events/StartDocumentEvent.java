/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.stax.events.EventBase;
import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent
extends EventBase
implements StartDocument {
    protected String _systemId;
    protected String _encoding = "UTF-8";
    protected boolean _standalone = true;
    protected String _version = "1.0";
    private boolean _encodingSet = false;
    private boolean _standaloneSet = false;

    public void reset() {
        this._encoding = "UTF-8";
        this._standalone = true;
        this._version = "1.0";
        this._encodingSet = false;
        this._standaloneSet = false;
    }

    public StartDocumentEvent() {
        this(null, null);
    }

    public StartDocumentEvent(String encoding) {
        this(encoding, null);
    }

    public StartDocumentEvent(String encoding, String version) {
        if (encoding != null) {
            this._encoding = encoding;
            this._encodingSet = true;
        }
        if (version != null) {
            this._version = version;
        }
        this.setEventType(7);
    }

    @Override
    public String getSystemId() {
        return super.getSystemId();
    }

    @Override
    public String getCharacterEncodingScheme() {
        return this._encoding;
    }

    @Override
    public boolean encodingSet() {
        return this._encodingSet;
    }

    @Override
    public boolean isStandalone() {
        return this._standalone;
    }

    @Override
    public boolean standaloneSet() {
        return this._standaloneSet;
    }

    @Override
    public String getVersion() {
        return this._version;
    }

    public void setStandalone(boolean standalone) {
        this._standaloneSet = true;
        this._standalone = standalone;
    }

    public void setStandalone(String s) {
        this._standaloneSet = true;
        if (s == null) {
            this._standalone = true;
            return;
        }
        this._standalone = s.equals("yes");
    }

    public void setEncoding(String encoding) {
        this._encoding = encoding;
        this._encodingSet = true;
    }

    void setDeclaredEncoding(boolean value) {
        this._encodingSet = value;
    }

    public void setVersion(String s) {
        this._version = s;
    }

    void clear() {
        this._encoding = "UTF-8";
        this._standalone = true;
        this._version = "1.0";
        this._encodingSet = false;
        this._standaloneSet = false;
    }

    public String toString() {
        String s = "<?xml version=\"" + this._version + "\"";
        s = s + " encoding='" + this._encoding + "'";
        s = this._standaloneSet ? (this._standalone ? s + " standalone='yes'?>" : s + " standalone='no'?>") : s + "?>";
        return s;
    }

    @Override
    public boolean isStartDocument() {
        return true;
    }
}

