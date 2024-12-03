/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

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
import com.atlassian.confluence.xhtml.api.Link;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EditorLinkMarshallerContextConfig {
    @Resource
    private ResourceIdentifierContextUtility resourceIdentifierContextUtility;
    @Resource
    private Marshaller<Link> editorPageLinkMarshaller;
    @Resource
    private Marshaller<Link> editorBlogPostLinkMarshaller;
    @Resource
    private Marshaller<Link> editorAttachmentLinkMarshaller;
    @Resource
    private Marshaller<Link> editorShortcutLinkMarshaller;
    @Resource
    private Marshaller<Link> editorUserLinkMarshaller;
    @Resource
    private Marshaller<Link> editorSpaceLinkMarshaller;
    @Resource
    private Marshaller<Link> editorContentEntityLinkMarshaller;
    @Resource
    private Marshaller<Link> editorIdAndTypeLinkMarshaller;
    @Resource
    private Marshaller<Link> editorDraftLinkMarshaller;
    @Resource
    private Marshaller<Link> editorPageTemplateLinkMarshaller;

    EditorLinkMarshallerContextConfig() {
    }

    @Bean
    Marshaller<Link> editorLinkMarshaller() {
        return new DelegatingLinkMarshaller((Map<String, Marshaller<Link>>)EditorLinkMarshallerContextConfig.transformKeys(this.getDelegateMarshallersByResourceIdentifierType(), Class::getSimpleName), this.resourceIdentifierContextUtility);
    }

    private Map<Class<? extends ResourceIdentifier>, Marshaller<Link>> getDelegateMarshallersByResourceIdentifierType() {
        return ImmutableMap.builder().put(PageResourceIdentifier.class, this.editorPageLinkMarshaller).put(BlogPostResourceIdentifier.class, this.editorBlogPostLinkMarshaller).put(AttachmentResourceIdentifier.class, this.editorAttachmentLinkMarshaller).put(ShortcutResourceIdentifier.class, this.editorShortcutLinkMarshaller).put(UserResourceIdentifier.class, this.editorUserLinkMarshaller).put(SpaceResourceIdentifier.class, this.editorSpaceLinkMarshaller).put(ContentEntityResourceIdentifier.class, this.editorContentEntityLinkMarshaller).put(IdAndTypeResourceIdentifier.class, this.editorIdAndTypeLinkMarshaller).put(DraftResourceIdentifier.class, this.editorDraftLinkMarshaller).put(PageTemplateResourceIdentifier.class, this.editorPageTemplateLinkMarshaller).build();
    }

    private static <K1, K2, V> ImmutableMap<K2, V> transformKeys(Map<K1, V> map, Function<K1, K2> keyFunction) {
        return (ImmutableMap)map.entrySet().stream().collect(ImmutableMap.toImmutableMap(entry -> keyFunction.apply(entry.getKey()), Map.Entry::getValue));
    }
}

