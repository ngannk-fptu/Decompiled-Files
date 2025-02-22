/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.scanner;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

final class SimpleKey {
    private int tokenNumber;
    private boolean required;
    private int index;
    private int line;
    private int column;
    private Optional<Mark> mark;

    public SimpleKey(int tokenNumber, boolean required, int index, int line, int column, Optional<Mark> mark) {
        this.tokenNumber = tokenNumber;
        this.required = required;
        this.index = index;
        this.line = line;
        this.column = column;
        this.mark = mark;
    }

    public int getTokenNumber() {
        return this.tokenNumber;
    }

    public int getColumn() {
        return this.column;
    }

    public Optional<Mark> getMark() {
        return this.mark;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLine() {
        return this.line;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String toString() {
        return "SimpleKey - tokenNumber=" + this.tokenNumber + " required=" + this.required + " index=" + this.index + " line=" + this.line + " column=" + this.column;
    }
}

