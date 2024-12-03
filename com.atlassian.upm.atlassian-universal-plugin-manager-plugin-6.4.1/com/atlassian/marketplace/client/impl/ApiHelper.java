/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  io.atlassian.fugue.Option
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.AddonVersionsQuery;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.EnumWithKey;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.ProductQuery;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.QueryProperties;
import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.api.VendorQuery;
import com.atlassian.marketplace.client.http.HttpTransport;
import com.atlassian.marketplace.client.http.RequestDecorator;
import com.atlassian.marketplace.client.http.SimpleHttpResponse;
import com.atlassian.marketplace.client.impl.EntityEncoding;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.ErrorDetail;
import com.atlassian.marketplace.client.model.Link;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.util.Convert;
import com.atlassian.marketplace.client.util.UriBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import io.atlassian.fugue.Option;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;

class ApiHelper {
    public static final String JSON = "application/json";
    private static final RequestDecorator NO_CACHE = RequestDecorator.Instances.forHeaders((Map<String, String>)ImmutableMap.of((Object)"Cache-Control", (Object)"no-cache"));
    private final URI baseUri;
    private final HttpTransport httpHelper;
    private final EntityEncoding encoding;

    public ApiHelper(URI baseUri, HttpTransport httpHelper, EntityEncoding encoding) {
        this.baseUri = baseUri;
        this.httpHelper = httpHelper;
        this.encoding = encoding;
    }

    public EntityEncoding getEncoding() {
        return this.encoding;
    }

    public HttpTransport getHttp() {
        return this.httpHelper;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean checkReachable(URI resource) {
        boolean bl;
        SimpleHttpResponse response = null;
        try {
            response = this.httpHelper.get(resource);
            bl = !this.errorOrEmpty(response.getStatus());
        }
        catch (MpacException e) {
            boolean bl2;
            try {
                bl2 = false;
            }
            catch (Throwable throwable) {
                ApiHelper.closeQuietly(response);
                throw throwable;
            }
            ApiHelper.closeQuietly(response);
            return bl2;
        }
        ApiHelper.closeQuietly(response);
        return bl;
    }

    public static URI normalizeBaseUri(URI baseUri) {
        URI norm = baseUri.normalize();
        if (norm.getPath().endsWith("/")) {
            return norm;
        }
        return URI.create(norm.toString() + "/");
    }

    public <T> T getEntity(URI uri, Class<T> type) throws MpacException {
        return this.getEntityInternal(this.httpHelper, uri, type);
    }

    public <T> T getEntityUncached(URI uri, Class<T> type) throws MpacException {
        return this.getEntityInternal(this.httpHelper.withRequestDecorator(NO_CACHE), uri, type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T getEntityInternal(HttpTransport h, URI uri, Class<T> type) throws MpacException {
        T t;
        SimpleHttpResponse response = null;
        try {
            response = h.get(this.resolveLink(uri));
            if (this.errorOrEmpty(response.getStatus())) {
                throw this.responseException(response);
            }
            t = this.decode(response.getContentStream(), type);
        }
        catch (Throwable throwable) {
            ApiHelper.closeQuietly(response);
            throw throwable;
        }
        ApiHelper.closeQuietly(response);
        return t;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> Optional<T> safeGetOptionalEntity(URI uri, Class<T> type) throws MpacException {
        SimpleHttpResponse response;
        block6: {
            block5: {
                Optional optional;
                response = null;
                try {
                    response = this.httpHelper.get(this.resolveLink(uri));
                    if (response.getStatus() != 204 && response.getStatus() != 404) break block5;
                    optional = Optional.empty();
                }
                catch (Throwable throwable) {
                    ApiHelper.closeQuietly(response);
                    throw throwable;
                }
                ApiHelper.closeQuietly(response);
                return optional;
            }
            if (this.error(response.getStatus())) {
                throw this.responseException(response);
            }
            if (!response.isEmpty()) break block6;
            Optional optional = Optional.empty();
            ApiHelper.closeQuietly(response);
            return optional;
        }
        Optional<T> optional = Optional.of(this.decode(response.getContentStream(), type));
        ApiHelper.closeQuietly(response);
        return optional;
    }

    @Deprecated
    public <T> Option<T> getOptionalEntity(URI uri, Class<T> type) throws MpacException {
        return Convert.fugueOption(this.safeGetOptionalEntity(uri, type));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void postParams(URI uri, Multimap<String, String> params) throws MpacException {
        SimpleHttpResponse response = null;
        try {
            response = this.httpHelper.postParams(this.resolveLink(uri), params);
            if (this.error(response.getStatus())) {
                throw this.responseException(response);
            }
        }
        catch (Throwable throwable) {
            ApiHelper.closeQuietly(response);
            throw throwable;
        }
        ApiHelper.closeQuietly(response);
    }

    public <T, U> T postEntity(URI uri, U entity, Class<T> type) throws MpacException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.encoding.encode(bos, entity, false);
        byte[] bytes = bos.toByteArray();
        return this.postContent(uri, new ByteArrayInputStream(bytes), bytes.length, JSON, type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T postContent(URI uri, InputStream content, long length, String contentType, Class<T> returnType) throws MpacException {
        T t;
        SimpleHttpResponse response = null;
        try {
            response = this.httpHelper.post(this.resolveLink(uri), content, length, contentType, contentType, Optional.empty());
            if (this.errorOrEmpty(response.getStatus())) {
                throw this.responseException(response);
            }
            t = this.decode(response.getContentStream(), returnType);
        }
        catch (Throwable throwable) {
            ApiHelper.closeQuietly(response);
            throw throwable;
        }
        ApiHelper.closeQuietly(response);
        return t;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> void putEntity(URI uri, T entity) throws MpacException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.encoding.encode(bos, entity, false);
        SimpleHttpResponse response = null;
        try {
            response = this.httpHelper.put(this.resolveLink(uri), bos.toByteArray());
            if (this.error(response.getStatus())) {
                throw this.responseException(response);
            }
        }
        catch (Throwable throwable) {
            ApiHelper.closeQuietly(response);
            throw throwable;
        }
        ApiHelper.closeQuietly(response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T, U> T putEntity(URI uri, U entity, Class<T> type) throws MpacException {
        T t;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.encoding.encode(bos, entity, false);
        SimpleHttpResponse response = null;
        try {
            response = this.httpHelper.put(this.resolveLink(uri), bos.toByteArray());
            if (this.errorOrEmpty(response.getStatus())) {
                throw this.responseException(response);
            }
            t = this.decode(response.getContentStream(), type);
        }
        catch (Throwable throwable) {
            ApiHelper.closeQuietly(response);
            throw throwable;
        }
        ApiHelper.closeQuietly(response);
        return t;
    }

    public void deleteEntity(URI uri) throws MpacException {
        SimpleHttpResponse response = null;
        try {
            response = this.httpHelper.delete(this.resolveLink(uri));
            if (this.error(response.getStatus())) {
                throw this.responseException(response);
            }
        }
        catch (Throwable throwable) {
            ApiHelper.closeQuietly(response);
            throw throwable;
        }
        ApiHelper.closeQuietly(response);
    }

    public URI resolveLink(URI href) {
        return href.isAbsolute() ? href : this.baseUri.resolve(href.toString());
    }

    public static Link requireLink(Links links, String rel, Class<?> entityClass) throws MpacException {
        Iterator iterator = links.getLink(rel).iterator();
        if (iterator.hasNext()) {
            Link l = (Link)iterator.next();
            return l;
        }
        throw new MpacException("Missing required API link \"" + rel + "\" from " + entityClass.getSimpleName());
    }

    public URI requireLinkUri(Links links, String rel, Class<?> entityClass) throws MpacException {
        return this.resolveLink(ApiHelper.requireLink(links, rel, entityClass).getUri());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T decode(InputStream is, Class<T> type) throws MpacException {
        try {
            T t = this.encoding.decode(is, type);
            return t;
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
    }

    public boolean error(int statusCode) {
        return statusCode >= 400;
    }

    public boolean errorOrEmpty(int statusCode) {
        return statusCode >= 400 || statusCode == 204;
    }

    public MpacException responseException(SimpleHttpResponse response) {
        int status = response.getStatus();
        try {
            String body = IOUtils.toString((InputStream)response.getContentStream());
            if (body.trim().startsWith("{")) {
                try {
                    InternalModel.ErrorDetails ed = this.encoding.decode(new ByteArrayInputStream(body.getBytes()), InternalModel.ErrorDetails.class);
                    return new MpacException.ServerError(status, (Iterable<ErrorDetail>)ed.errors);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            return new MpacException.ServerError(status, body);
        }
        catch (Exception e) {
            return new MpacException.ServerError(status);
        }
    }

    protected static void closeQuietly(SimpleHttpResponse response) {
        if (response != null) {
            response.close();
        }
    }

    private static void addOptionalBoolean(UriBuilder uri, String name, boolean value) {
        if (value) {
            uri.queryParam(name, true);
        }
    }

    private static <T extends EnumWithKey> void addOptionalEnumKey(UriBuilder uri, String name, Optional<T> value) {
        for (EnumWithKey v : Convert.iterableOf(value)) {
            uri.queryParam(name, v.getKey());
        }
    }

    private static <T> void addMultiValuedParam(UriBuilder uri, String name, Iterable<T> value) {
        if (!Iterables.isEmpty(value)) {
            uri.queryParam(name, Iterables.toArray(value, Object.class));
        }
    }

    public static void addAddonQueryParams(AddonQuery query, UriBuilder uri) {
        ApiHelper.addAccessTokenParams(query, uri);
        ApiHelper.addApplicationCriteriaParams(query, uri);
        ApiHelper.addMultiValuedParam(uri, "category", query.getCategoryNames());
        ApiHelper.addOptionalEnumKey(uri, "filter", query.safeGetView());
        ApiHelper.addCostParam(query, uri);
        ApiHelper.addOptionalBoolean(uri, "forThisUser", query.isForThisUserOnly());
        ApiHelper.addMultiHostingParam(query, uri);
        ApiHelper.addOptionalEnumKey(uri, "includeHidden", query.safeGetIncludeHidden());
        ApiHelper.addOptionalBoolean(uri, "includePrivate", query.isIncludePrivate());
        for (String label : Convert.iterableOf(query.safeGetLabel())) {
            uri.queryParam("marketingLabel", label);
        }
        for (String searchText : Convert.iterableOf(query.safeGetSearchText())) {
            uri.queryParam("text", searchText);
        }
        ApiHelper.addOptionalEnumKey(uri, "treatPartlyFreeAs", query.safeGetTreatPartlyFreeAs());
        ApiHelper.addWithVersionParam(query, uri);
        ApiHelper.addBoundsParams(query, uri);
    }

    public static void addAddonVersionsQueryParams(AddonVersionsQuery query, UriBuilder uri) {
        ApiHelper.addAccessTokenParams(query, uri);
        ApiHelper.addApplicationCriteriaParams(query, uri);
        ApiHelper.addCostParam(query, uri);
        ApiHelper.addHostingParam(query, uri);
        for (String v : Convert.iterableOf(query.safeGetAfterVersionName())) {
            uri.queryParam("afterVersion", v);
        }
        ApiHelper.addBoundsParams(query, uri);
        ApiHelper.addIncludePrivateParam(query, uri);
    }

    public static void addProductQueryParams(ProductQuery query, UriBuilder uri) {
        ApiHelper.addApplicationCriteriaParams(query, uri);
        ApiHelper.addCostParam(query, uri);
        ApiHelper.addHostingParam(query, uri);
        ApiHelper.addWithVersionParam(query, uri);
        ApiHelper.addBoundsParams(query, uri);
    }

    public static void addAccessTokenParams(QueryProperties.AccessToken q, UriBuilder uriBuilder) {
        for (String a : Convert.iterableOf(q.safeGetAccessToken())) {
            uriBuilder.queryParam("accessToken", a);
        }
    }

    public static void addApplicationCriteriaParams(QueryProperties.ApplicationCriteria q, UriBuilder uriBuilder) {
        for (ApplicationKey a : Convert.iterableOf(q.safeGetApplication())) {
            uriBuilder.queryParam("application", a.getKey());
            for (Integer b : Convert.iterableOf(q.safeGetAppBuildNumber())) {
                uriBuilder.queryParam("applicationBuild", b);
            }
        }
    }

    public static void addBoundsParams(QueryBounds b, UriBuilder uriBuilder) {
        if (b.getOffset() > 0) {
            uriBuilder.queryParam("offset", b.getOffset());
        }
        for (Integer l : Convert.iterableOf(b.safeGetLimit())) {
            uriBuilder.queryParam("limit", l);
        }
    }

    public static void addBoundsParams(QueryProperties.Bounds q, UriBuilder uriBuilder) {
        ApiHelper.addBoundsParams(q.getBounds(), uriBuilder);
    }

    public static void addCostParam(QueryProperties.Cost q, UriBuilder uriBuilder) {
        ApiHelper.addOptionalEnumKey(uriBuilder, "cost", q.safeGetCost());
    }

    public static void addHostingParam(QueryProperties.Hosting q, UriBuilder uriBuilder) {
        ApiHelper.addOptionalEnumKey(uriBuilder, "hosting", q.safeGetHosting());
    }

    public static void addMultiHostingParam(QueryProperties.MultiHosting q, UriBuilder uriBuilder) {
        ApiHelper.addMultiValuedParam(uriBuilder, "hosting", q.getHostings().stream().map(HostingType::getKey).collect(Collectors.toList()));
    }

    public static void addIncludePrivateParam(QueryProperties.IncludePrivate q, UriBuilder uriBuilder) {
        ApiHelper.addOptionalBoolean(uriBuilder, "includePrivate", q.isIncludePrivate());
    }

    public static void addWithVersionParam(QueryProperties.WithVersion q, UriBuilder uriBuilder) {
        ApiHelper.addOptionalBoolean(uriBuilder, "withVersion", q.isWithVersion());
    }

    public static void addVendorQueryParams(VendorQuery q, UriBuilder uriBuilder) {
        ApiHelper.addBoundsParams(q, uriBuilder);
        ApiHelper.addOptionalBoolean(uriBuilder, "forThisUser", q.isForThisUserOnly());
    }

    public static URI withAccessToken(URI u, String token) {
        return UriBuilder.fromUri(u).queryParam("accessToken", token).build();
    }

    public static URI withZeroLimit(URI u) {
        return UriBuilder.fromUri(u).queryParam("limit", 0).build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> Page<T> getMore(PageReference<T> ref) throws MpacException {
        Page<T> page;
        SimpleHttpResponse response = null;
        try {
            response = this.httpHelper.get(this.resolveLink(ref.getUri()));
            if (this.errorOrEmpty(response.getStatus())) {
                throw this.responseException(response);
            }
            page = ref.getReader().readPage(ref, response.getContentStream());
        }
        catch (Throwable throwable) {
            ApiHelper.closeQuietly(response);
            throw throwable;
        }
        ApiHelper.closeQuietly(response);
        return page;
    }

    public static UriTemplate requireLinkUriTemplate(Links links, String rel, Class<?> entityClass) throws MpacException {
        Iterator iterator = links.getUriTemplate(rel).iterator();
        if (iterator.hasNext()) {
            UriTemplate ut = (UriTemplate)iterator.next();
            return ut;
        }
        throw new MpacException("Missing required API link \"" + rel + "\" from " + entityClass.getSimpleName());
    }

    public static <A extends Entity> URI getTemplatedLink(A a, String rel, String paramName, String paramValue) throws MpacException {
        UriTemplate t = ApiHelper.requireLinkUriTemplate(a.getLinks(), rel, a.getClass());
        return t.resolve((Map<String, String>)ImmutableMap.of((Object)paramName, (Object)paramValue));
    }
}

