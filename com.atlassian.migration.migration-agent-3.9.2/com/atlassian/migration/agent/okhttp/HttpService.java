/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Response
 *  okhttp3.ResponseBody
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.okhttp.AbstractContainerDeserialiser;
import com.atlassian.migration.agent.okhttp.ContainerV1Deserialiser;
import com.atlassian.migration.agent.okhttp.DefaultHttpServiceErrorHandler;
import com.atlassian.migration.agent.okhttp.HttpServiceErrorHandler;
import com.atlassian.migration.agent.okhttp.IOHttpException;
import com.atlassian.migration.agent.okhttp.MigrationDomainsAllowListResponseDeserialiser;
import com.atlassian.migration.agent.okhttp.ResponseParsingHttpException;
import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import com.atlassian.migration.agent.service.catalogue.model.MigrationDomainsAllowlistResponse;
import com.atlassian.migration.app.ContainerV1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpService {
    private static final Logger log = LoggerFactory.getLogger(HttpService.class);
    private static final HttpServiceErrorHandler DEFAULT_ERROR_HANDLER = new DefaultHttpServiceErrorHandler();
    private static final long ONE_KB_BYTE_COUNT = 1024L;
    private final Supplier<OkHttpClient> clientSupplier;
    private final HttpServiceErrorHandler errorHandler;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(ContainerV1.class, (Object)new ContainerV1Deserialiser()).registerTypeAdapter(AbstractContainer.class, (Object)new AbstractContainerDeserialiser()).registerTypeAdapter(MigrationDomainsAllowlistResponse.Entry.class, (Object)new MigrationDomainsAllowListResponseDeserialiser()).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    public HttpService(Supplier<OkHttpClient> httpClientSupplier) {
        this.clientSupplier = httpClientSupplier;
        this.errorHandler = DEFAULT_ERROR_HANDLER;
    }

    public HttpService(Supplier<OkHttpClient> httpClientSupplier, HttpServiceErrorHandler errorHandler) {
        this.clientSupplier = httpClientSupplier;
        this.errorHandler = errorHandler;
    }

    public <T> T callJson(Request request, TypeReference<T> bodyType) {
        return (T)this.call(request, (ResponseBody body) -> {
            try {
                return Jsons.OBJECT_MAPPER.readValue(body.charStream(), bodyType);
            }
            catch (IOException e) {
                throw new ResponseParsingHttpException(String.format("Failed to deserialize response body to %s", bodyType.getType().getTypeName()), e);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T callGson(Request request, Class<T> bodyType) {
        try (Response response = null;){
            response = this.callImpl(request, Collections.emptySet());
            Object object = this.gson.fromJson(response.body().charStream(), bodyType);
            return (T)object;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T callGsonWithExpectedErrorCodes(Request request, Class<T> bodyType, Set<Integer> expectedErrorCodes) {
        try (Response response = null;){
            response = this.callImpl(request, expectedErrorCodes);
            Object object = this.gson.fromJson(response.body().charStream(), bodyType);
            return (T)object;
        }
    }

    public Response call(Request request) {
        Response response = this.callImpl(request, Collections.emptySet());
        response.close();
        return response;
    }

    public void call(Request request, Set<Integer> expectedErrorCodes) {
        this.callImpl(request, expectedErrorCodes).close();
    }

    public Response callStream(Request request) {
        return this.callImpl(request, Collections.emptySet());
    }

    public Response callStream(Request request, Set<Integer> expectedErrorCodes) {
        return this.callImpl(request, expectedErrorCodes);
    }

    public <T> T call(Request request, Function<ResponseBody, T> bodyMapper) {
        Response response = this.callImpl(request, Collections.emptySet());
        ResponseBody body = response.body();
        if (body == null) {
            throw new ResponseParsingHttpException("No body in response");
        }
        String rawResponseBody = this.getRawResponseBody(response);
        try {
            T t = bodyMapper.apply(body);
            return t;
        }
        catch (RuntimeException e) {
            throw new ResponseParsingHttpException("Failed to parse response body, responseBody: " + rawResponseBody + ", headers: " + response.headers(), e);
        }
        finally {
            body.close();
        }
    }

    private String getRawResponseBody(Response response) {
        String rawResponseBody = null;
        try {
            rawResponseBody = response.peekBody(1024L).string();
        }
        catch (IOException e) {
            log.error("Failed to fetch raw response body", (Throwable)e);
        }
        return rawResponseBody;
    }

    private Response callImpl(Request request, Set<Integer> expectedErrorCodes) {
        Response response;
        try {
            response = this.clientSupplier.get().newCall(request).execute();
        }
        catch (IOException | UnsupportedOperationException e) {
            throw new IOHttpException("An IO exception occurred when communicating with a downstream service", e);
        }
        if (!expectedErrorCodes.contains(response.code()) && !response.isSuccessful()) {
            log.warn("Request failed with code " + response.code());
            this.errorHandler.accept(response);
        }
        return response;
    }
}

