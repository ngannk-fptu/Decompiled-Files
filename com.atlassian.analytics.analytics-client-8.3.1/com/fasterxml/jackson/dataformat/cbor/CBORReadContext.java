/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.json.DupDetector;

public final class CBORReadContext
extends JsonStreamContext {
    protected final CBORReadContext _parent;
    protected final DupDetector _dups;
    protected int _expEntryCount;
    protected String _currentName;
    protected Object _currentValue;
    protected CBORReadContext _child = null;

    public CBORReadContext(CBORReadContext parent, DupDetector dups, int type, int expEntryCount) {
        this._parent = parent;
        this._dups = dups;
        this._type = type;
        this._expEntryCount = expEntryCount;
        this._index = -1;
    }

    protected void reset(int type, int expEntryCount) {
        this._type = type;
        this._expEntryCount = expEntryCount;
        this._index = -1;
        this._currentName = null;
        this._currentValue = null;
        if (this._dups != null) {
            this._dups.reset();
        }
    }

    @Override
    public Object getCurrentValue() {
        return this._currentValue;
    }

    @Override
    public void setCurrentValue(Object v) {
        this._currentValue = v;
    }

    public static CBORReadContext createRootContext(DupDetector dups) {
        return new CBORReadContext(null, dups, 0, -1);
    }

    public CBORReadContext createChildArrayContext(int expEntryCount) {
        CBORReadContext ctxt = this._child;
        if (ctxt == null) {
            this._child = ctxt = new CBORReadContext(this, this._dups == null ? null : this._dups.child(), 1, expEntryCount);
        } else {
            ctxt.reset(1, expEntryCount);
        }
        return ctxt;
    }

    public CBORReadContext createChildObjectContext(int expEntryCount) {
        CBORReadContext ctxt = this._child;
        if (ctxt == null) {
            this._child = ctxt = new CBORReadContext(this, this._dups == null ? null : this._dups.child(), 2, expEntryCount);
            return ctxt;
        }
        ctxt.reset(2, expEntryCount);
        return ctxt;
    }

    @Override
    public String getCurrentName() {
        return this._currentName;
    }

    @Override
    public CBORReadContext getParent() {
        return this._parent;
    }

    public boolean hasExpectedLength() {
        return this._expEntryCount >= 0;
    }

    public int getExpectedLength() {
        return this._expEntryCount;
    }

    public int getRemainingExpectedLength() {
        int diff = this._expEntryCount - this._index;
        return Math.max(0, diff);
    }

    public boolean acceptsBreakMarker() {
        return this._expEntryCount < 0 && this._type != 0;
    }

    public boolean expectMoreValues() {
        return ++this._index != this._expEntryCount;
    }

    @Override
    public JsonLocation startLocation(ContentReference srcRef) {
        return new JsonLocation(srcRef, 1L, -1, -1);
    }

    @Override
    @Deprecated
    public JsonLocation getStartLocation(Object rawSrc) {
        return this.startLocation(ContentReference.rawReference(rawSrc));
    }

    public void setCurrentName(String name) throws JsonProcessingException {
        this._currentName = name;
        if (this._dups != null) {
            this._checkDup(this._dups, name);
        }
    }

    private void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
        if (dd.isDup(name)) {
            throw new JsonParseException(null, "Duplicate field '" + name + "'", dd.findLocation());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        switch (this._type) {
            case 0: {
                sb.append("/");
                break;
            }
            case 1: {
                sb.append('[');
                sb.append(this.getCurrentIndex());
                sb.append(']');
                break;
            }
            case 2: {
                sb.append('{');
                if (this._currentName != null) {
                    sb.append('\"');
                    CharTypes.appendQuoted(sb, this._currentName);
                    sb.append('\"');
                } else {
                    sb.append('?');
                }
                sb.append('}');
            }
        }
        return sb.toString();
    }
}

