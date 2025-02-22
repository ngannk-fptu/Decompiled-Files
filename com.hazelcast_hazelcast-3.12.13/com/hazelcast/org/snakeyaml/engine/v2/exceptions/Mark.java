/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.exceptions;

import com.hazelcast.org.snakeyaml.engine.v2.common.CharConstants;
import java.io.Serializable;

public final class Mark
implements Serializable {
    private String name;
    private int index;
    private int line;
    private int column;
    private int[] buffer;
    private int pointer;

    private static int[] toCodePoints(char[] str) {
        int[] codePoints = new int[Character.codePointCount(str, 0, str.length)];
        int i = 0;
        int c = 0;
        while (i < str.length) {
            int cp;
            codePoints[c] = cp = Character.codePointAt(str, i);
            i += Character.charCount(cp);
            ++c;
        }
        return codePoints;
    }

    public Mark(String name, int index, int line, int column, int[] buffer, int pointer) {
        this.name = name;
        this.index = index;
        this.line = line;
        this.column = column;
        this.buffer = buffer;
        this.pointer = pointer;
    }

    public Mark(String name, int index, int line, int column, char[] str, int pointer) {
        this(name, index, line, column, Mark.toCodePoints(str), pointer);
    }

    private boolean isLineBreak(int c) {
        return CharConstants.NULL_OR_LINEBR.has(c);
    }

    public String createSnippet(int indent, int maxLength) {
        int i;
        float half = (float)maxLength / 2.0f - 1.0f;
        int start = this.pointer;
        String head = "";
        while (start > 0 && !this.isLineBreak(this.buffer[start - 1])) {
            if (!((float)(this.pointer - --start) > half)) continue;
            head = " ... ";
            start += 5;
            break;
        }
        String tail = "";
        int end = this.pointer;
        while (end < this.buffer.length && !this.isLineBreak(this.buffer[end])) {
            if (!((float)(++end - this.pointer) > half)) continue;
            tail = " ... ";
            end -= 5;
            break;
        }
        StringBuilder result = new StringBuilder();
        for (i = 0; i < indent; ++i) {
            result.append(" ");
        }
        result.append(head);
        for (i = start; i < end; ++i) {
            result.appendCodePoint(this.buffer[i]);
        }
        result.append(tail);
        result.append("\n");
        for (i = 0; i < indent + this.pointer - start + head.length(); ++i) {
            result.append(" ");
        }
        result.append("^");
        return result.toString();
    }

    public String createSnippet() {
        return this.createSnippet(4, 75);
    }

    public String toString() {
        String snippet = this.createSnippet();
        StringBuilder builder = new StringBuilder(" in ");
        builder.append(this.name);
        builder.append(", line ");
        builder.append(this.line + 1);
        builder.append(", column ");
        builder.append(this.column + 1);
        builder.append(":\n");
        builder.append(snippet);
        return builder.toString();
    }

    public String getName() {
        return this.name;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public int getIndex() {
        return this.index;
    }

    public int[] getBuffer() {
        return this.buffer;
    }

    public int getPointer() {
        return this.pointer;
    }
}

