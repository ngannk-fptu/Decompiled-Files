/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.links.CreatePageLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.user.User;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewCreatePageLinkMarshaller
implements Marshaller<CreatePageLink> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final PermissionManager permissionManager;
    private final Marshaller<Link> unresolvedLinkMarshaller;
    private final StaxStreamMarshaller<PageResourceIdentifier> pageResourceIdentifierStaxStreamMarshaller;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final SpaceManager spaceManager;

    public ViewCreatePageLinkMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, PermissionManager permissionManager, Marshaller<Link> unresolvedLinkMarshaller, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, @Nullable StaxStreamMarshaller<PageResourceIdentifier> pageResourceIdentifierStaxStreamMarshaller, SpaceManager spaceManager) {
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.unresolvedLinkMarshaller = Objects.requireNonNull(unresolvedLinkMarshaller);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.pageResourceIdentifierStaxStreamMarshaller = pageResourceIdentifierStaxStreamMarshaller;
        this.spaceManager = Objects.requireNonNull(spaceManager);
    }

    @Override
    public Streamable marshal(CreatePageLink createPageLink, ConversionContext conversionContext) throws XhtmlException {
        Space space;
        ResourceIdentifier resourceId = createPageLink.getDelegate().getDestinationResourceIdentifier();
        if (!(resourceId instanceof PageResourceIdentifier)) {
            throw new XhtmlException("A CreatePageLink can only contain a PageResourceIdentifier Link.");
        }
        PageResourceIdentifier pageResourceIdentifier = (PageResourceIdentifier)resourceId;
        String spaceKey = pageResourceIdentifier.getSpaceKey();
        if (StringUtils.isBlank((CharSequence)spaceKey) && conversionContext != null && conversionContext.getPageContext() != null) {
            spaceKey = conversionContext.getPageContext().getSpaceKey();
        }
        if ((space = this.spaceManager.getSpace(spaceKey)) == null || !this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)space, Page.class)) {
            return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(createPageLink.getDelegate()), conversionContext);
        }
        String url = RequestCacheThreadLocal.getContextPath() + "/pages/createpage.action?spaceKey=" + space.getKey() + "&title=" + HtmlUtil.urlEncode(pageResourceIdentifier.getTitle()) + this.getParentPageRequestParameters(conversionContext, space.getKey());
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(createPageLink.getDelegate(), conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            this.commonLinkAttributesWriter.writeCommonAttributes(createPageLink, xmlStreamWriter, conversionContext);
            xmlStreamWriter.writeAttribute("href", url);
            if (this.pageResourceIdentifierStaxStreamMarshaller != null) {
                this.pageResourceIdentifierStaxStreamMarshaller.marshal(pageResourceIdentifier, xmlStreamWriter, conversionContext);
            }
            StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, marshalledLinkBody);
            xmlStreamWriter.writeEndElement();
        });
    }

    private String getParentPageRequestParameters(ConversionContext conversionContext, String spaceKey) {
        Object result = "";
        PageContext pageContext = conversionContext.getPageContext();
        if (pageContext.getEntity() != null && pageContext.getEntity() instanceof Page && spaceKey.equalsIgnoreCase(pageContext.getSpaceKey())) {
            result = "&linkCreation=true&fromPageId=" + pageContext.getEntity().getId();
        }
        return result;
    }
}

