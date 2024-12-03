/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpPost
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReader;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.http.SimpleHttpResponse;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.impl.PageImpl;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.TestModelBuilders;
import com.atlassian.marketplace.client.util.UriBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.http.client.methods.HttpPost;

abstract class ApiImplBase {
    protected final ApiHelper apiHelper;
    protected final InternalModel.MinimalLinks root;
    protected final URI apiRoot;

    protected ApiImplBase(ApiHelper apiHelper, InternalModel.MinimalLinks root, String rootLinkRel) throws MpacException {
        this.apiHelper = apiHelper;
        this.root = root;
        this.apiRoot = apiHelper.requireLinkUri(root.getLinks(), rootLinkRel, root.getClass());
    }

    protected URI getApiRoot() {
        return this.apiRoot;
    }

    protected UriBuilder fromApiRoot() {
        return UriBuilder.fromUri(this.apiRoot);
    }

    protected Links getLinksOnly(URI uri) throws MpacException {
        InternalModel.MinimalLinks rep = this.apiHelper.getEntity(uri, InternalModel.MinimalLinks.class);
        return rep.getLinks();
    }

    protected <T> T genericCreate(URI collectionUri, T entity, Optional<Consumer<HttpPost>> modifyRequest) throws MpacException {
        return this.genericCreate(collectionUri, entity, Function.identity(), modifyRequest);
    }

    protected <T> T genericCreate(URI collectionUri, T entity, Function<URI, URI> resultUriTransform, Optional<Consumer<HttpPost>> modifyRequest) throws MpacException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.apiHelper.getEncoding().encode(bos, entity, false);
        byte[] bytes = bos.toByteArray();
        return this.createdOrUpdatedEntityResult(this.apiHelper.getHttp().post(this.apiHelper.resolveLink(collectionUri), new ByteArrayInputStream(bytes), bytes.length, "application/json", "application/json", modifyRequest), entity.getClass(), resultUriTransform);
    }

    protected <T> T genericUpdate(URI uri, T original, T updated) throws MpacException {
        if (uri == TestModelBuilders.DEFAULT_URI) {
            throw new MpacException.CannotUpdateNonServerSideEntity();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.apiHelper.getEncoding().encodeChanges(bos, original, updated);
        byte[] bytes = bos.toByteArray();
        return this.createdOrUpdatedEntityResult(this.apiHelper.getHttp().patch(this.apiHelper.resolveLink(uri), bytes), original.getClass(), Function.identity());
    }

    private <T> T createdOrUpdatedEntityResult(SimpleHttpResponse response, Class<?> entityClass, Function<URI, URI> resultUriTransform) throws MpacException {
        try {
            int status = response.getStatus();
            if (status == 200 || status == 201 || status == 204) {
                Iterator<String> iterator = response.getHeader("Location").iterator();
                if (iterator.hasNext()) {
                    String location = iterator.next();
                    Object obj = this.apiHelper.getEntityUncached(resultUriTransform.apply(URI.create(location)), entityClass);
                    return (T)obj;
                }
                throw new MpacException("Server did not return expected Location header");
            }
            throw this.apiHelper.responseException(response);
        }
        finally {
            ApiHelper.closeQuietly(response);
        }
    }

    protected <T, U extends InternalModel.EntityCollection<T>> PageReader<T> pageReader(final Class<U> collectionRepClass) {
        return new PageReader<T>(){

            @Override
            public Page<T> readPage(PageReference<T> ref, InputStream in) throws MpacException {
                InternalModel.EntityCollection rep = (InternalModel.EntityCollection)ApiImplBase.this.apiHelper.decode(in, collectionRepClass);
                return new PageImpl(ref, rep.getLinks(), rep.getItems(), rep.getCount(), this);
            }
        };
    }
}

