/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class TailerListenerAdapter
implements TailerListener {
    public void endOfFileReached() {
    }

    @Override
    public void fileNotFound() {
    }

    @Override
    public void fileRotated() {
    }

    @Override
    public void handle(Exception ex) {
    }

    @Override
    public void handle(String line) {
    }

    @Override
    public void init(Tailer tailer) {
    }
}

