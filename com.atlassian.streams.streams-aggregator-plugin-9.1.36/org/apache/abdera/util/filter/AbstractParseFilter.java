/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util.filter;

import org.apache.abdera.filter.ParseFilter;

public abstract class AbstractParseFilter
implements ParseFilter {
    private static final long serialVersionUID = -1866308276050148524L;
    private static final byte COMMENTS = 1;
    private static final byte WHITESPACE = 2;
    private static final byte PI = 4;
    protected byte flags = 0;

    private void toggle(boolean s, byte flag) {
        this.flags = s ? (byte)(this.flags | flag) : (byte)(this.flags & ~flag);
    }

    private boolean check(byte flag) {
        return (this.flags & flag) == flag;
    }

    public ParseFilter setIgnoreComments(boolean ignore) {
        this.toggle(ignore, (byte)1);
        return this;
    }

    public ParseFilter setIgnoreWhitespace(boolean ignore) {
        this.toggle(ignore, (byte)2);
        return this;
    }

    public ParseFilter setIgnoreProcessingInstructions(boolean ignore) {
        this.toggle(ignore, (byte)4);
        return this;
    }

    public boolean getIgnoreComments() {
        return this.check((byte)1);
    }

    public boolean getIgnoreProcessingInstructions() {
        return this.check((byte)4);
    }

    public boolean getIgnoreWhitespace() {
        return this.check((byte)2);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

