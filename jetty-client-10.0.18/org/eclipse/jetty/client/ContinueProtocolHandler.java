/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpHeaderValue
 */
package org.eclipse.jetty.client;

import java.util.List;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpRequestException;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;

public class ContinueProtocolHandler
implements ProtocolHandler {
    public static final String NAME = "continue";
    private static final String ATTRIBUTE = ContinueProtocolHandler.class.getName() + ".100continue";
    private final ResponseNotifier notifier = new ResponseNotifier();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean accept(Request request, Response response) {
        boolean is100 = response.getStatus() == 100;
        boolean expect100 = request.getHeaders().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
        boolean handled100 = request.getAttributes().containsKey(ATTRIBUTE);
        return (is100 || expect100) && !handled100;
    }

    @Override
    public Response.Listener getResponseListener() {
        return new ContinueListener();
    }

    protected void onContinue(Request request) {
    }

    protected class ContinueListener
    extends BufferingResponseListener {
        protected ContinueListener() {
        }

        @Override
        public void onSuccess(Response response) {
            Request request = response.getRequest();
            HttpConversation conversation = ((HttpRequest)request).getConversation();
            request.attribute(ATTRIBUTE, Boolean.TRUE);
            conversation.updateResponseListeners(null);
            HttpExchange exchange = conversation.getExchanges().peekLast();
            if (response.getStatus() == 100) {
                exchange.resetResponse();
                exchange.proceed(null);
                ContinueProtocolHandler.this.onContinue(request);
            } else {
                List<Response.ResponseListener> listeners = exchange.getResponseListeners();
                HttpContentResponse contentResponse = new HttpContentResponse(response, this.getContent(), this.getMediaType(), this.getEncoding());
                ContinueProtocolHandler.this.notifier.forwardSuccess(listeners, contentResponse);
                exchange.proceed(new HttpRequestException("Expectation failed", request));
            }
        }

        @Override
        public void onFailure(Response response, Throwable failure) {
            HttpConversation conversation = ((HttpRequest)response.getRequest()).getConversation();
            conversation.setAttribute(ATTRIBUTE, Boolean.TRUE);
            conversation.updateResponseListeners(null);
            HttpExchange exchange = conversation.getExchanges().peekLast();
            assert (exchange.getResponse() == response);
            List<Response.ResponseListener> listeners = exchange.getResponseListeners();
            HttpContentResponse contentResponse = new HttpContentResponse(response, this.getContent(), this.getMediaType(), this.getEncoding());
            ContinueProtocolHandler.this.notifier.forwardFailureComplete(listeners, exchange.getRequest(), exchange.getRequestFailure(), contentResponse, failure);
        }

        @Override
        public void onComplete(Result result) {
        }
    }
}

