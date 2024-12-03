/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.TextBuffer;

public final class ReadConstrainedTextBuffer
extends TextBuffer {
    private final StreamReadConstraints _streamReadConstraints;

    public ReadConstrainedTextBuffer(StreamReadConstraints streamReadConstraints, BufferRecycler bufferRecycler) {
        super(bufferRecycler);
        this._streamReadConstraints = streamReadConstraints;
    }

    @Override
    protected void validateStringLength(int length) throws StreamConstraintsException {
        this._streamReadConstraints.validateStringLength(length);
    }
}

