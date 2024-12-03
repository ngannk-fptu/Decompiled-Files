/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

public interface RestOperations {
    @Nullable
    public <T> T getForObject(String var1, Class<T> var2, Object ... var3) throws RestClientException;

    @Nullable
    public <T> T getForObject(String var1, Class<T> var2, Map<String, ?> var3) throws RestClientException;

    @Nullable
    public <T> T getForObject(URI var1, Class<T> var2) throws RestClientException;

    public <T> ResponseEntity<T> getForEntity(String var1, Class<T> var2, Object ... var3) throws RestClientException;

    public <T> ResponseEntity<T> getForEntity(String var1, Class<T> var2, Map<String, ?> var3) throws RestClientException;

    public <T> ResponseEntity<T> getForEntity(URI var1, Class<T> var2) throws RestClientException;

    public HttpHeaders headForHeaders(String var1, Object ... var2) throws RestClientException;

    public HttpHeaders headForHeaders(String var1, Map<String, ?> var2) throws RestClientException;

    public HttpHeaders headForHeaders(URI var1) throws RestClientException;

    @Nullable
    public URI postForLocation(String var1, @Nullable Object var2, Object ... var3) throws RestClientException;

    @Nullable
    public URI postForLocation(String var1, @Nullable Object var2, Map<String, ?> var3) throws RestClientException;

    @Nullable
    public URI postForLocation(URI var1, @Nullable Object var2) throws RestClientException;

    @Nullable
    public <T> T postForObject(String var1, @Nullable Object var2, Class<T> var3, Object ... var4) throws RestClientException;

    @Nullable
    public <T> T postForObject(String var1, @Nullable Object var2, Class<T> var3, Map<String, ?> var4) throws RestClientException;

    @Nullable
    public <T> T postForObject(URI var1, @Nullable Object var2, Class<T> var3) throws RestClientException;

    public <T> ResponseEntity<T> postForEntity(String var1, @Nullable Object var2, Class<T> var3, Object ... var4) throws RestClientException;

    public <T> ResponseEntity<T> postForEntity(String var1, @Nullable Object var2, Class<T> var3, Map<String, ?> var4) throws RestClientException;

    public <T> ResponseEntity<T> postForEntity(URI var1, @Nullable Object var2, Class<T> var3) throws RestClientException;

    public void put(String var1, @Nullable Object var2, Object ... var3) throws RestClientException;

    public void put(String var1, @Nullable Object var2, Map<String, ?> var3) throws RestClientException;

    public void put(URI var1, @Nullable Object var2) throws RestClientException;

    @Nullable
    public <T> T patchForObject(String var1, @Nullable Object var2, Class<T> var3, Object ... var4) throws RestClientException;

    @Nullable
    public <T> T patchForObject(String var1, @Nullable Object var2, Class<T> var3, Map<String, ?> var4) throws RestClientException;

    @Nullable
    public <T> T patchForObject(URI var1, @Nullable Object var2, Class<T> var3) throws RestClientException;

    public void delete(String var1, Object ... var2) throws RestClientException;

    public void delete(String var1, Map<String, ?> var2) throws RestClientException;

    public void delete(URI var1) throws RestClientException;

    public Set<HttpMethod> optionsForAllow(String var1, Object ... var2) throws RestClientException;

    public Set<HttpMethod> optionsForAllow(String var1, Map<String, ?> var2) throws RestClientException;

    public Set<HttpMethod> optionsForAllow(URI var1) throws RestClientException;

    public <T> ResponseEntity<T> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, Class<T> var4, Object ... var5) throws RestClientException;

    public <T> ResponseEntity<T> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, Class<T> var4, Map<String, ?> var5) throws RestClientException;

    public <T> ResponseEntity<T> exchange(URI var1, HttpMethod var2, @Nullable HttpEntity<?> var3, Class<T> var4) throws RestClientException;

    public <T> ResponseEntity<T> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, ParameterizedTypeReference<T> var4, Object ... var5) throws RestClientException;

    public <T> ResponseEntity<T> exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, ParameterizedTypeReference<T> var4, Map<String, ?> var5) throws RestClientException;

    public <T> ResponseEntity<T> exchange(URI var1, HttpMethod var2, @Nullable HttpEntity<?> var3, ParameterizedTypeReference<T> var4) throws RestClientException;

    public <T> ResponseEntity<T> exchange(RequestEntity<?> var1, Class<T> var2) throws RestClientException;

    public <T> ResponseEntity<T> exchange(RequestEntity<?> var1, ParameterizedTypeReference<T> var2) throws RestClientException;

    @Nullable
    public <T> T execute(String var1, HttpMethod var2, @Nullable RequestCallback var3, @Nullable ResponseExtractor<T> var4, Object ... var5) throws RestClientException;

    @Nullable
    public <T> T execute(String var1, HttpMethod var2, @Nullable RequestCallback var3, @Nullable ResponseExtractor<T> var4, Map<String, ?> var5) throws RestClientException;

    @Nullable
    public <T> T execute(URI var1, HttpMethod var2, @Nullable RequestCallback var3, @Nullable ResponseExtractor<T> var4) throws RestClientException;
}

