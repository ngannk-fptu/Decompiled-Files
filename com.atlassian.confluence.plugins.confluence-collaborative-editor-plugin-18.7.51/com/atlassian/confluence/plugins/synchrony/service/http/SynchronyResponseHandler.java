/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableMap
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.entity.ContentType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import com.atlassian.confluence.plugins.synchrony.events.SynchronyExternalChangesErrorEvent;
import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronyResponseHandler
implements ResponseHandler<Either<SynchronyError, JSONObject>> {
    private static final Logger log = LoggerFactory.getLogger(SynchronyResponseHandler.class);
    private final long id;
    private final JSONObject data;
    private final EventPublisher eventPublisher;

    public SynchronyResponseHandler(long id, JSONObject data, @ComponentImport EventPublisher eventPublisher) {
        this.id = id;
        this.data = data;
        this.eventPublisher = eventPublisher;
    }

    public Either<SynchronyError, JSONObject> handleResponse(HttpResponse response) throws IOException {
        JSONObject result = this.getResult(response);
        int status = response.getStatusLine().getStatusCode();
        if (status == 200 && result.containsKey("rev")) {
            return Either.right((Object)result);
        }
        return Either.left((Object)this.failed(status, result));
    }

    private JSONObject getResult(HttpResponse response) throws IOException {
        String charset = this.getResponseContentType(response).getCharset().name();
        JSONObject result = (JSONObject)JSONValue.parse(new InputStreamReader(response.getEntity().getContent(), charset));
        if (result == null) {
            result = new JSONObject((Map<String, ?>)ImmutableMap.of((Object)"message", (Object)"No result returned"));
        }
        return result;
    }

    private ContentType getResponseContentType(HttpResponse response) {
        ContentType resContentType = ContentType.get((HttpEntity)response.getEntity());
        if (resContentType == null) {
            resContentType = ContentType.APPLICATION_JSON;
        }
        return resContentType;
    }

    private SynchronyError failed(int status, JSONObject result) {
        this.eventPublisher.publish((Object)new SynchronyExternalChangesErrorEvent(status, result.get("message").toString(), this.id, (String)this.data.get("ancestor")));
        log.warn(this.getMessage(result, status));
        SynchronyError.Code synchronyCode = SynchronyError.Code.from((String)result.get("type"));
        return new SynchronyError(synchronyCode, (String)result.get("conflicting-rev"));
    }

    private String getMessage(JSONObject result, int status) {
        JSONObject meta;
        JSONObject master;
        JSONObject filteredMerges = new JSONObject();
        JSONObject merges = (JSONObject)this.data.get("merges");
        if (merges != null && (master = (JSONObject)merges.get("master")) != null && (meta = (JSONObject)master.get("meta")) != null) {
            filteredMerges.put("type", meta.get("type"));
            filteredMerges.put("trigger", meta.get("trigger"));
            filteredMerges.put("confVersion", meta.get("confVersion"));
        }
        return "Synchrony external changes API call returned " + status + ": " + result.toJSONString() + " content-id: " + this.id + " rev: " + this.data.get("rev") + " ancestor: " + this.data.get("ancestor") + " merges: " + filteredMerges + " generate-rev: " + this.data.get("generate-rev") + " generate-reset: " + this.data.get("generate-reset");
    }
}

