/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpHeader
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpRedirector;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;

public class RedirectProtocolHandler
extends Response.Listener.Adapter
implements ProtocolHandler {
    public static final String NAME = "redirect";
    private final HttpRedirector redirector;

    public RedirectProtocolHandler(HttpClient client) {
        this.redirector = new HttpRedirector(client);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean accept(Request request, Response response) {
        return this.redirector.isRedirect(response) && request.isFollowRedirects();
    }

    @Override
    public Response.Listener getResponseListener() {
        return this;
    }

    @Override
    public boolean onHeader(Response response, HttpField field) {
        return field.getHeader() != HttpHeader.CONTENT_ENCODING;
    }

    @Override
    public void onComplete(Result result) {
        Request request = result.getRequest();
        Response response = result.getResponse();
        if (result.isSucceeded()) {
            this.redirector.redirect(request, response, null);
        } else {
            this.redirector.fail(request, response, result.getFailure());
        }
    }
}

