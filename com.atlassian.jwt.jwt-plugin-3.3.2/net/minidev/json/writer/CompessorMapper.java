/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import java.io.IOException;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.writer.JsonReaderI;

public class CompessorMapper
extends JsonReaderI<CompessorMapper> {
    private Appendable out;
    private JSONStyle compression;
    private Boolean _isObj;
    private boolean needSep = false;
    private boolean isOpen = false;
    private boolean isClosed = false;

    private boolean isArray() {
        return this._isObj == Boolean.FALSE;
    }

    private boolean isObject() {
        return this._isObj == Boolean.TRUE;
    }

    private boolean isCompressor(Object obj) {
        return obj instanceof CompessorMapper;
    }

    public CompessorMapper(JsonReader base, Appendable out, JSONStyle compression) {
        this(base, out, compression, null);
    }

    public CompessorMapper(JsonReader base, Appendable out, JSONStyle compression, Boolean isObj) {
        super(base);
        this.out = out;
        this.compression = compression;
        this._isObj = isObj;
    }

    @Override
    public JsonReaderI<?> startObject(String key) throws IOException {
        this.open(this);
        this.startKey(key);
        CompessorMapper r = new CompessorMapper(this.base, this.out, this.compression, true);
        this.open(r);
        return r;
    }

    @Override
    public JsonReaderI<?> startArray(String key) throws IOException {
        this.open(this);
        this.startKey(key);
        CompessorMapper r = new CompessorMapper(this.base, this.out, this.compression, false);
        this.open(r);
        return r;
    }

    private void startKey(String key) throws IOException {
        this.addComma();
        if (this.isArray()) {
            return;
        }
        if (!this.compression.mustProtectKey(key)) {
            this.out.append(key);
        } else {
            this.out.append('\"');
            JSONValue.escape(key, this.out, this.compression);
            this.out.append('\"');
        }
        this.out.append(':');
    }

    @Override
    public void setValue(Object current, String key, Object value) throws IOException {
        if (this.isCompressor(value)) {
            this.addComma();
            return;
        }
        this.startKey(key);
        this.writeValue(value);
    }

    @Override
    public void addValue(Object current, Object value) throws IOException {
        this.addComma();
        this.writeValue(value);
    }

    private void addComma() throws IOException {
        if (this.needSep) {
            this.out.append(',');
        } else {
            this.needSep = true;
        }
    }

    private void writeValue(Object value) throws IOException {
        if (value instanceof String) {
            this.compression.writeString(this.out, (String)value);
        } else if (this.isCompressor(value)) {
            this.close(value);
        } else {
            JSONValue.writeJSONString(value, this.out, this.compression);
        }
    }

    @Override
    public Object createObject() {
        this._isObj = true;
        try {
            this.open(this);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this;
    }

    @Override
    public Object createArray() {
        this._isObj = false;
        try {
            this.open(this);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this;
    }

    @Override
    public CompessorMapper convert(Object current) {
        try {
            this.close(current);
            return this;
        }
        catch (Exception e) {
            return this;
        }
    }

    private void close(Object obj) throws IOException {
        if (!this.isCompressor(obj)) {
            return;
        }
        if (((CompessorMapper)obj).isClosed) {
            return;
        }
        ((CompessorMapper)obj).isClosed = true;
        if (((CompessorMapper)obj).isObject()) {
            this.out.append('}');
            this.needSep = true;
        } else if (((CompessorMapper)obj).isArray()) {
            this.out.append(']');
            this.needSep = true;
        }
    }

    private void open(Object obj) throws IOException {
        if (!this.isCompressor(obj)) {
            return;
        }
        if (((CompessorMapper)obj).isOpen) {
            return;
        }
        ((CompessorMapper)obj).isOpen = true;
        if (((CompessorMapper)obj).isObject()) {
            this.out.append('{');
            this.needSep = false;
        } else if (((CompessorMapper)obj).isArray()) {
            this.out.append('[');
            this.needSep = false;
        }
    }
}

