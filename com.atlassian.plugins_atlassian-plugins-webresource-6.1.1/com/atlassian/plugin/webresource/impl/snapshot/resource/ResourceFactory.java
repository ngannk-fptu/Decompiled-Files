/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider.ContentProviderStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider.ContentProviderStrategyFactory;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategyFactory;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategyFactory;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategyFactory;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ResourceFactory {
    private final StreamStrategyFactory streamStrategyFactory;
    private final PathStrategyFactory pathStrategyFactory;
    private final ContentProviderStrategyFactory contentProviderStrategyFactory;
    private final ContentTypeStrategyFactory contentTypeStrategyFactory;

    public ResourceFactory(ServletContextFactory servletContextFactory, WebResourceIntegration webResourceIntegration) {
        this.streamStrategyFactory = new StreamStrategyFactory(servletContextFactory, webResourceIntegration);
        this.pathStrategyFactory = new PathStrategyFactory();
        this.contentProviderStrategyFactory = new ContentProviderStrategyFactory();
        this.contentTypeStrategyFactory = new ContentTypeStrategyFactory();
    }

    public Resource createResource(@Nonnull Bundle parent, @Nonnull ResourceLocation resourceLocation, @Nonnull String nameType, @Nonnull String locationType) {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(resourceLocation);
        Objects.requireNonNull(nameType);
        Objects.requireNonNull(locationType);
        ContentTypeStrategy contentTypeStrategy = this.contentTypeStrategyFactory.createDefaultContentTypeStrategy(resourceLocation);
        StreamStrategy streamStrategy = this.streamStrategyFactory.createStandardModuleStreamStrategy(parent, resourceLocation);
        PathStrategy pathStrategy = this.pathStrategyFactory.createPath(resourceLocation);
        ContentProviderStrategy contentProviderStrategy = this.contentProviderStrategyFactory.createStreamContentProviderStrategy(streamStrategy, contentTypeStrategy, pathStrategy);
        return new Resource(parent, resourceLocation, nameType, locationType, contentTypeStrategy, streamStrategy, pathStrategy, contentProviderStrategy);
    }

    @Deprecated
    public Resource createResourceWithRelativePath(@Nonnull Bundle parent, @Nonnull ResourceLocation resourceLocation, @Nonnull String nameType, @Nonnull String locationType, @Nonnull String overriddenPath) {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(resourceLocation);
        Objects.requireNonNull(nameType);
        Objects.requireNonNull(locationType);
        Objects.requireNonNull(overriddenPath);
        ContentTypeStrategy contentTypeStrategy = this.contentTypeStrategyFactory.createDefaultContentTypeStrategy(resourceLocation);
        StreamStrategy streamStrategy = this.streamStrategyFactory.createStandardModuleStreamStrategy(parent, resourceLocation);
        PathStrategy pathStrategy = this.pathStrategyFactory.createRelativePath(resourceLocation, overriddenPath);
        ContentProviderStrategy contentProviderStrategy = this.contentProviderStrategyFactory.createStreamContentProviderStrategy(streamStrategy, contentTypeStrategy, pathStrategy);
        return new Resource(parent, resourceLocation, nameType, locationType, contentTypeStrategy, streamStrategy, pathStrategy, contentProviderStrategy);
    }
}

