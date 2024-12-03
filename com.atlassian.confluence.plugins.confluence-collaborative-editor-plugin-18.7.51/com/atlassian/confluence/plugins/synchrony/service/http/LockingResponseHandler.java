/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ResponseHandler
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.codehaus.jackson.map.ObjectMapper;

public class LockingResponseHandler
implements ResponseHandler<Optional<SynchronyError.Code>> {
    public Optional<SynchronyError.Code> handleResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
            return Optional.empty();
        }
        Map body = this.getBody(response);
        return Optional.of(SynchronyError.Code.from((String)body.get("type")));
    }

    private Map getBody(HttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return (Map)objectMapper.readValue(response.getEntity().getContent(), Map.class);
    }
}

