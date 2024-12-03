/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpOutputMessage;

public interface StreamingHttpOutputMessage
extends HttpOutputMessage {
    public void setBody(Body var1);

    @FunctionalInterface
    public static interface Body {
        public void writeTo(OutputStream var1) throws IOException;
    }
}

