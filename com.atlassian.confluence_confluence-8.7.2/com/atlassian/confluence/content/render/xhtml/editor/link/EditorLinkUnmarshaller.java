/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.link.CannotUnmarshalLinkException;
import com.atlassian.confluence.content.render.xhtml.editor.link.EmptyLinkBodyException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.links.EmptyLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import java.util.Objects;
import java.util.Optional;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class EditorLinkUnmarshaller
implements Unmarshaller<Link> {
    private static final String DATA_ANCHOR_ATTRIBUTE = "data-anchor";
    private static final String TARGET_ATTRIBUTE = "target";
    private static final String TITLE_ATTRIBUTE = "title";
    private final Unmarshaller<LinkBody<?>> linkBodyUnmarshaller;
    private final Unmarshaller<ResourceIdentifier> actualLinkStateAnalyzingResourceIdentifierUnmarshaller;
    private final Unmarshaller<ResourceIdentifier> idAndTypeResourceIdentifierUnmarshaller;
    private final ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver;
    private final DarkFeaturesManager darkFeaturesManager;

    public EditorLinkUnmarshaller(Unmarshaller<LinkBody<?>> linkBodyUnmarshaller, Unmarshaller<ResourceIdentifier> actualLinkStateAnalyzingResourceIdentifierUnmarshaller, Unmarshaller<ResourceIdentifier> idAndTypeResourceIdentifierUnmarshaller, ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver, DarkFeaturesManager darkFeaturesManager) {
        this.linkBodyUnmarshaller = linkBodyUnmarshaller;
        this.actualLinkStateAnalyzingResourceIdentifierUnmarshaller = actualLinkStateAnalyzingResourceIdentifierUnmarshaller;
        this.idAndTypeResourceIdentifierUnmarshaller = idAndTypeResourceIdentifierUnmarshaller;
        this.idAndTypeResourceIdentifierResolver = idAndTypeResourceIdentifierResolver;
        this.darkFeaturesManager = Objects.requireNonNull(darkFeaturesManager);
    }

    @Override
    public Link unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        String linkNameForException = null;
        ResourceIdentifier resourceIdentifier = null;
        try {
            LinkBody<?> linkBody;
            StartElement startElement = reader.peek().asStartElement();
            String tooltip = StaxUtils.getAttributeValue(startElement, TITLE_ATTRIBUTE);
            String anchor = StaxUtils.getAttributeValue(startElement, DATA_ANCHOR_ATTRIBUTE);
            Optional<String> target = this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("link.openInNewWindow") ? Optional.ofNullable(StaxUtils.getAttributeValue(startElement, TARGET_ATTRIBUTE)) : Optional.empty();
            linkNameForException = StaxUtils.getAttributeValue(startElement, "data-linked-resource-default-alias");
            if (this.idAndTypeResourceIdentifierUnmarshaller.handles(startElement, conversionContext)) {
                resourceIdentifier = this.idAndTypeResourceIdentifierUnmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
            } else if (this.actualLinkStateAnalyzingResourceIdentifierUnmarshaller.handles(startElement, conversionContext)) {
                resourceIdentifier = this.actualLinkStateAnalyzingResourceIdentifierUnmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
            }
            try {
                linkBody = this.linkBodyUnmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
            }
            catch (EmptyLinkBodyException e) {
                DefaultLink link = DefaultLink.builder().withDestinationResourceIdentifier(resourceIdentifier).withTooltip(tooltip).withAnchor(anchor).withTarget(target).build();
                return new EmptyLink(link);
            }
            if (linkBody instanceof PlainTextLinkBody) {
                PlainTextLinkBody plainBody = (PlainTextLinkBody)linkBody;
                if (resourceIdentifier instanceof AttachmentResourceIdentifier && ((AttachmentResourceIdentifier)resourceIdentifier).getFilename() != null && ((AttachmentResourceIdentifier)resourceIdentifier).getFilename().equals(plainBody.getBody())) {
                    linkNameForException = plainBody.getBody();
                    linkBody = null;
                } else if (resourceIdentifier == null || resourceIdentifier instanceof IdAndTypeResourceIdentifier) {
                    Object obj;
                    Object object = obj = conversionContext != null ? conversionContext.getEntity() : null;
                    if (resourceIdentifier != null) {
                        obj = this.idAndTypeResourceIdentifierResolver.resolve((IdAndTypeResourceIdentifier)resourceIdentifier, conversionContext);
                    }
                    if (obj instanceof Addressable && plainBody.getBody().equals(((Addressable)obj).getDisplayTitle())) {
                        linkNameForException = plainBody.getBody();
                        linkBody = null;
                    }
                }
            }
            return DefaultLink.builder().withDestinationResourceIdentifier(resourceIdentifier).withBody(linkBody).withTooltip(tooltip).withAnchor(anchor).withTarget(target).build();
        }
        catch (CannotResolveResourceIdentifierException e) {
            throw new CannotUnmarshalLinkException(resourceIdentifier, linkNameForException, e);
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("An error occurred while parsing a link during unmarshalling from the editor.", e);
        }
        catch (XhtmlException e) {
            throw new XhtmlException("An error occurred while unmarshalling a link in the editor with the title " + linkNameForException, e);
        }
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.isALink(startElementEvent.getName().getLocalPart()) && this.hasLinkClass(startElementEvent);
    }

    private boolean isALink(String tagName) {
        return "a".equals(tagName);
    }

    private boolean hasLinkClass(StartElement startElementEvent) {
        return StaxUtils.hasClass(startElementEvent, "confluence-link") || StaxUtils.hasClass(startElementEvent, "user-mention");
    }
}

