/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewShortcutLinkMarshaller
implements Marshaller<Link> {
    private final ShortcutLinksManager shortcutLinksManager;
    private final Marshaller<UnresolvedLink> unresolvedLinkMarshaller;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final StaxStreamMarshaller<ShortcutResourceIdentifier> resourceIdentifierStaxStreamMarshaller;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;

    public ViewShortcutLinkMarshaller(ShortcutLinksManager shortcutLinksManager, Marshaller<UnresolvedLink> unresolvedLinkMarshaller, XmlStreamWriterTemplate xmlStreamWriterTemplate, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, @Nullable StaxStreamMarshaller<ShortcutResourceIdentifier> resourceIdentifierStaxStreamMarshaller, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller) {
        this.shortcutLinksManager = Objects.requireNonNull(shortcutLinksManager);
        this.unresolvedLinkMarshaller = Objects.requireNonNull(unresolvedLinkMarshaller);
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.resourceIdentifierStaxStreamMarshaller = resourceIdentifierStaxStreamMarshaller;
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
    }

    @Override
    public Streamable marshal(Link shortcutLink, ConversionContext conversionContext) throws XhtmlException {
        ShortcutResourceIdentifier shortcutResourceIdentifier = (ShortcutResourceIdentifier)shortcutLink.getDestinationResourceIdentifier();
        if (!this.shortcutLinksManager.hasShortcutLink(shortcutResourceIdentifier.getShortcutKey())) {
            return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(shortcutLink), conversionContext);
        }
        Streamable linkBody = shortcutLink.getBody() != null ? this.linkBodyMarshaller.marshal(shortcutLink, conversionContext) : Streamables.from(this.shortcutLinksManager.resolveDefaultLinkAlias(shortcutResourceIdentifier.getShortcutKey(), shortcutResourceIdentifier.getShortcutParameter()));
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            this.commonLinkAttributesWriter.writeCommonAttributes(shortcutLink, xmlStreamWriter, conversionContext);
            xmlStreamWriter.writeAttribute("href", this.shortcutLinksManager.resolveShortcutUrl(shortcutResourceIdentifier.getShortcutKey(), shortcutResourceIdentifier.getShortcutParameter()));
            if (this.resourceIdentifierStaxStreamMarshaller != null) {
                this.resourceIdentifierStaxStreamMarshaller.marshal(shortcutResourceIdentifier, xmlStreamWriter, conversionContext);
            }
            if (this.linkStaxStreamMarshaller != null) {
                this.linkStaxStreamMarshaller.marshal(shortcutLink, xmlStreamWriter, conversionContext);
            }
            if (linkBody != null) {
                StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, linkBody);
            }
            xmlStreamWriter.writeEndElement();
        });
    }
}

