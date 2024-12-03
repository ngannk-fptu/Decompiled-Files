/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.plugins.rest.module.json;

import com.atlassian.plugins.rest.common.MediaTypes;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.lang3.Validate;

class JsonWithPaddingResponseAdapter
implements ContainerResponseWriter {
    private final String callbackFunction;
    private final ContainerResponseWriter containerResponseWriter;
    private OutputStream out;

    public JsonWithPaddingResponseAdapter(String callbackFunction, ContainerResponseWriter containerResponseWriter) {
        Validate.notNull((Object)callbackFunction);
        Validate.notNull((Object)containerResponseWriter);
        this.callbackFunction = callbackFunction;
        this.containerResponseWriter = containerResponseWriter;
    }

    @Override
    public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse response) throws IOException {
        response.getHttpHeaders().putSingle("Content-Type", MediaTypes.APPLICATION_JAVASCRIPT_TYPE);
        this.out = this.containerResponseWriter.writeStatusAndHeaders(-1L, response);
        this.out.write((this.callbackFunction + "(").getBytes());
        return this.out;
    }

    @Override
    public void finish() throws IOException {
        this.out.write(");".getBytes());
    }
}

