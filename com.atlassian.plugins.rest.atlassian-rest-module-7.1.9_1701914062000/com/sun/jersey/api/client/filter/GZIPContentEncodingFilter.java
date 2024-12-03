/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPContentEncodingFilter
extends ClientFilter {
    private final boolean compressRequestEntity;

    public GZIPContentEncodingFilter() {
        this(true);
    }

    public GZIPContentEncodingFilter(boolean compressRequestEntity) {
        this.compressRequestEntity = compressRequestEntity;
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        String encodings;
        ClientResponse response;
        if (!request.getHeaders().containsKey("Accept-Encoding")) {
            request.getHeaders().add("Accept-Encoding", "gzip");
        }
        if (request.getEntity() != null) {
            Object o = request.getHeaders().getFirst("Content-Encoding");
            if (o != null && o.equals("gzip")) {
                request.setAdapter(new Adapter(request.getAdapter()));
            } else if (this.compressRequestEntity) {
                request.getHeaders().add("Content-Encoding", "gzip");
                request.setAdapter(new Adapter(request.getAdapter()));
            }
        }
        if ((response = this.getNext().handle(request)).hasEntity() && response.getHeaders().containsKey("Content-Encoding") && (encodings = response.getHeaders().getFirst("Content-Encoding")).equals("gzip")) {
            response.getHeaders().remove("Content-Encoding");
            InputStream entityStream = response.getEntityInputStream();
            try {
                response.setEntityInputStream(new GZIPInputStream(entityStream));
            }
            catch (IOException ex) {
                if (entityStream != null) {
                    try {
                        entityStream.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
                throw new ClientHandlerException(ex);
            }
        }
        return response;
    }

    private static final class Adapter
    extends AbstractClientRequestAdapter {
        Adapter(ClientRequestAdapter cra) {
            super(cra);
        }

        @Override
        public OutputStream adapt(ClientRequest request, OutputStream out) throws IOException {
            return new GZIPOutputStream(this.getAdapter().adapt(request, out));
        }
    }
}

