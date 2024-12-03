/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.internal.HighlightingFormatter;

public enum FormatStyle {
    BASIC("basic", new BasicFormatterImpl()),
    DDL("ddl", DDLFormatterImpl.INSTANCE),
    HIGHLIGHT("highlight", HighlightingFormatter.INSTANCE),
    NONE("none", NoFormatImpl.INSTANCE);

    private final String name;
    private final Formatter formatter;

    private FormatStyle(String name, Formatter formatter) {
        this.name = name;
        this.formatter = formatter;
    }

    public String getName() {
        return this.name;
    }

    public Formatter getFormatter() {
        return this.formatter;
    }

    private static class NoFormatImpl
    implements Formatter {
        public static final NoFormatImpl INSTANCE = new NoFormatImpl();

        private NoFormatImpl() {
        }

        @Override
        public String format(String source) {
            return source;
        }
    }
}

