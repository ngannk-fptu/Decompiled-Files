/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.QuotedStringTokenizer
 */
package org.eclipse.jetty.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.http.QuotedCSVParser;
import org.eclipse.jetty.util.QuotedStringTokenizer;

public class QuotedCSV
extends QuotedCSVParser
implements Iterable<String> {
    public static final String ABNF_REQUIRED_QUOTING = "\"'\\\n\r\t\f\b%+ ;=,";
    protected final List<String> _values = new ArrayList<String>();

    public static String join(List<String> values) {
        if (values == null) {
            return null;
        }
        int size = values.size();
        if (size <= 0) {
            return "";
        }
        if (size == 1) {
            return values.get(0);
        }
        StringBuilder ret = new StringBuilder();
        QuotedCSV.join(ret, values);
        return ret.toString();
    }

    public static String join(String ... values) {
        if (values == null) {
            return null;
        }
        if (values.length <= 0) {
            return "";
        }
        if (values.length == 1) {
            return values[0];
        }
        StringBuilder ret = new StringBuilder();
        QuotedCSV.join(ret, Arrays.asList(values));
        return ret.toString();
    }

    public static void join(StringBuilder builder, List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        boolean needsDelim = false;
        for (String value : values) {
            if (needsDelim) {
                builder.append(", ");
            } else {
                needsDelim = true;
            }
            QuotedStringTokenizer.quoteIfNeeded((StringBuilder)builder, (String)value, (String)ABNF_REQUIRED_QUOTING);
        }
    }

    public QuotedCSV(String ... values) {
        this(true, values);
    }

    public QuotedCSV(boolean keepQuotes, String ... values) {
        super(keepQuotes);
        for (String v : values) {
            this.addValue(v);
        }
    }

    @Override
    protected void parsedValueAndParams(StringBuffer buffer) {
        this._values.add(buffer.toString());
    }

    public int size() {
        return this._values.size();
    }

    public boolean isEmpty() {
        return this._values.isEmpty();
    }

    public List<String> getValues() {
        return this._values;
    }

    @Override
    public Iterator<String> iterator() {
        return this._values.iterator();
    }

    public String toString() {
        ArrayList<String> list = new ArrayList<String>();
        for (String s : this) {
            list.add(s);
        }
        return ((Object)list).toString();
    }
}

