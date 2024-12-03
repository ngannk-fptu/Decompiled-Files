/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCrowdServiceException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.service.client.ClientProperties
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  com.google.common.base.Charsets
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 *  javax.xml.bind.DataBindingException
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpResponse
 *  org.apache.http.auth.AuthScheme
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.UsernamePasswordCredentials
 *  org.apache.http.client.AuthCache
 *  org.apache.http.client.CredentialsProvider
 *  org.apache.http.client.cache.HttpCacheContext
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.ByteArrayEntity
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.impl.EnglishReasonPhraseCatalog
 *  org.apache.http.impl.auth.BasicScheme
 *  org.apache.http.impl.client.BasicAuthCache
 *  org.apache.http.impl.client.BasicCredentialsProvider
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.EntityUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.integration.rest.service;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCrowdServiceException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.integration.rest.entity.ErrorEntity;
import com.atlassian.crowd.integration.rest.service.CrowdRestException;
import com.atlassian.crowd.integration.rest.util.JAXBContextCache;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.security.xml.SecureXmlParserFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public abstract class RestExecutor
implements Closeable {
    private static final String INVALID_REST_SERVICE_MSG_FORMAT = "The following URL does not specify a valid Crowd User Management REST service: %s";
    static final String EMBEDDED_CROWD_VERSION_NAME = "X-Embedded-Crowd-Version";
    private static final Logger logger = LoggerFactory.getLogger(RestExecutor.class);
    private final String baseUrl;
    private final HttpHost httpHost;
    private final CredentialsProvider credsProvider;
    private final CloseableHttpClient client;
    private final JAXBContextCache jaxbContexts = new JAXBContextCache();
    private static final ContentType APPLICATION_XML = ContentType.create((String)"application/xml", (Charset)Charsets.UTF_8);
    private static final Pattern HTML_CONTENT_TYPE = Pattern.compile("text/html(\\s*;.*)?", 2);
    private static final Pattern UP_TO_HTML_BODY = Pattern.compile(".*<body[^>]*>", 32);

    public RestExecutor(String baseUrl, HttpHost httpHost, CredentialsProvider credsProvider, CloseableHttpClient client) {
        this.baseUrl = (String)Preconditions.checkNotNull((Object)baseUrl);
        this.httpHost = (HttpHost)Preconditions.checkNotNull((Object)httpHost);
        this.credsProvider = (CredentialsProvider)Preconditions.checkNotNull((Object)credsProvider);
        this.client = (CloseableHttpClient)Preconditions.checkNotNull((Object)client);
    }

    protected static CredentialsProvider createCredentialsProvider(ClientProperties clientProperties) {
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        if (clientProperties.getHttpProxyHost() != null) {
            HttpHost proxy = new HttpHost(clientProperties.getHttpProxyHost(), NumberUtils.toInt((String)clientProperties.getHttpProxyPort(), (int)-1));
            if (clientProperties.getHttpProxyUsername() != null && clientProperties.getHttpProxyPassword() != null) {
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(clientProperties.getHttpProxyUsername(), clientProperties.getHttpProxyPassword());
                credsProvider.setCredentials(new AuthScope(proxy), (Credentials)credentials);
            }
        }
        return credsProvider;
    }

    protected static HttpHost createHttpHost(ClientProperties clientProperties) {
        try {
            URI uri = new URI(clientProperties.getBaseURL());
            return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }

    private HttpCacheContext contextForThisThread() {
        HttpCacheContext context = HttpCacheContext.create();
        context.setCredentialsProvider(this.credsProvider);
        BasicAuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(this.httpHost, (AuthScheme)basicAuth);
        context.setAuthCache((AuthCache)authCache);
        return context;
    }

    protected static String createBaseUrl(String url) {
        StringBuilder sb = new StringBuilder(url);
        if (url.endsWith("/")) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("/rest/usermanagement/1");
        return sb.toString();
    }

    MethodExecutor get(String format, Object ... args) {
        return this.createMethodExecutor((HttpUriRequest)new HttpGet(RestExecutor.buildUrl(this.baseUrl, format, args)));
    }

    MethodExecutor delete(String format, Object ... args) {
        return this.createMethodExecutor((HttpUriRequest)new HttpDelete(RestExecutor.buildUrl(this.baseUrl, format, args)));
    }

    MethodExecutor postEmpty(String format, Object ... args) {
        HttpPost method = new HttpPost(RestExecutor.buildUrl(this.baseUrl, format, args));
        method.setEntity((HttpEntity)new StringEntity("", APPLICATION_XML));
        return this.createMethodExecutor((HttpUriRequest)method);
    }

    MethodExecutor post(Object body, String format, Object ... args) {
        HttpPost method = new HttpPost(RestExecutor.buildUrl(this.baseUrl, format, args));
        this.setBody((HttpEntityEnclosingRequestBase)method, body);
        return this.createMethodExecutor((HttpUriRequest)method);
    }

    MethodExecutor put(Object body, String format, Object ... args) {
        HttpPut method = new HttpPut(RestExecutor.buildUrl(this.baseUrl, format, args));
        this.setBody((HttpEntityEnclosingRequestBase)method, body);
        return this.createMethodExecutor((HttpUriRequest)method);
    }

    private void setBody(HttpEntityEnclosingRequestBase method, Object body) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            this.getMarshaller(body.getClass()).marshal(body, (OutputStream)bs);
        }
        catch (JAXBException e) {
            throw new DataBindingException("Cannot marshall object " + body, (Throwable)e);
        }
        method.setEntity((HttpEntity)new ByteArrayEntity(bs.toByteArray(), APPLICATION_XML));
    }

    static String buildUrl(String baseUrl, String format, Object ... args) {
        Object[] encodedArgs = new Object[args.length];
        int pathArgCount = RestExecutor.pathArgumentCount(format);
        try {
            int i;
            for (i = 0; i < pathArgCount; ++i) {
                encodedArgs[i] = args[i] instanceof String ? URLEncoder.encode((String)args[i], "utf-8").replace("+", "%20") : args[i];
            }
            for (i = pathArgCount; i < args.length; ++i) {
                encodedArgs[i] = args[i] instanceof String ? URLEncoder.encode((String)args[i], "utf-8") : args[i];
            }
            String url = baseUrl + String.format(format, encodedArgs);
            logger.debug("Constructed " + url);
            return url;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    static int pathArgumentCount(String format) {
        int queryStart = format.indexOf(63);
        if (queryStart == -1) {
            queryStart = format.length();
        }
        int count = 0;
        int i = format.indexOf(37);
        while (i != -1 && i < queryStart) {
            ++count;
            i = format.indexOf(37, i + 1);
        }
        return count;
    }

    private Marshaller getMarshaller(Class<?> clazz) {
        try {
            return this.jaxbContexts.getJAXBContext(clazz).createMarshaller();
        }
        catch (JAXBException e) {
            throw new DataBindingException("Cannot create marshaller for class " + clazz, (Throwable)e);
        }
    }

    private Unmarshaller getUnmarshaller(Class<?> clazz) {
        try {
            return this.jaxbContexts.getJAXBContext(clazz).createUnmarshaller();
        }
        catch (JAXBException e) {
            throw new DataBindingException("Cannot create unmarshaller for class " + clazz, (Throwable)e);
        }
    }

    protected MethodExecutor createMethodExecutor(HttpUriRequest request, Set<Integer> statusCodesWithoutErrorEntity) {
        return new MethodExecutor(request, statusCodesWithoutErrorEntity);
    }

    protected final MethodExecutor createMethodExecutor(HttpUriRequest request) {
        return this.createMethodExecutor(request, (Set<Integer>)ImmutableSet.of());
    }

    static String getExceptionMessageFromResponse(HttpResponse method) throws IOException {
        Header h = method.getFirstHeader("Content-Type");
        if (h != null && HTML_CONTENT_TYPE.matcher(h.getValue()).matches()) {
            return RestExecutor.stripHtml(EntityUtils.toString((HttpEntity)method.getEntity()));
        }
        return EntityUtils.toString((HttpEntity)method.getEntity());
    }

    static String stripHtml(String s) {
        String onlyTheBody = UP_TO_HTML_BODY.matcher(s).replaceAll("");
        String withoutTags = onlyTheBody.replaceAll("<[^>]*>", "");
        return StringUtils.normalizeSpace((String)StringEscapeUtils.unescapeHtml4((String)withoutTags));
    }

    class MethodExecutor {
        protected final HttpUriRequest request;
        protected final Set<Integer> statusCodesWithoutErrorEntity;
        protected HttpResponse response;

        protected MethodExecutor(HttpUriRequest request, Set<Integer> statusCodesWithoutErrorEntity) {
            this.request = request;
            this.statusCodesWithoutErrorEntity = statusCodesWithoutErrorEntity;
        }

        public MethodExecutor ignoreErrorEntityForStatusCode(int statusCode) {
            ImmutableSet newStatusCodesWithoutErrorEntity = ImmutableSet.builder().addAll(this.statusCodesWithoutErrorEntity).add((Object)statusCode).build();
            return RestExecutor.this.createMethodExecutor(this.request, (Set<Integer>)newStatusCodesWithoutErrorEntity);
        }

        <T> T andReceive(Class<T> returnType) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, CrowdRestException {
            this.configureHeaders();
            try {
                int statusCode = this.executeCrowdServiceMethod();
                if (!this.isSuccess(statusCode)) {
                    this.throwError(statusCode);
                    throw new OperationFailedException(this.response.getStatusLine().getReasonPhrase());
                }
                Unmarshaller unmarshaller = RestExecutor.this.getUnmarshaller(returnType);
                SAXSource source = new SAXSource(SecureXmlParserFactory.newNamespaceAwareXmlReader(), new InputSource(this.response.getEntity().getContent()));
                Object object = unmarshaller.unmarshal((Source)source, returnType).getValue();
                return (T)object;
            }
            catch (IOException | JAXBException e) {
                throw new OperationFailedException(e);
            }
            finally {
                if (this.response != null) {
                    EntityUtils.consumeQuietly((HttpEntity)this.response.getEntity());
                }
            }
        }

        @Nullable
        <T> T andOptionallyReceive(Class<T> returnType) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, CrowdRestException {
            this.configureHeaders();
            try {
                int statusCode = this.executeCrowdServiceMethod();
                if (!this.isSuccess(statusCode)) {
                    this.throwError(statusCode);
                    throw new OperationFailedException(this.response.getStatusLine().getReasonPhrase());
                }
                if (this.response.getEntity() == null) {
                    T t = null;
                    return t;
                }
                Object object = RestExecutor.this.getUnmarshaller(returnType).unmarshal((Source)new StreamSource(this.response.getEntity().getContent()), returnType).getValue();
                return (T)object;
            }
            catch (IOException | JAXBException e) {
                throw new OperationFailedException(e);
            }
            finally {
                if (this.response != null) {
                    EntityUtils.consumeQuietly((HttpEntity)this.response.getEntity());
                }
            }
        }

        boolean doesExist() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, CrowdRestException {
            try {
                int statusCode = this.executeCrowdServiceMethod();
                EntityUtils.consume((HttpEntity)this.response.getEntity());
                if (this.isSuccess(statusCode)) {
                    return true;
                }
                if (statusCode == 404) {
                    return false;
                }
                this.throwError(statusCode);
                throw new OperationFailedException(this.response.getStatusLine().getReasonPhrase());
            }
            catch (IOException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }

        void andCheckResponse() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, CrowdRestException {
            this.configureHeaders();
            try {
                int statusCode = this.executeCrowdServiceMethod();
                if (!this.isSuccess(statusCode)) {
                    this.throwError(statusCode);
                    throw new OperationFailedException(this.response.getStatusLine().getReasonPhrase());
                }
                EntityUtils.consumeQuietly((HttpEntity)this.response.getEntity());
            }
            catch (IOException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }

        private boolean isSuccess(int statusCode) {
            return statusCode >= 200 && statusCode < 300;
        }

        int executeCrowdServiceMethod() throws InvalidCrowdServiceException, IOException, InvalidAuthenticationException {
            HttpCacheContext context = RestExecutor.this.contextForThisThread();
            this.response = RestExecutor.this.client.execute(this.request, (HttpContext)context);
            logger.debug("Cache response for {} {} was {}", new Object[]{this.request.getMethod(), this.request.getURI(), context.getCacheResponseStatus()});
            if (!this.isCrowdRestService()) {
                EntityUtils.consumeQuietly((HttpEntity)this.response.getEntity());
                throw new InvalidCrowdServiceException(String.format(RestExecutor.INVALID_REST_SERVICE_MSG_FORMAT, this.request.getURI().toString()));
            }
            return this.response.getStatusLine().getStatusCode();
        }

        private boolean isCrowdRestService() {
            return this.response.containsHeader(RestExecutor.EMBEDDED_CROWD_VERSION_NAME);
        }

        private void configureHeaders() {
            this.request.setHeader("Accept", "application/xml");
            this.request.setHeader("X-Atlassian-Token", "no-check");
        }

        void throwError(int errorCode) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, CrowdRestException {
            String reasonPhrase = Strings.isNullOrEmpty((String)this.response.getStatusLine().getReasonPhrase()) ? EnglishReasonPhraseCatalog.INSTANCE.getReason(this.response.getStatusLine().getStatusCode(), Locale.getDefault()) : this.response.getStatusLine().getReasonPhrase();
            try {
                if (errorCode == 403) {
                    throw new ApplicationPermissionException(RestExecutor.getExceptionMessageFromResponse(this.response));
                }
                if (errorCode == 401) {
                    throw new InvalidAuthenticationException(RestExecutor.getExceptionMessageFromResponse(this.response));
                }
                if (errorCode >= 300) {
                    if (this.statusCodesWithoutErrorEntity.contains(errorCode)) {
                        throw new CrowdRestException("HTTP error: " + errorCode + " " + reasonPhrase + ". Response body: " + EntityUtils.toString((HttpEntity)this.response.getEntity()), null, errorCode);
                    }
                    ErrorEntity errorEntity = (ErrorEntity)RestExecutor.this.getUnmarshaller(ErrorEntity.class).unmarshal((Source)new StreamSource(this.response.getEntity().getContent()), ErrorEntity.class).getValue();
                    throw new CrowdRestException(errorEntity.getMessage(), errorEntity, errorCode);
                }
            }
            catch (IOException | JAXBException e) {
                throw new OperationFailedException(reasonPhrase);
            }
            catch (DataBindingException dbe) {
                throw new OperationFailedException(reasonPhrase, (Throwable)dbe);
            }
            finally {
                EntityUtils.consumeQuietly((HttpEntity)this.response.getEntity());
            }
        }
    }
}

