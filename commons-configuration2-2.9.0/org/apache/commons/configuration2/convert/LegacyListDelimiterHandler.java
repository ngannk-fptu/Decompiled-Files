/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration2.convert.AbstractListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;
import org.apache.commons.lang3.StringUtils;

public class LegacyListDelimiterHandler
extends AbstractListDelimiterHandler {
    private static final String ESCAPE = "\\";
    private static final String DOUBLE_ESC = "\\\\";
    private static final String QUAD_ESC = "\\\\\\\\";
    private final char delimiter;

    public LegacyListDelimiterHandler(char listDelimiter) {
        this.delimiter = listDelimiter;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    @Override
    public Object escape(Object value, ValueTransformer transformer) {
        return this.escapeValue(value, false, transformer);
    }

    @Override
    public Object escapeList(List<?> values, ValueTransformer transformer) {
        if (!values.isEmpty()) {
            Iterator<?> it = values.iterator();
            String lastValue = this.escapeValue(it.next(), true, transformer);
            StringBuilder buf = new StringBuilder(lastValue);
            while (it.hasNext()) {
                if (lastValue.endsWith(ESCAPE) && LegacyListDelimiterHandler.countTrailingBS(lastValue) / 2 % 2 != 0) {
                    buf.append(ESCAPE).append(ESCAPE);
                }
                buf.append(this.getDelimiter());
                lastValue = this.escapeValue(it.next(), true, transformer);
                buf.append(lastValue);
            }
            return buf.toString();
        }
        return null;
    }

    @Override
    protected Collection<String> splitString(String s, boolean trim) {
        if (s.indexOf(this.getDelimiter()) < 0) {
            return Collections.singleton(s);
        }
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder token = new StringBuilder();
        boolean inEscape = false;
        char esc = ESCAPE.charAt(0);
        for (int begin = 0; begin < s.length(); ++begin) {
            char c = s.charAt(begin);
            if (inEscape) {
                if (c != this.getDelimiter() && c != esc) {
                    token.append(esc);
                }
                token.append(c);
                inEscape = false;
                continue;
            }
            if (c == this.getDelimiter()) {
                String t = token.toString();
                if (trim) {
                    t = t.trim();
                }
                list.add(t);
                token = new StringBuilder();
                continue;
            }
            if (c == esc) {
                inEscape = true;
                continue;
            }
            token.append(c);
        }
        if (inEscape) {
            token.append(esc);
        }
        String t = token.toString();
        if (trim) {
            t = t.trim();
        }
        list.add(t);
        return list;
    }

    @Override
    protected String escapeString(String s) {
        return null;
    }

    protected String escapeBackslashs(Object value, boolean inList) {
        String strValue = String.valueOf(value);
        if (inList && strValue.contains(DOUBLE_ESC)) {
            strValue = StringUtils.replace((String)strValue, (String)DOUBLE_ESC, (String)QUAD_ESC);
        }
        return strValue;
    }

    protected String escapeValue(Object value, boolean inList, ValueTransformer transformer) {
        String escapedValue = String.valueOf(transformer.transformValue(this.escapeBackslashs(value, inList)));
        if (this.getDelimiter() != '\u0000') {
            escapedValue = StringUtils.replace((String)escapedValue, (String)String.valueOf(this.getDelimiter()), (String)(ESCAPE + this.getDelimiter()));
        }
        return escapedValue;
    }

    private static int countTrailingBS(String line) {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; --idx) {
            ++bsCount;
        }
        return bsCount;
    }
}

