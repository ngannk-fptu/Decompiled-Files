/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.sun.jersey.api.client.ClientHandlerException
 *  com.sun.jersey.api.client.ClientRequest
 *  com.sun.jersey.api.client.ClientResponse
 *  com.sun.jersey.api.client.filter.ClientFilter
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.rest.util;

import com.google.common.base.Charsets;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JerseyLoggingFilter
extends ClientFilter {
    private static final Logger log = LoggerFactory.getLogger(JerseyLoggingFilter.class);

    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        ClientResponse response = this.getNext().handle(cr);
        this.logResponse(cr, response);
        return response;
    }

    private void logResponse(ClientRequest cr, ClientResponse response) {
        if (log.isDebugEnabled()) {
            InputStream inputStream = response.getEntityInputStream();
            try {
                byte[] entity = IOUtils.toByteArray((InputStream)inputStream);
                String entityAsString = new String(entity, 0, entity.length, Charsets.UTF_8);
                log.debug("Request URI {}, Response body: {}", (Object)cr.getURI(), (Object)entityAsString);
                response.setEntityInputStream((InputStream)new ByteArrayInputStream(entity));
            }
            catch (IOException e) {
                throw new ClientHandlerException((Throwable)e);
            }
            finally {
                IOUtils.closeQuietly((InputStream)inputStream);
            }
        }
    }
}

