/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.MessageException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.TraceInformation;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.core.header.AcceptableLanguageTag;
import com.sun.jersey.core.header.AcceptableMediaType;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.header.MatchingEntityTag;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.server.impl.VariantSelector;
import com.sun.jersey.server.impl.model.HttpHelper;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.container.WebApplication;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

public class ContainerRequest
implements HttpRequestContext {
    private static final Logger LOGGER = Logger.getLogger(ContainerRequest.class.getName());
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
    public static final String VARY_HEADER = "Vary";
    private final WebApplication wa;
    private final boolean isTraceEnabled;
    private Map<String, Object> properties;
    private String method;
    private InputStream entity;
    private URI baseUri;
    private URI requestUri;
    private URI absolutePathUri;
    private String encodedPath;
    private String decodedPath;
    private List<PathSegment> decodedPathSegments;
    private List<PathSegment> encodedPathSegments;
    private MultivaluedMap<String, String> decodedQueryParameters;
    private MultivaluedMap<String, String> encodedQueryParameters;
    private InBoundHeaders headers;
    private int headersModCount;
    private MediaType contentType;
    private List<MediaType> accept;
    private List<Locale> acceptLanguages;
    private Map<String, Cookie> cookies;
    private MultivaluedMap<String, String> cookieNames;
    private SecurityContext securityContext;

    public ContainerRequest(WebApplication wa, String method, URI baseUri, URI requestUri, InBoundHeaders headers, InputStream entity) {
        this.wa = wa;
        this.isTraceEnabled = wa.isTracingEnabled();
        this.method = method;
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        this.headers = headers;
        this.headersModCount = headers.getModCount();
        this.entity = entity;
    }

    ContainerRequest(ContainerRequest r) {
        this.wa = r.wa;
        this.isTraceEnabled = r.isTraceEnabled;
    }

    public Map<String, Object> getProperties() {
        if (this.properties != null) {
            return this.properties;
        }
        this.properties = new HashMap<String, Object>();
        return this.properties;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUris(URI baseUri, URI requestUri) {
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        this.absolutePathUri = null;
        this.decodedPath = null;
        this.encodedPath = null;
        this.encodedPathSegments = null;
        this.decodedPathSegments = null;
        this.encodedQueryParameters = null;
        this.decodedQueryParameters = null;
    }

    public InputStream getEntityInputStream() {
        return this.entity;
    }

    public void setEntityInputStream(InputStream entity) {
        this.entity = entity;
    }

    public void setHeaders(InBoundHeaders headers) {
        this.headers = headers;
        this.headersModCount = headers.getModCount();
        this.contentType = null;
        this.accept = null;
        this.cookies = null;
        this.cookieNames = null;
    }

    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public SecurityContext getSecurityContext() {
        return this.securityContext;
    }

    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.wa.getMessageBodyWorkers();
    }

    @Override
    public boolean isTracingEnabled() {
        return this.isTraceEnabled;
    }

    @Override
    public void trace(String message) {
        if (!this.isTracingEnabled()) {
            return;
        }
        if (this.wa.getFeaturesAndProperties().getFeature("com.sun.jersey.config.feature.TracePerRequest") && !this.getRequestHeaders().containsKey("X-Jersey-Trace-Accept")) {
            return;
        }
        TraceInformation ti = (TraceInformation)this.getProperties().get(TraceInformation.class.getName());
        ti.trace(message);
    }

    @Override
    public URI getBaseUri() {
        return this.baseUri;
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        return UriBuilder.fromUri(this.getBaseUri());
    }

    @Override
    public URI getRequestUri() {
        return this.requestUri;
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        return UriBuilder.fromUri(this.getRequestUri());
    }

    @Override
    public URI getAbsolutePath() {
        if (this.absolutePathUri != null) {
            return this.absolutePathUri;
        }
        this.absolutePathUri = UriBuilder.fromUri(this.requestUri).replaceQuery("").fragment("").build(new Object[0]);
        return this.absolutePathUri;
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return UriBuilder.fromUri(this.getAbsolutePath());
    }

    @Override
    public String getPath() {
        return this.getPath(true);
    }

    @Override
    public String getPath(boolean decode) {
        if (decode) {
            if (this.decodedPath != null) {
                return this.decodedPath;
            }
            this.decodedPath = UriComponent.decode(this.getEncodedPath(), UriComponent.Type.PATH);
            return this.decodedPath;
        }
        return this.getEncodedPath();
    }

    private String getEncodedPath() {
        if (this.encodedPath != null) {
            return this.encodedPath;
        }
        int length = this.getBaseUri().getRawPath().length();
        if (length < this.getRequestUri().getRawPath().length()) {
            this.encodedPath = this.getRequestUri().getRawPath().substring(length);
            return this.encodedPath;
        }
        return "";
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return this.getPathSegments(true);
    }

    @Override
    public List<PathSegment> getPathSegments(boolean decode) {
        if (decode) {
            if (this.decodedPathSegments != null) {
                return this.decodedPathSegments;
            }
            this.decodedPathSegments = UriComponent.decodePath(this.getPath(false), true);
            return this.decodedPathSegments;
        }
        if (this.encodedPathSegments != null) {
            return this.encodedPathSegments;
        }
        this.encodedPathSegments = UriComponent.decodePath(this.getPath(false), false);
        return this.encodedPathSegments;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return this.getQueryParameters(true);
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        if (decode) {
            if (this.decodedQueryParameters != null) {
                return this.decodedQueryParameters;
            }
            this.decodedQueryParameters = UriComponent.decodeQuery(this.getRequestUri(), true);
            return this.decodedQueryParameters;
        }
        if (this.encodedQueryParameters != null) {
            return this.encodedQueryParameters;
        }
        this.encodedQueryParameters = UriComponent.decodeQuery(this.getRequestUri(), false);
        return this.encodedQueryParameters;
    }

    @Override
    public String getHeaderValue(String name) {
        List v = (List)this.getRequestHeaders().get(name);
        if (v == null) {
            return null;
        }
        if (v.isEmpty()) {
            return "";
        }
        if (v.size() == 1) {
            return (String)v.get(0);
        }
        StringBuilder sb = new StringBuilder((String)v.get(0));
        for (int i = 1; i < v.size(); ++i) {
            String s = (String)v.get(i);
            if (s.length() <= 0) continue;
            sb.append(',').append(s);
        }
        return sb.toString();
    }

    @Override
    public <T> T getEntity(Class<T> type, Type genericType, Annotation[] as) {
        MessageBodyReader<T> bw;
        MediaType mediaType = this.getMediaType();
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        if ((bw = this.getMessageBodyWorkers().getMessageBodyReader(type, genericType, as, mediaType)) == null) {
            String message = "A message body reader for Java class " + type.getName() + ", and Java type " + genericType + ", and MIME media type " + mediaType + " was not found.\n";
            Map<MediaType, List<MessageBodyReader>> m = this.getMessageBodyWorkers().getReaders(mediaType);
            LOGGER.severe(message + "The registered message body readers compatible with the MIME media type are:\n" + this.getMessageBodyWorkers().readersToString(m));
            throw new WebApplicationException((Throwable)new MessageException(message), Responses.unsupportedMediaType().build());
        }
        if (this.isTracingEnabled()) {
            this.trace(String.format("matched message body reader: %s, \"%s\" -> %s", genericType, mediaType, ReflectionHelper.objectToString(bw)));
        }
        try {
            return bw.readFrom(type, genericType, as, mediaType, this.headers, this.entity);
        }
        catch (WebApplicationException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw new MappableContainerException(e);
        }
    }

    public <T> void setEntity(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, T entity) {
        MessageBodyWriter<T> writer = this.getMessageBodyWorkers().getMessageBodyWriter(type, genericType, annotations, mediaType);
        if (writer == null) {
            String message = "A message body writer for Java class " + type.getName() + ", and Java type " + genericType + ", and MIME media type " + mediaType + " was not found.\n";
            Map<MediaType, List<MessageBodyReader>> m = this.getMessageBodyWorkers().getReaders(mediaType);
            LOGGER.severe(message + "The registered message body readers compatible with the MIME media type are:\n" + this.getMessageBodyWorkers().readersToString(m));
            throw new WebApplicationException((Throwable)new MessageException(message), Responses.unsupportedMediaType().build());
        }
        if (this.isTracingEnabled()) {
            this.trace(String.format("matched message body writer: %s, \"%s\" -> %s", genericType, mediaType, ReflectionHelper.objectToString(writer)));
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            writer.writeTo(entity, type, genericType, annotations, mediaType, httpHeaders, byteArrayOutputStream);
        }
        catch (IOException e) {
            throw new MappableContainerException(e);
        }
        this.entity = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    @Override
    public <T> T getEntity(Class<T> type) {
        return this.getEntity(type, type, EMPTY_ANNOTATIONS);
    }

    @Override
    public MediaType getAcceptableMediaType(List<MediaType> mediaTypes) {
        if (mediaTypes.isEmpty()) {
            return this.getAcceptableMediaTypes().get(0);
        }
        for (MediaType a : this.getAcceptableMediaTypes()) {
            if (a.getType().equals("*")) {
                return mediaTypes.get(0);
            }
            for (MediaType m : mediaTypes) {
                if (!m.isCompatible(a) || m.isWildcardType() || m.isWildcardSubtype()) continue;
                return m;
            }
        }
        return null;
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes(List<QualitySourceMediaType> priorityMediaTypes) {
        return new ArrayList<MediaType>(HttpHelper.getAccept(this, priorityMediaTypes));
    }

    @Override
    public MultivaluedMap<String, String> getCookieNameValueMap() {
        if (this.cookieNames == null || this.headersModCount != this.headers.getModCount()) {
            this.cookieNames = new MultivaluedMapImpl();
            for (Map.Entry<String, Cookie> e : this.getCookies().entrySet()) {
                this.cookieNames.putSingle(e.getKey(), e.getValue().getValue());
            }
        }
        return this.cookieNames;
    }

    @Override
    public Form getFormParameters() {
        if (MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, this.getMediaType())) {
            InputStream in = this.getEntityInputStream();
            if (in.getClass() != ByteArrayInputStream.class) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    ReaderWriter.writeTo(in, byteArrayOutputStream);
                }
                catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
                in = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                this.setEntityInputStream(in);
            }
            ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)in;
            Form f = this.getEntity(Form.class);
            byteArrayInputStream.reset();
            return f;
        }
        return new Form();
    }

    @Override
    public MultivaluedMap<String, String> getRequestHeaders() {
        return this.headers;
    }

    @Override
    public List<String> getRequestHeader(String name) {
        return (List)this.headers.get(name);
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        if (this.accept == null || this.headersModCount != this.headers.getModCount()) {
            this.accept = new ArrayList<AcceptableMediaType>(HttpHelper.getAccept(this));
        }
        return this.accept;
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        if (this.acceptLanguages == null || this.headersModCount != this.headers.getModCount()) {
            List<AcceptableLanguageTag> alts = HttpHelper.getAcceptLangauge(this);
            this.acceptLanguages = new ArrayList<Locale>(alts.size());
            for (AcceptableLanguageTag alt : alts) {
                this.acceptLanguages.add(alt.getAsLocale());
            }
        }
        return this.acceptLanguages;
    }

    @Override
    public MediaType getMediaType() {
        if (this.contentType == null || this.headersModCount != this.headers.getModCount()) {
            this.contentType = HttpHelper.getContentType(this);
        }
        return this.contentType;
    }

    @Override
    public Locale getLanguage() {
        return HttpHelper.getContentLanguageAsLocale(this);
    }

    @Override
    public Map<String, Cookie> getCookies() {
        if (this.cookies == null || this.headersModCount != this.headers.getModCount()) {
            this.cookies = new HashMap<String, Cookie>();
            List cl = (List)this.getRequestHeaders().get("Cookie");
            if (cl != null) {
                for (String cookie : cl) {
                    if (cookie == null) continue;
                    this.cookies.putAll(HttpHeaderReader.readCookies(cookie));
                }
            }
        }
        return this.cookies;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public Variant selectVariant(List<Variant> variants) {
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException("The list of variants is null or empty");
        }
        return VariantSelector.selectVariant(this, variants);
    }

    @Override
    public Response.ResponseBuilder evaluatePreconditions() {
        Set<MatchingEntityTag> matchingTags = HttpHelper.getIfMatch(this);
        if (matchingTags == null) {
            return null;
        }
        return Responses.preconditionFailed();
    }

    @Override
    public Response.ResponseBuilder evaluatePreconditions(EntityTag eTag) {
        if (eTag == null) {
            throw new IllegalArgumentException("Parameter 'eTag' cannot be null.");
        }
        Response.ResponseBuilder r = this.evaluateIfMatch(eTag);
        if (r != null) {
            return r;
        }
        return this.evaluateIfNoneMatch(eTag);
    }

    @Override
    public Response.ResponseBuilder evaluatePreconditions(Date lastModified) {
        if (lastModified == null) {
            throw new IllegalArgumentException("Parameter 'lastModified' cannot be null.");
        }
        long lastModifiedTime = lastModified.getTime();
        Response.ResponseBuilder r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
        if (r != null) {
            return r;
        }
        return this.evaluateIfModifiedSince(lastModifiedTime);
    }

    @Override
    public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
        if (lastModified == null || eTag == null) {
            throw new IllegalArgumentException("Parameters 'lastModified' and 'eTag' cannot be null.");
        }
        Response.ResponseBuilder r = this.evaluateIfMatch(eTag);
        if (r != null) {
            return r;
        }
        long lastModifiedTime = lastModified.getTime();
        r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
        if (r != null) {
            return r;
        }
        boolean isGetOrHead = this.getMethod().equals("GET") || this.getMethod().equals("HEAD");
        Set<MatchingEntityTag> matchingTags = HttpHelper.getIfNoneMatch(this);
        if (matchingTags != null && (r = this.evaluateIfNoneMatch(eTag, matchingTags, isGetOrHead)) == null) {
            return r;
        }
        String ifModifiedSinceHeader = this.getRequestHeaders().getFirst("If-Modified-Since");
        if (ifModifiedSinceHeader != null && isGetOrHead && (r = this.evaluateIfModifiedSince(lastModifiedTime, ifModifiedSinceHeader)) != null) {
            r.tag(eTag);
        }
        return r;
    }

    private Response.ResponseBuilder evaluateIfMatch(EntityTag eTag) {
        Set<MatchingEntityTag> matchingTags = HttpHelper.getIfMatch(this);
        if (matchingTags == null) {
            return null;
        }
        if (eTag.isWeak()) {
            return Responses.preconditionFailed();
        }
        if (matchingTags != MatchingEntityTag.ANY_MATCH && !matchingTags.contains(eTag)) {
            return Responses.preconditionFailed();
        }
        return null;
    }

    private Response.ResponseBuilder evaluateIfNoneMatch(EntityTag eTag) {
        Set<MatchingEntityTag> matchingTags = HttpHelper.getIfNoneMatch(this);
        if (matchingTags == null) {
            return null;
        }
        String httpMethod = this.getMethod();
        return this.evaluateIfNoneMatch(eTag, matchingTags, httpMethod.equals("GET") || httpMethod.equals("HEAD"));
    }

    private Response.ResponseBuilder evaluateIfNoneMatch(EntityTag eTag, Set<MatchingEntityTag> matchingTags, boolean isGetOrHead) {
        if (isGetOrHead) {
            if (matchingTags == MatchingEntityTag.ANY_MATCH) {
                return Response.notModified(eTag);
            }
            if (matchingTags.contains(eTag) || matchingTags.contains(new EntityTag(eTag.getValue(), !eTag.isWeak()))) {
                return Response.notModified(eTag);
            }
        } else {
            if (eTag.isWeak()) {
                return null;
            }
            if (matchingTags == MatchingEntityTag.ANY_MATCH || matchingTags.contains(eTag)) {
                return Responses.preconditionFailed();
            }
        }
        return null;
    }

    private Response.ResponseBuilder evaluateIfUnmodifiedSince(long lastModified) {
        String ifUnmodifiedSinceHeader = this.getRequestHeaders().getFirst("If-Unmodified-Since");
        if (ifUnmodifiedSinceHeader != null) {
            try {
                long ifUnmodifiedSince = HttpHeaderReader.readDate(ifUnmodifiedSinceHeader).getTime();
                if (ContainerRequest.roundDown(lastModified) > ifUnmodifiedSince) {
                    return Responses.preconditionFailed();
                }
            }
            catch (ParseException parseException) {
                // empty catch block
            }
        }
        return null;
    }

    private Response.ResponseBuilder evaluateIfModifiedSince(long lastModified) {
        String ifModifiedSinceHeader = this.getRequestHeaders().getFirst("If-Modified-Since");
        if (ifModifiedSinceHeader == null) {
            return null;
        }
        String httpMethod = this.getMethod();
        if (httpMethod.equals("GET") || httpMethod.equals("HEAD")) {
            return this.evaluateIfModifiedSince(lastModified, ifModifiedSinceHeader);
        }
        return null;
    }

    private Response.ResponseBuilder evaluateIfModifiedSince(long lastModified, String ifModifiedSinceHeader) {
        try {
            long ifModifiedSince = HttpHeaderReader.readDate(ifModifiedSinceHeader).getTime();
            if (ContainerRequest.roundDown(lastModified) <= ifModifiedSince) {
                return Responses.notModified();
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return null;
    }

    private static long roundDown(long time) {
        return time - time % 1000L;
    }

    @Override
    public Principal getUserPrincipal() {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(String role) {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.getAuthenticationScheme();
    }
}

