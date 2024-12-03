/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierMatcher;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkBodyMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkSubMarshallerFactory;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewUnresolvedLinkBodyMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewUnresolvedLinkMarshaller;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;

public class ViewLinkSubMarshallerFactoryImpl
implements ViewLinkSubMarshallerFactory {
    private final Marshaller<EmbeddedImage> embeddedImageMarshaller;
    private final ResourceIdentifierResolver<SpaceResourceIdentifier, Space> spaceResourceIdentifierResolver;
    private final ResourceIdentifierResolver<ContentEntityResourceIdentifier, ContentEntityObject> contentEntityResourceIdentifierResolver;
    private final ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver;
    private final ConfluenceUserResolver userResolver;
    private final ResourceIdentifierMatcher resourceIdentifierMatcher;
    private final ResourceIdentifierContextUtility riContextUtil;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final ModelToRenderedClassMapper mapper;
    private final Marshaller<Link> unresolvedLinkBodyMarshallerSingleton;
    private final ShortcutLinksManager shortcutLinksManager;

    public ViewLinkSubMarshallerFactoryImpl(Marshaller<EmbeddedImage> embeddedImageMarshaller, ResourceIdentifierResolver<SpaceResourceIdentifier, Space> spaceResourceIdentifierResolver, ResourceIdentifierResolver<ContentEntityResourceIdentifier, ContentEntityObject> contentEntityResourceIdentifierResolver, ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver, ConfluenceUserResolver userResolver, ResourceIdentifierMatcher resourceIdentifierMatcher, ResourceIdentifierContextUtility riContextUtil, XmlStreamWriterTemplate xmlStreamWriterTemplate, ModelToRenderedClassMapper mapper, ShortcutLinksManager shortcutLinksManager) {
        this.embeddedImageMarshaller = embeddedImageMarshaller;
        this.spaceResourceIdentifierResolver = spaceResourceIdentifierResolver;
        this.contentEntityResourceIdentifierResolver = contentEntityResourceIdentifierResolver;
        this.idAndTypeResourceIdentifierResolver = idAndTypeResourceIdentifierResolver;
        this.userResolver = userResolver;
        this.resourceIdentifierMatcher = resourceIdentifierMatcher;
        this.riContextUtil = riContextUtil;
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.unresolvedLinkBodyMarshallerSingleton = this.newUnresolvedLinkBodyMarshaller();
        this.mapper = mapper;
        this.shortcutLinksManager = shortcutLinksManager;
    }

    @Override
    public Marshaller<Link> newLinkBodyMarshaller() {
        return new ViewLinkBodyMarshaller(this.embeddedImageMarshaller, this.spaceResourceIdentifierResolver, this.contentEntityResourceIdentifierResolver, this.idAndTypeResourceIdentifierResolver, this.userResolver, this.resourceIdentifierMatcher, this.riContextUtil, this.shortcutLinksManager);
    }

    @Override
    public Marshaller<UnresolvedLink> newUnresolvedLinkMarshaller() {
        return new ViewUnresolvedLinkMarshaller(this.xmlStreamWriterTemplate, null, this.unresolvedLinkBodyMarshallerSingleton, this.mapper);
    }

    @Override
    public Marshaller<Link> newUnresolvedLinkBodyMarshaller() {
        return new ViewUnresolvedLinkBodyMarshaller(this.embeddedImageMarshaller);
    }
}

