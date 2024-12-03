/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.RestoredUnknownAttachment;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.InitializingBean;

@ConfluenceComponent
public class UnknownAttachmentEditorMarshaller
implements Marshaller<RestoredUnknownAttachment>,
InitializingBean {
    private MarshallingRegistry marshallingRegistry;
    private XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private SettingsManager settingsManager;
    private ContextPathHolder contextPathHolder;

    public UnknownAttachmentEditorMarshaller(@ComponentImport MarshallingRegistry marshallingRegistry, @ComponentImport XmlStreamWriterTemplate xmlStreamWriterTemplate, @ComponentImport SettingsManager settingsManager, @ComponentImport ContextPathHolder contextPathHolder) {
        this.marshallingRegistry = marshallingRegistry;
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.settingsManager = settingsManager;
        this.contextPathHolder = contextPathHolder;
    }

    public Streamable marshal(RestoredUnknownAttachment restoredUnknownAttachment, ConversionContext conversionContext) {
        Attachment attachment = restoredUnknownAttachment.getAttachment();
        return Streamables.from((XmlStreamWriterTemplate)this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            String source = this.contextPathHolder.getContextPath() + attachment.getDownloadPath();
            xmlStreamWriter.writeStartElement("img");
            xmlStreamWriter.writeAttribute("class", "confluence-embedded-image");
            xmlStreamWriter.writeAttribute("src", source);
            xmlStreamWriter.writeAttribute("height", "250");
            xmlStreamWriter.writeAttribute("data-image-src", source);
            xmlStreamWriter.writeAttribute("data-linked-resource-id", Long.toString(attachment.getId()));
            xmlStreamWriter.writeAttribute("data-linked-resource-version", String.valueOf(attachment.getVersion()));
            xmlStreamWriter.writeAttribute("data-linked-resource-type", attachment.getType());
            xmlStreamWriter.writeAttribute("data-linked-resource-default-alias", attachment.getFileName());
            xmlStreamWriter.writeAttribute("data-base-url", this.settingsManager.getGlobalSettings().getBaseUrl());
            xmlStreamWriter.writeAttribute("data-linked-resource-content-type", attachment.getMediaType());
            xmlStreamWriter.writeEndElement();
        });
    }

    public void afterPropertiesSet() throws Exception {
        this.marshallingRegistry.register((Marshaller)this, RestoredUnknownAttachment.class, MarshallingType.EDITOR);
        this.marshallingRegistry.register((Marshaller)this, RestoredUnknownAttachment.class, MarshallingType.STORAGE);
        this.marshallingRegistry.register((Marshaller)this, RestoredUnknownAttachment.class, MarshallingType.VIEW);
    }
}

