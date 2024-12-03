/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

import javax.json.JsonException;
import javax.json.stream.JsonLocation;

public class JsonParsingException
extends JsonException {
    private final JsonLocation location;

    public JsonParsingException(String message, JsonLocation location) {
        super(message);
        this.location = location;
    }

    public JsonParsingException(String message, Throwable cause, JsonLocation location) {
        super(message, cause);
        this.location = location;
    }

    public JsonLocation getLocation() {
        return this.location;
    }
}

