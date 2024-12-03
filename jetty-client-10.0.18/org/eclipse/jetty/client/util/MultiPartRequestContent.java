/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.RuntimeIOException
 *  org.eclipse.jetty.util.Callback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.AbstractRequestContent;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiPartRequestContent
extends AbstractRequestContent
implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(MultiPartRequestContent.class);
    private static final byte[] COLON_SPACE_BYTES = new byte[]{58, 32};
    private static final byte[] CR_LF_BYTES = new byte[]{13, 10};
    private final List<Part> parts = new ArrayList<Part>();
    private final ByteBuffer firstBoundary;
    private final ByteBuffer middleBoundary;
    private final ByteBuffer onlyBoundary;
    private final ByteBuffer lastBoundary;
    private long length;
    private boolean closed;
    private Request.Content.Subscription subscription;

    private static String makeBoundary() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder("JettyHttpClientBoundary");
        int length = builder.length();
        while (builder.length() < length + 16) {
            long rnd = random.nextLong();
            builder.append(Long.toString(rnd < 0L ? -rnd : rnd, 36));
        }
        builder.setLength(length + 16);
        return builder.toString();
    }

    public MultiPartRequestContent() {
        this(MultiPartRequestContent.makeBoundary());
    }

    public MultiPartRequestContent(String boundary) {
        super("multipart/form-data; boundary=" + boundary);
        String firstBoundaryLine = "--" + boundary + "\r\n";
        this.firstBoundary = ByteBuffer.wrap(firstBoundaryLine.getBytes(StandardCharsets.US_ASCII));
        String middleBoundaryLine = "\r\n" + firstBoundaryLine;
        this.middleBoundary = ByteBuffer.wrap(middleBoundaryLine.getBytes(StandardCharsets.US_ASCII));
        String onlyBoundaryLine = "--" + boundary + "--\r\n";
        this.onlyBoundary = ByteBuffer.wrap(onlyBoundaryLine.getBytes(StandardCharsets.US_ASCII));
        String lastBoundaryLine = "\r\n" + onlyBoundaryLine;
        this.lastBoundary = ByteBuffer.wrap(lastBoundaryLine.getBytes(StandardCharsets.US_ASCII));
        this.length = -1L;
    }

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    protected Request.Content.Subscription newSubscription(Request.Content.Consumer consumer, boolean emitInitialContent) {
        if (!this.closed) {
            throw new IllegalStateException("MultiPartRequestContent must be closed before sending the request");
        }
        if (this.subscription != null) {
            throw new IllegalStateException("Multiple subscriptions not supported on " + this);
        }
        this.length = this.calculateLength();
        this.subscription = new SubscriptionImpl(consumer, emitInitialContent);
        return this.subscription;
    }

    @Override
    public void fail(Throwable failure) {
        this.parts.stream().map(part -> part.content).forEach(content -> content.fail(failure));
    }

    public void addFieldPart(String name, Request.Content content, HttpFields fields) {
        this.addPart(new Part(name, null, content, fields));
    }

    public void addFilePart(String name, String fileName, Request.Content content, HttpFields fields) {
        this.addPart(new Part(name, fileName, content, fields));
    }

    private void addPart(Part part) {
        this.parts.add(part);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added {}", (Object)part);
        }
    }

    @Override
    public void close() {
        this.closed = true;
    }

    private long calculateLength() {
        if (this.parts.isEmpty()) {
            return this.onlyBoundary.remaining();
        }
        long result = 0L;
        for (int i = 0; i < this.parts.size(); ++i) {
            result += i == 0 ? (long)this.firstBoundary.remaining() : (long)this.middleBoundary.remaining();
            Part part = this.parts.get(i);
            long partLength = part.length;
            result += partLength;
            if (partLength >= 0L) continue;
            result = -1L;
            break;
        }
        if (result > 0L) {
            result += (long)this.lastBoundary.remaining();
        }
        return result;
    }

    private class SubscriptionImpl
    extends AbstractRequestContent.AbstractSubscription
    implements Request.Content.Consumer {
        private State state;
        private int index;
        private Request.Content.Subscription subscription;

        private SubscriptionImpl(Request.Content.Consumer consumer, boolean emitInitialContent) {
            super(MultiPartRequestContent.this, consumer, emitInitialContent);
            this.state = State.FIRST_BOUNDARY;
        }

        @Override
        protected boolean produceContent(AbstractRequestContent.Producer producer) throws IOException {
            ByteBuffer buffer;
            boolean last = false;
            switch (this.state) {
                case FIRST_BOUNDARY: {
                    if (MultiPartRequestContent.this.parts.isEmpty()) {
                        this.state = State.COMPLETE;
                        buffer = MultiPartRequestContent.this.onlyBoundary.slice();
                        last = true;
                        break;
                    }
                    this.state = State.HEADERS;
                    buffer = MultiPartRequestContent.this.firstBoundary.slice();
                    break;
                }
                case HEADERS: {
                    Part part = MultiPartRequestContent.this.parts.get(this.index);
                    Request.Content content = part.content;
                    this.subscription = content.subscribe(this, true);
                    this.state = State.CONTENT;
                    buffer = part.headers.slice();
                    break;
                }
                case CONTENT: {
                    buffer = null;
                    this.subscription.demand();
                    break;
                }
                case MIDDLE_BOUNDARY: {
                    this.state = State.HEADERS;
                    buffer = MultiPartRequestContent.this.middleBoundary.slice();
                    break;
                }
                case LAST_BOUNDARY: {
                    this.state = State.COMPLETE;
                    buffer = MultiPartRequestContent.this.lastBoundary.slice();
                    last = true;
                    break;
                }
                case COMPLETE: {
                    throw new EOFException("Demand after last content");
                }
                default: {
                    throw new IllegalStateException("Invalid state " + this.state);
                }
            }
            return producer.produce(buffer, last, Callback.NOOP);
        }

        @Override
        public void onContent(ByteBuffer buffer, boolean last, Callback callback) {
            if (last) {
                ++this.index;
                this.state = this.index < MultiPartRequestContent.this.parts.size() ? State.MIDDLE_BOUNDARY : State.LAST_BOUNDARY;
            }
            this.notifyContent(buffer, false, callback);
        }

        @Override
        public void onFailure(Throwable failure) {
            if (this.subscription != null) {
                this.subscription.fail(failure);
            }
        }
    }

    private static class Part {
        private final String name;
        private final String fileName;
        private final Request.Content content;
        private final HttpFields fields;
        private final ByteBuffer headers;
        private final long length;

        private Part(String name, String fileName, Request.Content content, HttpFields fields) {
            this.name = name;
            this.fileName = fileName;
            this.content = content;
            this.fields = fields;
            this.headers = this.headers();
            this.length = content.getLength() < 0L ? -1L : (long)this.headers.remaining() + content.getLength();
        }

        private ByteBuffer headers() {
            try {
                Object contentType;
                String contentDisposition = "Content-Disposition: form-data; name=\"" + this.name + "\"";
                if (this.fileName != null) {
                    contentDisposition = contentDisposition + "; filename=\"" + this.fileName + "\"";
                }
                contentDisposition = contentDisposition + "\r\n";
                Object object = contentType = this.fields == null ? null : this.fields.get(HttpHeader.CONTENT_TYPE);
                if (contentType == null) {
                    contentType = this.content.getContentType();
                }
                contentType = "Content-Type: " + (String)contentType + "\r\n";
                if (this.fields == null || this.fields.size() == 0) {
                    String headers = contentDisposition;
                    headers = headers + (String)contentType;
                    headers = headers + "\r\n";
                    return ByteBuffer.wrap(headers.getBytes(StandardCharsets.UTF_8));
                }
                ByteArrayOutputStream buffer = new ByteArrayOutputStream((this.fields.size() + 1) * contentDisposition.length());
                buffer.write(contentDisposition.getBytes(StandardCharsets.UTF_8));
                buffer.write(((String)contentType).getBytes(StandardCharsets.UTF_8));
                for (HttpField field : this.fields) {
                    if (HttpHeader.CONTENT_TYPE.equals((Object)field.getHeader())) continue;
                    buffer.write(field.getName().getBytes(StandardCharsets.US_ASCII));
                    buffer.write(COLON_SPACE_BYTES);
                    String value = field.getValue();
                    if (value != null) {
                        buffer.write(value.getBytes(StandardCharsets.UTF_8));
                    }
                    buffer.write(CR_LF_BYTES);
                }
                buffer.write(CR_LF_BYTES);
                return ByteBuffer.wrap(buffer.toByteArray());
            }
            catch (IOException x) {
                throw new RuntimeIOException((Throwable)x);
            }
        }

        public String toString() {
            return String.format("%s@%x[name=%s,fileName=%s,length=%d,headers=%s]", this.getClass().getSimpleName(), this.hashCode(), this.name, this.fileName, this.content.getLength(), this.fields);
        }
    }

    private static enum State {
        FIRST_BOUNDARY,
        HEADERS,
        CONTENT,
        MIDDLE_BOUNDARY,
        LAST_BOUNDARY,
        COMPLETE;

    }
}

