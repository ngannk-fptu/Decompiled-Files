/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonProcessingException;

public class JsonParseException
extends JsonProcessingException {
    static final long serialVersionUID = 123L;

    public JsonParseException(String msg, JsonLocation loc) {
        super(msg, loc);
    }

    public JsonParseException(String msg, JsonLocation loc, Throwable root) {
        super(msg, loc, root);
    }
}

