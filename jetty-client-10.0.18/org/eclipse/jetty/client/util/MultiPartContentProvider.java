/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.RuntimeIOException
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.NanoTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.client.AsyncContentProvider;
import org.eclipse.jetty.client.Synchronizable;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.util.AbstractTypedContentProvider;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class MultiPartContentProvider
extends AbstractTypedContentProvider
implements AsyncContentProvider,
Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(MultiPartContentProvider.class);
    private static final byte[] COLON_SPACE_BYTES = new byte[]{58, 32};
    private static final byte[] CR_LF_BYTES = new byte[]{13, 10};
    private final List<Part> parts = new ArrayList<Part>();
    private final ByteBuffer firstBoundary;
    private final ByteBuffer middleBoundary;
    private final ByteBuffer onlyBoundary;
    private final ByteBuffer lastBoundary;
    private final AtomicBoolean closed = new AtomicBoolean();
    private AsyncContentProvider.Listener listener;
    private long length = -1L;

    public MultiPartContentProvider() {
        this(MultiPartContentProvider.makeBoundary());
    }

    public MultiPartContentProvider(String boundary) {
        super("multipart/form-data; boundary=" + boundary);
        String firstBoundaryLine = "--" + boundary + "\r\n";
        this.firstBoundary = ByteBuffer.wrap(firstBoundaryLine.getBytes(StandardCharsets.US_ASCII));
        String middleBoundaryLine = "\r\n" + firstBoundaryLine;
        this.middleBoundary = ByteBuffer.wrap(middleBoundaryLine.getBytes(StandardCharsets.US_ASCII));
        String onlyBoundaryLine = "--" + boundary + "--\r\n";
        this.onlyBoundary = ByteBuffer.wrap(onlyBoundaryLine.getBytes(StandardCharsets.US_ASCII));
        String lastBoundaryLine = "\r\n" + onlyBoundaryLine;
        this.lastBoundary = ByteBuffer.wrap(lastBoundaryLine.getBytes(StandardCharsets.US_ASCII));
    }

    private static String makeBoundary() {
        StringBuilder builder = new StringBuilder("JettyHttpClientBoundary");
        builder.append(Long.toString(System.identityHashCode(builder), 36));
        builder.append(Long.toString(System.identityHashCode(Thread.currentThread()), 36));
        builder.append(Long.toString(NanoTime.now(), 36));
        return builder.toString();
    }

    public void addFieldPart(String name, ContentProvider content, HttpFields fields) {
        this.addPart(new Part(name, null, "text/plain", content, fields));
    }

    public void addFilePart(String name, String fileName, ContentProvider content, HttpFields fields) {
        this.addPart(new Part(name, fileName, "application/octet-stream", content, fields));
    }

    private void addPart(Part part) {
        this.parts.add(part);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added {}", (Object)part);
        }
    }

    @Override
    public void setListener(AsyncContentProvider.Listener listener) {
        this.listener = listener;
        if (this.closed.get()) {
            this.length = this.calculateLength();
        }
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

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return new MultiPartIterator();
    }

    @Override
    public void close() {
        this.closed.compareAndSet(false, true);
    }

    private static class Part {
        private final String name;
        private final String fileName;
        private final String contentType;
        private final ContentProvider content;
        private final HttpFields fields;
        private final ByteBuffer headers;
        private final long length;

        private Part(String name, String fileName, String contentType, ContentProvider content, HttpFields fields) {
            this.name = name;
            this.fileName = fileName;
            this.contentType = contentType;
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
                    contentType = this.content instanceof ContentProvider.Typed ? ((ContentProvider.Typed)this.content).getContentType() : this.contentType;
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

    private class MultiPartIterator
    implements Iterator<ByteBuffer>,
    Synchronizable,
    Callback,
    Closeable {
        private Iterator<ByteBuffer> iterator;
        private int index;
        private State state = State.FIRST_BOUNDARY;

        private MultiPartIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.state != State.COMPLETE;
        }

        @Override
        public ByteBuffer next() {
            block8: while (true) {
                switch (this.state) {
                    case FIRST_BOUNDARY: {
                        if (MultiPartContentProvider.this.parts.isEmpty()) {
                            this.state = State.COMPLETE;
                            return MultiPartContentProvider.this.onlyBoundary.slice();
                        }
                        this.state = State.HEADERS;
                        return MultiPartContentProvider.this.firstBoundary.slice();
                    }
                    case HEADERS: {
                        Part part = MultiPartContentProvider.this.parts.get(this.index);
                        ContentProvider content = part.content;
                        if (content instanceof AsyncContentProvider) {
                            ((AsyncContentProvider)content).setListener(MultiPartContentProvider.this.listener);
                        }
                        this.iterator = content.iterator();
                        this.state = State.CONTENT;
                        return part.headers.slice();
                    }
                    case CONTENT: {
                        if (this.iterator.hasNext()) {
                            return this.iterator.next();
                        }
                        ++this.index;
                        if (this.index < MultiPartContentProvider.this.parts.size()) {
                            this.state = State.MIDDLE_BOUNDARY;
                            if (!(this.iterator instanceof Closeable)) continue block8;
                            IO.close((Closeable)((Closeable)((Object)this.iterator)));
                            continue block8;
                        }
                        this.state = State.LAST_BOUNDARY;
                        continue block8;
                    }
                    case MIDDLE_BOUNDARY: {
                        this.state = State.HEADERS;
                        return MultiPartContentProvider.this.middleBoundary.slice();
                    }
                    case LAST_BOUNDARY: {
                        this.state = State.COMPLETE;
                        return MultiPartContentProvider.this.lastBoundary.slice();
                    }
                    case COMPLETE: {
                        throw new NoSuchElementException();
                    }
                }
                break;
            }
            throw new IllegalStateException(this.state.toString());
        }

        @Override
        public Object getLock() {
            if (this.iterator instanceof Synchronizable) {
                return ((Synchronizable)((Object)this.iterator)).getLock();
            }
            return this;
        }

        public void succeeded() {
            if (this.state == State.CONTENT && this.iterator instanceof Callback) {
                ((Callback)this.iterator).succeeded();
            }
        }

        public void failed(Throwable x) {
            if (this.state == State.CONTENT && this.iterator instanceof Callback) {
                ((Callback)this.iterator).failed(x);
            }
        }

        @Override
        public void close() throws IOException {
            if (this.iterator instanceof Closeable) {
                ((Closeable)((Object)this.iterator)).close();
            }
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

