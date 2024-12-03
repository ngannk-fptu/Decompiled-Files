/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core;

import software.amazon.awssdk.thirdparty.jackson.core.JsonLocation;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParser;
import software.amazon.awssdk.thirdparty.jackson.core.exc.StreamReadException;
import software.amazon.awssdk.thirdparty.jackson.core.util.RequestPayload;

public class JsonParseException
extends StreamReadException {
    private static final long serialVersionUID = 2L;

    @Deprecated
    public JsonParseException(String msg, JsonLocation loc) {
        super(msg, loc, null);
    }

    @Deprecated
    public JsonParseException(String msg, JsonLocation loc, Throwable root) {
        super(msg, loc, root);
    }

    public JsonParseException(JsonParser p, String msg) {
        super(p, msg);
    }

    public JsonParseException(JsonParser p, String msg, Throwable root) {
        super(p, msg, root);
    }

    public JsonParseException(JsonParser p, String msg, JsonLocation loc) {
        super(p, msg, loc);
    }

    public JsonParseException(JsonParser p, String msg, JsonLocation loc, Throwable root) {
        super(p, msg, loc, root);
    }

    public JsonParseException(String msg) {
        super(msg);
    }

    @Override
    public JsonParseException withParser(JsonParser p) {
        this._processor = p;
        return this;
    }

    @Override
    public JsonParseException withRequestPayload(RequestPayload payload) {
        this._requestPayload = payload;
        return this;
    }

    @Override
    public JsonParser getProcessor() {
        return super.getProcessor();
    }

    @Override
    public RequestPayload getRequestPayload() {
        return super.getRequestPayload();
    }

    @Override
    public String getRequestPayloadAsString() {
        return super.getRequestPayloadAsString();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

