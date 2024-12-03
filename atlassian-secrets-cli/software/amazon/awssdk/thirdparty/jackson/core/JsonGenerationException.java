/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core;

import software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator;
import software.amazon.awssdk.thirdparty.jackson.core.exc.StreamWriteException;

public class JsonGenerationException
extends StreamWriteException {
    private static final long serialVersionUID = 123L;

    @Deprecated
    public JsonGenerationException(Throwable rootCause) {
        super(rootCause, null);
    }

    @Deprecated
    public JsonGenerationException(String msg) {
        super(msg, (JsonGenerator)null);
    }

    @Deprecated
    public JsonGenerationException(String msg, Throwable rootCause) {
        super(msg, rootCause, null);
    }

    public JsonGenerationException(Throwable rootCause, JsonGenerator g) {
        super(rootCause, g);
    }

    public JsonGenerationException(String msg, JsonGenerator g) {
        super(msg, g);
        this._processor = g;
    }

    public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g) {
        super(msg, rootCause, g);
        this._processor = g;
    }

    @Override
    public JsonGenerationException withGenerator(JsonGenerator g) {
        this._processor = g;
        return this;
    }

    @Override
    public JsonGenerator getProcessor() {
        return this._processor;
    }
}

