/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.QuotedStringTokenizer
 *  org.eclipse.jetty.util.StringUtil
 */
package org.eclipse.jetty.http;

import java.util.Map;
import java.util.Objects;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.StringUtil;

public class HttpField {
    private static final String __zeroquality = "q=0";
    private final HttpHeader _header;
    private final String _name;
    private final String _value;
    private int hash = 0;

    public HttpField(HttpHeader header, String name, String value) {
        this._header = header;
        this._name = this._header != null && name == null ? this._header.asString() : Objects.requireNonNull(name, "name");
        this._value = value;
    }

    public HttpField(HttpHeader header, String value) {
        this(header, header.asString(), value);
    }

    public HttpField(HttpHeader header, HttpHeaderValue value) {
        this(header, header.asString(), value.asString());
    }

    public HttpField(String name, String value) {
        this((HttpHeader)((Object)HttpHeader.CACHE.get(name)), name, value);
    }

    public static String getValueParameters(String value, Map<String, String> parameters) {
        if (value == null) {
            return null;
        }
        int i = value.indexOf(59);
        if (i < 0) {
            return value;
        }
        if (parameters == null) {
            return value.substring(0, i).trim();
        }
        QuotedStringTokenizer tok1 = new QuotedStringTokenizer(value.substring(i), ";", false, true);
        while (tok1.hasMoreTokens()) {
            String token = tok1.nextToken();
            QuotedStringTokenizer tok2 = new QuotedStringTokenizer(token, "= ");
            if (!tok2.hasMoreTokens()) continue;
            String paramName = tok2.nextToken();
            String paramVal = null;
            if (tok2.hasMoreTokens()) {
                paramVal = tok2.nextToken();
            }
            parameters.put(paramName, paramVal);
        }
        return value.substring(0, i).trim();
    }

    public static String stripParameters(String value) {
        if (value == null) {
            return null;
        }
        int i = value.indexOf(59);
        if (i < 0) {
            return value;
        }
        return value.substring(0, i).trim();
    }

    public static String valueParameters(String value, Map<String, String> parameters) {
        return HttpField.getValueParameters(value, parameters);
    }

    public boolean contains(String search) {
        if (search == null) {
            return this._value == null;
        }
        if (search.isEmpty()) {
            return false;
        }
        if (this._value == null) {
            return false;
        }
        if (search.equalsIgnoreCase(this._value)) {
            return true;
        }
        int state = 0;
        int match = 0;
        int param = 0;
        block31: for (int i = 0; i < this._value.length(); ++i) {
            char c = StringUtil.asciiToLowerCase((char)this._value.charAt(i));
            switch (state) {
                case 0: {
                    switch (c) {
                        case '\"': {
                            match = 0;
                            state = 2;
                            continue block31;
                        }
                        case ',': {
                            continue block31;
                        }
                        case ';': {
                            param = -1;
                            match = -1;
                            state = 5;
                            continue block31;
                        }
                        case '\t': 
                        case ' ': {
                            continue block31;
                        }
                    }
                    match = c == StringUtil.asciiToLowerCase((char)search.charAt(0)) ? 1 : -1;
                    state = 1;
                    continue block31;
                }
                case 1: {
                    switch (c) {
                        case ',': {
                            if (match == search.length()) {
                                return true;
                            }
                            state = 0;
                            continue block31;
                        }
                        case ';': {
                            param = match >= 0 ? 0 : -1;
                            state = 5;
                            continue block31;
                        }
                    }
                    if (match <= 0) continue block31;
                    if (match < search.length()) {
                        match = c == StringUtil.asciiToLowerCase((char)search.charAt(match)) ? match + 1 : -1;
                        continue block31;
                    }
                    if (c == ' ' || c == '\t') continue block31;
                    match = -1;
                    continue block31;
                }
                case 2: {
                    switch (c) {
                        case '\\': {
                            state = 3;
                            continue block31;
                        }
                        case '\"': {
                            state = 4;
                            continue block31;
                        }
                    }
                    if (match < 0) continue block31;
                    if (match < search.length()) {
                        match = c == StringUtil.asciiToLowerCase((char)search.charAt(match)) ? match + 1 : -1;
                        continue block31;
                    }
                    match = -1;
                    continue block31;
                }
                case 3: {
                    if (match >= 0) {
                        match = match < search.length() ? (c == StringUtil.asciiToLowerCase((char)search.charAt(match)) ? match + 1 : -1) : -1;
                    }
                    state = 2;
                    continue block31;
                }
                case 4: {
                    switch (c) {
                        case '\t': 
                        case ' ': {
                            continue block31;
                        }
                        case ';': {
                            state = 5;
                            continue block31;
                        }
                        case ',': {
                            if (match == search.length()) {
                                return true;
                            }
                            state = 0;
                            continue block31;
                        }
                    }
                    match = -1;
                    continue block31;
                }
                case 5: {
                    switch (c) {
                        case ',': {
                            if (param != __zeroquality.length() && match == search.length()) {
                                return true;
                            }
                            param = 0;
                            state = 0;
                            continue block31;
                        }
                        case '\t': 
                        case ' ': {
                            continue block31;
                        }
                    }
                    if (param < 0) continue block31;
                    if (param < __zeroquality.length()) {
                        param = c == __zeroquality.charAt(param) ? param + 1 : -1;
                        continue block31;
                    }
                    if (c == '0' || c == '.') continue block31;
                    param = -1;
                    continue block31;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        return param != __zeroquality.length() && match == search.length();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HttpField)) {
            return false;
        }
        HttpField field = (HttpField)o;
        if (this._header != field.getHeader()) {
            return false;
        }
        if (!this._name.equalsIgnoreCase(field.getName())) {
            return false;
        }
        return Objects.equals(this._value, field.getValue());
    }

    public HttpHeader getHeader() {
        return this._header;
    }

    public int getIntValue() {
        return Integer.parseInt(this._value);
    }

    public long getLongValue() {
        return Long.parseLong(this._value);
    }

    public String getLowerCaseName() {
        return this._header != null ? this._header.lowerCaseName() : StringUtil.asciiToLowerCase((String)this._name);
    }

    public String getName() {
        return this._name;
    }

    public String getValue() {
        return this._value;
    }

    public String[] getValues() {
        if (this._value == null) {
            return null;
        }
        QuotedCSV list = new QuotedCSV(false, this._value);
        return list.getValues().toArray(new String[list.size()]);
    }

    public int hashCode() {
        int vhc = Objects.hashCode(this._value);
        if (this._header == null) {
            return vhc ^ this.nameHashCode();
        }
        return vhc ^ this._header.hashCode();
    }

    public boolean isSameName(HttpField field) {
        if (field == null) {
            return false;
        }
        if (field == this) {
            return true;
        }
        if (this._header != null && this._header == field.getHeader()) {
            return true;
        }
        return this._name.equalsIgnoreCase(field.getName());
    }

    public boolean is(String name) {
        return this._name.equalsIgnoreCase(name);
    }

    private int nameHashCode() {
        int h = this.hash;
        int len = this._name.length();
        if (h == 0 && len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = this._name.charAt(i);
                if (c >= 'a' && c <= 'z') {
                    c = (char)(c - 32);
                }
                h = 31 * h + c;
            }
            this.hash = h;
        }
        return h;
    }

    public String toString() {
        String v = this.getValue();
        return this.getName() + ": " + (v == null ? "" : v);
    }

    public static class LongValueHttpField
    extends HttpField {
        private final long _long;

        public LongValueHttpField(HttpHeader header, String name, String value, long longValue) {
            super(header, name, value);
            this._long = longValue;
        }

        public LongValueHttpField(HttpHeader header, String name, String value) {
            this(header, name, value, Long.parseLong(value));
        }

        public LongValueHttpField(HttpHeader header, String name, long value) {
            this(header, name, Long.toString(value), value);
        }

        public LongValueHttpField(HttpHeader header, long value) {
            this(header, header.asString(), value);
        }

        @Override
        public int getIntValue() {
            return (int)this._long;
        }

        @Override
        public long getLongValue() {
            return this._long;
        }
    }

    public static class IntValueHttpField
    extends HttpField {
        private final int _int;

        public IntValueHttpField(HttpHeader header, String name, String value, int intValue) {
            super(header, name, value);
            this._int = intValue;
        }

        public IntValueHttpField(HttpHeader header, String name, String value) {
            this(header, name, value, Integer.parseInt(value));
        }

        public IntValueHttpField(HttpHeader header, String name, int intValue) {
            this(header, name, Integer.toString(intValue), intValue);
        }

        public IntValueHttpField(HttpHeader header, int value) {
            this(header, header.asString(), value);
        }

        @Override
        public int getIntValue() {
            return this._int;
        }

        @Override
        public long getLongValue() {
            return this._int;
        }
    }
}

