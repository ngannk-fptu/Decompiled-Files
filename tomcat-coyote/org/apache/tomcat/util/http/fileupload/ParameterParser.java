/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.util.mime.MimeUtility;
import org.apache.tomcat.util.http.fileupload.util.mime.RFC2231Utility;

public class ParameterParser {
    private char[] chars = null;
    private int pos = 0;
    private int len = 0;
    private int i1 = 0;
    private int i2 = 0;
    private boolean lowerCaseNames = false;

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
        if (this.i2 > this.i1) {
            result = new String(this.chars, this.i1, this.i2 - this.i1);
        }
        return result;
    }

    private boolean isOneOf(char ch, char[] charray) {
        boolean result = false;
        for (char element : charray) {
            if (ch != element) continue;
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

    public boolean isLowerCaseNames() {
        return this.lowerCaseNames;
    }

    public void setLowerCaseNames(boolean b) {
        this.lowerCaseNames = b;
    }

    public Map<String, String> parse(String str, char[] separators) {
        if (separators == null || separators.length == 0) {
            return new HashMap<String, String>();
        }
        char separator = separators[0];
        if (str != null) {
            int idx = str.length();
            for (char separator2 : separators) {
                int tmp = str.indexOf(separator2);
                if (tmp == -1 || tmp >= idx) continue;
                idx = tmp;
                separator = separator2;
            }
        }
        return this.parse(str, separator);
    }

    public Map<String, String> parse(String str, char separator) {
        if (str == null) {
            return new HashMap<String, String>();
        }
        return this.parse(str.toCharArray(), separator);
    }

    public Map<String, String> parse(char[] charArray, char separator) {
        if (charArray == null) {
            return new HashMap<String, String>();
        }
        return this.parse(charArray, 0, charArray.length, separator);
    }

    public Map<String, String> parse(char[] charArray, int offset, int length, char separator) {
        if (charArray == null) {
            return new HashMap<String, String>();
        }
        HashMap<String, String> params = new HashMap<String, String>();
        this.chars = (char[])charArray.clone();
        this.pos = offset;
        this.len = length;
        while (this.hasChar()) {
            String paramName = this.parseToken(new char[]{'=', separator});
            String paramValue = null;
            if (this.hasChar() && charArray[this.pos] == '=') {
                ++this.pos;
                paramValue = this.parseQuotedToken(new char[]{separator});
                if (paramValue != null) {
                    try {
                        paramValue = RFC2231Utility.hasEncodedValue(paramName) ? RFC2231Utility.decodeText(paramValue) : MimeUtility.decodeText(paramValue);
                    }
                    catch (UnsupportedEncodingException unsupportedEncodingException) {
                        // empty catch block
                    }
                }
            }
            if (this.hasChar() && charArray[this.pos] == separator) {
                ++this.pos;
            }
            if (paramName == null || paramName.isEmpty()) continue;
            paramName = RFC2231Utility.stripDelimiter(paramName);
            if (this.lowerCaseNames) {
                paramName = paramName.toLowerCase(Locale.ENGLISH);
            }
            params.put(paramName, paramValue);
        }
        return params;
    }
}

