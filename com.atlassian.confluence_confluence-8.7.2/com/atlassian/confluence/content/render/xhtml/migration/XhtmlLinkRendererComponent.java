/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.SubRenderer
 *  com.atlassian.renderer.v2.components.RendererComponent
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.LinkResolver;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.InvalidMigrationException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLinkBuilder;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.links.ConfluenceLinkResolver;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.RendererComponent;
import java.text.MessageFormat;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class XhtmlLinkRendererComponent
implements RendererComponent {
    private static final char START_LINK_CHAR = '[';
    private static final char ESCAPE_CHAR = '\\';
    private static final char END_LINK_CHAR = ']';
    private static final char NEW_LINE_CHAR = '\n';
    private static final String MACRO_ELEMENT_FRAGMENT = "<" + StorageMacroConstants.MACRO_ELEMENT.getPrefix() + ":" + StorageMacroConstants.MACRO_ELEMENT.getLocalPart();
    private final LinkResolver linkResolver;
    private final Marshaller<Link> linkMarshaller;
    private final SubRenderer subRenderer;
    private final StorageFormatCleaner storageFormatCleaner;

    public XhtmlLinkRendererComponent(LinkResolver linkResolver, Marshaller<Link> linkMarshaller, SubRenderer subRenderer, StorageFormatCleaner storageFormatCleaner) {
        this.linkResolver = linkResolver;
        this.linkMarshaller = linkMarshaller;
        this.subRenderer = subRenderer;
        this.storageFormatCleaner = storageFormatCleaner;
    }

    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinks();
    }

    public String render(String wiki, RenderContext renderContext) {
        if (!(renderContext instanceof PageContext)) {
            throw new IllegalArgumentException("context must be an instance of PageContext.");
        }
        PageContext pageContext = (PageContext)renderContext;
        if (wiki == null || wiki.length() < 3) {
            return wiki;
        }
        StringBuffer result = new StringBuffer(wiki.length());
        char[] wikiChars = wiki.toCharArray();
        boolean inLink = false;
        StringBuffer linkText = new StringBuffer(20);
        char prev = '\u0000';
        for (int i = 0; i < wikiChars.length; ++i) {
            char c = wikiChars[i];
            if ('[' == c) {
                if (inLink) {
                    if (prev == '\\') {
                        linkText.setCharAt(linkText.length() - 1, c);
                    } else {
                        result.append(linkText);
                        linkText = new StringBuffer(20);
                        linkText.append(c);
                    }
                } else if (prev == '\\') {
                    linkText.setCharAt(linkText.length() - 1, c);
                } else {
                    inLink = true;
                    linkText.append(c);
                }
            } else if (']' == c && inLink) {
                if (prev == '\\') {
                    linkText.setCharAt(linkText.length() - 1, c);
                } else {
                    inLink = false;
                    if (linkText.length() == 1) {
                        result.append(linkText);
                        result.append(c);
                    } else {
                        String linkBody = linkText.substring(1);
                        this.appendLink(result, pageContext, linkBody);
                    }
                    linkText = new StringBuffer(20);
                }
            } else if (Character.isWhitespace(c) && '[' == prev) {
                inLink = false;
                result.append(linkText);
                result.append(c);
                linkText = new StringBuffer(20);
            } else if ('\n' == c && inLink) {
                inLink = false;
                result.append(linkText);
                result.append(c);
                linkText = new StringBuffer(20);
            } else if (!inLink) {
                result.append(c);
            } else {
                linkText.append(c);
            }
            prev = c;
        }
        if (linkText.length() > 0) {
            result.append(linkText);
        }
        return result.toString();
    }

    private void appendLink(StringBuffer result, PageContext context, String linkText) {
        Link link = this.resolveLink(linkText, context);
        if (link == null) {
            return;
        }
        ResourceIdentifier destination = link.getDestinationResourceIdentifier();
        String renderedOutput = "";
        if (this.isUncleanUrlResourceIdentifier(destination)) {
            String linkBody = "";
            if (link.getBody() != null && link.getBody().getBody() != null) {
                linkBody = link.getBody().getBody().toString();
            }
            renderedOutput = ConfluenceLinkResolver.getLinkAsPlainText(linkBody, ((UrlResourceIdentifier)destination).getUrl());
        } else {
            renderedOutput = this.convertLink(link, context, linkText);
        }
        result.append(context.getRenderedContentStore().addInline((Object)renderedOutput));
    }

    private boolean isUncleanUrlResourceIdentifier(ResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier instanceof UrlResourceIdentifier) {
            return !this.storageFormatCleaner.isCleanUrlAttribute(((UrlResourceIdentifier)resourceIdentifier).getUrl());
        }
        return false;
    }

    private String convertLink(Link link, PageContext context, String linkText) {
        Object linkMarshalOutput;
        PlainTextLinkBody body = (PlainTextLinkBody)link.getBody();
        String linkBody = body.getBody();
        linkBody = linkText != null && linkText.equals(linkBody) ? this.subRenderer.render(linkBody, (RenderContext)context, RenderMode.allow((long)4224L)) : this.subRenderer.render(linkBody, (RenderContext)context, RenderMode.PHRASES_IMAGES);
        DefaultConversionContext conversionContext = new DefaultConversionContext(context);
        try {
            DefaultLinkBuilder linkBuilder = DefaultLink.builder(link);
            linkMarshalOutput = StringUtils.isBlank((CharSequence)linkBody) ? Streamables.writeToString(this.linkMarshaller.marshal(linkBuilder.withBody(Optional.empty()).build(), conversionContext)) : (linkBody != null && linkBody.contains(MACRO_ELEMENT_FRAGMENT) ? linkBody + " " + Streamables.writeToString(this.linkMarshaller.marshal(linkBuilder.withBody(Optional.empty()).build(), conversionContext)) : Streamables.writeToString(this.linkMarshaller.marshal(linkBuilder.withBody(new RichTextLinkBody(linkBody)).build(), conversionContext)));
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
        return linkMarshalOutput;
    }

    private Link resolveLink(String linkText, PageContext context) {
        Link link = this.linkResolver.resolve(linkText, context);
        if (link == null) {
            throw new InvalidMigrationException(MessageFormat.format("The link text \"{0}\" could not be resolved as a link", linkText));
        }
        return link;
    }
}

