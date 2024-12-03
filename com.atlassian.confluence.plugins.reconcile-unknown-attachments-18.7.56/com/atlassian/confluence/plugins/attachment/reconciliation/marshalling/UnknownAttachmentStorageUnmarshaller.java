/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.RestoredUnknownAttachment;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentFormatException;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentUnmarshalEvent;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.springframework.beans.factory.InitializingBean;

@ConfluenceComponent
public final class UnknownAttachmentStorageUnmarshaller
implements Unmarshaller<RestoredUnknownAttachment>,
InitializingBean {
    private MarshallingRegistry marshallingRegistry;
    private AttachmentManager attachmentManager;
    private EventPublisher eventPublisher;
    private LabelManager labelManager;
    private static final QName AC_IMAGE = new QName("http://atlassian.com/content", "image", "ac");
    private static final QName AC_TITLE = new QName("http://atlassian.com/content", "title", "ac");
    private static final QName RI_URL = new QName("http://atlassian.com/resource/identifier", "url", "ri");
    private static final QName RI_VALUE = new QName("http://atlassian.com/resource/identifier", "value", "ri");
    private static final String UNKNOWN_ATTACHMENT_CONTENT_PROPERTY_STORAGE = "UNKNOWN_ATTACHMENT_RECONCILED_STORAGE_UNMARSHAL";

    public UnknownAttachmentStorageUnmarshaller(@ComponentImport MarshallingRegistry marshallingRegistry, @ComponentImport AttachmentManager attachmentManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport LabelManager labelManager) {
        this.marshallingRegistry = marshallingRegistry;
        this.attachmentManager = attachmentManager;
        this.eventPublisher = eventPublisher;
        this.labelManager = labelManager;
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Unmarshaller)this, RestoredUnknownAttachment.class, MarshallingType.STORAGE);
    }

    public RestoredUnknownAttachment unmarshal(XMLEventReader xmlEventReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StartElement imageElement;
        StartElement riElement = null;
        ContentEntityObject ceo = conversionContext.getEntity();
        if (ceo == null) {
            throw new UnknownAttachmentFormatException("conversion context has no content entity object");
        }
        try {
            imageElement = xmlEventReader.nextEvent().asStartElement();
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (!xmlEvent.isStartElement() || !RI_URL.equals(xmlEvent.asStartElement().getName())) continue;
                riElement = xmlEvent.asStartElement();
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException((Throwable)e);
        }
        if (riElement == null || riElement.getAttributeByName(RI_VALUE) == null) {
            return new RestoredUnknownAttachment(null, RestoredUnknownAttachment.Status.INVALID_UNKNOWN_ATTACHMENT);
        }
        if (!riElement.getAttributeByName(RI_VALUE).getValue().contains("placeholder/unknown-attachment")) {
            return new RestoredUnknownAttachment(null, RestoredUnknownAttachment.Status.INVALID_UNKNOWN_ATTACHMENT);
        }
        if (UnknownAttachmentUtils.countAttachmentsOnContent(conversionContext, this.attachmentManager) == 1) {
            this.eventPublisher.publish((Object)new UnknownAttachmentUnmarshalEvent(this, UnknownAttachmentUnmarshalEvent.UnmarshalType.STORAGE, UnknownAttachmentUnmarshalEvent.UnmarshalCase.UNMARSHAL_CASE_SUCCESS_SINGLE_ATTACHMENT, ceo.getId()));
            ceo.getProperties().setStringProperty(UNKNOWN_ATTACHMENT_CONTENT_PROPERTY_STORAGE, "restored");
            return new RestoredUnknownAttachment((Attachment)this.attachmentManager.getLatestVersionsOfAttachments(ceo).get(0), RestoredUnknownAttachment.Status.VALID_UNKNOWN_ATTACHMENT);
        }
        Attribute title = imageElement.getAttributeByName(AC_TITLE);
        if (title == null) {
            this.eventPublisher.publish((Object)new UnknownAttachmentUnmarshalEvent(this, UnknownAttachmentUnmarshalEvent.UnmarshalType.STORAGE, UnknownAttachmentUnmarshalEvent.UnmarshalCase.UNMARSHAL_CASE_NO_TITLE, ceo.getId()));
            this.labelManager.addLabel((Labelable)ceo, new Label("unrestored-unknown-attachment"));
            return new RestoredUnknownAttachment(null, RestoredUnknownAttachment.Status.INVALID_UNKNOWN_ATTACHMENT);
        }
        Attachment attachment = this.attachmentManager.getAttachment(ceo, title.getValue());
        if (attachment == null) {
            this.eventPublisher.publish((Object)new UnknownAttachmentUnmarshalEvent(this, UnknownAttachmentUnmarshalEvent.UnmarshalType.STORAGE, UnknownAttachmentUnmarshalEvent.UnmarshalCase.UNMARSHAL_CASE_NO_MATCHING_ATTACHMENT, ceo.getId()));
            this.labelManager.addLabel((Labelable)ceo, new Label("unrestored-unknown-attachment"));
            return new RestoredUnknownAttachment(null, RestoredUnknownAttachment.Status.INVALID_UNKNOWN_ATTACHMENT);
        }
        this.eventPublisher.publish((Object)new UnknownAttachmentUnmarshalEvent(this, UnknownAttachmentUnmarshalEvent.UnmarshalType.STORAGE, UnknownAttachmentUnmarshalEvent.UnmarshalCase.UNMARSHAL_CASE_SUCCESS, ceo.getId()));
        ceo.getProperties().setStringProperty(UNKNOWN_ATTACHMENT_CONTENT_PROPERTY_STORAGE, "restored");
        return new RestoredUnknownAttachment(attachment, RestoredUnknownAttachment.Status.VALID_UNKNOWN_ATTACHMENT);
    }

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        if (!AC_IMAGE.equals(startElement.getName())) {
            return false;
        }
        ContentEntityObject ceo = conversionContext.getEntity();
        return StaxUtils.getAttributeValue((StartElement)startElement, (QName)AC_TITLE) != null || ceo != null && UnknownAttachmentUtils.countAttachmentsOnContent(conversionContext, this.attachmentManager) == 1;
    }
}

