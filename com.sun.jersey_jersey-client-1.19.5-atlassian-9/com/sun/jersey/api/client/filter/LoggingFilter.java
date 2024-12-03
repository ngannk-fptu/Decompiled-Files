/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;

public class LoggingFilter
extends ClientFilter {
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";
    private final PrintStream loggingStream;
    private final Logger logger;
    private final int maxEntitySize;
    private long _id = 0L;

    public LoggingFilter() {
        this(LOGGER);
    }

    public LoggingFilter(Logger logger) {
        this(logger, null);
    }

    public LoggingFilter(PrintStream loggingStream) {
        this(null, loggingStream);
    }

    public LoggingFilter(Logger logger, int maxEntitySize) {
        this(logger, null, maxEntitySize);
    }

    public LoggingFilter(PrintStream loggingStream, int maxEntitySize) {
        this(null, loggingStream, maxEntitySize);
    }

    private LoggingFilter(Logger logger, PrintStream loggingStream) {
        this(logger, loggingStream, 10240);
    }

    private LoggingFilter(Logger logger, PrintStream loggingStream, int maxEntitySize) {
        this.loggingStream = loggingStream;
        this.logger = logger;
        this.maxEntitySize = maxEntitySize;
    }

    private void log(StringBuilder b) {
        if (this.logger != null) {
            this.logger.info(b.toString());
        } else {
            this.loggingStream.print(b);
        }
    }

    private StringBuilder prefixId(StringBuilder b, long id) {
        b.append(Long.toString(id)).append(" ");
        return b;
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        long id = ++this._id;
        this.logRequest(id, request);
        ClientResponse response = this.getNext().handle(request);
        this.logResponse(id, response);
        return response;
    }

    private void logRequest(long id, ClientRequest request) {
        StringBuilder b = new StringBuilder();
        this.printRequestLine(b, id, request);
        this.printRequestHeaders(b, id, request.getHeaders());
        if (request.getEntity() != null) {
            request.setAdapter(new Adapter(request.getAdapter(), b));
        } else {
            this.log(b);
        }
    }

    private void printRequestLine(StringBuilder b, long id, ClientRequest request) {
        this.prefixId(b, id).append(NOTIFICATION_PREFIX).append("Client out-bound request").append("\n");
        this.prefixId(b, id).append(REQUEST_PREFIX).append(request.getMethod()).append(" ").append(request.getURI().toASCIIString()).append("\n");
    }

    private void printRequestHeaders(StringBuilder b, long id, MultivaluedMap<String, Object> headers) {
        for (Map.Entry e : headers.entrySet()) {
            List val = (List)e.getValue();
            String header = (String)e.getKey();
            if (val.size() == 1) {
                this.prefixId(b, id).append(REQUEST_PREFIX).append(header).append(": ").append(ClientRequest.getHeaderValue(val.get(0))).append("\n");
                continue;
            }
            StringBuilder sb = new StringBuilder();
            boolean add = false;
            for (Object o : val) {
                if (add) {
                    sb.append(',');
                }
                add = true;
                sb.append(ClientRequest.getHeaderValue(o));
            }
            this.prefixId(b, id).append(REQUEST_PREFIX).append(header).append(": ").append(sb.toString()).append("\n");
        }
    }

    private void logResponse(long id, ClientResponse response) {
        StringBuilder b = new StringBuilder();
        this.printResponseLine(b, id, response);
        this.printResponseHeaders(b, id, response.getHeaders());
        InputStream stream = response.getEntityInputStream();
        try {
            if (!response.getEntityInputStream().markSupported()) {
                stream = new BufferedInputStream(stream);
                response.setEntityInputStream(stream);
            }
            stream.mark(this.maxEntitySize + 1);
            byte[] entity = new byte[this.maxEntitySize + 1];
            int entitySize = stream.read(entity);
            if (entitySize > 0) {
                b.append(new String(entity, 0, Math.min(entitySize, this.maxEntitySize)));
                if (entitySize > this.maxEntitySize) {
                    b.append("...more...");
                }
                b.append('\n');
                stream.reset();
            }
        }
        catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
        this.log(b);
    }

    private void printResponseLine(StringBuilder b, long id, ClientResponse response) {
        this.prefixId(b, id).append(NOTIFICATION_PREFIX).append("Client in-bound response").append("\n");
        this.prefixId(b, id).append(RESPONSE_PREFIX).append(Integer.toString(response.getStatus())).append("\n");
    }

    private void printResponseHeaders(StringBuilder b, long id, MultivaluedMap<String, String> headers) {
        for (Map.Entry e : headers.entrySet()) {
            String header = (String)e.getKey();
            for (String value : (List)e.getValue()) {
                this.prefixId(b, id).append(RESPONSE_PREFIX).append(header).append(": ").append(value).append("\n");
            }
        }
        this.prefixId(b, id).append(RESPONSE_PREFIX).append("\n");
    }

    private void printEntity(StringBuilder b, byte[] entity) throws IOException {
        if (entity.length == 0) {
            return;
        }
        b.append(new String(entity)).append("\n");
    }

    private final class LoggingOutputStream
    extends OutputStream {
        private final OutputStream out;
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final StringBuilder b;

        LoggingOutputStream(OutputStream out, StringBuilder b) {
            this.out = out;
            this.b = b;
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.baos.write(b);
            this.out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.baos.write(b, off, len);
            this.out.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            this.baos.write(b);
            this.out.write(b);
        }

        @Override
        public void close() throws IOException {
            LoggingFilter.this.printEntity(this.b, this.baos.toByteArray());
            LoggingFilter.this.log(this.b);
            this.out.close();
        }
    }

    private final class Adapter
    extends AbstractClientRequestAdapter {
        private final StringBuilder b;

        Adapter(ClientRequestAdapter cra, StringBuilder b) {
            super(cra);
            this.b = b;
        }

        @Override
        public OutputStream adapt(ClientRequest request, OutputStream out) throws IOException {
            return new LoggingOutputStream(this.getAdapter().adapt(request, out), this.b);
        }
    }
}

