/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.http.HttpStatus
 *  org.eclipse.jetty.http.QuotedCSV
 *  org.eclipse.jetty.util.Attributes
 *  org.eclipse.jetty.util.NanoTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AuthenticationProtocolHandler
implements ProtocolHandler {
    public static final int DEFAULT_MAX_CONTENT_LENGTH = 16384;
    public static final Logger LOG = LoggerFactory.getLogger(AuthenticationProtocolHandler.class);
    private final HttpClient client;
    private final int maxContentLength;
    private final ResponseNotifier notifier;
    private static final Pattern CHALLENGE_PATTERN = Pattern.compile("(?<schemeOnly>[!#$%&'*+\\-.^_`|~0-9A-Za-z]+)|(?:(?<scheme>[!#$%&'*+\\-.^_`|~0-9A-Za-z]+)\\s+)?(?:(?<token68>[a-zA-Z0-9\\-._~+/]+=*)|(?<paramName>[!#$%&'*+\\-.^_`|~0-9A-Za-z]+)\\s*=\\s*(?:(?<paramValue>.*)))");

    protected AuthenticationProtocolHandler(HttpClient client, int maxContentLength) {
        this.client = client;
        this.maxContentLength = maxContentLength;
        this.notifier = new ResponseNotifier();
    }

    protected HttpClient getHttpClient() {
        return this.client;
    }

    protected abstract HttpHeader getAuthenticateHeader();

    protected abstract HttpHeader getAuthorizationHeader();

    protected abstract URI getAuthenticationURI(Request var1);

    protected abstract String getAuthenticationAttribute();

    @Override
    public Response.Listener getResponseListener() {
        return new AuthenticationListener();
    }

    protected List<Authentication.HeaderInfo> getHeaderInfo(String header) throws IllegalArgumentException {
        ArrayList<Authentication.HeaderInfo> headerInfos = new ArrayList<Authentication.HeaderInfo>();
        for (String value : new QuotedCSV(true, new String[]{header})) {
            Matcher m = CHALLENGE_PATTERN.matcher(value);
            if (!m.matches()) continue;
            if (m.group("schemeOnly") != null) {
                headerInfos.add(new Authentication.HeaderInfo(this.getAuthorizationHeader(), m.group(1), new HashMap<String, String>()));
                continue;
            }
            if (m.group("scheme") != null) {
                headerInfos.add(new Authentication.HeaderInfo(this.getAuthorizationHeader(), m.group("scheme"), new HashMap<String, String>()));
            }
            if (headerInfos.isEmpty()) {
                throw new IllegalArgumentException("Parameters without auth-scheme");
            }
            Map<String, String> authParams = ((Authentication.HeaderInfo)headerInfos.get(headerInfos.size() - 1)).getParameters();
            if (m.group("paramName") != null) {
                String paramVal = QuotedCSV.unquote((String)m.group("paramValue"));
                authParams.put(m.group("paramName"), paramVal);
                continue;
            }
            if (m.group("token68") == null) continue;
            if (!authParams.isEmpty()) {
                throw new IllegalArgumentException("token68 after auth-params");
            }
            authParams.put("base64", m.group("token68"));
        }
        return headerInfos;
    }

    private class AuthenticationListener
    extends BufferingResponseListener {
        private AuthenticationListener() {
            super(AuthenticationProtocolHandler.this.maxContentLength);
        }

        @Override
        public void onComplete(Result result) {
            HttpRequest request = (HttpRequest)result.getRequest();
            HttpContentResponse response = new HttpContentResponse(result.getResponse(), this.getContent(), this.getMediaType(), this.getEncoding());
            if (result.getResponseFailure() != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authentication challenge failed", result.getFailure());
                }
                this.forwardFailureComplete(request, result.getRequestFailure(), response, result.getResponseFailure());
                return;
            }
            String authenticationAttribute = AuthenticationProtocolHandler.this.getAuthenticationAttribute();
            HttpConversation conversation = request.getConversation();
            if (conversation.getAttribute(authenticationAttribute) != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Bad credentials for {}", (Object)request);
                }
                this.forwardSuccessComplete(request, response);
                return;
            }
            HttpHeader header = AuthenticationProtocolHandler.this.getAuthenticateHeader();
            List<Authentication.HeaderInfo> headerInfos = this.parseAuthenticateHeader(response, header);
            if (headerInfos.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authentication challenge without {} header", (Object)header);
                }
                this.forwardFailureComplete(request, result.getRequestFailure(), response, new HttpResponseException("HTTP protocol violation: Authentication challenge without " + header + " header", response));
                return;
            }
            Authentication authentication = null;
            Authentication.HeaderInfo headerInfo = null;
            URI authURI = this.resolveURI(request, AuthenticationProtocolHandler.this.getAuthenticationURI(request));
            if (authURI != null) {
                for (Authentication.HeaderInfo element : headerInfos) {
                    authentication = AuthenticationProtocolHandler.this.client.getAuthenticationStore().findAuthentication(element.getType(), authURI, element.getRealm());
                    if (authentication == null) continue;
                    headerInfo = element;
                    break;
                }
            }
            if (authentication == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No authentication available for {}", (Object)request);
                }
                this.forwardSuccessComplete(request, response);
                return;
            }
            Request.Content requestContent = request.getBody();
            if (!requestContent.isReproducible()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request content not reproducible for {}", (Object)request);
                }
                this.forwardSuccessComplete(request, response);
                return;
            }
            try {
                long timeoutNanoTime;
                Authentication.Result authnResult = authentication.authenticate(request, response, headerInfo, (Attributes)conversation);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authentication result {}", (Object)authnResult);
                }
                if (authnResult == null) {
                    this.forwardSuccessComplete(request, response);
                    return;
                }
                conversation.setAttribute(authenticationAttribute, true);
                Request newRequest = AuthenticationProtocolHandler.this.client.copyRequest(request, request.getURI());
                if (HttpMethod.CONNECT.is(newRequest.getMethod())) {
                    newRequest.path(request.getPath());
                }
                if ((timeoutNanoTime = request.getTimeoutNanoTime()) < Long.MAX_VALUE) {
                    long newTimeout = NanoTime.until((long)timeoutNanoTime);
                    if (newTimeout > 0L) {
                        newRequest.timeout(newTimeout, TimeUnit.NANOSECONDS);
                    } else {
                        TimeoutException failure = new TimeoutException("Total timeout " + request.getConversation().getTimeout() + " ms elapsed");
                        this.forwardFailureComplete(request, failure, response, failure);
                        return;
                    }
                }
                authnResult.apply(newRequest);
                this.copyIfAbsent(request, newRequest, HttpHeader.AUTHORIZATION);
                this.copyIfAbsent(request, newRequest, HttpHeader.PROXY_AUTHORIZATION);
                AfterAuthenticationListener listener = new AfterAuthenticationListener(authnResult);
                Connection connection = (Connection)request.getAttributes().get(Connection.class.getName());
                if (connection != null) {
                    connection.send(newRequest, listener);
                } else {
                    newRequest.send(listener);
                }
            }
            catch (Throwable x) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authentication failed", x);
                }
                this.forwardFailureComplete(request, null, response, x);
            }
        }

        private URI resolveURI(HttpRequest request, URI uri) {
            if (uri != null) {
                return uri;
            }
            String target = request.getScheme() + "://" + request.getHost();
            int port = request.getPort();
            if (port > 0) {
                target = target + ":" + port;
            }
            return URI.create(target);
        }

        private void copyIfAbsent(HttpRequest oldRequest, Request newRequest, HttpHeader header) {
            HttpField field = oldRequest.getHeaders().getField(header);
            if (field != null && !newRequest.getHeaders().contains(header)) {
                newRequest.headers(headers -> headers.put(field));
            }
        }

        private void forwardSuccessComplete(HttpRequest request, Response response) {
            HttpConversation conversation = request.getConversation();
            conversation.updateResponseListeners(null);
            AuthenticationProtocolHandler.this.notifier.forwardSuccessComplete(conversation.getResponseListeners(), request, response);
        }

        private void forwardFailureComplete(HttpRequest request, Throwable requestFailure, Response response, Throwable responseFailure) {
            HttpConversation conversation = request.getConversation();
            conversation.updateResponseListeners(null);
            List<Response.ResponseListener> responseListeners = conversation.getResponseListeners();
            if (responseFailure == null) {
                AuthenticationProtocolHandler.this.notifier.forwardSuccess(responseListeners, response);
            } else {
                AuthenticationProtocolHandler.this.notifier.forwardFailure(responseListeners, response, responseFailure);
            }
            AuthenticationProtocolHandler.this.notifier.notifyComplete(responseListeners, new Result(request, requestFailure, response, responseFailure));
        }

        private List<Authentication.HeaderInfo> parseAuthenticateHeader(Response response, HttpHeader header) {
            ArrayList<Authentication.HeaderInfo> result = new ArrayList<Authentication.HeaderInfo>();
            List values = response.getHeaders().getValuesList(header);
            for (String value : values) {
                try {
                    result.addAll(AuthenticationProtocolHandler.this.getHeaderInfo(value));
                }
                catch (IllegalArgumentException e) {
                    if (!LOG.isDebugEnabled()) continue;
                    LOG.debug("Failed to parse authentication header", (Throwable)e);
                }
            }
            return result;
        }
    }

    private class AfterAuthenticationListener
    extends Response.Listener.Adapter {
        private final Authentication.Result authenticationResult;

        private AfterAuthenticationListener(Authentication.Result authenticationResult) {
            this.authenticationResult = authenticationResult;
        }

        @Override
        public void onSuccess(Response response) {
            int status = response.getStatus();
            if (HttpStatus.isSuccess((int)status) || HttpStatus.isRedirection((int)status)) {
                AuthenticationProtocolHandler.this.client.getAuthenticationStore().addAuthenticationResult(this.authenticationResult);
            }
        }
    }
}

