/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpMessage;

public interface HttpInputMessage
extends HttpMessage {
    public InputStream getBody() throws IOException;
}

