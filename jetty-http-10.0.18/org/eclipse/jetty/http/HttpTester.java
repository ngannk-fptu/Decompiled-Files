/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.StringUtil
 */
package org.eclipse.jetty.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.StringUtil;

public class HttpTester {
    public static Input from(ByteBuffer data) {
        return new Input(data.slice()){

            @Override
            public int fillBuffer() throws IOException {
                this._eof = true;
                return -1;
            }
        };
    }

    public static Input from(final InputStream in) {
        return new Input(){

            @Override
            public int fillBuffer() throws IOException {
                BufferUtil.compact((ByteBuffer)this._buffer);
                int len = in.read(this._buffer.array(), this._buffer.arrayOffset() + this._buffer.limit(), BufferUtil.space((ByteBuffer)this._buffer));
                if (len < 0) {
                    this._eof = true;
                } else {
                    this._buffer.limit(this._buffer.limit() + len);
                }
                return len;
            }
        };
    }

    public static Input from(final ReadableByteChannel in) {
        return new Input(){

            @Override
            public int fillBuffer() throws IOException {
                BufferUtil.compact((ByteBuffer)this._buffer);
                int pos = BufferUtil.flipToFill((ByteBuffer)this._buffer);
                int len = in.read(this._buffer);
                if (len < 0) {
                    this._eof = true;
                }
                BufferUtil.flipToFlush((ByteBuffer)this._buffer, (int)pos);
                return len;
            }
        };
    }

    private HttpTester() {
    }

    public static Request newRequest() {
        Request r = new Request();
        r.setMethod(HttpMethod.GET.asString());
        r.setURI("/");
        r.setVersion(HttpVersion.HTTP_1_1);
        return r;
    }

    public static Request parseRequest(String request) {
        Request r = new Request();
        HttpParser parser = new HttpParser(r);
        parser.parseNext(BufferUtil.toBuffer((String)request));
        return r;
    }

    public static Request parseRequest(ByteBuffer request) {
        Request r = new Request();
        HttpParser parser = new HttpParser(r);
        parser.parseNext(request);
        return r;
    }

    public static Request parseRequest(InputStream inputStream) throws IOException {
        return HttpTester.parseRequest(HttpTester.from(inputStream));
    }

    public static Request parseRequest(ReadableByteChannel channel) throws IOException {
        return HttpTester.parseRequest(HttpTester.from(channel));
    }

    public static Request parseRequest(Input input) throws IOException {
        Request request;
        HttpParser parser = input.takeHttpParser();
        if (parser != null) {
            request = (Request)parser.getHandler();
        } else {
            request = HttpTester.newRequest();
            parser = new HttpParser(request);
        }
        HttpTester.parse(input, parser);
        if (request.isComplete()) {
            return request;
        }
        input.setHttpParser(parser);
        return null;
    }

    public static Response parseResponse(String response) {
        Response r = new Response();
        HttpParser parser = new HttpParser(r);
        parser.parseNext(BufferUtil.toBuffer((String)response));
        return r;
    }

    public static Response parseResponse(ByteBuffer response) {
        Response r = new Response();
        HttpParser parser = new HttpParser(r);
        parser.parseNext(response);
        return r;
    }

    public static Response parseResponse(InputStream responseStream) throws IOException {
        int l;
        Response r = new Response();
        HttpParser parser = new HttpParser(r);
        byte[] array = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.limit(1);
        do {
            buffer.position(1);
            l = responseStream.read(array);
            if (l < 0) {
                parser.atEOF();
            } else {
                buffer.position(0);
            }
            if (!parser.parseNext(buffer)) continue;
            return r;
        } while (l >= 0);
        return null;
    }

    public static Response parseResponse(Input in) throws IOException {
        Response r;
        HttpParser parser = in.takeHttpParser();
        if (parser == null) {
            r = new Response();
            parser = new HttpParser(r);
        } else {
            r = (Response)parser.getHandler();
        }
        HttpTester.parse(in, parser);
        if (r.isComplete()) {
            return r;
        }
        in.setHttpParser(parser);
        return null;
    }

    public static void parseResponse(Input in, Response response) throws IOException {
        HttpParser parser = in.takeHttpParser();
        if (parser == null) {
            parser = new HttpParser(response);
        }
        HttpTester.parse(in, parser);
        if (!response.isComplete()) {
            in.setHttpParser(parser);
        }
    }

    private static void parse(Input in, HttpParser parser) throws IOException {
        int len;
        ByteBuffer buffer = in.getBuffer();
        while (!(BufferUtil.hasContent((ByteBuffer)buffer) && parser.parseNext(buffer) || (len = in.fillBuffer()) == 0)) {
            if (len > 0) continue;
            parser.atEOF();
            parser.parseNext(buffer);
            break;
        }
    }

    public static class Request
    extends Message
    implements HttpParser.RequestHandler {
        private String _method;
        private String _uri;

        @Override
        public void startRequest(String method, String uri, HttpVersion version) {
            this._method = method;
            this._uri = uri;
            this._version = version;
        }

        public String getMethod() {
            return this._method;
        }

        public String getUri() {
            return this._uri;
        }

        public void setMethod(String method) {
            this._method = method;
        }

        public void setURI(String uri) {
            this._uri = uri;
        }

        @Override
        public MetaData.Request getInfo() {
            return new MetaData.Request(this._method, HttpURI.from(this._uri), this._version, this, this._content == null ? 0L : (long)this._content.size());
        }

        @Override
        public String toString() {
            return String.format("%s %s %s\n%s\n", new Object[]{this._method, this._uri, this._version, super.toString()});
        }

        public void setHeader(String name, String value) {
            this.put(name, value);
        }
    }

    public static abstract class Input {
        protected final ByteBuffer _buffer;
        protected boolean _eof = false;
        protected HttpParser _parser;

        public Input() {
            this(BufferUtil.allocate((int)8192));
        }

        Input(ByteBuffer buffer) {
            this._buffer = buffer;
        }

        public ByteBuffer getBuffer() {
            return this._buffer;
        }

        public void setHttpParser(HttpParser parser) {
            this._parser = parser;
        }

        public HttpParser getHttpParser() {
            return this._parser;
        }

        public HttpParser takeHttpParser() {
            HttpParser p = this._parser;
            this._parser = null;
            return p;
        }

        public boolean isEOF() {
            return BufferUtil.isEmpty((ByteBuffer)this._buffer) && this._eof;
        }

        public abstract int fillBuffer() throws IOException;
    }

    public static class Response
    extends Message
    implements HttpParser.ResponseHandler {
        private int _status;
        private String _reason;

        @Override
        public void startResponse(HttpVersion version, int status, String reason) {
            this._version = version;
            this._status = status;
            this._reason = reason;
        }

        public int getStatus() {
            return this._status;
        }

        public String getReason() {
            return this._reason;
        }

        @Override
        public MetaData.Response getInfo() {
            return new MetaData.Response(this._version, this._status, this._reason, this, this._content == null ? -1L : (long)this._content.size());
        }

        @Override
        public String toString() {
            return String.format("%s %s %s\n%s\n", new Object[]{this._version, this._status, this._reason, super.toString()});
        }
    }

    public static abstract class Message
    extends HttpFields.Mutable
    implements HttpParser.HttpHandler {
        boolean _earlyEOF;
        boolean _complete = false;
        ByteArrayOutputStream _content;
        HttpVersion _version = HttpVersion.HTTP_1_0;

        public boolean isComplete() {
            return this._complete;
        }

        public HttpVersion getVersion() {
            return this._version;
        }

        public void setVersion(String version) {
            this.setVersion((HttpVersion)((Object)HttpVersion.CACHE.get(version)));
        }

        public void setVersion(HttpVersion version) {
            this._version = version;
        }

        public void setContent(byte[] bytes) {
            try {
                this._content = new ByteArrayOutputStream();
                this._content.write(bytes);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void setContent(String content) {
            try {
                this._content = new ByteArrayOutputStream();
                this._content.write(StringUtil.getBytes((String)content));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void setContent(ByteBuffer content) {
            try {
                this._content = new ByteArrayOutputStream();
                this._content.write(BufferUtil.toArray((ByteBuffer)content));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] getContentBytes() {
            if (this._content == null) {
                return null;
            }
            return this._content.toByteArray();
        }

        public String getContent() {
            if (this._content == null) {
                return null;
            }
            byte[] bytes = this._content.toByteArray();
            String contentType = this.get(HttpHeader.CONTENT_TYPE);
            String encoding = MimeTypes.getCharsetFromContentType(contentType);
            Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
            return new String(bytes, charset);
        }

        @Override
        public void parsedHeader(HttpField field) {
            this.add(field.getName(), field.getValue());
        }

        @Override
        public boolean contentComplete() {
            return false;
        }

        @Override
        public boolean messageComplete() {
            this._complete = true;
            return true;
        }

        @Override
        public boolean headerComplete() {
            this._content = new ByteArrayOutputStream();
            return false;
        }

        @Override
        public void earlyEOF() {
            this._earlyEOF = true;
        }

        public boolean isEarlyEOF() {
            return this._earlyEOF;
        }

        @Override
        public boolean content(ByteBuffer ref) {
            try {
                this._content.write(BufferUtil.toArray((ByteBuffer)ref));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        @Override
        public void badMessage(BadMessageException failure) {
            throw failure;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public ByteBuffer generate() {
            try {
                HttpGenerator generator = new HttpGenerator();
                MetaData info = this.getInfo();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Buffer header = null;
                ByteBuffer chunk = null;
                ByteBuffer content = this._content == null ? null : ByteBuffer.wrap(this._content.toByteArray());
                block11: while (!generator.isEnd()) {
                    HttpGenerator.Result result = info instanceof MetaData.Request ? generator.generateRequest((MetaData.Request)info, (ByteBuffer)header, chunk, content, true) : generator.generateResponse((MetaData.Response)info, false, (ByteBuffer)header, chunk, content, true);
                    switch (result) {
                        case NEED_HEADER: {
                            header = BufferUtil.allocate((int)8192);
                            continue block11;
                        }
                        case HEADER_OVERFLOW: {
                            if (header.capacity() >= 32768) {
                                throw new BadMessageException(500, "Header too large");
                            }
                            header = BufferUtil.allocate((int)32768);
                            continue block11;
                        }
                        case NEED_CHUNK: {
                            chunk = BufferUtil.allocate((int)12);
                            continue block11;
                        }
                        case NEED_CHUNK_TRAILER: {
                            chunk = BufferUtil.allocate((int)8192);
                            continue block11;
                        }
                        case NEED_INFO: {
                            throw new IllegalStateException();
                        }
                        case FLUSH: {
                            if (BufferUtil.hasContent((ByteBuffer)header)) {
                                out.write(BufferUtil.toArray((ByteBuffer)header));
                                BufferUtil.clear((ByteBuffer)header);
                            }
                            if (BufferUtil.hasContent((ByteBuffer)chunk)) {
                                out.write(BufferUtil.toArray((ByteBuffer)chunk));
                                BufferUtil.clear((ByteBuffer)chunk);
                            }
                            if (!BufferUtil.hasContent((ByteBuffer)content)) break;
                            out.write(BufferUtil.toArray((ByteBuffer)content));
                            BufferUtil.clear((ByteBuffer)content);
                            break;
                        }
                        case SHUTDOWN_OUT: {
                            return ByteBuffer.wrap(out.toByteArray());
                        }
                    }
                }
                return ByteBuffer.wrap(out.toByteArray());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public abstract MetaData getInfo();
    }
}

