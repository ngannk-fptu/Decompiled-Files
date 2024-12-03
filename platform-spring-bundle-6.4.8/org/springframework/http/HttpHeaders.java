/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.ReadOnlyHttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class HttpHeaders
implements MultiValueMap<String, String>,
Serializable {
    private static final long serialVersionUID = -8578554704772377436L;
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCEPT_PATCH = "Accept-Patch";
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String AGE = "Age";
    public static final String ALLOW = "Allow";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String COOKIE = "Cookie";
    public static final String DATE = "Date";
    public static final String ETAG = "ETag";
    public static final String EXPECT = "Expect";
    public static final String EXPIRES = "Expires";
    public static final String FROM = "From";
    public static final String HOST = "Host";
    public static final String IF_MATCH = "If-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String IF_RANGE = "If-Range";
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String LINK = "Link";
    public static final String LOCATION = "Location";
    public static final String MAX_FORWARDS = "Max-Forwards";
    public static final String ORIGIN = "Origin";
    public static final String PRAGMA = "Pragma";
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String RANGE = "Range";
    public static final String REFERER = "Referer";
    public static final String RETRY_AFTER = "Retry-After";
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String SET_COOKIE2 = "Set-Cookie2";
    public static final String TE = "TE";
    public static final String TRAILER = "Trailer";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String UPGRADE = "Upgrade";
    public static final String USER_AGENT = "User-Agent";
    public static final String VARY = "Vary";
    public static final String VIA = "Via";
    public static final String WARNING = "Warning";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final HttpHeaders EMPTY = new ReadOnlyHttpHeaders(new LinkedMultiValueMap<String, String>());
    private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");
    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.ENGLISH);
    private static final ZoneId GMT = ZoneId.of("GMT");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(GMT);
    private static final DateTimeFormatter[] DATE_PARSERS = new DateTimeFormatter[]{DateTimeFormatter.RFC_1123_DATE_TIME, DateTimeFormatter.ofPattern("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.US).withZone(GMT)};
    final MultiValueMap<String, String> headers;

    public HttpHeaders() {
        this(CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH)));
    }

    public HttpHeaders(MultiValueMap<String, String> headers) {
        Assert.notNull(headers, "MultiValueMap must not be null");
        this.headers = headers;
    }

    public List<String> getOrEmpty(Object headerName) {
        List<String> values = this.get(headerName);
        return values != null ? values : Collections.emptyList();
    }

    public void setAccept(List<MediaType> acceptableMediaTypes) {
        this.set(ACCEPT, MediaType.toString(acceptableMediaTypes));
    }

    public List<MediaType> getAccept() {
        return MediaType.parseMediaTypes((List<String>)this.get(ACCEPT));
    }

    public void setAcceptLanguage(List<Locale.LanguageRange> languages) {
        Assert.notNull(languages, "LanguageRange List must not be null");
        DecimalFormat decimal = new DecimalFormat("0.0", DECIMAL_FORMAT_SYMBOLS);
        List<String> values = languages.stream().map(range -> range.getWeight() == 1.0 ? range.getRange() : range.getRange() + ";q=" + decimal.format(range.getWeight())).collect(Collectors.toList());
        this.set(ACCEPT_LANGUAGE, this.toCommaDelimitedString(values));
    }

    public List<Locale.LanguageRange> getAcceptLanguage() {
        String value = this.getFirst(ACCEPT_LANGUAGE);
        return StringUtils.hasText(value) ? Locale.LanguageRange.parse(value) : Collections.emptyList();
    }

    public void setAcceptLanguageAsLocales(List<Locale> locales) {
        this.setAcceptLanguage(locales.stream().map(locale -> new Locale.LanguageRange(locale.toLanguageTag())).collect(Collectors.toList()));
    }

    public List<Locale> getAcceptLanguageAsLocales() {
        List<Locale.LanguageRange> ranges = this.getAcceptLanguage();
        if (ranges.isEmpty()) {
            return Collections.emptyList();
        }
        return ranges.stream().map(range -> Locale.forLanguageTag(range.getRange())).filter(locale -> StringUtils.hasText(locale.getDisplayName())).collect(Collectors.toList());
    }

    public void setAcceptPatch(List<MediaType> mediaTypes) {
        this.set(ACCEPT_PATCH, MediaType.toString(mediaTypes));
    }

    public List<MediaType> getAcceptPatch() {
        return MediaType.parseMediaTypes((List<String>)this.get(ACCEPT_PATCH));
    }

    public void setAccessControlAllowCredentials(boolean allowCredentials) {
        this.set(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(allowCredentials));
    }

    public boolean getAccessControlAllowCredentials() {
        return Boolean.parseBoolean(this.getFirst(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    public void setAccessControlAllowHeaders(List<String> allowedHeaders) {
        this.set(ACCESS_CONTROL_ALLOW_HEADERS, this.toCommaDelimitedString(allowedHeaders));
    }

    public List<String> getAccessControlAllowHeaders() {
        return this.getValuesAsList(ACCESS_CONTROL_ALLOW_HEADERS);
    }

    public void setAccessControlAllowMethods(List<HttpMethod> allowedMethods) {
        this.set(ACCESS_CONTROL_ALLOW_METHODS, StringUtils.collectionToCommaDelimitedString(allowedMethods));
    }

    public List<HttpMethod> getAccessControlAllowMethods() {
        ArrayList<HttpMethod> result = new ArrayList<HttpMethod>();
        String value = this.getFirst(ACCESS_CONTROL_ALLOW_METHODS);
        if (value != null) {
            String[] tokens;
            for (String token : tokens = StringUtils.tokenizeToStringArray(value, ",")) {
                HttpMethod resolved = HttpMethod.resolve(token);
                if (resolved == null) continue;
                result.add(resolved);
            }
        }
        return result;
    }

    public void setAccessControlAllowOrigin(@Nullable String allowedOrigin) {
        this.setOrRemove(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
    }

    @Nullable
    public String getAccessControlAllowOrigin() {
        return this.getFieldValues(ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    public void setAccessControlExposeHeaders(List<String> exposedHeaders) {
        this.set(ACCESS_CONTROL_EXPOSE_HEADERS, this.toCommaDelimitedString(exposedHeaders));
    }

    public List<String> getAccessControlExposeHeaders() {
        return this.getValuesAsList(ACCESS_CONTROL_EXPOSE_HEADERS);
    }

    public void setAccessControlMaxAge(Duration maxAge) {
        this.set(ACCESS_CONTROL_MAX_AGE, Long.toString(maxAge.getSeconds()));
    }

    public void setAccessControlMaxAge(long maxAge) {
        this.set(ACCESS_CONTROL_MAX_AGE, Long.toString(maxAge));
    }

    public long getAccessControlMaxAge() {
        String value = this.getFirst(ACCESS_CONTROL_MAX_AGE);
        return value != null ? Long.parseLong(value) : -1L;
    }

    public void setAccessControlRequestHeaders(List<String> requestHeaders) {
        this.set(ACCESS_CONTROL_REQUEST_HEADERS, this.toCommaDelimitedString(requestHeaders));
    }

    public List<String> getAccessControlRequestHeaders() {
        return this.getValuesAsList(ACCESS_CONTROL_REQUEST_HEADERS);
    }

    public void setAccessControlRequestMethod(@Nullable HttpMethod requestMethod) {
        this.setOrRemove(ACCESS_CONTROL_REQUEST_METHOD, requestMethod != null ? requestMethod.name() : null);
    }

    @Nullable
    public HttpMethod getAccessControlRequestMethod() {
        return HttpMethod.resolve(this.getFirst(ACCESS_CONTROL_REQUEST_METHOD));
    }

    public void setAcceptCharset(List<Charset> acceptableCharsets) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Charset charset : acceptableCharsets) {
            joiner.add(charset.name().toLowerCase(Locale.ENGLISH));
        }
        this.set(ACCEPT_CHARSET, joiner.toString());
    }

    public List<Charset> getAcceptCharset() {
        String value = this.getFirst(ACCEPT_CHARSET);
        if (value != null) {
            String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
            ArrayList<Charset> result = new ArrayList<Charset>(tokens.length);
            for (String token : tokens) {
                int paramIdx = token.indexOf(59);
                String charsetName = paramIdx == -1 ? token : token.substring(0, paramIdx);
                if (charsetName.equals("*")) continue;
                result.add(Charset.forName(charsetName));
            }
            return result;
        }
        return Collections.emptyList();
    }

    public void setAllow(Set<HttpMethod> allowedMethods) {
        this.set(ALLOW, StringUtils.collectionToCommaDelimitedString(allowedMethods));
    }

    public Set<HttpMethod> getAllow() {
        String value = this.getFirst(ALLOW);
        if (StringUtils.hasLength(value)) {
            String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
            ArrayList<HttpMethod> result = new ArrayList<HttpMethod>(tokens.length);
            for (String token : tokens) {
                HttpMethod resolved = HttpMethod.resolve(token);
                if (resolved == null) continue;
                result.add(resolved);
            }
            return EnumSet.copyOf(result);
        }
        return EnumSet.noneOf(HttpMethod.class);
    }

    public void setBasicAuth(String username, String password) {
        this.setBasicAuth(username, password, null);
    }

    public void setBasicAuth(String username, String password, @Nullable Charset charset) {
        this.setBasicAuth(HttpHeaders.encodeBasicAuth(username, password, charset));
    }

    public void setBasicAuth(String encodedCredentials) {
        Assert.hasText(encodedCredentials, "'encodedCredentials' must not be null or blank");
        this.set(AUTHORIZATION, "Basic " + encodedCredentials);
    }

    public void setBearerAuth(String token) {
        this.set(AUTHORIZATION, "Bearer " + token);
    }

    public void setCacheControl(CacheControl cacheControl) {
        this.setOrRemove(CACHE_CONTROL, cacheControl.getHeaderValue());
    }

    public void setCacheControl(@Nullable String cacheControl) {
        this.setOrRemove(CACHE_CONTROL, cacheControl);
    }

    @Nullable
    public String getCacheControl() {
        return this.getFieldValues(CACHE_CONTROL);
    }

    public void setConnection(String connection) {
        this.set(CONNECTION, connection);
    }

    public void setConnection(List<String> connection) {
        this.set(CONNECTION, this.toCommaDelimitedString(connection));
    }

    public List<String> getConnection() {
        return this.getValuesAsList(CONNECTION);
    }

    public void setContentDispositionFormData(String name, @Nullable String filename) {
        Assert.notNull((Object)name, "Name must not be null");
        ContentDisposition.Builder disposition = ContentDisposition.formData().name(name);
        if (StringUtils.hasText(filename)) {
            disposition.filename(filename);
        }
        this.setContentDisposition(disposition.build());
    }

    public void setContentDisposition(ContentDisposition contentDisposition) {
        this.set(CONTENT_DISPOSITION, contentDisposition.toString());
    }

    public ContentDisposition getContentDisposition() {
        String contentDisposition = this.getFirst(CONTENT_DISPOSITION);
        if (StringUtils.hasText(contentDisposition)) {
            return ContentDisposition.parse(contentDisposition);
        }
        return ContentDisposition.empty();
    }

    public void setContentLanguage(@Nullable Locale locale) {
        this.setOrRemove(CONTENT_LANGUAGE, locale != null ? locale.toLanguageTag() : null);
    }

    @Nullable
    public Locale getContentLanguage() {
        return this.getValuesAsList(CONTENT_LANGUAGE).stream().findFirst().map(Locale::forLanguageTag).orElse(null);
    }

    public void setContentLength(long contentLength) {
        this.set(CONTENT_LENGTH, Long.toString(contentLength));
    }

    public long getContentLength() {
        String value = this.getFirst(CONTENT_LENGTH);
        return value != null ? Long.parseLong(value) : -1L;
    }

    public void setContentType(@Nullable MediaType mediaType) {
        if (mediaType != null) {
            Assert.isTrue(!mediaType.isWildcardType(), "Content-Type cannot contain wildcard type '*'");
            Assert.isTrue(!mediaType.isWildcardSubtype(), "Content-Type cannot contain wildcard subtype '*'");
            this.set(CONTENT_TYPE, mediaType.toString());
        } else {
            this.remove(CONTENT_TYPE);
        }
    }

    @Nullable
    public MediaType getContentType() {
        String value = this.getFirst(CONTENT_TYPE);
        return StringUtils.hasLength(value) ? MediaType.parseMediaType(value) : null;
    }

    public void setDate(ZonedDateTime date) {
        this.setZonedDateTime(DATE, date);
    }

    public void setDate(Instant date) {
        this.setInstant(DATE, date);
    }

    public void setDate(long date) {
        this.setDate(DATE, date);
    }

    public long getDate() {
        return this.getFirstDate(DATE);
    }

    public void setETag(@Nullable String etag) {
        if (etag != null) {
            Assert.isTrue(etag.startsWith("\"") || etag.startsWith("W/"), "Invalid ETag: does not start with W/ or \"");
            Assert.isTrue(etag.endsWith("\""), "Invalid ETag: does not end with \"");
            this.set(ETAG, etag);
        } else {
            this.remove(ETAG);
        }
    }

    @Nullable
    public String getETag() {
        return this.getFirst(ETAG);
    }

    public void setExpires(ZonedDateTime expires) {
        this.setZonedDateTime(EXPIRES, expires);
    }

    public void setExpires(Instant expires) {
        this.setInstant(EXPIRES, expires);
    }

    public void setExpires(long expires) {
        this.setDate(EXPIRES, expires);
    }

    public long getExpires() {
        return this.getFirstDate(EXPIRES, false);
    }

    public void setHost(@Nullable InetSocketAddress host) {
        if (host != null) {
            String value = host.getHostString();
            int port = host.getPort();
            if (port != 0) {
                value = value + ":" + port;
            }
            this.set(HOST, value);
        } else {
            this.remove(HOST, null);
        }
    }

    @Nullable
    public InetSocketAddress getHost() {
        int separator;
        String value = this.getFirst(HOST);
        if (value == null) {
            return null;
        }
        String host = null;
        int port = 0;
        int n = separator = value.startsWith("[") ? value.indexOf(58, value.indexOf(93)) : value.lastIndexOf(58);
        if (separator != -1) {
            host = value.substring(0, separator);
            String portString = value.substring(separator + 1);
            try {
                port = Integer.parseInt(portString);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (host == null) {
            host = value;
        }
        return InetSocketAddress.createUnresolved(host, port);
    }

    public void setIfMatch(String ifMatch) {
        this.set(IF_MATCH, ifMatch);
    }

    public void setIfMatch(List<String> ifMatchList) {
        this.set(IF_MATCH, this.toCommaDelimitedString(ifMatchList));
    }

    public List<String> getIfMatch() {
        return this.getETagValuesAsList(IF_MATCH);
    }

    public void setIfModifiedSince(ZonedDateTime ifModifiedSince) {
        this.setZonedDateTime(IF_MODIFIED_SINCE, ifModifiedSince.withZoneSameInstant(GMT));
    }

    public void setIfModifiedSince(Instant ifModifiedSince) {
        this.setInstant(IF_MODIFIED_SINCE, ifModifiedSince);
    }

    public void setIfModifiedSince(long ifModifiedSince) {
        this.setDate(IF_MODIFIED_SINCE, ifModifiedSince);
    }

    public long getIfModifiedSince() {
        return this.getFirstDate(IF_MODIFIED_SINCE, false);
    }

    public void setIfNoneMatch(String ifNoneMatch) {
        this.set(IF_NONE_MATCH, ifNoneMatch);
    }

    public void setIfNoneMatch(List<String> ifNoneMatchList) {
        this.set(IF_NONE_MATCH, this.toCommaDelimitedString(ifNoneMatchList));
    }

    public List<String> getIfNoneMatch() {
        return this.getETagValuesAsList(IF_NONE_MATCH);
    }

    public void setIfUnmodifiedSince(ZonedDateTime ifUnmodifiedSince) {
        this.setZonedDateTime(IF_UNMODIFIED_SINCE, ifUnmodifiedSince.withZoneSameInstant(GMT));
    }

    public void setIfUnmodifiedSince(Instant ifUnmodifiedSince) {
        this.setInstant(IF_UNMODIFIED_SINCE, ifUnmodifiedSince);
    }

    public void setIfUnmodifiedSince(long ifUnmodifiedSince) {
        this.setDate(IF_UNMODIFIED_SINCE, ifUnmodifiedSince);
    }

    public long getIfUnmodifiedSince() {
        return this.getFirstDate(IF_UNMODIFIED_SINCE, false);
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.setZonedDateTime(LAST_MODIFIED, lastModified.withZoneSameInstant(GMT));
    }

    public void setLastModified(Instant lastModified) {
        this.setInstant(LAST_MODIFIED, lastModified);
    }

    public void setLastModified(long lastModified) {
        this.setDate(LAST_MODIFIED, lastModified);
    }

    public long getLastModified() {
        return this.getFirstDate(LAST_MODIFIED, false);
    }

    public void setLocation(@Nullable URI location) {
        this.setOrRemove(LOCATION, location != null ? location.toASCIIString() : null);
    }

    @Nullable
    public URI getLocation() {
        String value = this.getFirst(LOCATION);
        return value != null ? URI.create(value) : null;
    }

    public void setOrigin(@Nullable String origin) {
        this.setOrRemove(ORIGIN, origin);
    }

    @Nullable
    public String getOrigin() {
        return this.getFirst(ORIGIN);
    }

    public void setPragma(@Nullable String pragma) {
        this.setOrRemove(PRAGMA, pragma);
    }

    @Nullable
    public String getPragma() {
        return this.getFirst(PRAGMA);
    }

    public void setRange(List<HttpRange> ranges) {
        String value = HttpRange.toString(ranges);
        this.set(RANGE, value);
    }

    public List<HttpRange> getRange() {
        String value = this.getFirst(RANGE);
        return HttpRange.parseRanges(value);
    }

    public void setUpgrade(@Nullable String upgrade) {
        this.setOrRemove(UPGRADE, upgrade);
    }

    @Nullable
    public String getUpgrade() {
        return this.getFirst(UPGRADE);
    }

    public void setVary(List<String> requestHeaders) {
        this.set(VARY, this.toCommaDelimitedString(requestHeaders));
    }

    public List<String> getVary() {
        return this.getValuesAsList(VARY);
    }

    public void setZonedDateTime(String headerName, ZonedDateTime date) {
        this.set(headerName, DATE_FORMATTER.format(date));
    }

    public void setInstant(String headerName, Instant date) {
        this.setZonedDateTime(headerName, ZonedDateTime.ofInstant(date, GMT));
    }

    public void setDate(String headerName, long date) {
        this.setInstant(headerName, Instant.ofEpochMilli(date));
    }

    public long getFirstDate(String headerName) {
        return this.getFirstDate(headerName, true);
    }

    private long getFirstDate(String headerName, boolean rejectInvalid) {
        ZonedDateTime zonedDateTime = this.getFirstZonedDateTime(headerName, rejectInvalid);
        return zonedDateTime != null ? zonedDateTime.toInstant().toEpochMilli() : -1L;
    }

    @Nullable
    public ZonedDateTime getFirstZonedDateTime(String headerName) {
        return this.getFirstZonedDateTime(headerName, true);
    }

    @Nullable
    private ZonedDateTime getFirstZonedDateTime(String headerName, boolean rejectInvalid) {
        String headerValue = this.getFirst(headerName);
        if (headerValue == null) {
            return null;
        }
        if (headerValue.length() >= 3) {
            int parametersIndex = headerValue.indexOf(59);
            if (parametersIndex != -1) {
                headerValue = headerValue.substring(0, parametersIndex);
            }
            for (DateTimeFormatter dateFormatter : DATE_PARSERS) {
                try {
                    return ZonedDateTime.parse(headerValue, dateFormatter);
                }
                catch (DateTimeParseException dateTimeParseException) {
                }
            }
        }
        if (rejectInvalid) {
            throw new IllegalArgumentException("Cannot parse date value \"" + headerValue + "\" for \"" + headerName + "\" header");
        }
        return null;
    }

    public List<String> getValuesAsList(String headerName) {
        Object values = this.get(headerName);
        if (values != null) {
            ArrayList<String> result = new ArrayList<String>();
            Iterator iterator = values.iterator();
            while (iterator.hasNext()) {
                String value = (String)iterator.next();
                if (value == null) continue;
                Collections.addAll(result, StringUtils.tokenizeToStringArray(value, ","));
            }
            return result;
        }
        return Collections.emptyList();
    }

    public void clearContentHeaders() {
        this.headers.remove(CONTENT_DISPOSITION);
        this.headers.remove(CONTENT_ENCODING);
        this.headers.remove(CONTENT_LANGUAGE);
        this.headers.remove(CONTENT_LENGTH);
        this.headers.remove(CONTENT_LOCATION);
        this.headers.remove(CONTENT_RANGE);
        this.headers.remove(CONTENT_TYPE);
    }

    protected List<String> getETagValuesAsList(String headerName) {
        Object values = this.get(headerName);
        if (values != null) {
            ArrayList<String> result = new ArrayList<String>();
            Iterator iterator = values.iterator();
            while (iterator.hasNext()) {
                String value = (String)iterator.next();
                if (value == null) continue;
                Matcher matcher = ETAG_HEADER_VALUE_PATTERN.matcher(value);
                while (matcher.find()) {
                    if ("*".equals(matcher.group())) {
                        result.add(matcher.group());
                        continue;
                    }
                    result.add(matcher.group(1));
                }
                if (!result.isEmpty()) continue;
                throw new IllegalArgumentException("Could not parse header '" + headerName + "' with value '" + value + "'");
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Nullable
    protected String getFieldValues(String headerName) {
        Object headerValues = this.get(headerName);
        return headerValues != null ? this.toCommaDelimitedString((List<String>)headerValues) : null;
    }

    protected String toCommaDelimitedString(List<String> headerValues) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String val : headerValues) {
            if (val == null) continue;
            joiner.add(val);
        }
        return joiner.toString();
    }

    private void setOrRemove(String headerName, @Nullable String headerValue) {
        if (headerValue != null) {
            this.set(headerName, headerValue);
        } else {
            this.remove(headerName);
        }
    }

    @Override
    @Nullable
    public String getFirst(String headerName) {
        return this.headers.getFirst(headerName);
    }

    @Override
    public void add(String headerName, @Nullable String headerValue) {
        this.headers.add(headerName, headerValue);
    }

    @Override
    public void addAll(String key, List<? extends String> values) {
        this.headers.addAll(key, values);
    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {
        this.headers.addAll(values);
    }

    @Override
    public void set(String headerName, @Nullable String headerValue) {
        this.headers.set(headerName, headerValue);
    }

    @Override
    public void setAll(Map<String, String> values) {
        this.headers.setAll(values);
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        return this.headers.toSingleValueMap();
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    @Override
    @Nullable
    public List<String> get(Object key) {
        return (List)this.headers.get(key);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return this.headers.put(key, (String)((Object)value));
    }

    @Override
    public List<String> remove(Object key) {
        return (List)this.headers.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        this.headers.putAll(map);
    }

    @Override
    public void clear() {
        this.headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.headers.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return this.headers.values();
    }

    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return this.headers.entrySet();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HttpHeaders)) {
            return false;
        }
        return HttpHeaders.unwrap(this).equals(HttpHeaders.unwrap((HttpHeaders)other));
    }

    private static MultiValueMap<String, String> unwrap(HttpHeaders headers) {
        while (headers.headers instanceof HttpHeaders) {
            headers = (HttpHeaders)headers.headers;
        }
        return headers.headers;
    }

    @Override
    public int hashCode() {
        return this.headers.hashCode();
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this.headers);
    }

    public static HttpHeaders readOnlyHttpHeaders(MultiValueMap<String, String> headers) {
        return headers instanceof HttpHeaders ? HttpHeaders.readOnlyHttpHeaders((HttpHeaders)headers) : new ReadOnlyHttpHeaders(headers);
    }

    public static HttpHeaders readOnlyHttpHeaders(HttpHeaders headers) {
        Assert.notNull((Object)headers, "HttpHeaders must not be null");
        return headers instanceof ReadOnlyHttpHeaders ? headers : new ReadOnlyHttpHeaders(headers.headers);
    }

    public static HttpHeaders writableHttpHeaders(HttpHeaders headers) {
        Assert.notNull((Object)headers, "HttpHeaders must not be null");
        if (headers == EMPTY) {
            return new HttpHeaders();
        }
        return headers instanceof ReadOnlyHttpHeaders ? new HttpHeaders(headers.headers) : headers;
    }

    public static String formatHeaders(MultiValueMap<String, String> headers) {
        return headers.entrySet().stream().map(entry -> {
            List values = (List)entry.getValue();
            return (String)entry.getKey() + ":" + (values.size() == 1 ? "\"" + (String)values.get(0) + "\"" : values.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")));
        }).collect(Collectors.joining(", ", "[", "]"));
    }

    public static String encodeBasicAuth(String username, String password, @Nullable Charset charset) {
        CharsetEncoder encoder;
        Assert.notNull((Object)username, "Username must not be null");
        Assert.doesNotContain(username, ":", "Username must not contain a colon");
        Assert.notNull((Object)password, "Password must not be null");
        if (charset == null) {
            charset = StandardCharsets.ISO_8859_1;
        }
        if (!(encoder = charset.newEncoder()).canEncode(username) || !encoder.canEncode(password)) {
            throw new IllegalArgumentException("Username or password contains characters that cannot be encoded to " + charset.displayName());
        }
        String credentialsString = username + ":" + password;
        byte[] encodedBytes = Base64.getEncoder().encode(credentialsString.getBytes(charset));
        return new String(encodedBytes, charset);
    }

    static String formatDate(long date) {
        Instant instant = Instant.ofEpochMilli(date);
        ZonedDateTime time = ZonedDateTime.ofInstant(instant, GMT);
        return DATE_FORMATTER.format(time);
    }
}

