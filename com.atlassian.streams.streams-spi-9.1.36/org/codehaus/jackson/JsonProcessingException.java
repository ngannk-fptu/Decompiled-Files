/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

import java.io.IOException;
import org.codehaus.jackson.JsonLocation;

public class JsonProcessingException
extends IOException {
    static final long serialVersionUID = 123L;
    protected JsonLocation mLocation;

    protected JsonProcessingException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg);
        if (rootCause != null) {
            this.initCause(rootCause);
        }
        this.mLocation = loc;
    }

    protected JsonProcessingException(String msg) {
        super(msg);
    }

    protected JsonProcessingException(String msg, JsonLocation loc) {
        this(msg, loc, null);
    }

    protected JsonProcessingException(String msg, Throwable rootCause) {
        this(msg, null, rootCause);
    }

    protected JsonProcessingException(Throwable rootCause) {
        this(null, null, rootCause);
    }

    public JsonLocation getLocation() {
        return this.mLocation;
    }

    public String getMessage() {
        JsonLocation loc;
        String msg = super.getMessage();
        if (msg == null) {
            msg = "N/A";
        }
        if ((loc = this.getLocation()) != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(msg);
            sb.append('\n');
            sb.append(" at ");
            sb.append(loc.toString());
            return sb.toString();
        }
        return msg;
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.getMessage();
    }
}

