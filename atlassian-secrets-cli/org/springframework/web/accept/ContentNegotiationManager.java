/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.MediaTypeFileExtensionResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class ContentNegotiationManager
implements ContentNegotiationStrategy,
MediaTypeFileExtensionResolver {
    private final List<ContentNegotiationStrategy> strategies = new ArrayList<ContentNegotiationStrategy>();
    private final Set<MediaTypeFileExtensionResolver> resolvers = new LinkedHashSet<MediaTypeFileExtensionResolver>();

    public ContentNegotiationManager(ContentNegotiationStrategy ... strategies) {
        this(Arrays.asList(strategies));
    }

    public ContentNegotiationManager(Collection<ContentNegotiationStrategy> strategies) {
        Assert.notEmpty(strategies, "At least one ContentNegotiationStrategy is expected");
        this.strategies.addAll(strategies);
        for (ContentNegotiationStrategy strategy : this.strategies) {
            if (!(strategy instanceof MediaTypeFileExtensionResolver)) continue;
            this.resolvers.add((MediaTypeFileExtensionResolver)((Object)strategy));
        }
    }

    public ContentNegotiationManager() {
        this(new HeaderContentNegotiationStrategy());
    }

    public List<ContentNegotiationStrategy> getStrategies() {
        return this.strategies;
    }

    @Nullable
    public <T extends ContentNegotiationStrategy> T getStrategy(Class<T> strategyType) {
        for (ContentNegotiationStrategy strategy : this.getStrategies()) {
            if (!strategyType.isInstance(strategy)) continue;
            return (T)strategy;
        }
        return null;
    }

    public void addFileExtensionResolvers(MediaTypeFileExtensionResolver ... resolvers) {
        Collections.addAll(this.resolvers, resolvers);
    }

    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        for (ContentNegotiationStrategy strategy : this.strategies) {
            List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);
            if (mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) continue;
            return mediaTypes;
        }
        return MEDIA_TYPE_ALL_LIST;
    }

    @Override
    public List<String> resolveFileExtensions(MediaType mediaType) {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            result.addAll(resolver.resolveFileExtensions(mediaType));
        }
        return new ArrayList<String>(result);
    }

    @Override
    public List<String> getAllFileExtensions() {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            result.addAll(resolver.getAllFileExtensions());
        }
        return new ArrayList<String>(result);
    }
}

