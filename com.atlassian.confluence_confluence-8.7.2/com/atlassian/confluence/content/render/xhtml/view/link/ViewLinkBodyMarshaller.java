/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierMatcher;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.EmbeddedImageLinkBody;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class ViewLinkBodyMarshaller
implements Marshaller<Link> {
    private final Marshaller<EmbeddedImage> embeddedImageMarshaller;
    private final ResourceIdentifierResolver<SpaceResourceIdentifier, Space> spaceResourceIdentifierResolver;
    private final ResourceIdentifierResolver<ContentEntityResourceIdentifier, ContentEntityObject> contentEntityResourceIdentifierResolver;
    private final ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final ResourceIdentifierMatcher resourceIdentifierMatcher;
    private final ResourceIdentifierContextUtility riContextUtil;
    private final ShortcutLinksManager shortcutLinksManager;

    public ViewLinkBodyMarshaller(Marshaller<EmbeddedImage> embeddedImageMarshaller, ResourceIdentifierResolver<SpaceResourceIdentifier, Space> spaceResourceIdentifierResolver, ResourceIdentifierResolver<ContentEntityResourceIdentifier, ContentEntityObject> contentEntityResourceIdentifierResolver, ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver, ConfluenceUserResolver confluenceUserResolver, ResourceIdentifierMatcher resourceIdentifierMatcher, ResourceIdentifierContextUtility resourceIdentifierContextUtility, ShortcutLinksManager shortcutLinksManager) {
        this.embeddedImageMarshaller = embeddedImageMarshaller;
        this.spaceResourceIdentifierResolver = spaceResourceIdentifierResolver;
        this.contentEntityResourceIdentifierResolver = contentEntityResourceIdentifierResolver;
        this.idAndTypeResourceIdentifierResolver = idAndTypeResourceIdentifierResolver;
        this.confluenceUserResolver = confluenceUserResolver;
        this.resourceIdentifierMatcher = resourceIdentifierMatcher;
        this.riContextUtil = resourceIdentifierContextUtility;
        this.shortcutLinksManager = shortcutLinksManager;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        if (this.isNullOrEmptyBody(link.getBody())) {
            Object generatedLinkBody = "";
            ResourceIdentifier ri = link.getDestinationResourceIdentifier();
            if (ri == null && conversionContext != null && conversionContext.getEntity() != null) {
                ri = this.riContextUtil.createAbsoluteResourceIdentifier(conversionContext.getEntity());
            }
            if (ri instanceof IdAndTypeResourceIdentifier) {
                Object obj = this.idAndTypeResourceIdentifierResolver.resolve((IdAndTypeResourceIdentifier)ri, conversionContext);
                if (obj instanceof Addressable) {
                    generatedLinkBody = ((Addressable)obj).getDisplayTitle();
                }
            } else if (ri instanceof NamedResourceIdentifier) {
                generatedLinkBody = ((NamedResourceIdentifier)ri).getResourceName();
            } else if (ri instanceof SpaceResourceIdentifier) {
                Space space = this.spaceResourceIdentifierResolver.resolve((SpaceResourceIdentifier)ri, conversionContext);
                if (space != null) {
                    generatedLinkBody = space.getName();
                }
            } else if (ri instanceof ContentEntityResourceIdentifier) {
                ContentEntityObject entity = this.contentEntityResourceIdentifierResolver.resolve((ContentEntityResourceIdentifier)ri, conversionContext);
                if (entity != null) {
                    generatedLinkBody = entity.getTitle();
                }
            } else if (ri instanceof UserResourceIdentifier) {
                UserResourceIdentifier userResourceIdentifier = (UserResourceIdentifier)ri;
                ConfluenceUser user = this.confluenceUserResolver.getUserByKey(userResourceIdentifier.getUserKey());
                if (user != null) {
                    generatedLinkBody = user.getFullName();
                }
            } else if (ri instanceof ShortcutResourceIdentifier) {
                ShortcutLinkConfig shortcutLinkConfig = this.shortcutLinksManager.getShortcutLinkConfig(((ShortcutResourceIdentifier)ri).getShortcutKey());
                generatedLinkBody = shortcutLinkConfig.getDefaultAlias();
            }
            if (StringUtils.isNotBlank((CharSequence)link.getAnchor())) {
                generatedLinkBody = ri instanceof DraftResourceIdentifier || ri instanceof PageTemplateResourceIdentifier || conversionContext != null && conversionContext.getEntity() != null && this.resourceIdentifierMatcher.matches(conversionContext.getEntity(), ri) ? link.getAnchor() : (String)generatedLinkBody + "#" + link.getAnchor();
            }
            if (StringUtils.isBlank((CharSequence)generatedLinkBody)) {
                generatedLinkBody = "unnamed link";
            }
            return Streamables.from(StringEscapeUtils.escapeHtml4((String)generatedLinkBody));
        }
        if (link.getBody() instanceof EmbeddedImageLinkBody) {
            return this.embeddedImageMarshaller.marshal(((EmbeddedImageLinkBody)link.getBody()).getBody(), conversionContext);
        }
        if (link.getBody() instanceof RichTextLinkBody) {
            return Streamables.from(((RichTextLinkBody)link.getBody()).getBody());
        }
        if (link.getBody() instanceof PlainTextLinkBody) {
            return Streamables.from(StringEscapeUtils.escapeHtml4((String)((PlainTextLinkBody)link.getBody()).getBody()));
        }
        throw new UnsupportedOperationException("Unsupported link body: " + link.getBody());
    }

    private boolean isNullOrEmptyBody(LinkBody linkBody) {
        if (linkBody == null) {
            return true;
        }
        return linkBody instanceof PlainTextLinkBody && StringUtils.isBlank((CharSequence)((PlainTextLinkBody)linkBody).getBody());
    }
}

