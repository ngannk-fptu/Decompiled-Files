/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.HostPort
 *  org.eclipse.jetty.util.Index
 *  org.eclipse.jetty.util.Index$Builder
 *  org.eclipse.jetty.util.Index$Mutable
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.StringUtil
 *  org.eclipse.jetty.util.Utf8StringBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.ComplianceViolation;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpTokens;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {
    public static final Logger LOG = LoggerFactory.getLogger(HttpParser.class);
    public static final int INITIAL_URI_LENGTH = 256;
    private static final int MAX_CHUNK_LENGTH = 0x7FFFFEF;
    public static final Index<HttpField> CACHE = new Index.Builder().caseSensitive(false).with((Object)new HttpField(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE)).with((Object)new HttpField(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE)).with((Object)new HttpField(HttpHeader.CONNECTION, HttpHeaderValue.UPGRADE)).with((Object)new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip")).with((Object)new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip, deflate")).with((Object)new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip, deflate, br")).with((Object)new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-US,enq=0.5")).with((Object)new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-GB,en-USq=0.8,enq=0.6")).with((Object)new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-AU,enq=0.9,it-ITq=0.8,itq=0.7,en-GBq=0.6,en-USq=0.5")).with((Object)new HttpField(HttpHeader.ACCEPT_CHARSET, "ISO-8859-1,utf-8q=0.7,*q=0.3")).with((Object)new HttpField(HttpHeader.ACCEPT, "*/*")).with((Object)new HttpField(HttpHeader.ACCEPT, "image/png,image/*q=0.8,*/*q=0.5")).with((Object)new HttpField(HttpHeader.ACCEPT, "text/html,application/xhtml+xml,application/xmlq=0.9,*/*q=0.8")).with((Object)new HttpField(HttpHeader.ACCEPT, "text/html,application/xhtml+xml,application/xmlq=0.9,image/webp,image/apng,*/*q=0.8")).with((Object)new HttpField(HttpHeader.ACCEPT_RANGES, HttpHeaderValue.BYTES)).with((Object)new HttpField(HttpHeader.PRAGMA, "no-cache")).with((Object)new HttpField(HttpHeader.CACHE_CONTROL, "private, no-cache, no-cache=Set-Cookie, proxy-revalidate")).with((Object)new HttpField(HttpHeader.CACHE_CONTROL, "no-cache")).with((Object)new HttpField(HttpHeader.CACHE_CONTROL, "max-age=0")).with((Object)new HttpField(HttpHeader.CONTENT_LENGTH, "0")).with((Object)new HttpField(HttpHeader.CONTENT_ENCODING, "gzip")).with((Object)new HttpField(HttpHeader.CONTENT_ENCODING, "deflate")).with((Object)new HttpField(HttpHeader.TRANSFER_ENCODING, "chunked")).with((Object)new HttpField(HttpHeader.EXPIRES, "Fri, 01 Jan 1990 00:00:00 GMT")).withAll(() -> {
        LinkedHashMap<String, PreEncodedHttpField> map = new LinkedHashMap<String, PreEncodedHttpField>();
        for (String type : new String[]{"text/plain", "text/html", "text/xml", "text/json", "application/json", "application/x-www-form-urlencoded"}) {
            PreEncodedHttpField field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type);
            map.put(field.toString(), field);
            for (String charset : new String[]{"utf-8", "iso-8859-1"}) {
                PreEncodedHttpField field1 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + ";charset=" + charset);
                map.put(field1.toString(), field1);
                PreEncodedHttpField field2 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + "; charset=" + charset);
                map.put(field2.toString(), field2);
                PreEncodedHttpField field3 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + ";charset=" + charset.toUpperCase(Locale.ENGLISH));
                map.put(field3.toString(), field3);
                PreEncodedHttpField field4 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + "; charset=" + charset.toUpperCase(Locale.ENGLISH));
                map.put(field4.toString(), field4);
            }
        }
        return map;
    }).withAll(() -> {
        LinkedHashMap<String, HttpField> map = new LinkedHashMap<String, HttpField>();
        for (HttpHeader h : HttpHeader.values()) {
            HttpField httpField = new HttpField(h, (String)null);
            map.put(httpField.toString(), httpField);
        }
        return map;
    }).build();
    private static final Index.Mutable<HttpField> NO_CACHE = new Index.Builder().caseSensitive(false).mutable().maxCapacity(0).build();
    private static final EnumSet<State> __idleStates = EnumSet.of(State.START, State.END, State.CLOSE, State.CLOSED);
    private static final EnumSet<State> __completeStates = EnumSet.of(State.END, State.CLOSE, State.CLOSED);
    private static final EnumSet<State> __terminatedStates = EnumSet.of(State.CLOSE, State.CLOSED);
    private final boolean debugEnabled = LOG.isDebugEnabled();
    private final HttpHandler _handler;
    private final RequestHandler _requestHandler;
    private final ResponseHandler _responseHandler;
    private final ComplianceViolation.Listener _complianceListener;
    private final int _maxHeaderBytes;
    private final HttpCompliance _complianceMode;
    private final Utf8StringBuilder _uri = new Utf8StringBuilder(256);
    private final FieldCache _fieldCache = new FieldCache();
    private HttpField _field;
    private HttpHeader _header;
    private String _headerString;
    private String _valueString;
    private int _responseStatus;
    private int _headerBytes;
    private String _parsedHost;
    private boolean _headerComplete;
    private volatile State _state = State.START;
    private volatile FieldState _fieldState = FieldState.FIELD;
    private volatile boolean _eof;
    private HttpMethod _method;
    private String _methodString;
    private HttpVersion _version;
    private HttpTokens.EndOfContent _endOfContent;
    private boolean _hasContentLength;
    private boolean _hasTransferEncoding;
    private long _contentLength = -1L;
    private long _contentPosition;
    private int _chunkLength;
    private int _chunkPosition;
    private boolean _headResponse;
    private boolean _cr;
    private ByteBuffer _contentChunk;
    private int _length;
    private final StringBuilder _string = new StringBuilder();
    private long _beginNanoTime = Long.MIN_VALUE;

    private static HttpCompliance compliance() {
        return HttpCompliance.RFC7230;
    }

    public HttpParser(RequestHandler handler) {
        this(handler, -1, HttpParser.compliance());
    }

    public HttpParser(ResponseHandler handler) {
        this(handler, -1, HttpParser.compliance());
    }

    public HttpParser(RequestHandler handler, int maxHeaderBytes) {
        this(handler, maxHeaderBytes, HttpParser.compliance());
    }

    public HttpParser(ResponseHandler handler, int maxHeaderBytes) {
        this(handler, maxHeaderBytes, HttpParser.compliance());
    }

    public HttpParser(RequestHandler handler, HttpCompliance compliance) {
        this(handler, -1, compliance);
    }

    public HttpParser(RequestHandler handler, int maxHeaderBytes, HttpCompliance compliance) {
        this(handler, null, maxHeaderBytes, compliance == null ? HttpParser.compliance() : compliance);
    }

    public HttpParser(ResponseHandler handler, int maxHeaderBytes, HttpCompliance compliance) {
        this(null, handler, maxHeaderBytes, compliance == null ? HttpParser.compliance() : compliance);
    }

    private HttpParser(RequestHandler requestHandler, ResponseHandler responseHandler, int maxHeaderBytes, HttpCompliance compliance) {
        this._handler = requestHandler != null ? requestHandler : responseHandler;
        this._requestHandler = requestHandler;
        this._responseHandler = responseHandler;
        this._maxHeaderBytes = maxHeaderBytes;
        this._complianceMode = compliance;
        this._complianceListener = (ComplianceViolation.Listener)((Object)(this._handler instanceof ComplianceViolation.Listener ? this._handler : null));
    }

    public long getBeginNanoTime() {
        return this._beginNanoTime;
    }

    public HttpHandler getHandler() {
        return this._handler;
    }

    public int getHeaderCacheSize() {
        return this._fieldCache.getCapacity();
    }

    public void setHeaderCacheSize(int headerCacheSize) {
        this._fieldCache.setCapacity(headerCacheSize);
    }

    public boolean isHeaderCacheCaseSensitive() {
        return this._fieldCache.isCaseSensitive();
    }

    public void setHeaderCacheCaseSensitive(boolean headerCacheCaseSensitive) {
        this._fieldCache.setCaseSensitive(headerCacheCaseSensitive);
    }

    protected void checkViolation(HttpCompliance.Violation violation) throws BadMessageException {
        if (!violation.isAllowedBy(this._complianceMode)) {
            throw new BadMessageException(400, violation.getDescription());
        }
        this.reportComplianceViolation(violation, violation.getDescription());
    }

    protected void reportComplianceViolation(HttpCompliance.Violation violation) {
        this.reportComplianceViolation(violation, violation.getDescription());
    }

    protected void reportComplianceViolation(HttpCompliance.Violation violation, String reason) {
        if (this._complianceListener != null) {
            this._complianceListener.onComplianceViolation(this._complianceMode, violation, reason);
        }
    }

    protected String caseInsensitiveHeader(String orig, String normative) {
        if (HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode)) {
            return normative;
        }
        if (!orig.equals(normative)) {
            this.reportComplianceViolation(HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME, orig);
        }
        return orig;
    }

    public long getContentLength() {
        return this._contentLength;
    }

    public long getContentRead() {
        return this._contentPosition;
    }

    public int getHeaderLength() {
        return this._headerBytes;
    }

    public void setHeadResponse(boolean head) {
        this._headResponse = head;
    }

    protected void setResponseStatus(int status) {
        this._responseStatus = status;
    }

    public State getState() {
        return this._state;
    }

    public boolean inContentState() {
        return this._state.ordinal() >= State.CONTENT.ordinal() && this._state.ordinal() < State.END.ordinal();
    }

    public boolean inHeaderState() {
        return this._state.ordinal() < State.CONTENT.ordinal();
    }

    public boolean isChunking() {
        return this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT;
    }

    public boolean isStart() {
        return this.isState(State.START);
    }

    public boolean isClose() {
        return this.isState(State.CLOSE);
    }

    public boolean isClosed() {
        return this.isState(State.CLOSED);
    }

    public boolean isIdle() {
        return __idleStates.contains((Object)this._state);
    }

    public boolean isComplete() {
        return __completeStates.contains((Object)this._state);
    }

    public boolean isTerminated() {
        return __terminatedStates.contains((Object)this._state);
    }

    public boolean isState(State state) {
        return this._state == state;
    }

    private HttpTokens.Token next(ByteBuffer buffer) {
        byte ch = buffer.get();
        HttpTokens.Token t = HttpTokens.getToken(ch);
        switch (t.getType()) {
            case CNTL: {
                throw new IllegalCharacterException(this._state, t, buffer);
            }
            case LF: {
                this._cr = false;
                break;
            }
            case CR: {
                if (this._cr) {
                    throw new BadMessageException("Bad EOL");
                }
                this._cr = true;
                if (buffer.hasRemaining()) {
                    if (this._maxHeaderBytes > 0 && (this._state == State.HEADER || this._state == State.TRAILER)) {
                        ++this._headerBytes;
                    }
                    return this.next(buffer);
                }
                return null;
            }
            case ALPHA: 
            case DIGIT: 
            case TCHAR: 
            case VCHAR: 
            case HTAB: 
            case SPACE: 
            case OTEXT: 
            case COLON: {
                if (!this._cr) break;
                throw new BadMessageException("Bad EOL");
            }
        }
        return t;
    }

    private void quickStart(ByteBuffer buffer) {
        HttpTokens.Token t;
        if (this._requestHandler != null) {
            this._method = HttpMethod.lookAheadGet(buffer);
            if (this._method != null) {
                this._methodString = this._method.asString();
                buffer.position(buffer.position() + this._methodString.length() + 1);
                this.setState(State.SPACE1);
                return;
            }
        } else if (this._responseHandler != null) {
            this._version = HttpVersion.lookAheadGet(buffer);
            if (this._version != null) {
                buffer.position(buffer.position() + this._version.asString().length() + 1);
                this.setState(State.SPACE1);
                return;
            }
        }
        while (this._state == State.START && buffer.hasRemaining() && (t = this.next(buffer)) != null) {
            switch (t.getType()) {
                case ALPHA: 
                case DIGIT: 
                case TCHAR: 
                case VCHAR: {
                    this._string.setLength(0);
                    this._string.append(t.getChar());
                    this.setState(this._requestHandler != null ? State.METHOD : State.RESPONSE_VERSION);
                    return;
                }
                case HTAB: 
                case SPACE: 
                case OTEXT: {
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
            }
            if (this._maxHeaderBytes <= 0 || ++this._headerBytes <= this._maxHeaderBytes) continue;
            LOG.warn("padding is too large >{}", (Object)this._maxHeaderBytes);
            throw new BadMessageException(400);
        }
    }

    private void setString(String s) {
        this._string.setLength(0);
        this._string.append(s);
        this._length = s.length();
    }

    private String takeString() {
        this._string.setLength(this._length);
        String s = this._string.toString();
        this._string.setLength(0);
        this._length = -1;
        return s;
    }

    private boolean handleHeaderContentMessage() {
        boolean handleHeader = this._handler.headerComplete();
        this._headerComplete = true;
        if (handleHeader) {
            return true;
        }
        this.setState(State.CONTENT_END);
        return this.handleContentMessage();
    }

    private boolean handleContentMessage() {
        boolean handleContent = this._handler.contentComplete();
        if (handleContent) {
            return true;
        }
        this.setState(State.END);
        return this._handler.messageComplete();
    }

    private boolean parseLine(ByteBuffer buffer) {
        HttpTokens.Token t;
        boolean handle = false;
        block47: while (this._state.ordinal() < State.HEADER.ordinal() && buffer.hasRemaining() && !handle && (t = this.next(buffer)) != null) {
            if (this._maxHeaderBytes > 0 && ++this._headerBytes > this._maxHeaderBytes) {
                if (this._state == State.URI) {
                    LOG.warn("URI is too large >{}", (Object)this._maxHeaderBytes);
                    throw new BadMessageException(414);
                }
                if (this._requestHandler != null) {
                    LOG.warn("request is too large >{}", (Object)this._maxHeaderBytes);
                } else {
                    LOG.warn("response is too large >{}", (Object)this._maxHeaderBytes);
                }
                throw new BadMessageException(431);
            }
            block0 : switch (this._state) {
                case METHOD: {
                    switch (t.getType()) {
                        case SPACE: {
                            HttpMethod method;
                            this._length = this._string.length();
                            this._methodString = this.takeString();
                            if (HttpCompliance.Violation.CASE_INSENSITIVE_METHOD.isAllowedBy(this._complianceMode)) {
                                method = (HttpMethod)((Object)HttpMethod.INSENSITIVE_CACHE.get(this._methodString));
                                if (method != null) {
                                    if (!method.asString().equals(this._methodString)) {
                                        this.reportComplianceViolation(HttpCompliance.Violation.CASE_INSENSITIVE_METHOD, this._methodString);
                                    }
                                    this._methodString = method.asString();
                                }
                            } else {
                                method = (HttpMethod)((Object)HttpMethod.CACHE.get(this._methodString));
                                if (method != null) {
                                    this._methodString = method.asString();
                                }
                            }
                            this.setState(State.SPACE1);
                            break block0;
                        }
                        case LF: {
                            throw new BadMessageException("No URI");
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: {
                            this._string.append(t.getChar());
                            break block0;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case RESPONSE_VERSION: {
                    Object version;
                    switch (t.getType()) {
                        case SPACE: {
                            this._length = this._string.length();
                            version = this.takeString();
                            this._version = (HttpVersion)((Object)HttpVersion.CACHE.get(version));
                            this.checkVersion();
                            this.setState(State.SPACE1);
                            break block0;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case COLON: {
                            this._string.append(t.getChar());
                            break block0;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case SPACE1: {
                    switch (t.getType()) {
                        case SPACE: {
                            break block0;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case COLON: {
                            if (this._responseHandler != null) {
                                if (t.getType() != HttpTokens.Type.DIGIT) {
                                    throw new IllegalCharacterException(this._state, t, buffer);
                                }
                                this.setState(State.STATUS);
                                this.setResponseStatus(t.getByte() - 48);
                                break block0;
                            }
                            this._uri.reset();
                            this.setState(State.URI);
                            if (buffer.hasArray()) {
                                int i;
                                byte[] array = buffer.array();
                                int p = buffer.arrayOffset() + buffer.position();
                                int l = buffer.arrayOffset() + buffer.limit();
                                for (i = p; i < l && array[i] > 32; ++i) {
                                }
                                int len = i - p;
                                this._headerBytes += len;
                                if (this._maxHeaderBytes > 0 && ++this._headerBytes > this._maxHeaderBytes) {
                                    LOG.warn("URI is too large >{}", (Object)this._maxHeaderBytes);
                                    throw new BadMessageException(414);
                                }
                                this._uri.append(array, p - 1, len + 1);
                                buffer.position(i - buffer.arrayOffset());
                                break block0;
                            }
                            this._uri.append(t.getByte());
                            break block0;
                        }
                    }
                    throw new BadMessageException(400, this._requestHandler != null ? "No URI" : "No Status");
                }
                case STATUS: {
                    switch (t.getType()) {
                        case SPACE: {
                            this.setState(State.SPACE2);
                            break block0;
                        }
                        case DIGIT: {
                            this._responseStatus = this._responseStatus * 10 + (t.getByte() - 48);
                            if (this._responseStatus < 1000) continue block47;
                            throw new BadMessageException("Bad status");
                        }
                        case LF: {
                            this._fieldCache.prepare();
                            this.setState(State.HEADER);
                            this._responseHandler.startResponse(this._version, this._responseStatus, null);
                            break block0;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case URI: {
                    switch (t.getType()) {
                        case SPACE: {
                            this.setState(State.SPACE2);
                            break block0;
                        }
                        case LF: {
                            if (HttpCompliance.Violation.HTTP_0_9.isAllowedBy(this._complianceMode)) {
                                this.reportComplianceViolation(HttpCompliance.Violation.HTTP_0_9, HttpCompliance.Violation.HTTP_0_9.getDescription());
                                this._requestHandler.startRequest(this._methodString, this._uri.toString(), HttpVersion.HTTP_0_9);
                                this.setState(State.CONTENT);
                                this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                                BufferUtil.clear((ByteBuffer)buffer);
                                handle = this.handleHeaderContentMessage();
                                break block0;
                            }
                            throw new BadMessageException(505, "HTTP/0.9 not supported");
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case OTEXT: 
                        case COLON: {
                            this._uri.append(t.getByte());
                            break block0;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case SPACE2: {
                    Object version;
                    switch (t.getType()) {
                        case SPACE: {
                            break block0;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case COLON: {
                            int pos;
                            this._string.setLength(0);
                            this._string.append(t.getChar());
                            if (this._responseHandler != null) {
                                this._length = 1;
                                this.setState(State.REASON);
                                break block0;
                            }
                            this.setState(State.REQUEST_VERSION);
                            version = buffer.position() > 0 && buffer.hasArray() ? HttpVersion.lookAheadGet(buffer.array(), buffer.arrayOffset() + buffer.position() - 1, buffer.arrayOffset() + buffer.limit()) : (HttpVersion)((Object)HttpVersion.CACHE.getBest(buffer, 0, buffer.remaining()));
                            if (version == null || (pos = buffer.position() + ((HttpVersion)((Object)version)).asString().length() - 1) >= buffer.limit()) continue block47;
                            byte n = buffer.get(pos);
                            if (n == 13) {
                                this._cr = true;
                                this._version = version;
                                this.checkVersion();
                                this._string.setLength(0);
                                buffer.position(pos + 1);
                                break block0;
                            }
                            if (n != 10) continue block47;
                            this._version = version;
                            this.checkVersion();
                            this._string.setLength(0);
                            buffer.position(pos);
                            break block0;
                        }
                        case LF: {
                            if (this._responseHandler != null) {
                                this._fieldCache.prepare();
                                this.setState(State.HEADER);
                                this._responseHandler.startResponse(this._version, this._responseStatus, null);
                                break block0;
                            }
                            this.checkViolation(HttpCompliance.Violation.HTTP_0_9);
                            this._requestHandler.startRequest(this._methodString, this._uri.toString(), HttpVersion.HTTP_0_9);
                            this.setState(State.CONTENT);
                            this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                            BufferUtil.clear((ByteBuffer)buffer);
                            handle = this.handleHeaderContentMessage();
                            break block0;
                        }
                        default: {
                            throw new IllegalCharacterException(this._state, t, buffer);
                        }
                    }
                }
                case REQUEST_VERSION: {
                    switch (t.getType()) {
                        case LF: {
                            if (this._version == null) {
                                this._length = this._string.length();
                                this._version = (HttpVersion)((Object)HttpVersion.CACHE.get(this.takeString()));
                            }
                            this.checkVersion();
                            this._fieldCache.prepare();
                            this.setState(State.HEADER);
                            this._requestHandler.startRequest(this._methodString, this._uri.toString(), this._version);
                            continue block47;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case COLON: {
                            this._string.append(t.getChar());
                            break block0;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case REASON: {
                    switch (t.getType()) {
                        case LF: {
                            String reason = this.takeString();
                            this._fieldCache.prepare();
                            this.setState(State.HEADER);
                            this._responseHandler.startResponse(this._version, this._responseStatus, reason);
                            continue block47;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case OTEXT: 
                        case COLON: {
                            this._string.append(t.getChar());
                            this._length = this._string.length();
                            break block0;
                        }
                        case HTAB: 
                        case SPACE: {
                            this._string.append(t.getChar());
                            break block0;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                default: {
                    throw new IllegalStateException(this._state.toString());
                }
            }
        }
        return handle;
    }

    private void checkVersion() {
        if (this._version == null) {
            throw new BadMessageException(505, "Unknown Version");
        }
        if (this._version.getVersion() < 10 || this._version.getVersion() > 20) {
            throw new BadMessageException(505, "Unsupported Version");
        }
    }

    private void parsedHeader() {
        if (this._headerString != null || this._valueString != null) {
            if (this._header != null) {
                boolean addToFieldCache = false;
                switch (this._header) {
                    case CONTENT_LENGTH: {
                        if (this._hasTransferEncoding) {
                            this.checkViolation(HttpCompliance.Violation.TRANSFER_ENCODING_WITH_CONTENT_LENGTH);
                        }
                        long contentLength = this.convertContentLength(this._valueString);
                        if (this._hasContentLength) {
                            this.checkViolation(HttpCompliance.Violation.MULTIPLE_CONTENT_LENGTHS);
                            if (contentLength != this._contentLength) {
                                throw new BadMessageException(400, HttpCompliance.Violation.MULTIPLE_CONTENT_LENGTHS.getDescription());
                            }
                        }
                        this._hasContentLength = true;
                        if (this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT) break;
                        this._contentLength = contentLength;
                        this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
                        break;
                    }
                    case TRANSFER_ENCODING: {
                        this._hasTransferEncoding = true;
                        if (this._hasContentLength) {
                            this.checkViolation(HttpCompliance.Violation.TRANSFER_ENCODING_WITH_CONTENT_LENGTH);
                        }
                        if (this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT) {
                            throw new BadMessageException(400, "Bad Transfer-Encoding, chunked not last");
                        }
                        if (HttpHeaderValue.CHUNKED.is(this._valueString)) {
                            this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
                            this._contentLength = -1L;
                            break;
                        }
                        List<String> values = new QuotedCSV(this._valueString).getValues();
                        int chunked = -1;
                        int len = values.size();
                        for (int i = 0; i < len; ++i) {
                            if (HttpHeaderValue.CHUNKED.is(values.get(i))) {
                                if (chunked != -1) {
                                    throw new BadMessageException(400, "Bad Transfer-Encoding, multiple chunked tokens");
                                }
                                chunked = i;
                                this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
                                this._contentLength = -1L;
                                continue;
                            }
                            if (this._endOfContent != HttpTokens.EndOfContent.CHUNKED_CONTENT) continue;
                            throw new BadMessageException(400, "Bad Transfer-Encoding, chunked not last");
                        }
                        break;
                    }
                    case HOST: {
                        if (this._parsedHost != null) {
                            if (LOG.isWarnEnabled()) {
                                LOG.warn("Encountered multiple `Host` headers.  Previous `Host` header already seen as `{}`, new `Host` header has appeared as `{}`", (Object)this._parsedHost, (Object)this._valueString);
                            }
                            this.checkViolation(HttpCompliance.Violation.DUPLICATE_HOST_HEADERS);
                        }
                        this._parsedHost = this._valueString;
                        if (this._field instanceof HostPortHttpField || this._valueString == null || this._valueString.isEmpty()) break;
                        this._field = HttpCompliance.Violation.UNSAFE_HOST_HEADER.isAllowedBy(this._complianceMode) ? new HostPortHttpField(this._header, HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode) ? this._headerString : this._header.asString(), HostPort.unsafe((String)this._valueString)) : new HostPortHttpField(this._header, HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode) ? this._headerString : this._header.asString(), this._valueString);
                        addToFieldCache = this._fieldCache.isEnabled();
                        break;
                    }
                    case CONNECTION: {
                        if (this._field == null) {
                            this._field = new HttpField(this._header, this.caseInsensitiveHeader(this._headerString, this._header.asString()), this._valueString);
                        }
                        if (this.getHeaderCacheSize() <= 0 || !this._field.contains(HttpHeaderValue.CLOSE.asString())) break;
                        this._fieldCache.setCapacity(-1);
                        break;
                    }
                    case AUTHORIZATION: 
                    case ACCEPT: 
                    case ACCEPT_CHARSET: 
                    case ACCEPT_ENCODING: 
                    case ACCEPT_LANGUAGE: 
                    case COOKIE: 
                    case CACHE_CONTROL: 
                    case USER_AGENT: {
                        addToFieldCache = this._field == null && this._fieldCache.cacheable(this._header, this._valueString);
                        break;
                    }
                }
                if (addToFieldCache) {
                    if (this._field == null) {
                        this._field = new HttpField(this._header, this.caseInsensitiveHeader(this._headerString, this._header.asString()), this._valueString);
                    }
                    this._fieldCache.add(this._field);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("parsedHeader({}) header={}, headerString=[{}], valueString=[{}]", new Object[]{this._field, this._header, this._headerString, this._valueString});
            }
            this._handler.parsedHeader(this._field != null ? this._field : new HttpField(this._header, this._headerString, this._valueString));
        }
        this._valueString = null;
        this._headerString = null;
        this._header = null;
        this._field = null;
    }

    private void parsedTrailer() {
        if (this._headerString != null || this._valueString != null) {
            this._handler.parsedTrailer(this._field != null ? this._field : new HttpField(this._header, this._headerString, this._valueString));
        }
        this._valueString = null;
        this._headerString = null;
        this._header = null;
        this._field = null;
    }

    private long convertContentLength(String valueString) {
        if (valueString == null || valueString.length() == 0) {
            throw new BadMessageException("Invalid Content-Length Value", new NumberFormatException());
        }
        long value = 0L;
        int length = valueString.length();
        for (int i = 0; i < length; ++i) {
            char c = valueString.charAt(i);
            if (c < '0' || c > '9') {
                throw new BadMessageException("Invalid Content-Length Value", new NumberFormatException());
            }
            value = Math.addExact(Math.multiplyExact(value, 10), (long)(c - 48));
        }
        return value;
    }

    protected boolean parseFields(ByteBuffer buffer) {
        HttpTokens.Token t;
        block37: while ((this._state == State.HEADER || this._state == State.TRAILER) && buffer.hasRemaining() && (t = this.next(buffer)) != null) {
            if (this._maxHeaderBytes > 0 && ++this._headerBytes > this._maxHeaderBytes) {
                boolean header = this._state == State.HEADER;
                LOG.warn("{} is too large {}>{}", new Object[]{header ? "Header" : "Trailer", this._headerBytes, this._maxHeaderBytes});
                throw new BadMessageException(header ? 431 : 413);
            }
            switch (this._fieldState) {
                case FIELD: {
                    switch (t.getType()) {
                        case HTAB: 
                        case SPACE: 
                        case COLON: {
                            this.checkViolation(HttpCompliance.Violation.MULTILINE_FIELD_VALUE);
                            if (StringUtil.isEmpty((String)this._valueString)) {
                                this._string.setLength(0);
                                this._length = 0;
                            } else {
                                this.setString(this._valueString);
                                this._string.append(' ');
                                ++this._length;
                                this._valueString = null;
                            }
                            this.setState(FieldState.VALUE);
                            continue block37;
                        }
                        case LF: {
                            if (this._state == State.HEADER) {
                                this.parsedHeader();
                            } else {
                                this.parsedTrailer();
                            }
                            this._contentPosition = 0L;
                            if (this._state == State.TRAILER) {
                                this.setState(State.END);
                                return this._handler.messageComplete();
                            }
                            if (this._hasTransferEncoding && this._endOfContent != HttpTokens.EndOfContent.CHUNKED_CONTENT && (this._responseHandler == null || this._endOfContent != HttpTokens.EndOfContent.EOF_CONTENT)) {
                                throw new BadMessageException(400, "Bad Transfer-Encoding, chunked not last");
                            }
                            if (this._parsedHost == null && this._version == HttpVersion.HTTP_1_1 && this._requestHandler != null) {
                                throw new BadMessageException(400, "No Host");
                            }
                            if (this._responseHandler != null && (this._responseStatus == 304 || this._responseStatus == 204 || this._responseStatus < 200)) {
                                this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                            } else if (this._endOfContent == HttpTokens.EndOfContent.UNKNOWN_CONTENT) {
                                this._endOfContent = this._responseStatus == 0 || this._responseStatus == 304 || this._responseStatus == 204 || this._responseStatus < 200 ? HttpTokens.EndOfContent.NO_CONTENT : HttpTokens.EndOfContent.EOF_CONTENT;
                            }
                            switch (this._endOfContent) {
                                case EOF_CONTENT: {
                                    this.setState(State.EOF_CONTENT);
                                    boolean handle = this._handler.headerComplete();
                                    this._headerComplete = true;
                                    return handle;
                                }
                                case CHUNKED_CONTENT: {
                                    this.setState(State.CHUNKED_CONTENT);
                                    boolean handle = this._handler.headerComplete();
                                    this._headerComplete = true;
                                    return handle;
                                }
                            }
                            this.setState(State.CONTENT);
                            boolean handle = this._handler.headerComplete();
                            this._headerComplete = true;
                            return handle;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: {
                            if (this._state == State.HEADER) {
                                this.parsedHeader();
                            } else {
                                this.parsedTrailer();
                            }
                            if (buffer.hasRemaining()) {
                                HttpField cachedField = this._fieldCache.getBest(buffer, -1, buffer.remaining());
                                if (cachedField == null) {
                                    cachedField = (HttpField)CACHE.getBest(buffer, -1, buffer.remaining());
                                }
                                if (cachedField != null) {
                                    String ev;
                                    String en;
                                    String n = cachedField.getName();
                                    String v = cachedField.getValue();
                                    if (HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode) && !n.equals(en = BufferUtil.toString((ByteBuffer)buffer, (int)(buffer.position() - 1), (int)n.length(), (Charset)StandardCharsets.US_ASCII))) {
                                        this.reportComplianceViolation(HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME, en);
                                        n = en;
                                        cachedField = new HttpField(cachedField.getHeader(), n, v);
                                    }
                                    if (v != null && this.isHeaderCacheCaseSensitive() && !v.equals(ev = BufferUtil.toString((ByteBuffer)buffer, (int)(buffer.position() + n.length() + 1), (int)v.length(), (Charset)StandardCharsets.ISO_8859_1))) {
                                        v = ev;
                                        cachedField = new HttpField(cachedField.getHeader(), n, v);
                                    }
                                    this._header = cachedField.getHeader();
                                    this._headerString = n;
                                    if (v == null) {
                                        this.setState(FieldState.VALUE);
                                        this._string.setLength(0);
                                        this._length = 0;
                                        buffer.position(buffer.position() + n.length() + 1);
                                        continue block37;
                                    }
                                    int pos = buffer.position() + n.length() + v.length() + 1;
                                    byte peek = buffer.get(pos);
                                    if (peek == 13 || peek == 10) {
                                        this._field = cachedField;
                                        this._valueString = v;
                                        this.setState(FieldState.IN_VALUE);
                                        if (peek == 13) {
                                            this._cr = true;
                                            buffer.position(pos + 1);
                                            continue block37;
                                        }
                                        buffer.position(pos);
                                        continue block37;
                                    }
                                    this.setState(FieldState.IN_VALUE);
                                    this.setString(v);
                                    buffer.position(pos);
                                    continue block37;
                                }
                            }
                            this.setState(FieldState.IN_NAME);
                            this._string.setLength(0);
                            this._string.append(t.getChar());
                            this._length = 1;
                            continue block37;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case IN_NAME: {
                    switch (t.getType()) {
                        case HTAB: 
                        case SPACE: {
                            if (HttpCompliance.Violation.WHITESPACE_AFTER_FIELD_NAME.isAllowedBy(this._complianceMode)) {
                                this._headerString = this.takeString();
                                this.reportComplianceViolation(HttpCompliance.Violation.WHITESPACE_AFTER_FIELD_NAME, "Space after " + this._headerString);
                                this._header = (HttpHeader)((Object)HttpHeader.CACHE.get(this._headerString));
                                this._length = -1;
                                this.setState(FieldState.WS_AFTER_NAME);
                                continue block37;
                            }
                            throw new IllegalCharacterException(this._state, t, buffer);
                        }
                        case COLON: {
                            this._headerString = this.takeString();
                            this._header = (HttpHeader)((Object)HttpHeader.CACHE.get(this._headerString));
                            this._length = -1;
                            this.setState(FieldState.VALUE);
                            continue block37;
                        }
                        case LF: {
                            this._headerString = this.takeString();
                            this._header = (HttpHeader)((Object)HttpHeader.CACHE.get(this._headerString));
                            this._string.setLength(0);
                            this._valueString = "";
                            this._length = -1;
                            if (HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME.isAllowedBy(this._complianceMode)) {
                                this.reportComplianceViolation(HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME, "Field " + this._headerString);
                                this.setState(FieldState.FIELD);
                                continue block37;
                            }
                            throw new IllegalCharacterException(this._state, t, buffer);
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: {
                            this._string.append(t.getChar());
                            this._length = this._string.length();
                            continue block37;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case WS_AFTER_NAME: {
                    switch (t.getType()) {
                        case HTAB: 
                        case SPACE: {
                            continue block37;
                        }
                        case COLON: {
                            this.setState(FieldState.VALUE);
                            continue block37;
                        }
                        case LF: {
                            if (HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME.isAllowedBy(this._complianceMode)) {
                                this.reportComplianceViolation(HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME, "Field " + this._headerString);
                                this.setState(FieldState.FIELD);
                                continue block37;
                            }
                            throw new IllegalCharacterException(this._state, t, buffer);
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case VALUE: {
                    switch (t.getType()) {
                        case LF: {
                            this._string.setLength(0);
                            this._valueString = "";
                            this._length = -1;
                            this.setState(FieldState.FIELD);
                            continue block37;
                        }
                        case HTAB: 
                        case SPACE: {
                            continue block37;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case OTEXT: 
                        case COLON: {
                            this._string.append(t.getChar());
                            this._length = this._string.length();
                            this.setState(FieldState.IN_VALUE);
                            continue block37;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case IN_VALUE: {
                    switch (t.getType()) {
                        case LF: {
                            if (this._length > 0) {
                                this._valueString = this.takeString();
                                this._length = -1;
                            }
                            this.setState(FieldState.FIELD);
                            continue block37;
                        }
                        case HTAB: 
                        case SPACE: {
                            this._string.append(t.getChar());
                            continue block37;
                        }
                        case ALPHA: 
                        case DIGIT: 
                        case TCHAR: 
                        case VCHAR: 
                        case OTEXT: 
                        case COLON: {
                            this._string.append(t.getChar());
                            this._length = this._string.length();
                            continue block37;
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
            }
            throw new IllegalStateException(this._state.toString());
        }
        return false;
    }

    public boolean parseNext(ByteBuffer buffer) {
        if (this.debugEnabled) {
            LOG.debug("parseNext s={} {}", (Object)this._state, (Object)BufferUtil.toDetailString((ByteBuffer)buffer));
        }
        try {
            if (this._state == State.START) {
                this._version = null;
                this._method = null;
                this._methodString = null;
                this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
                this._header = null;
                if (buffer.hasRemaining()) {
                    this._beginNanoTime = NanoTime.now();
                }
                this.quickStart(buffer);
            }
            if (this._state.ordinal() < State.HEADER.ordinal() && this.parseLine(buffer)) {
                return true;
            }
            if (this._state == State.HEADER && this.parseFields(buffer)) {
                return true;
            }
            if (this._state.ordinal() >= State.CONTENT.ordinal() && this._state.ordinal() < State.TRAILER.ordinal()) {
                if (this._responseStatus > 0 && this._headResponse) {
                    if (this._state != State.CONTENT_END) {
                        this.setState(State.CONTENT_END);
                        return this.handleContentMessage();
                    }
                    this.setState(State.END);
                    return this._handler.messageComplete();
                }
                if (this.parseContent(buffer)) {
                    return true;
                }
            }
            if (this._state == State.TRAILER && this.parseFields(buffer)) {
                return true;
            }
            if (this._state == State.END) {
                byte b;
                int whiteSpace = 0;
                while (buffer.remaining() > 0 && ((b = buffer.get(buffer.position())) == 13 || b == 10)) {
                    buffer.get();
                    ++whiteSpace;
                }
                if (this.debugEnabled && whiteSpace > 0) {
                    LOG.debug("Discarded {} CR or LF characters", (Object)whiteSpace);
                }
            } else if (this.isTerminated()) {
                BufferUtil.clear((ByteBuffer)buffer);
            }
            if (this.isAtEOF() && !buffer.hasRemaining()) {
                switch (this._state) {
                    case CLOSED: {
                        break;
                    }
                    case END: 
                    case CLOSE: {
                        this.setState(State.CLOSED);
                        break;
                    }
                    case EOF_CONTENT: 
                    case TRAILER: {
                        if (this._fieldState == FieldState.FIELD) {
                            this.setState(State.CONTENT_END);
                            boolean handle = this.handleContentMessage();
                            if (handle && this._state == State.CONTENT_END) {
                                return true;
                            }
                            this.setState(State.CLOSED);
                            return handle;
                        }
                        this.setState(State.CLOSED);
                        this._handler.earlyEOF();
                        break;
                    }
                    case START: 
                    case CONTENT: 
                    case CHUNKED_CONTENT: 
                    case CHUNK_SIZE: 
                    case CHUNK_PARAMS: 
                    case CHUNK: {
                        this.setState(State.CLOSED);
                        this._handler.earlyEOF();
                        break;
                    }
                    default: {
                        if (this.debugEnabled) {
                            LOG.debug("{} EOF in {}", (Object)this, (Object)this._state);
                        }
                        this.setState(State.CLOSED);
                        this._handler.badMessage(new BadMessageException(400));
                    }
                }
            }
        }
        catch (BadMessageException x) {
            BufferUtil.clear((ByteBuffer)buffer);
            this.badMessage(x);
        }
        catch (Throwable x) {
            BufferUtil.clear((ByteBuffer)buffer);
            this.badMessage(new BadMessageException(400, this._requestHandler != null ? "Bad Request" : "Bad Response", x));
        }
        return false;
    }

    protected void badMessage(BadMessageException x) {
        if (this.debugEnabled) {
            LOG.debug("Parse exception: {} for {}", new Object[]{this, this._handler, x});
        }
        this.setState(State.CLOSE);
        if (this._headerComplete) {
            this._handler.earlyEOF();
        } else {
            this._handler.badMessage(x);
        }
    }

    protected boolean parseContent(ByteBuffer buffer) {
        long content;
        int remaining = buffer.remaining();
        if (remaining == 0) {
            switch (this._state) {
                case CONTENT: {
                    content = this._contentLength - this._contentPosition;
                    if (this._endOfContent != HttpTokens.EndOfContent.NO_CONTENT && content != 0L) break;
                    this.setState(State.CONTENT_END);
                    return this.handleContentMessage();
                }
                case CONTENT_END: {
                    this.setState(this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT ? State.CLOSED : State.END);
                    return this._handler.messageComplete();
                }
                default: {
                    return false;
                }
            }
        }
        while (this._state.ordinal() < State.TRAILER.ordinal() && remaining > 0) {
            block4 : switch (this._state) {
                case EOF_CONTENT: {
                    this._contentChunk = buffer.asReadOnlyBuffer();
                    this._contentPosition += (long)remaining;
                    buffer.position(buffer.position() + remaining);
                    if (!this._handler.content(this._contentChunk)) break;
                    return true;
                }
                case CONTENT: {
                    content = this._contentLength - this._contentPosition;
                    if (this._endOfContent == HttpTokens.EndOfContent.NO_CONTENT || content == 0L) {
                        this.setState(State.CONTENT_END);
                        return this.handleContentMessage();
                    }
                    this._contentChunk = buffer.asReadOnlyBuffer();
                    if (this._contentLength > -1L && (long)remaining > content) {
                        this._contentChunk.limit(this._contentChunk.position() + (int)content);
                    }
                    this._contentPosition += (long)this._contentChunk.remaining();
                    buffer.position(buffer.position() + this._contentChunk.remaining());
                    if (this._handler.content(this._contentChunk)) {
                        return true;
                    }
                    if (this._contentPosition != this._contentLength) break;
                    this.setState(State.CONTENT_END);
                    return this.handleContentMessage();
                }
                case CHUNKED_CONTENT: {
                    HttpTokens.Token t = this.next(buffer);
                    if (t == null) break;
                    switch (t.getType()) {
                        case LF: {
                            break block4;
                        }
                        case DIGIT: {
                            this._chunkLength = t.getHexDigit();
                            this._chunkPosition = 0;
                            this.setState(State.CHUNK_SIZE);
                            break block4;
                        }
                        case ALPHA: {
                            if (t.isHexDigit()) {
                                this._chunkLength = t.getHexDigit();
                                this._chunkPosition = 0;
                                this.setState(State.CHUNK_SIZE);
                                break block4;
                            }
                            throw new IllegalCharacterException(this._state, t, buffer);
                        }
                    }
                    throw new IllegalCharacterException(this._state, t, buffer);
                }
                case CHUNK_SIZE: {
                    HttpTokens.Token t = this.next(buffer);
                    if (t == null) break;
                    switch (t.getType()) {
                        case LF: {
                            if (this._chunkLength == 0) {
                                this.setState(State.TRAILER);
                                if (!this._handler.contentComplete()) break block4;
                                return true;
                            }
                            this.setState(State.CHUNK);
                            break block4;
                        }
                        case SPACE: {
                            this.setState(State.CHUNK_PARAMS);
                            break block4;
                        }
                    }
                    if (t.isHexDigit()) {
                        if (this._chunkLength > 0x7FFFFEF) {
                            throw new BadMessageException(413);
                        }
                        this._chunkLength = this._chunkLength * 16 + t.getHexDigit();
                        break;
                    }
                    this.setState(State.CHUNK_PARAMS);
                    break;
                }
                case CHUNK_PARAMS: {
                    HttpTokens.Token t = this.next(buffer);
                    if (t == null) break;
                    switch (t.getType()) {
                        case LF: {
                            if (this._chunkLength == 0) {
                                this.setState(State.TRAILER);
                                if (!this._handler.contentComplete()) break block4;
                                return true;
                            }
                            this.setState(State.CHUNK);
                            break block4;
                        }
                    }
                    break;
                }
                case CHUNK: {
                    int chunk = this._chunkLength - this._chunkPosition;
                    if (chunk == 0) {
                        this.setState(State.CHUNKED_CONTENT);
                        break;
                    }
                    this._contentChunk = buffer.asReadOnlyBuffer();
                    if (remaining > chunk) {
                        this._contentChunk.limit(this._contentChunk.position() + chunk);
                    }
                    chunk = this._contentChunk.remaining();
                    this._contentPosition += (long)chunk;
                    this._chunkPosition += chunk;
                    buffer.position(buffer.position() + chunk);
                    if (!this._handler.content(this._contentChunk)) break;
                    return true;
                }
                case CONTENT_END: {
                    this.setState(this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT ? State.CLOSED : State.END);
                    return this._handler.messageComplete();
                }
            }
            remaining = buffer.remaining();
        }
        return false;
    }

    public boolean isAtEOF() {
        return this._eof;
    }

    public void atEOF() {
        if (this.debugEnabled) {
            LOG.debug("atEOF {}", (Object)this);
        }
        this._eof = true;
    }

    public void close() {
        if (this.debugEnabled) {
            LOG.debug("close {}", (Object)this);
        }
        this.setState(State.CLOSE);
    }

    public void reset() {
        if (this.debugEnabled) {
            LOG.debug("reset {}", (Object)this);
        }
        if (this._state == State.CLOSE || this._state == State.CLOSED) {
            return;
        }
        this.setState(State.START);
        this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
        this._contentLength = -1L;
        this._hasContentLength = false;
        this._hasTransferEncoding = false;
        this._contentPosition = 0L;
        this._responseStatus = 0;
        this._contentChunk = null;
        this._headerBytes = 0;
        this._parsedHost = null;
        this._headerComplete = false;
    }

    public void servletUpgrade() {
        this.setState(State.CONTENT);
        this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
        this._contentLength = -1L;
    }

    protected void setState(State state) {
        if (this.debugEnabled) {
            LOG.debug("{} --> {}", (Object)this._state, (Object)state);
        }
        this._state = state;
    }

    protected void setState(FieldState state) {
        if (this.debugEnabled) {
            LOG.debug("{}:{} --> {}", new Object[]{this._state, this._field != null ? this._field : (this._headerString != null ? this._headerString : this._string), state});
        }
        this._fieldState = state;
    }

    public Index<HttpField> getFieldCache() {
        this._fieldCache.prepare();
        return this._fieldCache.getCache();
    }

    public String toString() {
        return String.format("%s{s=%s,%d of %d}", new Object[]{this.getClass().getSimpleName(), this._state, this.getContentRead(), this.getContentLength()});
    }

    public static interface RequestHandler
    extends HttpHandler {
        public void startRequest(String var1, String var2, HttpVersion var3);
    }

    public static interface ResponseHandler
    extends HttpHandler {
        public void startResponse(HttpVersion var1, int var2, String var3);
    }

    private static class FieldCache {
        private int _size = 1024;
        private Index.Mutable<HttpField> _cache;
        private List<HttpField> _cacheableFields;
        private boolean _caseSensitive;

        private FieldCache() {
        }

        public int getCapacity() {
            return this._size;
        }

        public void setCapacity(int size) {
            this._size = size;
            this._cache = this._size <= 0 ? NO_CACHE : null;
        }

        public boolean isCaseSensitive() {
            return this._caseSensitive;
        }

        public void setCaseSensitive(boolean caseSensitive) {
            this._caseSensitive = caseSensitive;
        }

        public boolean isEnabled() {
            return this._cache != NO_CACHE;
        }

        public Index<HttpField> getCache() {
            return this._cache;
        }

        public HttpField getBest(ByteBuffer buffer, int i, int remaining) {
            Index.Mutable<HttpField> cache = this._cache;
            return cache == null ? null : (HttpField)this._cache.getBest(buffer, i, remaining);
        }

        public void add(HttpField field) {
            if (this._cache == null) {
                if (this._cacheableFields == null) {
                    this._cacheableFields = new ArrayList<HttpField>();
                }
                this._cacheableFields.add(field);
            } else if (!this._cache.put((Object)field)) {
                this._cache.clear();
                this._cache.put((Object)field);
            }
        }

        public boolean cacheable(HttpHeader header, String valueString) {
            return this.isEnabled() && header != null && valueString.length() <= this._size;
        }

        private void prepare() {
            if (this._cache == null && this._cacheableFields != null) {
                this._cache = Index.buildMutableVisibleAsciiAlphabet((boolean)this._caseSensitive, (int)this._size);
                for (HttpField f : this._cacheableFields) {
                    if (!this._cache.put((Object)f)) break;
                }
                this._cacheableFields.clear();
                this._cacheableFields = null;
            }
        }
    }

    public static enum State {
        START,
        METHOD,
        RESPONSE_VERSION,
        SPACE1,
        STATUS,
        URI,
        SPACE2,
        REQUEST_VERSION,
        REASON,
        PROXY,
        HEADER,
        CONTENT,
        EOF_CONTENT,
        CHUNKED_CONTENT,
        CHUNK_SIZE,
        CHUNK_PARAMS,
        CHUNK,
        CONTENT_END,
        TRAILER,
        END,
        CLOSE,
        CLOSED;

    }

    public static enum FieldState {
        FIELD,
        IN_NAME,
        VALUE,
        IN_VALUE,
        WS_AFTER_NAME;

    }

    public static interface HttpHandler {
        public boolean content(ByteBuffer var1);

        public boolean headerComplete();

        public boolean contentComplete();

        public boolean messageComplete();

        public void parsedHeader(HttpField var1);

        default public void parsedTrailer(HttpField field) {
        }

        public void earlyEOF();

        default public void badMessage(BadMessageException failure) {
        }
    }

    private static class IllegalCharacterException
    extends BadMessageException {
        private IllegalCharacterException(State state, HttpTokens.Token token, ByteBuffer buffer) {
            super(400, String.format("Illegal character %s", token));
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Illegal character %s in state=%s for buffer %s", new Object[]{token, state, BufferUtil.toDetailString((ByteBuffer)buffer)}));
            }
        }
    }
}

