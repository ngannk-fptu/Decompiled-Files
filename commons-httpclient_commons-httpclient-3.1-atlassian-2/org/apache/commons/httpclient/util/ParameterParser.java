/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;

public class ParameterParser {
    private char[] chars = null;
    private int pos = 0;
    private int len = 0;
    private int i1 = 0;
    private int i2 = 0;

    private boolean hasChar() {
        return this.pos < this.len;
    }

    private String getToken(boolean quoted) {
        while (this.i1 < this.i2 && Character.isWhitespace(this.chars[this.i1])) {
            ++this.i1;
        }
        while (this.i2 > this.i1 && Character.isWhitespace(this.chars[this.i2 - 1])) {
            --this.i2;
        }
        if (quoted && this.i2 - this.i1 >= 2 && this.chars[this.i1] == '\"' && this.chars[this.i2 - 1] == '\"') {
            ++this.i1;
            --this.i2;
        }
        String result = null;
        if (this.i2 >= this.i1) {
            result = new String(this.chars, this.i1, this.i2 - this.i1);
        }
        return result;
    }

    private boolean isOneOf(char ch, char[] charray) {
        boolean result = false;
        for (int i = 0; i < charray.length; ++i) {
            if (ch != charray[i]) continue;
            result = true;
            break;
        }
        return result;
    }

    private String parseToken(char[] terminators) {
        char ch;
        this.i1 = this.pos;
        this.i2 = this.pos;
        while (this.hasChar() && !this.isOneOf(ch = this.chars[this.pos], terminators)) {
            ++this.i2;
            ++this.pos;
        }
        return this.getToken(false);
    }

    private String parseQuotedToken(char[] terminators) {
        this.i1 = this.pos;
        this.i2 = this.pos;
        boolean quoted = false;
        boolean charEscaped = false;
        while (this.hasChar()) {
            char ch = this.chars[this.pos];
            if (!quoted && this.isOneOf(ch, terminators)) break;
            if (!charEscaped && ch == '\"') {
                quoted = !quoted;
            }
            charEscaped = !charEscaped && ch == '\\';
            ++this.i2;
            ++this.pos;
        }
        return this.getToken(true);
    }

    public List parse(String str, char separator) {
        if (str == null) {
            return new ArrayList();
        }
        return this.parse(str.toCharArray(), separator);
    }

    public List parse(char[] chars, char separator) {
        if (chars == null) {
            return new ArrayList();
        }
        return this.parse(chars, 0, chars.length, separator);
    }

    public List parse(char[] chars, int offset, int length, char separator) {
        if (chars == null) {
            return new ArrayList();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        this.chars = chars;
        this.pos = offset;
        this.len = length;
        String paramName = null;
        String paramValue = null;
        while (this.hasChar()) {
            paramName = this.parseToken(new char[]{'=', separator});
            paramValue = null;
            if (this.hasChar() && chars[this.pos] == '=') {
                ++this.pos;
                paramValue = this.parseQuotedToken(new char[]{separator});
            }
            if (this.hasChar() && chars[this.pos] == separator) {
                ++this.pos;
            }
            if (paramName == null || paramName.equals("") && paramValue == null) continue;
            params.add(new NameValuePair(paramName, paramValue));
        }
        return params;
    }
}

