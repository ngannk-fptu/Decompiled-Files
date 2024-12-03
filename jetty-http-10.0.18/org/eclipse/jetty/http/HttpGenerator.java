/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Index
 *  org.eclipse.jetty.util.Index$Builder
 *  org.eclipse.jetty.util.StringUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpTokens;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(HttpGenerator.class);
    public static final boolean __STRICT = Boolean.getBoolean("org.eclipse.jetty.http.HttpGenerator.STRICT");
    private static final byte[] __colon_space = new byte[]{58, 32};
    public static final MetaData.Response CONTINUE_100_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 100, null, null, -1L);
    public static final MetaData.Response PROGRESS_102_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 102, null, null, -1L);
    public static final MetaData.Response RESPONSE_500_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 500, null, HttpFields.build().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE), 0L);
    public static final int CHUNK_SIZE = 12;
    private State _state = State.START;
    private HttpTokens.EndOfContent _endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
    private MetaData _info;
    private long _contentPrepared = 0L;
    private boolean _noContentResponse = false;
    private Boolean _persistent = null;
    private final int _send;
    private static final int SEND_SERVER = 1;
    private static final int SEND_XPOWEREDBY = 2;
    private static final Index<Boolean> ASSUMED_CONTENT_METHODS = new Index.Builder().caseSensitive(false).with(HttpMethod.POST.asString(), (Object)Boolean.TRUE).with(HttpMethod.PUT.asString(), (Object)Boolean.TRUE).build();
    private boolean _needCRLF = false;
    private static final byte[] ZERO_CHUNK = new byte[]{48, 13, 10};
    private static final byte[] LAST_CHUNK = new byte[]{48, 13, 10, 13, 10};
    private static final byte[] CONTENT_LENGTH_0 = StringUtil.getBytes((String)"Content-Length: 0\r\n");
    private static final byte[] CONNECTION_CLOSE = StringUtil.getBytes((String)"Connection: close\r\n");
    private static final byte[] HTTP_1_1_SPACE = StringUtil.getBytes((String)(HttpVersion.HTTP_1_1 + " "));
    private static final byte[] TRANSFER_ENCODING_CHUNKED = StringUtil.getBytes((String)"Transfer-Encoding: chunked\r\n");
    private static final byte[][] SEND = new byte[][]{new byte[0], StringUtil.getBytes((String)"Server: Jetty(10.x.x)\r\n"), StringUtil.getBytes((String)"X-Powered-By: Jetty(10.x.x)\r\n"), StringUtil.getBytes((String)"Server: Jetty(10.x.x)\r\nX-Powered-By: Jetty(10.x.x)\r\n")};
    private static final PreparedResponse[] __preprepared = new PreparedResponse[512];

    public static void setJettyVersion(String serverVersion) {
        HttpGenerator.SEND[1] = StringUtil.getBytes((String)("Server: " + serverVersion + "\r\n"));
        HttpGenerator.SEND[2] = StringUtil.getBytes((String)("X-Powered-By: " + serverVersion + "\r\n"));
        HttpGenerator.SEND[3] = StringUtil.getBytes((String)("Server: " + serverVersion + "\r\nX-Powered-By: " + serverVersion + "\r\n"));
    }

    public HttpGenerator() {
        this(false, false);
    }

    public HttpGenerator(boolean sendServerVersion, boolean sendXPoweredBy) {
        this._send = (sendServerVersion ? 1 : 0) | (sendXPoweredBy ? 2 : 0);
    }

    public void reset() {
        this._state = State.START;
        this._info = null;
        this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
        this._noContentResponse = false;
        this._persistent = null;
        this._contentPrepared = 0L;
        this._needCRLF = false;
    }

    public State getState() {
        return this._state;
    }

    public boolean isState(State state) {
        return this._state == state;
    }

    public boolean isIdle() {
        return this._state == State.START;
    }

    public boolean isEnd() {
        return this._state == State.END;
    }

    public boolean isCommitted() {
        return this._state.ordinal() >= State.COMMITTED.ordinal();
    }

    public boolean isChunking() {
        return this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT;
    }

    public boolean isNoContent() {
        return this._noContentResponse;
    }

    public void setPersistent(boolean persistent) {
        this._persistent = persistent;
    }

    public boolean isPersistent() {
        return Boolean.TRUE.equals(this._persistent);
    }

    public boolean isWritten() {
        return this._contentPrepared > 0L;
    }

    public long getContentPrepared() {
        return this._contentPrepared;
    }

    public void abort() {
        this._persistent = false;
        this._state = State.END;
        this._endOfContent = null;
    }

    public Result generateRequest(MetaData.Request info, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last) throws IOException {
        switch (this._state) {
            case START: {
                if (info == null) {
                    return Result.NEED_INFO;
                }
                this._info = info;
                if (header == null) {
                    return Result.NEED_HEADER;
                }
                int pos = BufferUtil.flipToFill((ByteBuffer)header);
                try {
                    this.generateRequestLine(info, header);
                    if (info.getHttpVersion() == HttpVersion.HTTP_0_9) {
                        throw new BadMessageException(500, "HTTP/0.9 not supported");
                    }
                    this.generateHeaders(header, content, last);
                    boolean expect100 = info.getFields().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
                    if (expect100) {
                        this._state = State.COMMITTED;
                    } else {
                        int len = BufferUtil.length((ByteBuffer)content);
                        if (len > 0) {
                            this._contentPrepared += (long)len;
                            if (this.isChunking()) {
                                this.prepareChunk(header, len);
                            }
                        }
                        this._state = last ? State.COMPLETING : State.COMMITTED;
                    }
                    Result result = Result.FLUSH;
                    return result;
                }
                catch (BadMessageException e) {
                    throw e;
                }
                catch (BufferOverflowException e) {
                    LOG.trace("IGNORED", (Throwable)e);
                    Result result = Result.HEADER_OVERFLOW;
                    return result;
                }
                catch (Exception e) {
                    throw new BadMessageException(500, e.getMessage(), e);
                }
                finally {
                    BufferUtil.flipToFlush((ByteBuffer)header, (int)pos);
                }
            }
            case COMMITTED: {
                return this.committed(chunk, content, last);
            }
            case COMPLETING: {
                return this.completing(chunk, content);
            }
            case END: {
                if (BufferUtil.hasContent((ByteBuffer)content)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("discarding content in COMPLETING");
                    }
                    BufferUtil.clear((ByteBuffer)content);
                }
                return Result.DONE;
            }
        }
        throw new IllegalStateException();
    }

    private Result committed(ByteBuffer chunk, ByteBuffer content, boolean last) {
        int len = BufferUtil.length((ByteBuffer)content);
        if (len > 0) {
            if (this.isChunking()) {
                if (chunk == null) {
                    return Result.NEED_CHUNK;
                }
                BufferUtil.clearToFill((ByteBuffer)chunk);
                this.prepareChunk(chunk, len);
                BufferUtil.flipToFlush((ByteBuffer)chunk, (int)0);
            }
            this._contentPrepared += (long)len;
        }
        if (last) {
            this._state = State.COMPLETING;
            return len > 0 ? Result.FLUSH : Result.CONTINUE;
        }
        return len > 0 ? Result.FLUSH : Result.DONE;
    }

    private Result completing(ByteBuffer chunk, ByteBuffer content) {
        if (BufferUtil.hasContent((ByteBuffer)content)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("discarding content in COMPLETING");
            }
            BufferUtil.clear((ByteBuffer)content);
        }
        if (this.isChunking()) {
            if (this._info.getTrailerSupplier() != null) {
                if (chunk == null || chunk.capacity() <= 12) {
                    return Result.NEED_CHUNK_TRAILER;
                }
                HttpFields trailers = this._info.getTrailerSupplier().get();
                if (trailers != null) {
                    BufferUtil.clearToFill((ByteBuffer)chunk);
                    this.generateTrailers(chunk, trailers);
                    BufferUtil.flipToFlush((ByteBuffer)chunk, (int)0);
                    this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
                    return Result.FLUSH;
                }
            }
            if (chunk == null) {
                return Result.NEED_CHUNK;
            }
            BufferUtil.clearToFill((ByteBuffer)chunk);
            this.prepareChunk(chunk, 0);
            BufferUtil.flipToFlush((ByteBuffer)chunk, (int)0);
            this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
            return Result.FLUSH;
        }
        this._state = State.END;
        return Boolean.TRUE.equals(this._persistent) ? Result.DONE : Result.SHUTDOWN_OUT;
    }

    /*
     * Unable to fully structure code
     */
    public Result generateResponse(MetaData.Response info, boolean head, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last) throws IOException {
        switch (1.$SwitchMap$org$eclipse$jetty$http$HttpGenerator$State[this._state.ordinal()]) {
            case 1: {
                if (info == null) {
                    return Result.NEED_INFO;
                }
                this._info = info;
                version = info.getHttpVersion();
                if (version == null) {
                    throw new BadMessageException(500, "No version");
                }
                if (version == HttpVersion.HTTP_0_9) {
                    this._persistent = false;
                    this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
                    if (BufferUtil.hasContent((ByteBuffer)content)) {
                        this._contentPrepared += (long)content.remaining();
                    }
                    this._state = last != false ? State.COMPLETING : State.COMMITTED;
                    return Result.FLUSH;
                }
                if (header == null) {
                    return Result.NEED_HEADER;
                }
                pos = BufferUtil.flipToFill((ByteBuffer)header);
                try {
                    this.generateResponseLine(info, header);
                    status = info.getStatus();
                    if (!HttpStatus.isInformational(status)) ** GOTO lbl38
                    this._noContentResponse = true;
                    switch (status) {
                        case 101: {
                            ** GOTO lbl40
                        }
                        case 103: {
                            this.generateHeaders(header, content, last);
                            this._state = State.COMPLETING_1XX;
                            var10_13 = Result.FLUSH;
                            return var10_13;
                        }
                        default: {
                            header.put(HttpTokens.CRLF);
                            this._state = State.COMPLETING_1XX;
                            var10_14 = Result.FLUSH;
                            return var10_14;
                        }
                    }
lbl38:
                    // 1 sources

                    if (status == 204 || status == 304) {
                        this._noContentResponse = true;
                    }
lbl40:
                    // 4 sources

                    this.generateHeaders(header, content, last);
                    len = BufferUtil.length((ByteBuffer)content);
                    if (len > 0) {
                        this._contentPrepared += (long)len;
                        if (this.isChunking() && !head) {
                            this.prepareChunk(header, len);
                        }
                    }
                    this._state = last != false ? State.COMPLETING : State.COMMITTED;
                }
                catch (BadMessageException e) {
                    throw e;
                }
                catch (BufferOverflowException e) {
                    HttpGenerator.LOG.trace("IGNORED", (Throwable)e);
                    var10_16 = Result.HEADER_OVERFLOW;
                    return var10_16;
                }
                catch (Exception e) {
                    throw new BadMessageException(500, e.getMessage(), e);
                }
                finally {
                    BufferUtil.flipToFlush((ByteBuffer)header, (int)pos);
                }
                return Result.FLUSH;
            }
            case 2: {
                return this.committed(chunk, content, last);
            }
            case 5: {
                this.reset();
                return Result.DONE;
            }
            case 3: {
                return this.completing(chunk, content);
            }
            case 4: {
                if (BufferUtil.hasContent((ByteBuffer)content)) {
                    if (HttpGenerator.LOG.isDebugEnabled()) {
                        HttpGenerator.LOG.debug("discarding content in COMPLETING");
                    }
                    BufferUtil.clear((ByteBuffer)content);
                }
                return Result.DONE;
            }
        }
        throw new IllegalStateException();
    }

    public void servletUpgrade() {
        this._noContentResponse = false;
        this._state = State.COMMITTED;
    }

    private void prepareChunk(ByteBuffer chunk, int remaining) {
        if (this._needCRLF) {
            BufferUtil.putCRLF((ByteBuffer)chunk);
        }
        if (remaining > 0) {
            BufferUtil.putHexInt((ByteBuffer)chunk, (int)remaining);
            BufferUtil.putCRLF((ByteBuffer)chunk);
            this._needCRLF = true;
        } else {
            chunk.put(LAST_CHUNK);
            this._needCRLF = false;
        }
    }

    private void generateTrailers(ByteBuffer buffer, HttpFields trailer) {
        if (this._needCRLF) {
            BufferUtil.putCRLF((ByteBuffer)buffer);
        }
        buffer.put(ZERO_CHUNK);
        int n = trailer.size();
        for (int f = 0; f < n; ++f) {
            HttpField field = trailer.getField(f);
            HttpGenerator.putTo(field, buffer);
        }
        BufferUtil.putCRLF((ByteBuffer)buffer);
    }

    private void generateRequestLine(MetaData.Request request, ByteBuffer header) {
        header.put(StringUtil.getBytes((String)request.getMethod()));
        header.put((byte)32);
        header.put(StringUtil.getBytes((String)request.getURIString()));
        header.put((byte)32);
        header.put(request.getHttpVersion().toBytes());
        header.put(HttpTokens.CRLF);
    }

    private void generateResponseLine(MetaData.Response response, ByteBuffer header) {
        int status = response.getStatus();
        PreparedResponse preprepared = status < __preprepared.length ? __preprepared[status] : null;
        String reason = response.getReason();
        if (preprepared != null) {
            if (reason == null) {
                header.put(preprepared._responseLine);
            } else {
                header.put(preprepared._schemeCode);
                header.put(this.getReasonBytes(reason));
                header.put(HttpTokens.CRLF);
            }
        } else {
            header.put(HTTP_1_1_SPACE);
            header.put((byte)(48 + status / 100));
            header.put((byte)(48 + status % 100 / 10));
            header.put((byte)(48 + status % 10));
            header.put((byte)32);
            if (reason == null) {
                header.put((byte)(48 + status / 100));
                header.put((byte)(48 + status % 100 / 10));
                header.put((byte)(48 + status % 10));
            } else {
                header.put(this.getReasonBytes(reason));
            }
            header.put(HttpTokens.CRLF);
        }
    }

    private byte[] getReasonBytes(String reason) {
        if (reason.length() > 1024) {
            reason = reason.substring(0, 1024);
        }
        byte[] bytes = StringUtil.getBytes((String)reason);
        int i = bytes.length;
        while (i-- > 0) {
            if (bytes[i] != 13 && bytes[i] != 10) continue;
            bytes[i] = 63;
        }
        return bytes;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void generateHeaders(ByteBuffer header, ByteBuffer content, boolean last) {
        int status;
        boolean noContentRequest;
        MetaData.Response response;
        MetaData.Request request = this._info instanceof MetaData.Request ? (MetaData.Request)this._info : null;
        MetaData.Response response2 = response = this._info instanceof MetaData.Response ? (MetaData.Response)this._info : null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateHeaders {} last={} content={}", new Object[]{this._info, last, BufferUtil.toDetailString((ByteBuffer)content)});
            LOG.debug(this._info.getFields().toString());
        }
        int send = this._send;
        HttpField transferEncoding = null;
        boolean http11 = this._info.getHttpVersion() == HttpVersion.HTTP_1_1;
        boolean close = false;
        boolean chunkedHint = this._info.getTrailerSupplier() != null;
        boolean contentType = false;
        long contentLength = this._info.getContentLength();
        boolean contentLengthField = false;
        HttpFields fields = this._info.getFields();
        if (fields != null) {
            int n = fields.size();
            block7: for (int f = 0; f < n; ++f) {
                HttpField field = fields.getField(f);
                HttpHeader h = field.getHeader();
                if (h == null) {
                    HttpGenerator.putTo(field, header);
                    continue;
                }
                switch (h) {
                    case CONTENT_LENGTH: {
                        if (contentLength < 0L) {
                            contentLength = field.getLongValue();
                        } else if (contentLength != field.getLongValue()) {
                            throw new BadMessageException(500, String.format("Incorrect Content-Length %d!=%d", contentLength, field.getLongValue()));
                        }
                        contentLengthField = true;
                        continue block7;
                    }
                    case CONTENT_TYPE: {
                        contentType = true;
                        HttpGenerator.putTo(field, header);
                        continue block7;
                    }
                    case TRANSFER_ENCODING: {
                        if (!http11) continue block7;
                        transferEncoding = field;
                        chunkedHint = field.contains(HttpHeaderValue.CHUNKED.asString());
                        continue block7;
                    }
                    case CONNECTION: {
                        boolean keepAlive = field.contains(HttpHeaderValue.KEEP_ALIVE.asString());
                        if (keepAlive && this._info.getHttpVersion() == HttpVersion.HTTP_1_0 && this._persistent == null) {
                            this._persistent = true;
                        }
                        if (field.contains(HttpHeaderValue.CLOSE.asString())) {
                            close = true;
                            this._persistent = false;
                        }
                        if (keepAlive && this._persistent == Boolean.FALSE) {
                            field = new HttpField(HttpHeader.CONNECTION, Stream.of(field.getValues()).filter(s -> !HttpHeaderValue.KEEP_ALIVE.is((String)s)).collect(Collectors.joining(", ")));
                        }
                        HttpGenerator.putTo(field, header);
                        continue block7;
                    }
                    case SERVER: {
                        send &= 0xFFFFFFFE;
                        HttpGenerator.putTo(field, header);
                        continue block7;
                    }
                    default: {
                        HttpGenerator.putTo(field, header);
                    }
                }
            }
        }
        if (last && contentLength < 0L && this._info.getTrailerSupplier() == null) {
            contentLength = this._contentPrepared + (long)BufferUtil.length((ByteBuffer)content);
        }
        boolean assumedContentRequest = request != null && ASSUMED_CONTENT_METHODS.get(request.getMethod()) != null;
        boolean assumedContent = assumedContentRequest || contentType || chunkedHint;
        boolean bl = noContentRequest = request != null && contentLength <= 0L && !assumedContent;
        if (this._persistent == null) {
            this._persistent = http11 || request != null && HttpMethod.CONNECT.is(request.getMethod());
        }
        if (this._noContentResponse || noContentRequest) {
            this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
            if (this._contentPrepared > 0L) {
                throw new BadMessageException(500, "Content for no content response");
            }
            if (contentLengthField) {
                if (response != null && response.getStatus() == 304) {
                    HttpGenerator.putContentLength(header, contentLength);
                } else if (contentLength > 0L) {
                    if (this._contentPrepared != 0L || !last) throw new BadMessageException(500, "Content for no content response");
                    content.clear();
                }
            }
        } else if (http11 && (chunkedHint || contentLength < 0L && (this._persistent.booleanValue() || assumedContentRequest))) {
            this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
            if (transferEncoding == null) {
                header.put(TRANSFER_ENCODING_CHUNKED);
            } else if (transferEncoding.toString().endsWith(HttpHeaderValue.CHUNKED.toString())) {
                HttpGenerator.putTo(transferEncoding, header);
                transferEncoding = null;
            } else {
                if (chunkedHint) throw new BadMessageException(500, "Bad Transfer-Encoding");
                HttpGenerator.putTo(new HttpField(HttpHeader.TRANSFER_ENCODING, transferEncoding.getValue() + ",chunked"), header);
                transferEncoding = null;
            }
        } else if (contentLength >= 0L && (request != null || this._persistent.booleanValue())) {
            this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
            HttpGenerator.putContentLength(header, contentLength);
        } else {
            if (response == null) throw new BadMessageException(500, "Unknown content length for request");
            this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
            this._persistent = false;
            if (contentLength >= 0L && (contentLength > 0L || assumedContent || contentLengthField)) {
                HttpGenerator.putContentLength(header, contentLength);
            }
            if (http11 && !close) {
                header.put(CONNECTION_CLOSE);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(this._endOfContent.toString());
        }
        if (transferEncoding != null) {
            if (chunkedHint) {
                String v = transferEncoding.getValue();
                int c = v.lastIndexOf(44);
                if (c > 0 && v.lastIndexOf(HttpHeaderValue.CHUNKED.toString(), c) > c) {
                    HttpGenerator.putTo(new HttpField(HttpHeader.TRANSFER_ENCODING, v.substring(0, c).trim()), header);
                }
            } else {
                HttpGenerator.putTo(transferEncoding, header);
            }
        }
        int n = status = response != null ? response.getStatus() : -1;
        if (status > 199) {
            header.put(SEND[send]);
        }
        header.put(HttpTokens.CRLF);
    }

    private static void putContentLength(ByteBuffer header, long contentLength) {
        if (contentLength == 0L) {
            header.put(CONTENT_LENGTH_0);
        } else {
            header.put(HttpHeader.CONTENT_LENGTH.getBytesColonSpace());
            BufferUtil.putDecLong((ByteBuffer)header, (long)contentLength);
            header.put(HttpTokens.CRLF);
        }
    }

    public static byte[] getReasonBuffer(int code) {
        PreparedResponse status;
        PreparedResponse preparedResponse = status = code < __preprepared.length ? __preprepared[code] : null;
        if (status != null) {
            return status._reason;
        }
        return null;
    }

    public String toString() {
        return String.format("%s@%x{s=%s}", new Object[]{this.getClass().getSimpleName(), this.hashCode(), this._state});
    }

    private static void putSanitisedName(String s, ByteBuffer buffer) {
        int l = s.length();
        for (int i = 0; i < l; ++i) {
            char c = s.charAt(i);
            if (c < '\u0000' || c > '\u00ff' || c == '\r' || c == '\n' || c == ':') {
                buffer.put((byte)63);
                continue;
            }
            buffer.put((byte)(0xFF & c));
        }
    }

    private static void putSanitisedValue(String s, ByteBuffer buffer) {
        int l = s.length();
        for (int i = 0; i < l; ++i) {
            char c = s.charAt(i);
            if (c < '\u0000' || c > '\u00ff' || c == '\r' || c == '\n') {
                buffer.put((byte)32);
                continue;
            }
            buffer.put((byte)(0xFF & c));
        }
    }

    public static void putTo(HttpField field, ByteBuffer bufferInFillMode) {
        if (field instanceof PreEncodedHttpField) {
            ((PreEncodedHttpField)field).putTo(bufferInFillMode, HttpVersion.HTTP_1_0);
        } else {
            HttpHeader header = field.getHeader();
            if (header != null) {
                bufferInFillMode.put(header.getBytesColonSpace());
                HttpGenerator.putSanitisedValue(field.getValue(), bufferInFillMode);
            } else {
                HttpGenerator.putSanitisedName(field.getName(), bufferInFillMode);
                bufferInFillMode.put(__colon_space);
                HttpGenerator.putSanitisedValue(field.getValue(), bufferInFillMode);
            }
            BufferUtil.putCRLF((ByteBuffer)bufferInFillMode);
        }
    }

    public static void putTo(HttpFields.Mutable fields, ByteBuffer bufferInFillMode) {
        for (HttpField field : fields) {
            if (field == null) continue;
            HttpGenerator.putTo(field, bufferInFillMode);
        }
        BufferUtil.putCRLF((ByteBuffer)bufferInFillMode);
    }

    static {
        int versionLength = HttpVersion.HTTP_1_1.toString().length();
        for (int i = 0; i < __preprepared.length; ++i) {
            HttpStatus.Code code = HttpStatus.getCode(i);
            if (code == null) continue;
            String reason = code.getMessage();
            byte[] line = new byte[versionLength + 5 + reason.length() + 2];
            HttpVersion.HTTP_1_1.toBuffer().get(line, 0, versionLength);
            line[versionLength + 0] = 32;
            line[versionLength + 1] = (byte)(48 + i / 100);
            line[versionLength + 2] = (byte)(48 + i % 100 / 10);
            line[versionLength + 3] = (byte)(48 + i % 10);
            line[versionLength + 4] = 32;
            for (int j = 0; j < reason.length(); ++j) {
                line[versionLength + 5 + j] = (byte)reason.charAt(j);
            }
            line[versionLength + 5 + reason.length()] = 13;
            line[versionLength + 6 + reason.length()] = 10;
            HttpGenerator.__preprepared[i] = new PreparedResponse();
            HttpGenerator.__preprepared[i]._schemeCode = Arrays.copyOfRange(line, 0, versionLength + 5);
            HttpGenerator.__preprepared[i]._reason = Arrays.copyOfRange(line, versionLength + 5, line.length - 2);
            HttpGenerator.__preprepared[i]._responseLine = line;
        }
    }

    public static enum State {
        START,
        COMMITTED,
        COMPLETING,
        COMPLETING_1XX,
        END;

    }

    public static enum Result {
        NEED_CHUNK,
        NEED_INFO,
        NEED_HEADER,
        HEADER_OVERFLOW,
        NEED_CHUNK_TRAILER,
        FLUSH,
        CONTINUE,
        SHUTDOWN_OUT,
        DONE;

    }

    private static class PreparedResponse {
        byte[] _reason;
        byte[] _schemeCode;
        byte[] _responseLine;

        private PreparedResponse() {
        }
    }
}

