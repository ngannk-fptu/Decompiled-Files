/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import java.util.ArrayDeque;
import org.springframework.lang.Nullable;

public final class ParseState {
    private final ArrayDeque<Entry> state;

    public ParseState() {
        this.state = new ArrayDeque();
    }

    private ParseState(ParseState other) {
        this.state = other.state.clone();
    }

    public void push(Entry entry) {
        this.state.push(entry);
    }

    public void pop() {
        this.state.pop();
    }

    @Nullable
    public Entry peek() {
        return this.state.peek();
    }

    public ParseState snapshot() {
        return new ParseState(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        int i = 0;
        for (Entry entry : this.state) {
            if (i > 0) {
                sb.append('\n');
                for (int j = 0; j < i; ++j) {
                    sb.append('\t');
                }
                sb.append("-> ");
            }
            sb.append(entry);
            ++i;
        }
        return sb.toString();
    }

    public static interface Entry {
    }
}

