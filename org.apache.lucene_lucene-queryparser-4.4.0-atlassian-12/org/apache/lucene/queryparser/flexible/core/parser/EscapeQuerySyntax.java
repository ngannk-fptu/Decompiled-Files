/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.parser;

import java.util.Locale;

public interface EscapeQuerySyntax {
    public CharSequence escape(CharSequence var1, Locale var2, Type var3);

    public static enum Type {
        STRING,
        NORMAL;

    }
}

