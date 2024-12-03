/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.DelegatingLinkMarshaller;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkMarshallerFactory;
import com.atlassian.confluence.xhtml.api.Link;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ViewLinkMarshallerContextConfig {
    @Resource
    private ResourceIdentifierContextUtility resourceIdentifierContextUtility;
    @Resource
    private ViewLinkMarshallerFactory viewLinkMarshallerFactory;
    @Resource
    private Marshaller<Link> viewAttachmentLinkMarshaller;
    @Resource
    private Marshaller<Link> viewShortcutLinkMarshaller;
    @Resource
    private Marshaller<Link> viewSpaceLinkMarshaller;
    @Resource
    private Marshaller<Link> viewContentEntityLinkMarshaller;
    @Resource
    private Marshaller<Link> viewIdAndTypeLinkMarshaller;
    @Resource
    private Marshaller<Link> viewDraftLinkMarshaller;

    ViewLinkMarshallerContextConfig() {
    }

    @Bean
    Marshaller<Link> viewLinkMarshaller() {
        return new DelegatingLinkMarshaller((Map<String, Marshaller<Link>>)ViewLinkMarshallerContextConfig.transformKeys(this.getDelegateMarshallersByResourceIdentifierType(), Class::getSimpleName), this.resourceIdentifierContextUtility);
    }

    private Map<Class<? extends ResourceIdentifier>, Marshaller<Link>> getDelegateMarshallersByResourceIdentifierType() {
        return ImmutableMap.builder().put(PageResourceIdentifier.class, this.viewLinkMarshallerFactory.newPageLinkMarshaller()).put(BlogPostResourceIdentifier.class, this.viewLinkMarshallerFactory.newBlogPostLinkMarshaller()).put(AttachmentResourceIdentifier.class, this.viewAttachmentLinkMarshaller).put(ShortcutResourceIdentifier.class, this.viewShortcutLinkMarshaller).put(UserResourceIdentifier.class, this.viewLinkMarshallerFactory.newUserLinkMarshaller()).put(SpaceResourceIdentifier.class, this.viewSpaceLinkMarshaller).put(ContentEntityResourceIdentifier.class, this.viewContentEntityLinkMarshaller).put(IdAndTypeResourceIdentifier.class, this.viewIdAndTypeLinkMarshaller).put(DraftResourceIdentifier.class, this.viewDraftLinkMarshaller).put(PageTemplateResourceIdentifier.class, this.viewLinkMarshallerFactory.newPageTemplateLinkMarshaller()).build();
    }

    private static <K1, K2, V> ImmutableMap<K2, V> transformKeys(Map<K1, V> map, Function<K1, K2> keyFunction) {
        return (ImmutableMap)map.entrySet().stream().collect(ImmutableMap.toImmutableMap(entry -> keyFunction.apply(entry.getKey()), Map.Entry::getValue));
    }
}

