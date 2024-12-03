/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import javax.json.JsonString;
import javax.json.JsonValue;

final class JsonStringImpl
implements JsonString {
    private final String value;

    JsonStringImpl(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return this.value;
    }

    @Override
    public CharSequence getChars() {
        return this.value;
    }

    @Override
    public JsonValue.ValueType getValueType() {
        return JsonValue.ValueType.STRING;
    }

    @Override
    public int hashCode() {
        return this.getString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JsonString)) {
            return false;
        }
        JsonString other = (JsonString)obj;
        return this.getString().equals(other.getString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('\"');
        block8: for (int i = 0; i < this.value.length(); ++i) {
            char c = this.value.charAt(i);
            if (c >= ' ' && c <= '\u10ffff' && c != '\"' && c != '\\') {
                sb.append(c);
                continue;
            }
            switch (c) {
                case '\"': 
                case '\\': {
                    sb.append('\\');
                    sb.append(c);
                    continue block8;
                }
                case '\b': {
                    sb.append('\\');
                    sb.append('b');
                    continue block8;
                }
                case '\f': {
                    sb.append('\\');
                    sb.append('f');
                    continue block8;
                }
                case '\n': {
                    sb.append('\\');
                    sb.append('n');
                    continue block8;
                }
                case '\r': {
                    sb.append('\\');
                    sb.append('r');
                    continue block8;
                }
                case '\t': {
                    sb.append('\\');
                    sb.append('t');
                    continue block8;
                }
                default: {
                    String hex = "000" + Integer.toHexString(c);
                    sb.append("\\u").append(hex.substring(hex.length() - 4));
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
}

