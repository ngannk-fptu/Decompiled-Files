/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
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
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.links.NotPermittedLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.user.User;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewSpaceLinkMarshaller
implements Marshaller<Link> {
    private static final String UNRESOLVED_SPACE_TOOLTIP_KEY = "com.atlassian.confluence.content.render.xhtml.view.link.ViewSpaceLinkMarshaller.unresolved.link.tooltip";
    private final SpaceResourceIdentifierResolver spaceResourceIdentifierResolver;
    private final PermissionManager permissionManager;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final Marshaller<UnresolvedLink> unresolvedLinkMarshaller;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;
    private final Marshaller<Link> notPermittedLinkMarshaller;
    private final HrefEvaluator hrefEvaluator;
    private final I18NBeanFactory i18NBeanFactory;

    public ViewSpaceLinkMarshaller(SpaceResourceIdentifierResolver spaceResourceIdentifierResolver, PermissionManager permissionManager, XmlStreamWriterTemplate xmlStreamWriterTemplate, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, Marshaller<UnresolvedLink> unresolvedLinkMarshaller, HrefEvaluator hrefEvaluator, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller, Marshaller<Link> notPermittedLinkMarshaller, I18NBeanFactory i18NBeanFactory) {
        this.spaceResourceIdentifierResolver = Objects.requireNonNull(spaceResourceIdentifierResolver);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.unresolvedLinkMarshaller = Objects.requireNonNull(unresolvedLinkMarshaller);
        this.hrefEvaluator = Objects.requireNonNull(hrefEvaluator);
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
        this.notPermittedLinkMarshaller = Objects.requireNonNull(notPermittedLinkMarshaller);
        this.i18NBeanFactory = Objects.requireNonNull(i18NBeanFactory);
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        SpaceResourceIdentifier spaceResourceIdentifier = (SpaceResourceIdentifier)link.getDestinationResourceIdentifier();
        Space space = this.spaceResourceIdentifierResolver.resolve(spaceResourceIdentifier, conversionContext);
        if (space == null) {
            I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
            String unresolvedSpaceLinkTooltip = i18NBean.getText(UNRESOLVED_SPACE_TOOLTIP_KEY);
            return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(link, Optional.of(unresolvedSpaceLinkTooltip)), conversionContext);
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, space)) {
            return this.notPermittedLinkMarshaller.marshal(new NotPermittedLink(link), conversionContext);
        }
        String url = this.hrefEvaluator.createHref(conversionContext, space, link.getAnchor());
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(link, conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            this.commonLinkAttributesWriter.writeCommonAttributes(link, xmlStreamWriter, conversionContext);
            xmlStreamWriter.writeAttribute("href", url);
            if (this.linkStaxStreamMarshaller != null) {
                this.linkStaxStreamMarshaller.marshal(link, xmlStreamWriter, conversionContext);
            }
            StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, marshalledLinkBody);
            xmlStreamWriter.writeEndElement();
        });
    }
}

