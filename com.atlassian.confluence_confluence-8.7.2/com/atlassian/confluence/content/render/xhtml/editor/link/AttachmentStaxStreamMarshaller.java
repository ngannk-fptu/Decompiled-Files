/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.setup.settings.SettingsManager;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class AttachmentStaxStreamMarshaller
implements StaxStreamMarshaller<Attachment> {
    private final SettingsManager settingsManager;

    AttachmentStaxStreamMarshaller(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void marshal(Attachment attachment, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        xmlStreamWriter.writeAttribute("data-linked-resource-id", Long.toString(attachment.getId()));
        xmlStreamWriter.writeAttribute("data-linked-resource-version", String.valueOf(attachment.getVersion()));
        xmlStreamWriter.writeAttribute("data-linked-resource-type", attachment.getType());
        xmlStreamWriter.writeAttribute("data-linked-resource-default-alias", attachment.getFileName());
        xmlStreamWriter.writeAttribute("data-base-url", this.settingsManager.getGlobalSettings().getBaseUrl());
        xmlStreamWriter.writeAttribute("data-linked-resource-content-type", attachment.getMediaType());
        ContentEntityObject container = attachment.getContainer();
        if (container != null) {
            xmlStreamWriter.writeAttribute("data-linked-resource-container-id", container.getIdAsString());
            xmlStreamWriter.writeAttribute("data-linked-resource-container-version", String.valueOf(container.getVersion()));
        }
    }
}

