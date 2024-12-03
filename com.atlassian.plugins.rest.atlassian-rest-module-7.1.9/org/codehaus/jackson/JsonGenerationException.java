/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonProcessingException;

public class JsonGenerationException
extends JsonProcessingException {
    static final long serialVersionUID = 123L;

    public JsonGenerationException(Throwable rootCause) {
        super(rootCause);
    }

    public JsonGenerationException(String msg) {
        super(msg, (JsonLocation)null);
    }

    public JsonGenerationException(String msg, Throwable rootCause) {
        super(msg, null, rootCause);
    }
}

