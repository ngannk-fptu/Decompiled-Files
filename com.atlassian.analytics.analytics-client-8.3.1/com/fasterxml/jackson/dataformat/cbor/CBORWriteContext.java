/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.json.DupDetector;

public final class CBORWriteContext
extends JsonStreamContext {
    protected final CBORWriteContext _parent;
    protected DupDetector _dups;
    protected CBORWriteContext _childToRecycle;
    protected String _currentName;
    protected Object _currentValue;
    protected long _currentFieldId;
    protected boolean _gotFieldId;

    protected CBORWriteContext(int type, CBORWriteContext parent, DupDetector dups, Object currentValue) {
        this._type = type;
        this._parent = parent;
        this._dups = dups;
        this._index = -1;
        this._currentValue = currentValue;
    }

    private CBORWriteContext reset(int type, Object currentValue) {
        this._type = type;
        this._index = -1;
        this._gotFieldId = false;
        this._currentValue = currentValue;
        if (this._dups != null) {
            this._dups.reset();
        }
        return this;
    }

    public CBORWriteContext withDupDetector(DupDetector dups) {
        this._dups = dups;
        return this;
    }

    @Override
    public Object getCurrentValue() {
        return this._currentValue;
    }

    @Override
    public void setCurrentValue(Object v) {
        this._currentValue = v;
    }

    public static CBORWriteContext createRootContext(DupDetector dd) {
        return new CBORWriteContext(0, null, dd, null);
    }

    public CBORWriteContext createChildArrayContext(Object currentValue) {
        CBORWriteContext ctxt = this._childToRecycle;
        if (ctxt == null) {
            this._childToRecycle = ctxt = new CBORWriteContext(1, this, this._dups == null ? null : this._dups.child(), currentValue);
            return ctxt;
        }
        return ctxt.reset(1, currentValue);
    }

    public CBORWriteContext createChildObjectContext(Object currentValue) {
        CBORWriteContext ctxt = this._childToRecycle;
        if (ctxt == null) {
            this._childToRecycle = ctxt = new CBORWriteContext(2, this, this._dups == null ? null : this._dups.child(), currentValue);
            return ctxt;
        }
        return ctxt.reset(2, currentValue);
    }

    @Override
    public final CBORWriteContext getParent() {
        return this._parent;
    }

    @Override
    public final String getCurrentName() {
        if (this._gotFieldId) {
            if (this._currentName != null) {
                return this._currentName;
            }
            return String.valueOf(this._currentFieldId);
        }
        return null;
    }

    @Override
    public boolean hasCurrentName() {
        return this._gotFieldId;
    }

    public CBORWriteContext clearAndGetParent() {
        this._currentValue = null;
        return this._parent;
    }

    public DupDetector getDupDetector() {
        return this._dups;
    }

    public boolean writeFieldName(String name) throws JsonProcessingException {
        if (this._type != 2 || this._gotFieldId) {
            return false;
        }
        this._gotFieldId = true;
        this._currentName = name;
        if (this._dups != null) {
            this._checkDup(this._dups, name);
        }
        return true;
    }

    public boolean writeFieldId(long fieldId) throws JsonProcessingException {
        if (this._type != 2 || this._gotFieldId) {
            return false;
        }
        this._gotFieldId = true;
        this._currentFieldId = fieldId;
        return true;
    }

    private final void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
        if (dd.isDup(name)) {
            Object src = dd.getSource();
            throw new JsonGenerationException("Duplicate field '" + name + "'", src instanceof JsonGenerator ? (JsonGenerator)src : null);
        }
    }

    public boolean writeValue() {
        if (this._type == 2) {
            if (!this._gotFieldId) {
                return false;
            }
            this._gotFieldId = false;
        }
        ++this._index;
        return true;
    }
}

