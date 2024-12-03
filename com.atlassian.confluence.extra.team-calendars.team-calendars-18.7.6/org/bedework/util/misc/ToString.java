/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.misc;

import java.util.Collection;

public class ToString {
    private StringBuilder sb;
    private String indent = "";
    private String delim = "";
    private int lastNewLinePos = 0;
    private boolean valuesOnly;
    private static final int maxLen = 80;
    private static final String indentVal = "  ";

    public ToString(Object o) {
        this.sb = new StringBuilder(o.getClass().getSimpleName()).append("{");
    }

    private ToString() {
    }

    public static ToString valuesOnly() {
        ToString ts = new ToString();
        ts.valuesOnly = true;
        ts.sb = new StringBuilder();
        return ts;
    }

    public ToString(Object o, String indent) {
        this.sb = new StringBuilder(indent);
        this.sb.append(o.getClass().getSimpleName()).append("{");
        this.indent = indent;
    }

    public StringBuilder getSb() {
        return this.sb;
    }

    public ToString delimit() {
        this.sb.append(this.delim);
        this.delim = ", ";
        if (this.lineLength() > 80) {
            this.outputNewLine();
        }
        return this;
    }

    public int lineLength() {
        return this.sb.length() - this.lastNewLinePos;
    }

    public ToString newLine() {
        this.sb.append(this.delim);
        this.delim = "";
        this.outputNewLine();
        return this;
    }

    public ToString append(String value) {
        this.delimit();
        this.sb.append(value);
        return this;
    }

    public ToString append(Object value) {
        this.delimit();
        this.sb.append(String.valueOf(value));
        return this;
    }

    public ToString append(String name, Iterable<?> val, boolean withNewLines) {
        this.nameEquals(name);
        this.sb.append("[");
        if (val == null) {
            this.sb.append("]");
            return this;
        }
        for (Object o : val) {
            if (withNewLines) {
                this.newLine();
            }
            this.append(o);
        }
        this.sb.append("]");
        return this;
    }

    public ToString append(String name, Object value) {
        this.nameEquals(name);
        this.sb.append(value);
        return this;
    }

    public ToString append(String name, Long value) {
        this.nameEquals(name);
        this.sb.append(value);
        return this;
    }

    public ToString append(String name, Collection value) {
        this.nameEquals(name);
        if (value == null) {
            this.sb.append("null");
            return this;
        }
        this.sb.append("[");
        String saveDelim = this.delim;
        this.delim = "";
        for (Object o : value) {
            this.delimit();
            this.sb.append(o);
        }
        this.sb.append("]");
        this.delim = saveDelim;
        return this;
    }

    public ToString append(String name, boolean value) {
        return this.append(name, String.valueOf(value));
    }

    public ToString append(String name, int value) {
        return this.append(name, String.valueOf(value));
    }

    public ToString append(Throwable t) {
        return this.append("Exception", t.getMessage());
    }

    public String toString() {
        if (!this.valuesOnly) {
            this.sb.append("}");
        }
        return this.sb.toString();
    }

    private void outputNewLine() {
        this.sb.append("\n");
        this.lastNewLinePos = this.sb.length();
        this.sb.append(this.indent);
        this.sb.append(indentVal);
    }

    private void nameEquals(String name) {
        this.delimit();
        this.sb.append(name);
        this.sb.append("=");
    }
}

