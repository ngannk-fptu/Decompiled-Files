/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.exc;

import software.amazon.awssdk.thirdparty.jackson.core.JsonLocation;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;

public class StreamConstraintsException
extends JsonProcessingException {
    private static final long serialVersionUID = 2L;

    public StreamConstraintsException(String msg) {
        super(msg);
    }

    public StreamConstraintsException(String msg, JsonLocation loc) {
        super(msg, loc);
    }
}

