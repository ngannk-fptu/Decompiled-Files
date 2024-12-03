/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public interface ClientHttpRequest
extends HttpRequest,
HttpOutputMessage {
    public ClientHttpResponse execute() throws IOException;
}

