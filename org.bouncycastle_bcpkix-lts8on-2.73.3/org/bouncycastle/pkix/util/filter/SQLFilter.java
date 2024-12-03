/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util.filter;

import org.bouncycastle.pkix.util.filter.Filter;

public class SQLFilter
implements Filter {
    @Override
    public String doFilter(String input) {
        StringBuffer buf = new StringBuffer(input);
        block11: for (int i = 0; i < buf.length(); ++i) {
            char ch = buf.charAt(i);
            switch (ch) {
                case '\'': {
                    buf.replace(i, i + 1, "\\'");
                    ++i;
                    continue block11;
                }
                case '\"': {
                    buf.replace(i, i + 1, "\\\"");
                    ++i;
                    continue block11;
                }
                case '=': {
                    buf.replace(i, i + 1, "\\=");
                    ++i;
                    continue block11;
                }
                case '-': {
                    buf.replace(i, i + 1, "\\-");
                    ++i;
                    continue block11;
                }
                case '/': {
                    buf.replace(i, i + 1, "\\/");
                    ++i;
                    continue block11;
                }
                case '\\': {
                    buf.replace(i, i + 1, "\\\\");
                    ++i;
                    continue block11;
                }
                case ';': {
                    buf.replace(i, i + 1, "\\;");
                    ++i;
                    continue block11;
                }
                case '\r': {
                    buf.replace(i, i + 1, "\\r");
                    ++i;
                    continue block11;
                }
                case '\n': {
                    buf.replace(i, i + 1, "\\n");
                    ++i;
                    continue block11;
                }
            }
        }
        return buf.toString();
    }

    @Override
    public String doFilterUrl(String input) {
        return this.doFilter(input);
    }
}

