/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json;

import java.io.IOException;
import net.minidev.json.JSONValue;
import net.minidev.json.JStylerObj;

public class JSONStyle {
    public static final int FLAG_PROTECT_KEYS = 1;
    public static final int FLAG_PROTECT_4WEB = 2;
    public static final int FLAG_PROTECT_VALUES = 4;
    public static final int FLAG_AGRESSIVE = 8;
    public static final int FLAG_IGNORE_NULL = 16;
    public static final JSONStyle NO_COMPRESS = new JSONStyle(0);
    public static final JSONStyle MAX_COMPRESS = new JSONStyle(-1);
    public static final JSONStyle LT_COMPRESS = new JSONStyle(2);
    private boolean _protectKeys;
    private boolean _protect4Web;
    private boolean _protectValues;
    private boolean _ignore_null;
    private JStylerObj.MustProtect mpKey;
    private JStylerObj.MustProtect mpValue;
    private JStylerObj.StringProtector esc;

    public JSONStyle(int FLAG) {
        this._protectKeys = (FLAG & 1) == 0;
        this._protectValues = (FLAG & 4) == 0;
        this._protect4Web = (FLAG & 2) == 0;
        this._ignore_null = (FLAG & 0x10) > 0;
        JStylerObj.MustProtect mp = (FLAG & 8) > 0 ? JStylerObj.MP_AGGRESIVE : JStylerObj.MP_SIMPLE;
        this.mpValue = this._protectValues ? JStylerObj.MP_TRUE : mp;
        this.mpKey = this._protectKeys ? JStylerObj.MP_TRUE : mp;
        this.esc = this._protect4Web ? JStylerObj.ESCAPE4Web : JStylerObj.ESCAPE_LT;
    }

    public JSONStyle() {
        this(0);
    }

    public boolean protectKeys() {
        return this._protectKeys;
    }

    public boolean protectValues() {
        return this._protectValues;
    }

    public boolean protect4Web() {
        return this._protect4Web;
    }

    public boolean ignoreNull() {
        return this._ignore_null;
    }

    public boolean indent() {
        return false;
    }

    public boolean mustProtectKey(String s) {
        return this.mpKey.mustBeProtect(s);
    }

    public boolean mustProtectValue(String s) {
        return this.mpValue.mustBeProtect(s);
    }

    public void writeString(Appendable out, String value) throws IOException {
        if (!this.mustProtectValue(value)) {
            out.append(value);
        } else {
            out.append('\"');
            JSONValue.escape(value, out, this);
            out.append('\"');
        }
    }

    public void escape(String s, Appendable out) {
        this.esc.escape(s, out);
    }

    public void objectStart(Appendable out) throws IOException {
        out.append('{');
    }

    public void objectStop(Appendable out) throws IOException {
        out.append('}');
    }

    public void objectFirstStart(Appendable out) throws IOException {
    }

    public void objectNext(Appendable out) throws IOException {
        out.append(',');
    }

    public void objectElmStop(Appendable out) throws IOException {
    }

    public void objectEndOfKey(Appendable out) throws IOException {
        out.append(':');
    }

    public void arrayStart(Appendable out) throws IOException {
        out.append('[');
    }

    public void arrayStop(Appendable out) throws IOException {
        out.append(']');
    }

    public void arrayfirstObject(Appendable out) throws IOException {
    }

    public void arrayNextElm(Appendable out) throws IOException {
        out.append(',');
    }

    public void arrayObjectEnd(Appendable out) throws IOException {
    }
}

