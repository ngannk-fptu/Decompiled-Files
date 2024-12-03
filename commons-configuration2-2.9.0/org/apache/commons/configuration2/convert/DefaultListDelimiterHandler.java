/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.convert;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration2.convert.AbstractListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;
import org.apache.commons.lang3.StringUtils;

public class DefaultListDelimiterHandler
extends AbstractListDelimiterHandler {
    private static final char ESCAPE = '\\';
    private static final int BUF_SIZE = 16;
    private final char delimiter;

    public DefaultListDelimiterHandler(char listDelimiter) {
        this.delimiter = listDelimiter;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    @Override
    public Object escapeList(List<?> values, ValueTransformer transformer) {
        Object[] escapedValues = new Object[values.size()];
        int idx = 0;
        for (Object v : values) {
            escapedValues[idx++] = this.escape(v, transformer);
        }
        return StringUtils.join((Object[])escapedValues, (char)this.getDelimiter());
    }

    @Override
    protected String escapeString(String s) {
        StringBuilder buf = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == this.getDelimiter() || c == '\\') {
                buf.append('\\');
            }
            buf.append(c);
        }
        return buf.toString();
    }

    @Override
    protected Collection<String> splitString(String s, boolean trim) {
        LinkedList<String> list = new LinkedList<String>();
        StringBuilder token = new StringBuilder();
        boolean inEscape = false;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (inEscape) {
                if (c != this.getDelimiter() && c != '\\') {
                    token.append('\\');
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
            if (c == '\\') {
                inEscape = true;
                continue;
            }
            token.append(c);
        }
        if (inEscape) {
            token.append('\\');
        }
        String t = token.toString();
        if (trim) {
            t = t.trim();
        }
        list.add(t);
        return list;
    }
}

