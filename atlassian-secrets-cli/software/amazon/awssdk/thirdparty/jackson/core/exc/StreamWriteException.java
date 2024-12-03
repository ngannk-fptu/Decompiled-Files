/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.exc;

import software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator;
import software.amazon.awssdk.thirdparty.jackson.core.JsonLocation;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;

public abstract class StreamWriteException
extends JsonProcessingException {
    private static final long serialVersionUID = 2L;
    protected transient JsonGenerator _processor;

    protected StreamWriteException(Throwable rootCause, JsonGenerator g) {
        super(rootCause);
        this._processor = g;
    }

    protected StreamWriteException(String msg, JsonGenerator g) {
        super(msg, (JsonLocation)null);
        this._processor = g;
    }

    protected StreamWriteException(String msg, Throwable rootCause, JsonGenerator g) {
        super(msg, null, rootCause);
        this._processor = g;
    }

    public abstract StreamWriteException withGenerator(JsonGenerator var1);

    @Override
    public JsonGenerator getProcessor() {
        return this._processor;
    }
}

