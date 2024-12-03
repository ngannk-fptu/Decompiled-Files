/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

@Deprecated
public class BasicAuthorizationInterceptor
implements ClientHttpRequestInterceptor {
    private final String username;
    private final String password;

    public BasicAuthorizationInterceptor(@Nullable String username, @Nullable String password) {
        Assert.doesNotContain(username, ":", "Username must not contain a colon");
        this.username = username != null ? username : "";
        this.password = password != null ? password : "";
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body2, ClientHttpRequestExecution execution) throws IOException {
        String token = Base64Utils.encodeToString((this.username + ":" + this.password).getBytes(StandardCharsets.UTF_8));
        request.getHeaders().add("Authorization", "Basic " + token);
        return execution.execute(request, body2);
    }
}

