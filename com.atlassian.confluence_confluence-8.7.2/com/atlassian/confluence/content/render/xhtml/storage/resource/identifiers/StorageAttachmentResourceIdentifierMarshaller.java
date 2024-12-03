/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageAttachmentResourceIdentifierMarshaller
implements Marshaller<AttachmentResourceIdentifier> {
    private static Logger logger = LoggerFactory.getLogger(StorageAttachmentResourceIdentifierMarshaller.class);
    private static final String DISABLE_ATTACHMENT_CONTAINER_CHECK = "attachment.container.check.disable";
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final Marshaller<ResourceIdentifier> attachmentContainerResourceIdentifierMarshaller;
    private final AttachmentManager attachmentManager;
    private final ContentEntityManager contentEntityManager;
    private final DarkFeaturesManager darkFeaturesManager;

    public StorageAttachmentResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<ResourceIdentifier> attachmentContainerResourceIdentifierMarshaller, AttachmentManager attachmentManager, ContentEntityManager contentEntityManager, DarkFeaturesManager darkFeaturesManager) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.attachmentContainerResourceIdentifierMarshaller = attachmentContainerResourceIdentifierMarshaller;
        this.attachmentManager = attachmentManager;
        this.contentEntityManager = contentEntityManager;
        this.darkFeaturesManager = darkFeaturesManager;
    }

    @Override
    public Streamable marshal(AttachmentResourceIdentifier attachmentResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        ContentEntityObject entity = conversionContext == null ? null : conversionContext.getEntity();
        AttachmentContainerResourceIdentifier attachmentContainer = attachmentResourceIdentifier.getAttachmentContainerResourceIdentifier();
        boolean skipMarshallContainer = this.shouldSkipMarshallContainer(entity, attachmentResourceIdentifier, attachmentContainer);
        Streamable marshalledResourceIdentifier = attachmentContainer == null ? null : this.attachmentContainerResourceIdentifierMarshaller.marshal(attachmentContainer, conversionContext);
        logger.debug("Attachment Container Marshaller: {}", (Object)marshalledResourceIdentifier);
        Streamable marshalledResourceIdentifierFinal = skipMarshallContainer ? null : marshalledResourceIdentifier;
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "attachment", "http://atlassian.com/resource/identifier");
            xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "filename", attachmentResourceIdentifier.getFilename());
            if (marshalledResourceIdentifierFinal != null) {
                xmlStreamWriter.writeCharacters("");
                xmlStreamWriter.flush();
                marshalledResourceIdentifierFinal.writeTo(underlyingWriter);
            }
            xmlStreamWriter.writeEndElement();
        });
    }

    private boolean shouldSkipMarshallContainer(ContentEntityObject entity, AttachmentResourceIdentifier attachmentResourceIdentifier, AttachmentContainerResourceIdentifier attachmentContainer) {
        ContentEntityResourceIdentifier contentEntityResourceIdentifier;
        long containerId;
        ContentEntityObject container;
        logger.debug("Checking attachment container is in valid context");
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(DISABLE_ATTACHMENT_CONTAINER_CHECK)) {
            logger.debug("Checking attachment container is disable will marshalling attachment container");
            return false;
        }
        if (entity == null || !entity.isDraft()) {
            logger.debug("Current content entity in Conversion Context is not shared draft or it is null");
            return false;
        }
        boolean skipMarshallContainer = false;
        if (attachmentContainer instanceof ContentEntityResourceIdentifier && (container = this.contentEntityManager.getById(containerId = (contentEntityResourceIdentifier = (ContentEntityResourceIdentifier)attachmentContainer).getContentId())) != null && container.isDraft()) {
            String downloadPath = this.attachmentManager.getAttachmentDownloadPath(container, attachmentResourceIdentifier.getResourceName());
            String downloadPathOfEntity = this.attachmentManager.getAttachmentDownloadPath(entity, attachmentResourceIdentifier.getResourceName());
            boolean bl = skipMarshallContainer = StringUtils.isEmpty((CharSequence)downloadPath) && StringUtils.isNotEmpty((CharSequence)downloadPathOfEntity);
            if (skipMarshallContainer) {
                logger.debug("Attachment container [{}] is a draft and does not content required attachment [{}]. But the current entity does [{}]", new Object[]{containerId, attachmentResourceIdentifier, entity});
            } else {
                logger.debug("The attachment container is valid will process marshalling it.");
            }
        }
        return skipMarshallContainer;
    }
}

