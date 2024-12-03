/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.fugue.Option
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.util.FugueConversionUtil;
import io.atlassian.fugue.Option;
import java.util.Map;
import java.util.Optional;

public class ContentExtensionsFactory {
    private final ApiSupportProvider supportProvider;

    public ContentExtensionsFactory(ApiSupportProvider supportProvider) {
        this.supportProvider = supportProvider;
    }

    public Map<String, Object> buildExtensions(ContentConvertible entity, Expansions expansions) {
        ModelMapBuilder builder = ModelMapBuilder.newExpandedInstance();
        ContentTypeApiSupport<ContentConvertible> apiSupport = this.getSupportForEntity(entity);
        Map<ContentId, Map<String, Object>> contentToExtensions = apiSupport.getExtensions((Iterable<ContentConvertible>)Option.some((Object)entity), expansions);
        Map<String, Object> extensions = contentToExtensions.get(entity.getContentId());
        if (extensions != null) {
            builder.copy(extensions);
        }
        return builder.build();
    }

    public Iterable<Link> buildLinks(ContentConvertible entity) {
        return this.getSupportForEntity(entity).getLinks(entity);
    }

    @Deprecated
    public com.atlassian.fugue.Option<Object> getContainerEntity(ContentConvertible entity, Expansions expansions) {
        return this.getSupportForEntity(entity).getContainer(entity, expansions);
    }

    public Optional<Object> containerEntity(ContentConvertible entity, Expansions expansions) {
        return FugueConversionUtil.toOptional(this.getContainerEntity(entity, expansions));
    }

    private <T extends ContentConvertible> ContentTypeApiSupport<T> getSupportForEntity(T entity) {
        return this.supportProvider.getForType(entity.getContentTypeObject());
    }
}

