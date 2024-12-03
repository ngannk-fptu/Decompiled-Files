/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.LocalContext;

final class LocalContextStack {
    private LocalContext[] buffer = new LocalContext[8];
    private int size;

    LocalContextStack() {
    }

    void push(LocalContext localContext) {
        int newSize;
        LocalContext[] buffer = this.buffer;
        if (buffer.length < (newSize = ++this.size)) {
            LocalContext[] newBuffer = new LocalContext[newSize * 2];
            for (int i = 0; i < buffer.length; ++i) {
                newBuffer[i] = buffer[i];
            }
            buffer = newBuffer;
            this.buffer = newBuffer;
        }
        buffer[newSize - 1] = localContext;
    }

    void pop() {
        this.buffer[--this.size] = null;
    }

    public LocalContext get(int index) {
        return this.buffer[index];
    }

    public int size() {
        return this.size;
    }
}

