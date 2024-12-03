/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.DefaultContentTypeStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ModuleContentTypeStrategyDecorator;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ContentTypeStrategyFactory {
    public ContentTypeStrategy createDefaultContentTypeStrategy(@Nonnull ResourceLocation resourceLocation) {
        return new DefaultContentTypeStrategy(Objects.requireNonNull(resourceLocation));
    }

    public ContentTypeStrategy createModuleContentTypeStrategy(@Nonnull ResourceLocation resourceLocation, @Nonnull String type) {
        return new ModuleContentTypeStrategyDecorator(this.createDefaultContentTypeStrategy(resourceLocation), Objects.requireNonNull(type));
    }
}

