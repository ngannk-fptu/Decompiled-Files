/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.util.Base64
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.Base64;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public final class HTTPBasicAuthFilter
extends ClientFilter {
    private final String authentication;
    private static final Charset CHARACTER_SET = Charset.forName("iso-8859-1");

    public HTTPBasicAuthFilter(String username, byte[] password) {
        try {
            byte[] prefix = (username + ":").getBytes(CHARACTER_SET);
            byte[] usernamePassword = new byte[prefix.length + password.length];
            System.arraycopy(prefix, 0, usernamePassword, 0, prefix.length);
            System.arraycopy(password, 0, usernamePassword, prefix.length, password.length);
            this.authentication = "Basic " + new String(Base64.encode((byte[])usernamePassword), "ASCII");
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public HTTPBasicAuthFilter(String username, String password) {
        this(username, password.getBytes(CHARACTER_SET));
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (!cr.getHeaders().containsKey((Object)"Authorization")) {
            cr.getHeaders().add((Object)"Authorization", (Object)this.authentication);
        }
        return this.getNext().handle(cr);
    }
}

