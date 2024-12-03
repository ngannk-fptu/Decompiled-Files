/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.handler.codec.Headers
 *  io.netty.handler.codec.UnsupportedValueConverter
 *  io.netty.handler.codec.ValueConverter
 *  io.netty.handler.codec.http.DefaultFullHttpRequest
 *  io.netty.handler.codec.http.DefaultFullHttpResponse
 *  io.netty.handler.codec.http.DefaultHttpRequest
 *  io.netty.handler.codec.http.DefaultHttpResponse
 *  io.netty.handler.codec.http.FullHttpMessage
 *  io.netty.handler.codec.http.FullHttpRequest
 *  io.netty.handler.codec.http.FullHttpResponse
 *  io.netty.handler.codec.http.HttpHeaderNames
 *  io.netty.handler.codec.http.HttpHeaderValues
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.HttpMethod
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.HttpResponse
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpScheme
 *  io.netty.handler.codec.http.HttpUtil
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.util.AsciiString
 *  io.netty.util.ByteProcessor
 *  io.netty.util.internal.InternalThreadLocalMap
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.StringUtil
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.CharSequenceMap;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.EmptyHttp2Headers;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class HttpConversionUtil {
    private static final CharSequenceMap<AsciiString> HTTP_TO_HTTP2_HEADER_BLACKLIST = new CharSequenceMap();
    public static final HttpMethod OUT_OF_MESSAGE_SEQUENCE_METHOD;
    public static final String OUT_OF_MESSAGE_SEQUENCE_PATH = "";
    public static final HttpResponseStatus OUT_OF_MESSAGE_SEQUENCE_RETURN_CODE;
    private static final AsciiString EMPTY_REQUEST_PATH;

    private HttpConversionUtil() {
    }

    public static HttpResponseStatus parseStatus(CharSequence status) throws Http2Exception {
        HttpResponseStatus result;
        try {
            result = HttpResponseStatus.parseLine((CharSequence)status);
            if (result == HttpResponseStatus.SWITCHING_PROTOCOLS) {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Invalid HTTP/2 status code '%d'", result.code());
            }
        }
        catch (Http2Exception e) {
            throw e;
        }
        catch (Throwable t) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, t, "Unrecognized HTTP status code '%s' encountered in translation to HTTP/1.x", status);
        }
        return result;
    }

    public static FullHttpResponse toFullHttpResponse(int streamId, Http2Headers http2Headers, ByteBufAllocator alloc, boolean validateHttpHeaders) throws Http2Exception {
        return HttpConversionUtil.toFullHttpResponse(streamId, http2Headers, alloc.buffer(), validateHttpHeaders);
    }

    public static FullHttpResponse toFullHttpResponse(int streamId, Http2Headers http2Headers, ByteBuf content, boolean validateHttpHeaders) throws Http2Exception {
        HttpResponseStatus status = HttpConversionUtil.parseStatus(http2Headers.status());
        DefaultFullHttpResponse msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content, validateHttpHeaders);
        try {
            HttpConversionUtil.addHttp2ToHttpHeaders(streamId, http2Headers, (FullHttpMessage)msg, false);
        }
        catch (Http2Exception e) {
            msg.release();
            throw e;
        }
        catch (Throwable t) {
            msg.release();
            throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, t, "HTTP/2 to HTTP/1.x headers conversion error", new Object[0]);
        }
        return msg;
    }

    public static FullHttpRequest toFullHttpRequest(int streamId, Http2Headers http2Headers, ByteBufAllocator alloc, boolean validateHttpHeaders) throws Http2Exception {
        return HttpConversionUtil.toFullHttpRequest(streamId, http2Headers, alloc.buffer(), validateHttpHeaders);
    }

    private static String extractPath(CharSequence method, Http2Headers headers) {
        if (HttpMethod.CONNECT.asciiName().contentEqualsIgnoreCase(method)) {
            return ((CharSequence)ObjectUtil.checkNotNull((Object)headers.authority(), (String)"authority header cannot be null in the conversion to HTTP/1.x")).toString();
        }
        return ((CharSequence)ObjectUtil.checkNotNull((Object)headers.path(), (String)"path header cannot be null in conversion to HTTP/1.x")).toString();
    }

    public static FullHttpRequest toFullHttpRequest(int streamId, Http2Headers http2Headers, ByteBuf content, boolean validateHttpHeaders) throws Http2Exception {
        CharSequence method = (CharSequence)ObjectUtil.checkNotNull((Object)http2Headers.method(), (String)"method header cannot be null in conversion to HTTP/1.x");
        String path = HttpConversionUtil.extractPath(method, http2Headers);
        DefaultFullHttpRequest msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf((String)method.toString()), path.toString(), content, validateHttpHeaders);
        try {
            HttpConversionUtil.addHttp2ToHttpHeaders(streamId, http2Headers, (FullHttpMessage)msg, false);
        }
        catch (Http2Exception e) {
            msg.release();
            throw e;
        }
        catch (Throwable t) {
            msg.release();
            throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, t, "HTTP/2 to HTTP/1.x headers conversion error", new Object[0]);
        }
        return msg;
    }

    public static HttpRequest toHttpRequest(int streamId, Http2Headers http2Headers, boolean validateHttpHeaders) throws Http2Exception {
        CharSequence method = (CharSequence)ObjectUtil.checkNotNull((Object)http2Headers.method(), (String)"method header cannot be null in conversion to HTTP/1.x");
        String path = HttpConversionUtil.extractPath(method, http2Headers);
        DefaultHttpRequest msg = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf((String)method.toString()), path.toString(), validateHttpHeaders);
        try {
            HttpConversionUtil.addHttp2ToHttpHeaders(streamId, http2Headers, msg.headers(), msg.protocolVersion(), false, true);
        }
        catch (Http2Exception e) {
            throw e;
        }
        catch (Throwable t) {
            throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, t, "HTTP/2 to HTTP/1.x headers conversion error", new Object[0]);
        }
        return msg;
    }

    public static HttpResponse toHttpResponse(int streamId, Http2Headers http2Headers, boolean validateHttpHeaders) throws Http2Exception {
        HttpResponseStatus status = HttpConversionUtil.parseStatus(http2Headers.status());
        DefaultHttpResponse msg = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status, validateHttpHeaders);
        try {
            HttpConversionUtil.addHttp2ToHttpHeaders(streamId, http2Headers, msg.headers(), msg.protocolVersion(), false, false);
        }
        catch (Http2Exception e) {
            throw e;
        }
        catch (Throwable t) {
            throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, t, "HTTP/2 to HTTP/1.x headers conversion error", new Object[0]);
        }
        return msg;
    }

    public static void addHttp2ToHttpHeaders(int streamId, Http2Headers inputHeaders, FullHttpMessage destinationMessage, boolean addToTrailer) throws Http2Exception {
        HttpConversionUtil.addHttp2ToHttpHeaders(streamId, inputHeaders, addToTrailer ? destinationMessage.trailingHeaders() : destinationMessage.headers(), destinationMessage.protocolVersion(), addToTrailer, destinationMessage instanceof HttpRequest);
    }

    public static void addHttp2ToHttpHeaders(int streamId, Http2Headers inputHeaders, HttpHeaders outputHeaders, HttpVersion httpVersion, boolean isTrailer, boolean isRequest) throws Http2Exception {
        Http2ToHttpHeaderTranslator translator = new Http2ToHttpHeaderTranslator(streamId, outputHeaders, isRequest);
        try {
            translator.translateHeaders((Iterable<Map.Entry<CharSequence, CharSequence>>)((Object)inputHeaders));
        }
        catch (Http2Exception ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw Http2Exception.streamError(streamId, Http2Error.PROTOCOL_ERROR, t, "HTTP/2 to HTTP/1.x headers conversion error", new Object[0]);
        }
        outputHeaders.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
        outputHeaders.remove((CharSequence)HttpHeaderNames.TRAILER);
        if (!isTrailer) {
            outputHeaders.setInt((CharSequence)ExtensionHeaderNames.STREAM_ID.text(), streamId);
            HttpUtil.setKeepAlive((HttpHeaders)outputHeaders, (HttpVersion)httpVersion, (boolean)true);
        }
    }

    public static Http2Headers toHttp2Headers(HttpMessage in, boolean validateHeaders) {
        HttpHeaders inHeaders = in.headers();
        DefaultHttp2Headers out = new DefaultHttp2Headers(validateHeaders, inHeaders.size());
        if (in instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)in;
            String host = inHeaders.getAsString((CharSequence)HttpHeaderNames.HOST);
            if (HttpUtil.isOriginForm((String)request.uri()) || HttpUtil.isAsteriskForm((String)request.uri())) {
                out.path((CharSequence)new AsciiString((CharSequence)request.uri()));
                HttpConversionUtil.setHttp2Scheme(inHeaders, out);
            } else {
                URI requestTargetUri = URI.create(request.uri());
                out.path((CharSequence)HttpConversionUtil.toHttp2Path(requestTargetUri));
                host = StringUtil.isNullOrEmpty((String)host) ? requestTargetUri.getAuthority() : host;
                HttpConversionUtil.setHttp2Scheme(inHeaders, requestTargetUri, out);
            }
            HttpConversionUtil.setHttp2Authority(host, out);
            out.method((CharSequence)request.method().asciiName());
        } else if (in instanceof HttpResponse) {
            HttpResponse response = (HttpResponse)in;
            out.status((CharSequence)response.status().codeAsText());
        }
        HttpConversionUtil.toHttp2Headers(inHeaders, out);
        return out;
    }

    public static Http2Headers toHttp2Headers(HttpHeaders inHeaders, boolean validateHeaders) {
        if (inHeaders.isEmpty()) {
            return EmptyHttp2Headers.INSTANCE;
        }
        DefaultHttp2Headers out = new DefaultHttp2Headers(validateHeaders, inHeaders.size());
        HttpConversionUtil.toHttp2Headers(inHeaders, out);
        return out;
    }

    private static CharSequenceMap<AsciiString> toLowercaseMap(Iterator<? extends CharSequence> valuesIter, int arraySizeHint) {
        UnsupportedValueConverter valueConverter = UnsupportedValueConverter.instance();
        CharSequenceMap<AsciiString> result = new CharSequenceMap<AsciiString>(true, (ValueConverter<AsciiString>)valueConverter, arraySizeHint);
        while (valuesIter.hasNext()) {
            AsciiString lowerCased = AsciiString.of((CharSequence)valuesIter.next()).toLowerCase();
            try {
                int index = lowerCased.forEachByte(ByteProcessor.FIND_COMMA);
                if (index != -1) {
                    int start = 0;
                    do {
                        result.add(lowerCased.subSequence(start, index, false).trim(), AsciiString.EMPTY_STRING);
                    } while ((start = index + 1) < lowerCased.length() && (index = lowerCased.forEachByte(start, lowerCased.length() - start, ByteProcessor.FIND_COMMA)) != -1);
                    result.add(lowerCased.subSequence(start, lowerCased.length(), false).trim(), AsciiString.EMPTY_STRING);
                    continue;
                }
                result.add(lowerCased.trim(), AsciiString.EMPTY_STRING);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return result;
    }

    private static void toHttp2HeadersFilterTE(Map.Entry<CharSequence, CharSequence> entry, Http2Headers out) {
        block2: {
            block1: {
                if (AsciiString.indexOf((CharSequence)entry.getValue(), (char)',', (int)0) != -1) break block1;
                if (!AsciiString.contentEqualsIgnoreCase((CharSequence)AsciiString.trim((CharSequence)entry.getValue()), (CharSequence)HttpHeaderValues.TRAILERS)) break block2;
                out.add(HttpHeaderNames.TE, HttpHeaderValues.TRAILERS);
                break block2;
            }
            List teValues = StringUtil.unescapeCsvFields((CharSequence)entry.getValue());
            for (CharSequence teValue : teValues) {
                if (!AsciiString.contentEqualsIgnoreCase((CharSequence)AsciiString.trim((CharSequence)teValue), (CharSequence)HttpHeaderValues.TRAILERS)) continue;
                out.add(HttpHeaderNames.TE, HttpHeaderValues.TRAILERS);
                break;
            }
        }
    }

    public static void toHttp2Headers(HttpHeaders inHeaders, Http2Headers out) {
        Iterator iter = inHeaders.iteratorCharSequence();
        CharSequenceMap<AsciiString> connectionBlacklist = HttpConversionUtil.toLowercaseMap(inHeaders.valueCharSequenceIterator((CharSequence)HttpHeaderNames.CONNECTION), 8);
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            AsciiString aName = AsciiString.of((CharSequence)((CharSequence)entry.getKey())).toLowerCase();
            if (HTTP_TO_HTTP2_HEADER_BLACKLIST.contains(aName) || connectionBlacklist.contains(aName)) continue;
            if (aName.contentEqualsIgnoreCase((CharSequence)HttpHeaderNames.TE)) {
                HttpConversionUtil.toHttp2HeadersFilterTE(entry, out);
                continue;
            }
            if (aName.contentEqualsIgnoreCase((CharSequence)HttpHeaderNames.COOKIE)) {
                AsciiString value = AsciiString.of((CharSequence)((CharSequence)entry.getValue()));
                try {
                    int index = value.forEachByte(ByteProcessor.FIND_SEMI_COLON);
                    if (index != -1) {
                        int start = 0;
                        do {
                            out.add(HttpHeaderNames.COOKIE, value.subSequence(start, index, false));
                        } while ((start = index + 2) < value.length() && (index = value.forEachByte(start, value.length() - start, ByteProcessor.FIND_SEMI_COLON)) != -1);
                        if (start >= value.length()) {
                            throw new IllegalArgumentException("cookie value is of unexpected format: " + value);
                        }
                        out.add(HttpHeaderNames.COOKIE, value.subSequence(start, value.length(), false));
                        continue;
                    }
                    out.add(HttpHeaderNames.COOKIE, value);
                    continue;
                }
                catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            out.add(aName, entry.getValue());
        }
    }

    private static AsciiString toHttp2Path(URI uri) {
        String path;
        StringBuilder pathBuilder = new StringBuilder(StringUtil.length((String)uri.getRawPath()) + StringUtil.length((String)uri.getRawQuery()) + StringUtil.length((String)uri.getRawFragment()) + 2);
        if (!StringUtil.isNullOrEmpty((String)uri.getRawPath())) {
            pathBuilder.append(uri.getRawPath());
        }
        if (!StringUtil.isNullOrEmpty((String)uri.getRawQuery())) {
            pathBuilder.append('?');
            pathBuilder.append(uri.getRawQuery());
        }
        if (!StringUtil.isNullOrEmpty((String)uri.getRawFragment())) {
            pathBuilder.append('#');
            pathBuilder.append(uri.getRawFragment());
        }
        return (path = pathBuilder.toString()).isEmpty() ? EMPTY_REQUEST_PATH : new AsciiString((CharSequence)path);
    }

    static void setHttp2Authority(String authority, Http2Headers out) {
        if (authority != null) {
            if (authority.isEmpty()) {
                out.authority((CharSequence)AsciiString.EMPTY_STRING);
            } else {
                int start = authority.indexOf(64) + 1;
                int length = authority.length() - start;
                if (length == 0) {
                    throw new IllegalArgumentException("authority: " + authority);
                }
                out.authority((CharSequence)new AsciiString((CharSequence)authority, start, length));
            }
        }
    }

    private static void setHttp2Scheme(HttpHeaders in, Http2Headers out) {
        HttpConversionUtil.setHttp2Scheme(in, URI.create(OUT_OF_MESSAGE_SEQUENCE_PATH), out);
    }

    private static void setHttp2Scheme(HttpHeaders in, URI uri, Http2Headers out) {
        String value = uri.getScheme();
        if (!StringUtil.isNullOrEmpty((String)value)) {
            out.scheme((CharSequence)new AsciiString((CharSequence)value));
            return;
        }
        String cValue = in.get((CharSequence)ExtensionHeaderNames.SCHEME.text());
        if (cValue != null) {
            out.scheme((CharSequence)AsciiString.of((CharSequence)cValue));
            return;
        }
        if (uri.getPort() == HttpScheme.HTTPS.port()) {
            out.scheme((CharSequence)HttpScheme.HTTPS.name());
        } else if (uri.getPort() == HttpScheme.HTTP.port()) {
            out.scheme((CharSequence)HttpScheme.HTTP.name());
        } else {
            throw new IllegalArgumentException(":scheme must be specified. see https://tools.ietf.org/html/rfc7540#section-8.1.2.3");
        }
    }

    static {
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.CONNECTION, AsciiString.EMPTY_STRING);
        AsciiString keepAlive = HttpHeaderNames.KEEP_ALIVE;
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(keepAlive, AsciiString.EMPTY_STRING);
        AsciiString proxyConnection = HttpHeaderNames.PROXY_CONNECTION;
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(proxyConnection, AsciiString.EMPTY_STRING);
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.TRANSFER_ENCODING, AsciiString.EMPTY_STRING);
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.HOST, AsciiString.EMPTY_STRING);
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.UPGRADE, AsciiString.EMPTY_STRING);
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(ExtensionHeaderNames.STREAM_ID.text(), AsciiString.EMPTY_STRING);
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(ExtensionHeaderNames.SCHEME.text(), AsciiString.EMPTY_STRING);
        HTTP_TO_HTTP2_HEADER_BLACKLIST.add(ExtensionHeaderNames.PATH.text(), AsciiString.EMPTY_STRING);
        OUT_OF_MESSAGE_SEQUENCE_METHOD = HttpMethod.OPTIONS;
        OUT_OF_MESSAGE_SEQUENCE_RETURN_CODE = HttpResponseStatus.OK;
        EMPTY_REQUEST_PATH = AsciiString.cached((String)"/");
    }

    private static final class Http2ToHttpHeaderTranslator {
        private static final CharSequenceMap<AsciiString> REQUEST_HEADER_TRANSLATIONS = new CharSequenceMap();
        private static final CharSequenceMap<AsciiString> RESPONSE_HEADER_TRANSLATIONS = new CharSequenceMap();
        private final int streamId;
        private final HttpHeaders output;
        private final CharSequenceMap<AsciiString> translations;

        Http2ToHttpHeaderTranslator(int streamId, HttpHeaders output, boolean request) {
            this.streamId = streamId;
            this.output = output;
            this.translations = request ? REQUEST_HEADER_TRANSLATIONS : RESPONSE_HEADER_TRANSLATIONS;
        }

        void translateHeaders(Iterable<Map.Entry<CharSequence, CharSequence>> inputHeaders) throws Http2Exception {
            StringBuilder cookies = null;
            for (Map.Entry<CharSequence, CharSequence> entry : inputHeaders) {
                CharSequence name = entry.getKey();
                CharSequence value = entry.getValue();
                AsciiString translatedName = (AsciiString)this.translations.get(name);
                if (translatedName != null) {
                    this.output.add((CharSequence)translatedName, (Object)AsciiString.of((CharSequence)value));
                    continue;
                }
                if (Http2Headers.PseudoHeaderName.isPseudoHeader(name)) continue;
                if (name.length() == 0 || name.charAt(0) == ':') {
                    throw Http2Exception.streamError(this.streamId, Http2Error.PROTOCOL_ERROR, "Invalid HTTP/2 header '%s' encountered in translation to HTTP/1.x", name);
                }
                if (HttpHeaderNames.COOKIE.equals((Object)name)) {
                    if (cookies == null) {
                        cookies = InternalThreadLocalMap.get().stringBuilder();
                    } else if (cookies.length() > 0) {
                        cookies.append("; ");
                    }
                    cookies.append(value);
                    continue;
                }
                this.output.add(name, (Object)value);
            }
            if (cookies != null) {
                this.output.add((CharSequence)HttpHeaderNames.COOKIE, (Object)cookies.toString());
            }
        }

        static {
            RESPONSE_HEADER_TRANSLATIONS.add(Http2Headers.PseudoHeaderName.AUTHORITY.value(), HttpHeaderNames.HOST);
            RESPONSE_HEADER_TRANSLATIONS.add(Http2Headers.PseudoHeaderName.SCHEME.value(), ExtensionHeaderNames.SCHEME.text());
            REQUEST_HEADER_TRANSLATIONS.add((Headers)RESPONSE_HEADER_TRANSLATIONS);
            RESPONSE_HEADER_TRANSLATIONS.add(Http2Headers.PseudoHeaderName.PATH.value(), ExtensionHeaderNames.PATH.text());
        }
    }

    public static enum ExtensionHeaderNames {
        STREAM_ID("x-http2-stream-id"),
        SCHEME("x-http2-scheme"),
        PATH("x-http2-path"),
        STREAM_PROMISE_ID("x-http2-stream-promise-id"),
        STREAM_DEPENDENCY_ID("x-http2-stream-dependency-id"),
        STREAM_WEIGHT("x-http2-stream-weight");

        private final AsciiString text;

        private ExtensionHeaderNames(String text) {
            this.text = AsciiString.cached((String)text);
        }

        public AsciiString text() {
            return this.text;
        }
    }
}

