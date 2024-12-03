/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

import javax.json.JsonException;

public class JsonGenerationException
extends JsonException {
    public JsonGenerationException(String message) {
        super(message);
    }

    public JsonGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

