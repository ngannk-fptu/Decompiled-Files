/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.util;

import software.amazon.awssdk.thirdparty.jackson.core.StreamReadConstraints;
import software.amazon.awssdk.thirdparty.jackson.core.exc.StreamConstraintsException;
import software.amazon.awssdk.thirdparty.jackson.core.util.BufferRecycler;
import software.amazon.awssdk.thirdparty.jackson.core.util.TextBuffer;

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

