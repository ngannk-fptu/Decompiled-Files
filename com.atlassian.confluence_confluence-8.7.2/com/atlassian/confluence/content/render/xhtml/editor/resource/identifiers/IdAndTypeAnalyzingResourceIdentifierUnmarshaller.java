/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierCreationException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class IdAndTypeAnalyzingResourceIdentifierUnmarshaller
implements Unmarshaller<ResourceIdentifier> {
    private final ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver;
    private final ResourceIdentifierFactory resourceIdentifierFactory;
    private final ResourceIdentifierContextUtility ricu;
    private final SettingsManager settingsManager;

    public IdAndTypeAnalyzingResourceIdentifierUnmarshaller(ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver, ResourceIdentifierFactory resourceIdentifierFactory, ResourceIdentifierContextUtility ricu, SettingsManager settingsManager) {
        this.idAndTypeResourceIdentifierResolver = idAndTypeResourceIdentifierResolver;
        this.resourceIdentifierFactory = resourceIdentifierFactory;
        this.ricu = ricu;
        this.settingsManager = settingsManager;
    }

    @Override
    public ResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        ResourceIdentifier resourceIdentifier;
        Object resource;
        StartElement startElement;
        try {
            startElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        long resourceId = Long.parseLong(StaxUtils.getAttributeValue(startElement, "data-linked-resource-id"));
        String resourceType = StaxUtils.getAttributeValue(startElement, "data-linked-resource-type");
        ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation(resourceType);
        if (contentType == null) {
            throw new XhtmlException("Unsupported resource type '" + resourceType + "' for resource '" + resourceId + "'.");
        }
        try {
            resource = this.idAndTypeResourceIdentifierResolver.resolve(new IdAndTypeResourceIdentifier(resourceId, contentType), conversionContext);
        }
        catch (CannotResolveResourceIdentifierException e) {
            if (contentType == ContentTypeEnum.SPACE) {
                String href = startElement.getAttributeByName(new QName("href")).getValue();
                String spaceKey = StringUtils.substringAfterLast((String)href, (String)"/");
                resource = new Space(spaceKey);
                ((Space)resource).setName(startElement.getAttributeByName(new QName("data-linked-resource-default-alias")).getValue());
            }
            String href = startElement.getAttributeByName(new QName("href")).getValue();
            if (StringUtils.isNotBlank((CharSequence)href)) {
                return new UrlResourceIdentifier(href);
            }
            throw e;
        }
        try {
            resourceIdentifier = this.resourceIdentifierFactory.getResourceIdentifier(resource, conversionContext);
            if (conversionContext != null) {
                resourceIdentifier = this.ricu.convertToRelative(resourceIdentifier, conversionContext.getEntity());
            }
        }
        catch (ResourceIdentifierCreationException e) {
            throw new XhtmlException(e);
        }
        return resourceIdentifier;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        if (StaxUtils.hasAttributes(startElementEvent, "data-linked-resource-id", "data-linked-resource-type", "data-base-url")) {
            String baseUrl = StaxUtils.getAttributeValue(startElementEvent, "data-base-url");
            return baseUrl.equals(this.settingsManager.getGlobalSettings().getBaseUrl());
        }
        return false;
    }
}

