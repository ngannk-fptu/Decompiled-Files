/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

public abstract class AbstractClientHttpResponse
implements ClientHttpResponse {
    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(this.getRawStatusCode());
    }
}

