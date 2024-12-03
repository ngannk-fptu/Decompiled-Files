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
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewContentEntityLinkMarshaller
implements Marshaller<Link> {
    private final ContentEntityResourceIdentifierResolver resourceIdentifierResolver;
    private final PermissionManager permissionManager;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final Marshaller<UnresolvedLink> unresolvedLinkMarshaller;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;
    private final HrefEvaluator hrefEvaluator;

    public ViewContentEntityLinkMarshaller(ContentEntityResourceIdentifierResolver resourceIdentifierResolver, PermissionManager permissionManager, XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<UnresolvedLink> unresolvedLinkMarshaller, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, HrefEvaluator hrefEvaluator, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller) {
        this.resourceIdentifierResolver = Objects.requireNonNull(resourceIdentifierResolver);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.unresolvedLinkMarshaller = Objects.requireNonNull(unresolvedLinkMarshaller);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.hrefEvaluator = Objects.requireNonNull(hrefEvaluator);
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        ContentEntityResourceIdentifier contentEntityResourceIdentifier = (ContentEntityResourceIdentifier)link.getDestinationResourceIdentifier();
        ContentEntityObject contentEntityObject = this.resourceIdentifierResolver.resolve(contentEntityResourceIdentifier, conversionContext);
        if (contentEntityObject == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, contentEntityObject)) {
            DefaultLink linkDelegate = DefaultLink.builder(link).withBody(link.getBody() != null ? link.getBody() : new PlainTextLinkBody(String.valueOf(contentEntityResourceIdentifier.getContentId()))).withTooltip(link.getTooltip()).withAnchor(link.getAnchor()).build();
            return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(linkDelegate), conversionContext);
        }
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(link, conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            this.commonLinkAttributesWriter.writeCommonAttributes(link, xmlStreamWriter, conversionContext);
            xmlStreamWriter.writeAttribute("href", this.hrefEvaluator.createHref(conversionContext, contentEntityObject, link.getAnchor()));
            if (this.linkStaxStreamMarshaller != null) {
                this.linkStaxStreamMarshaller.marshal(link, xmlStreamWriter, conversionContext);
            }
            StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, marshalledLinkBody);
            xmlStreamWriter.writeEndElement();
        });
    }
}

