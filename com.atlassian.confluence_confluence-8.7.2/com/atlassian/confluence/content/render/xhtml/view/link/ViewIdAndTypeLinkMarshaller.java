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
import com.atlassian.confluence.content.render.xhtml.model.links.NotPermittedLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewIdAndTypeLinkMarshaller
implements Marshaller<Link> {
    protected final IdAndTypeResourceIdentifierResolver resourceIdentifierResolver;
    protected final PermissionManager permissionManager;
    protected final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    protected final Marshaller<UnresolvedLink> unresolvedLinkMarshaller;
    protected final CommonLinkAttributesWriter commonLinkAttributesWriter;
    protected final Marshaller<Link> linkBodyMarshaller;
    protected final Marshaller<Link> notPermittedLinkMarshaller;
    protected final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;
    protected final HrefEvaluator hrefEvaluator;

    public ViewIdAndTypeLinkMarshaller(IdAndTypeResourceIdentifierResolver resourceIdentifierResolver, PermissionManager permissionManager, XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<UnresolvedLink> unresolvedLinkMarshaller, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, Marshaller<Link> notPermittedLinkMarshaller, HrefEvaluator hrefEvaluator, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller) {
        this.resourceIdentifierResolver = Objects.requireNonNull(resourceIdentifierResolver);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.unresolvedLinkMarshaller = Objects.requireNonNull(unresolvedLinkMarshaller);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.notPermittedLinkMarshaller = Objects.requireNonNull(notPermittedLinkMarshaller);
        this.hrefEvaluator = Objects.requireNonNull(hrefEvaluator);
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        IdAndTypeResourceIdentifier ri = (IdAndTypeResourceIdentifier)link.getDestinationResourceIdentifier();
        ConfluenceEntityObject entity = (ConfluenceEntityObject)this.resourceIdentifierResolver.resolve(ri, conversionContext);
        if (entity == null) {
            LinkBody<?> linkBody = null;
            if (linkBody instanceof PlainTextLinkBody || linkBody instanceof RichTextLinkBody) {
                linkBody = link.getBody();
            }
            DefaultLink linkDelegate = DefaultLink.builder(link).withBody(linkBody).build();
            return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(linkDelegate), conversionContext);
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, entity)) {
            return this.notPermittedLinkMarshaller.marshal(new NotPermittedLink(link), conversionContext);
        }
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(link, conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            this.commonLinkAttributesWriter.writeCommonAttributes(link, xmlStreamWriter, conversionContext);
            xmlStreamWriter.writeAttribute("href", this.hrefEvaluator.createHref(conversionContext, entity, link.getAnchor()));
            if (this.linkStaxStreamMarshaller != null) {
                this.linkStaxStreamMarshaller.marshal(link, xmlStreamWriter, conversionContext);
            }
            StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, marshalledLinkBody);
            xmlStreamWriter.writeEndElement();
        });
    }
}

