/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

public class LineWalker {
    private BufferedReader reader;
    private LinkedList<String> queue = new LinkedList();

    public LineWalker(String text) {
        this.reader = new BufferedReader(new StringReader(text));
    }

    public boolean hasNext() {
        if (this.queue.isEmpty()) {
            this.readNextLineToQueue();
        }
        return !this.queue.isEmpty();
    }

    public String peek() {
        if (!this.hasNext()) {
            throw new IllegalStateException("No more lines");
        }
        return this.queue.getFirst();
    }

    public String next() {
        if (!this.hasNext()) {
            throw new IllegalStateException("No more lines");
        }
        return this.queue.removeFirst();
    }

    private void readNextLineToQueue() {
        if (this.reader == null) {
            return;
        }
        try {
            String result = this.reader.readLine();
            if (result != null) {
                this.queue.add(result);
            } else {
                this.reader.close();
                this.reader = null;
            }
        }
        catch (IOException e) {
            throw new RuntimeException("IO Exception reading from string: " + e.getMessage(), e);
        }
    }

    public void pushBack(String line) {
        this.queue.addFirst(line);
    }
}

