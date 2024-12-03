/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringEscapeUtils
 */
package org.apache.velocity.tools.generic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="esc")
public class EscapeTool
extends SafeConfig {
    public static final String DEFAULT_KEY = "esc";
    private String key = "esc";

    @Override
    protected void configure(ValueParser values) {
        String altkey = values.getString("key");
        if (altkey != null) {
            this.setKey(altkey);
        }
    }

    protected void setKey(String key) {
        if (key == null) {
            throw new NullPointerException("EscapeTool key cannot be null");
        }
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String velocity(Object obj) {
        if (obj == null) {
            return null;
        }
        String string = String.valueOf(obj);
        return string.replace("$", "${" + this.getKey() + ".d}").replace("#", "${" + this.getKey() + ".h}");
    }

    public String java(Object string) {
        if (string == null) {
            return null;
        }
        return StringEscapeUtils.escapeJava((String)String.valueOf(string));
    }

    public String propertyKey(Object string) {
        if (string == null) {
            return null;
        }
        return this.dumpString(String.valueOf(string), true);
    }

    public String propertyValue(Object string) {
        if (string == null) {
            return null;
        }
        return this.dumpString(String.valueOf(string), false);
    }

    protected String dumpString(String string, boolean key) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        if (!key && i < string.length() && string.charAt(i) == ' ') {
            builder.append("\\ ");
            ++i;
        }
        while (i < string.length()) {
            char ch = string.charAt(i);
            switch (ch) {
                case '\t': {
                    builder.append("\\t");
                    break;
                }
                case '\n': {
                    builder.append("\\n");
                    break;
                }
                case '\f': {
                    builder.append("\\f");
                    break;
                }
                case '\r': {
                    builder.append("\\r");
                    break;
                }
                default: {
                    if ("\\#!=:".indexOf(ch) >= 0 || key && ch == ' ') {
                        builder.append('\\');
                    }
                    if (ch >= ' ' && ch <= '~') {
                        builder.append(ch);
                        break;
                    }
                    String hex = Integer.toHexString(ch);
                    builder.append("\\u");
                    for (int j = 0; j < 4 - hex.length(); ++j) {
                        builder.append("0");
                    }
                    builder.append(hex);
                }
            }
            ++i;
        }
        return builder.toString();
    }

    public String javascript(Object string) {
        if (string == null) {
            return null;
        }
        return StringEscapeUtils.escapeJavaScript((String)String.valueOf(string));
    }

    public String html(Object string) {
        if (string == null) {
            return null;
        }
        return StringEscapeUtils.escapeHtml((String)String.valueOf(string));
    }

    public String url(Object string) {
        if (string == null) {
            return null;
        }
        try {
            return URLEncoder.encode(String.valueOf(string), "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    public String xml(Object string) {
        if (string == null) {
            return null;
        }
        return StringEscapeUtils.escapeXml((String)String.valueOf(string));
    }

    public String sql(Object string) {
        if (string == null) {
            return null;
        }
        return StringEscapeUtils.escapeSql((String)String.valueOf(string));
    }

    public String unicode(Object code) {
        if (code == null) {
            return null;
        }
        String s = String.valueOf(code);
        if (s.startsWith("\\u")) {
            s = s.substring(2, s.length());
        }
        int codePoint = Integer.valueOf(s, 16);
        return String.valueOf(Character.toChars(codePoint));
    }

    public String getDollar() {
        return "$";
    }

    public String getD() {
        return this.getDollar();
    }

    public String getHash() {
        return "#";
    }

    public String getH() {
        return this.getHash();
    }

    public String getBackslash() {
        return "\\";
    }

    public String getB() {
        return this.getBackslash();
    }

    public String getQuote() {
        return "\"";
    }

    public String getQ() {
        return this.getQuote();
    }

    public String getSingleQuote() {
        return "'";
    }

    public String getS() {
        return this.getSingleQuote();
    }

    public String getNewline() {
        return "\n";
    }

    public String getN() {
        return this.getNewline();
    }

    public String getExclamation() {
        return "!";
    }

    public String getE() {
        return this.getExclamation();
    }
}

