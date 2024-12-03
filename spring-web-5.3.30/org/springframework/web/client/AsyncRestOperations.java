/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.lang.Nullable
 *  org.springframework.util.concurrent.ListenableFuture
 */
package org.springframework.web.client;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Deprecated
public interface AsyncRestOperations {
    public RestOperations getRestOperations();

    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String var1, Class<T> var2, Object ... var3) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String var1, Class<T> var2, Map<String, ?> var3) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(URI var1, Class<T> var2) throws RestClientException;

    public ListenableFuture<HttpHeaders> headForHeaders(String var1, Object ... var2) throws RestClientException;

    public ListenableFuture<HttpHeaders> headForHeaders(String var1, Map<String, ?> var2) throws RestClientException;

    public ListenableFuture<HttpHeaders> headForHeaders(URI var1) throws RestClientException;

    public ListenableFuture<URI> postForLocation(String var1, @Nullable HttpEntity<?> var2, Object ... var3) throws RestClientException;

    public ListenableFuture<URI> postForLocation(String var1, @Nullable HttpEntity<?> var2, Map<String, ?> var3) throws RestClientException;

    public ListenableFuture<URI> postForLocation(URI var1, @Nullable HttpEntity<?> var2) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String var1, @Nullable HttpEntity<?> var2, Class<T> var3, Object ... var4) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String var1, @Nullable HttpEntity<?> var2, Class<T> var3, Map<String, ?> var4) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(URI var1, @Nullable HttpEntity<?> var2, Class<T> var3) throws RestClientException;

    public ListenableFuture<?> put(String var1, @Nullable HttpEntity<?> var2, Object ... var3) throws RestClientException;

    public ListenableFuture<?> put(String var1, @Nullable HttpEntity<?> var2, Map<String, ?> var3) throws RestClientException;

    public ListenableFuture<?> put(URI var1, @Nullable HttpEntity<?> var2) throws RestClientException;

    public ListenableFuture<?> delete(String var1, Object ... var2) throws RestClientException;

    public ListenableFuture<?> delete(String var1, Map<String, ?> var2) throws RestClientException;

    public ListenableFuture<?> delete(URI var1) throws RestClientException;

    public ListenableFuture<Set<HttpMethod>> optionsForAllow(String var1, Object ... var2) throws RestClientException;

    public ListenableFuture<Set<HttpMethod>> optionsForAllow(String var1, Map<String, ?> var2) throws RestClientException;

    public ListenableFuture<Set<HttpMethod>> optionsForAllow(URI var1) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, Class<T> var4, Object ... var5) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, Class<T> var4, Map<String, ?> var5) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> exchange(URI var1, HttpMethod var2, @Nullable HttpEntity<?> var3, Class<T> var4) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, ParameterizedTypeReference<T> var4, Object ... var5) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, ParameterizedTypeReference<T> var4, Map<String, ?> var5) throws RestClientException;

    public <T> ListenableFuture<ResponseEntity<T>> exchange(URI var1, HttpMethod var2, @Nullable HttpEntity<?> var3, ParameterizedTypeReference<T> var4) throws RestClientException;

    public <T> ListenableFuture<T> execute(String var1, HttpMethod var2, @Nullable AsyncRequestCallback var3, @Nullable ResponseExtractor<T> var4, Object ... var5) throws RestClientException;

    public <T> ListenableFuture<T> execute(String var1, HttpMethod var2, @Nullable AsyncRequestCallback var3, @Nullable ResponseExtractor<T> var4, Map<String, ?> var5) throws RestClientException;

    public <T> ListenableFuture<T> execute(URI var1, HttpMethod var2, @Nullable AsyncRequestCallback var3, @Nullable ResponseExtractor<T> var4) throws RestClientException;
}

