/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.util.NanoTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRedirector {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRedirector.class);
    private static final String SCHEME_REGEXP = "(^https?)";
    private static final String AUTHORITY_REGEXP = "([^/?#]+)";
    private static final String DESTINATION_REGEXP = "((^https?)://([^/?#]+))?";
    private static final String PATH_REGEXP = "([^?#]*)";
    private static final String QUERY_REGEXP = "([^#]*)";
    private static final String FRAGMENT_REGEXP = "(.*)";
    private static final Pattern URI_PATTERN = Pattern.compile("((^https?)://([^/?#]+))?([^?#]*)([^#]*)(.*)");
    private static final String ATTRIBUTE = HttpRedirector.class.getName() + ".redirects";
    private final HttpClient client;
    private final ResponseNotifier notifier;

    public HttpRedirector(HttpClient client) {
        this.client = client;
        this.notifier = new ResponseNotifier();
    }

    public boolean isRedirect(Response response) {
        switch (response.getStatus()) {
            case 301: 
            case 302: 
            case 303: 
            case 307: 
            case 308: {
                return true;
            }
        }
        return false;
    }

    public Result redirect(Request request, Response response) throws InterruptedException, ExecutionException {
        final AtomicReference resultRef = new AtomicReference();
        final CountDownLatch latch = new CountDownLatch(1);
        Request redirect = this.redirect(request, response, new BufferingResponseListener(){

            @Override
            public void onComplete(Result result) {
                resultRef.set(new Result(result.getRequest(), result.getRequestFailure(), new HttpContentResponse(result.getResponse(), this.getContent(), this.getMediaType(), this.getEncoding()), result.getResponseFailure()));
                latch.countDown();
            }
        });
        try {
            latch.await();
            Result result = (Result)resultRef.get();
            if (result.isFailed()) {
                throw new ExecutionException(result.getFailure());
            }
            return result;
        }
        catch (InterruptedException x) {
            redirect.abort(x);
            throw x;
        }
    }

    public Request redirect(Request request, Response response, Response.CompleteListener listener) {
        if (this.isRedirect(response)) {
            String location = response.getHeaders().get("Location");
            URI newURI = this.extractRedirectURI(response);
            if (newURI != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Redirecting to {} (Location: {})", (Object)newURI, (Object)location);
                }
                return this.redirect(request, response, listener, newURI);
            }
            this.fail(request, response, new HttpResponseException("Invalid 'Location' header: " + location, response));
            return null;
        }
        this.fail(request, response, new HttpResponseException("Cannot redirect: " + response, response));
        return null;
    }

    private Request redirect(Request request, Response response, Response.CompleteListener listener, URI newURI) {
        if (!newURI.isAbsolute()) {
            URI requestURI = request.getURI();
            if (requestURI == null) {
                String uri = request.getScheme() + "://" + request.getHost();
                int port = request.getPort();
                if (port > 0) {
                    uri = uri + ":" + port;
                }
                requestURI = URI.create(uri);
            }
            newURI = requestURI.resolve(newURI);
        }
        int status = response.getStatus();
        switch (status) {
            case 301: {
                String method = request.getMethod();
                if (HttpMethod.GET.is(method) || HttpMethod.HEAD.is(method) || HttpMethod.PUT.is(method)) {
                    return this.redirect(request, response, listener, newURI, method);
                }
                if (HttpMethod.POST.is(method)) {
                    return this.redirect(request, response, listener, newURI, HttpMethod.GET.asString());
                }
                this.fail(request, response, new HttpResponseException("HTTP protocol violation: received 301 for non GET/HEAD/POST/PUT request", response));
                return null;
            }
            case 302: {
                String method = request.getMethod();
                if (HttpMethod.HEAD.is(method) || HttpMethod.PUT.is(method)) {
                    return this.redirect(request, response, listener, newURI, method);
                }
                return this.redirect(request, response, listener, newURI, HttpMethod.GET.asString());
            }
            case 303: {
                String method = request.getMethod();
                if (HttpMethod.HEAD.is(method)) {
                    return this.redirect(request, response, listener, newURI, method);
                }
                return this.redirect(request, response, listener, newURI, HttpMethod.GET.asString());
            }
            case 307: 
            case 308: {
                return this.redirect(request, response, listener, newURI, request.getMethod());
            }
        }
        this.fail(request, response, new HttpResponseException("Unhandled HTTP status code " + status, response));
        return null;
    }

    private Request redirect(Request request, Response response, Response.CompleteListener listener, URI location, String method) {
        int maxRedirects;
        HttpRequest httpRequest = (HttpRequest)request;
        HttpConversation conversation = httpRequest.getConversation();
        Integer redirects = (Integer)conversation.getAttribute(ATTRIBUTE);
        if (redirects == null) {
            redirects = 0;
        }
        if ((maxRedirects = this.client.getMaxRedirects()) < 0 || redirects < maxRedirects) {
            redirects = redirects + 1;
            conversation.setAttribute(ATTRIBUTE, redirects);
            return this.sendRedirect(httpRequest, response, listener, location, method);
        }
        this.fail(request, response, new HttpResponseException("Max redirects exceeded " + redirects, response));
        return null;
    }

    public URI extractRedirectURI(Response response) {
        String location = response.getHeaders().get("location");
        if (location != null) {
            return this.sanitize(location);
        }
        return null;
    }

    private URI sanitize(String location) {
        try {
            return new URI(location);
        }
        catch (URISyntaxException x) {
            Matcher matcher = URI_PATTERN.matcher(location);
            if (matcher.matches()) {
                String fragment;
                String scheme = matcher.group(2);
                String authority = matcher.group(3);
                String path = matcher.group(4);
                String query = matcher.group(5);
                if (query.length() == 0) {
                    query = null;
                }
                if ((fragment = matcher.group(6)).length() == 0) {
                    fragment = null;
                }
                try {
                    return new URI(scheme, authority, path, query, fragment);
                }
                catch (URISyntaxException uRISyntaxException) {
                    // empty catch block
                }
            }
            return null;
        }
    }

    private Request sendRedirect(HttpRequest httpRequest, Response response, Response.CompleteListener listener, URI location, String method) {
        try {
            Request redirect = this.client.copyRequest(httpRequest, location);
            redirect.method(method);
            if (HttpMethod.GET.is(method)) {
                redirect.body(null);
                redirect.headers(headers -> {
                    headers.remove(HttpHeader.CONTENT_LENGTH);
                    headers.remove(HttpHeader.CONTENT_TYPE);
                });
            } else if (HttpMethod.CONNECT.is(method)) {
                redirect.path(httpRequest.getPath());
            }
            Request.Content body = redirect.getBody();
            if (body != null && !body.isReproducible()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Could not redirect to {}, request body is not reproducible", (Object)location);
                }
                HttpConversation conversation = httpRequest.getConversation();
                conversation.updateResponseListeners(null);
                this.notifier.forwardSuccessComplete(conversation.getResponseListeners(), httpRequest, response);
                return null;
            }
            long timeoutNanoTime = httpRequest.getTimeoutNanoTime();
            if (timeoutNanoTime < Long.MAX_VALUE) {
                long newTimeout = NanoTime.until((long)timeoutNanoTime);
                if (newTimeout > 0L) {
                    redirect.timeout(newTimeout, TimeUnit.NANOSECONDS);
                } else {
                    TimeoutException failure = new TimeoutException("Total timeout " + httpRequest.getConversation().getTimeout() + " ms elapsed");
                    this.fail((Request)httpRequest, failure, response);
                    return null;
                }
            }
            redirect.onRequestBegin(request -> {
                Throwable cause = httpRequest.getAbortCause();
                if (cause != null) {
                    request.abort(cause);
                }
            });
            redirect.send(listener);
            return redirect;
        }
        catch (Throwable x) {
            this.fail((Request)httpRequest, x, response);
            return null;
        }
    }

    protected void fail(Request request, Response response, Throwable failure) {
        this.fail(request, null, response, failure);
    }

    protected void fail(Request request, Throwable failure, Response response) {
        this.fail(request, failure, response, failure);
    }

    private void fail(Request request, Throwable requestFailure, Response response, Throwable responseFailure) {
        HttpConversation conversation = ((HttpRequest)request).getConversation();
        conversation.updateResponseListeners(null);
        List<Response.ResponseListener> listeners = conversation.getResponseListeners();
        this.notifier.notifyFailure(listeners, response, responseFailure);
        this.notifier.notifyComplete(listeners, new Result(request, requestFailure, response, responseFailure));
    }
}

