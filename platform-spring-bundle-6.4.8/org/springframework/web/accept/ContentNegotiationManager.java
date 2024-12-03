/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.MappingMediaTypeFileExtensionResolver;
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
        return this.doResolveExtensions(resolver -> resolver.resolveFileExtensions(mediaType));
    }

    @Override
    public List<String> getAllFileExtensions() {
        return this.doResolveExtensions(MediaTypeFileExtensionResolver::getAllFileExtensions);
    }

    private List<String> doResolveExtensions(Function<MediaTypeFileExtensionResolver, List<String>> extractor) {
        ArrayList<String> result = null;
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            List<String> extensions = extractor.apply(resolver);
            if (CollectionUtils.isEmpty(extensions)) continue;
            result = result != null ? result : new ArrayList<String>(4);
            for (String extension : extensions) {
                if (result.contains(extension)) continue;
                result.add(extension);
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    public Map<String, MediaType> getMediaTypeMappings() {
        HashMap<String, MediaType> result = null;
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            Map<String, MediaType> map;
            if (!(resolver instanceof MappingMediaTypeFileExtensionResolver) || CollectionUtils.isEmpty(map = ((MappingMediaTypeFileExtensionResolver)resolver).getMediaTypes())) continue;
            result = result != null ? result : new HashMap<String, MediaType>(4);
            result.putAll(map);
        }
        return result != null ? result : Collections.emptyMap();
    }
}

