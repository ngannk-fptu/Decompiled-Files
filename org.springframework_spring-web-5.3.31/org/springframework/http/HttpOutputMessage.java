/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpMessage;

public interface HttpOutputMessage
extends HttpMessage {
    public OutputStream getBody() throws IOException;
}

