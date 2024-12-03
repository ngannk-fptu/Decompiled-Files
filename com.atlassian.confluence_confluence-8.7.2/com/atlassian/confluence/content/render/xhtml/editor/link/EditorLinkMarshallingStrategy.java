/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierMatcher;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifierResolver;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.Optional;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class EditorLinkMarshallingStrategy
implements StaxStreamMarshaller<Link> {
    private final Marshaller<Link> linkBodyMarshaller;
    private final PageResourceIdentifierResolver pageResourceIdentifierResolver;
    private final SpaceResourceIdentifierResolver spaceResourceIdentifierResolver;
    private final AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver;
    private final ContentEntityResourceIdentifierResolver contentEntityResourceIdentifierResolver;
    private final BlogPostResourceIdentifierResolver blogPostResourceIdentifierResolver;
    private final SettingsManager settingsManager;
    private final ResourceIdentifierMatcher resourceIdentifierMatcher;

    public EditorLinkMarshallingStrategy(Marshaller<Link> linkBodyMarshaller, PageResourceIdentifierResolver pageResourceIdentifierResolver, SpaceResourceIdentifierResolver spaceResourceIdentifierResolver, AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver, ContentEntityResourceIdentifierResolver contentEntityResourceIdentifierResolver, BlogPostResourceIdentifierResolver blogPostResourceIdentifierResolver, SettingsManager settingsManager, ResourceIdentifierMatcher resourceIdentifierMatcher) {
        this.linkBodyMarshaller = linkBodyMarshaller;
        this.pageResourceIdentifierResolver = pageResourceIdentifierResolver;
        this.spaceResourceIdentifierResolver = spaceResourceIdentifierResolver;
        this.attachmentResourceIdentifierResolver = attachmentResourceIdentifierResolver;
        this.contentEntityResourceIdentifierResolver = contentEntityResourceIdentifierResolver;
        this.blogPostResourceIdentifierResolver = blogPostResourceIdentifierResolver;
        this.settingsManager = settingsManager;
        this.resourceIdentifierMatcher = resourceIdentifierMatcher;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void marshal(Link link, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        String defaultAlias;
        if (StringUtils.isNotBlank((CharSequence)link.getTooltip())) {
            xmlStreamWriter.writeAttribute("title", link.getTooltip());
        }
        if (StringUtils.isNotBlank((CharSequence)link.getAnchor())) {
            xmlStreamWriter.writeAttribute("data-anchor", link.getAnchor());
        }
        if (link.getTarget().isPresent()) {
            xmlStreamWriter.writeAttribute("target", link.getTarget().get());
        }
        if (link instanceof UnresolvedLink) {
            return;
        }
        try {
            DefaultLink linkToMarshall = DefaultLink.builder(link).withBody(Optional.empty()).build();
            defaultAlias = Streamables.writeToString(this.linkBodyMarshaller.marshal(linkToMarshall, context));
        }
        catch (XhtmlException e) {
            throw new XMLStreamException(e);
        }
        String id = null;
        String version = null;
        String type = null;
        ResourceIdentifier ri = link.getDestinationResourceIdentifier();
        if (!(ri == null || context != null && context.getEntity() != null && this.resourceIdentifierMatcher.matches(context.getEntity(), ri))) {
            block23: {
                try {
                    if (ri instanceof PageResourceIdentifier) {
                        Page page = this.pageResourceIdentifierResolver.resolve((PageResourceIdentifier)ri, context);
                        id = page.getIdAsString();
                        version = String.valueOf(page.getVersion());
                        type = "page";
                        break block23;
                    }
                    if (ri instanceof SpaceResourceIdentifier) {
                        Space space = this.spaceResourceIdentifierResolver.resolve((SpaceResourceIdentifier)ri, context);
                        id = String.valueOf(space.getId());
                        type = "space";
                        break block23;
                    }
                    if (ri instanceof AttachmentResourceIdentifier) {
                        Attachment att = this.attachmentResourceIdentifierResolver.resolve((AttachmentResourceIdentifier)ri, context);
                        id = String.valueOf(att.getId());
                        version = String.valueOf(att.getVersion());
                        type = "attachment";
                        ContentEntityObject container = att.getContainer();
                        if (container != null) {
                            xmlStreamWriter.writeAttribute("data-linked-resource-container-id", container.getIdAsString());
                            xmlStreamWriter.writeAttribute("data-linked-resource-container-version", String.valueOf(container.getVersion()));
                        }
                        xmlStreamWriter.writeAttribute("data-linked-resource-content-type", att.getMediaType());
                        break block23;
                    }
                    if (ri instanceof IdAndTypeResourceIdentifier) {
                        id = String.valueOf(((IdAndTypeResourceIdentifier)ri).getId());
                        type = ((IdAndTypeResourceIdentifier)ri).getType().getRepresentation();
                        break block23;
                    }
                    if (ri instanceof ContentEntityResourceIdentifier) {
                        ContentEntityObject ceo = this.contentEntityResourceIdentifierResolver.resolve((ContentEntityResourceIdentifier)ri, context);
                        id = ceo.getIdAsString();
                        version = String.valueOf(ceo.getVersion());
                        type = ceo.getType();
                        break block23;
                    }
                    if (ri instanceof BlogPostResourceIdentifier) {
                        BlogPost post = this.blogPostResourceIdentifierResolver.resolve((BlogPostResourceIdentifier)ri, context);
                        id = post.getIdAsString();
                        version = String.valueOf(post.getVersion());
                        type = "blogpost";
                        break block23;
                    }
                    if (ri instanceof ShortcutResourceIdentifier) {
                        String shortcut = ((ShortcutResourceIdentifier)ri).getShortcutParameter();
                        xmlStreamWriter.writeAttribute("data-linked-resource-shortcut", shortcut + "@" + ((ShortcutResourceIdentifier)ri).getShortcutKey());
                        type = "shortcut";
                    } else if (!(ri instanceof DraftResourceIdentifier)) {
                        // empty if block
                    }
                }
                catch (CannotResolveResourceIdentifierException e) {
                    throw new XMLStreamException(e);
                }
            }
            if (StringUtils.isNotBlank((CharSequence)id)) {
                xmlStreamWriter.writeAttribute("data-linked-resource-id", id);
            }
            if (StringUtils.isNotBlank((CharSequence)version)) {
                xmlStreamWriter.writeAttribute("data-linked-resource-version", version);
            }
            if (StringUtils.isNotBlank((CharSequence)type)) {
                xmlStreamWriter.writeAttribute("data-linked-resource-type", type);
            }
        }
        if (StringUtils.isNotBlank((CharSequence)defaultAlias)) {
            xmlStreamWriter.writeAttribute("data-linked-resource-default-alias", defaultAlias);
        }
        xmlStreamWriter.writeAttribute("data-base-url", (String)StringUtils.defaultIfEmpty((CharSequence)this.settingsManager.getGlobalSettings().getBaseUrl(), (CharSequence)""));
    }
}

