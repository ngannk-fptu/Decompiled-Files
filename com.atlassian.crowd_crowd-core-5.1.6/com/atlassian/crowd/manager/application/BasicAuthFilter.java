/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.api.client.ClientHandlerException
 *  com.sun.jersey.api.client.ClientRequest
 *  com.sun.jersey.api.client.ClientResponse
 *  com.sun.jersey.api.client.filter.ClientFilter
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.crowd.manager.application;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;

public class BasicAuthFilter
extends ClientFilter {
    private final String authentication;

    public BasicAuthFilter(String username, String password) {
        String token = username + ":" + password;
        String encodedToken = null;
        try {
            byte[] rawToken = token.getBytes("UTF-8");
            encodedToken = new String(Base64.encodeBase64((byte[])rawToken), "UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        this.authentication = "Basic " + encodedToken;
    }

    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (!cr.getMetadata().containsKey((Object)"Authorization")) {
            cr.getMetadata().add((Object)"Authorization", (Object)this.authentication);
        }
        return this.getNext().handle(cr);
    }
}

