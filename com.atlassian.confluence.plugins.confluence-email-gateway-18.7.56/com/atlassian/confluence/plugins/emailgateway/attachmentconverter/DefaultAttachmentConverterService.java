/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InputStreamAttachmentResource
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.confluence.plugins.emailgateway.attachmentconverter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InputStreamAttachmentResource;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverterService;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentFile;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import com.atlassian.confluence.plugins.emailgateway.api.descriptor.AttachmentConverterModuleDescriptor;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class DefaultAttachmentConverterService
implements AttachmentConverterService {
    private final MarshallingRegistry marshallingRegistry;
    private final PluginModuleTracker<AttachmentConverter<?>, AttachmentConverterModuleDescriptor> moduleTracker;
    private final I18nResolver i18nResolver;
    private final FileUploadManager fileUploadManager;

    public DefaultAttachmentConverterService(PluginAccessor pluginAccessor, MarshallingRegistry marshallingRegistry, PluginEventManager pluginEventManager, I18nResolver i18nResolver, FileUploadManager fileUploadManager) {
        this.marshallingRegistry = marshallingRegistry;
        this.i18nResolver = i18nResolver;
        this.fileUploadManager = fileUploadManager;
        this.moduleTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, AttachmentConverterModuleDescriptor.class);
    }

    @Override
    public Streamable convert(AttachmentFile attachmentFile) {
        for (AttachmentConverter converter : this.moduleTracker.getModules()) {
            Object object = converter.convertAttachment(attachmentFile);
            if (object == null) continue;
            Class conversionClass = converter.getConversionClass();
            return this.marshallConvertedObject(object, conversionClass);
        }
        return null;
    }

    private Streamable marshallConvertedObject(Object object, Class<?> conversionClass) {
        Marshaller marshaller = this.marshallingRegistry.getMarshaller(conversionClass, MarshallingType.STORAGE);
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)new PageContext());
        try {
            return marshaller.marshal(object, (ConversionContext)conversionContext);
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void attachTo(ContentEntityObject ceo, List<SerializableAttachment> attachments) {
        if (attachments == null) {
            return;
        }
        for (SerializableAttachment attachment : attachments) {
            String contentType = attachment.getContentType().split(";")[0];
            byte[] attachmentData = attachment.getContents();
            String comment = this.i18nResolver.getText("attachment.added.from.email");
            InputStreamAttachmentResource resource = new InputStreamAttachmentResource((InputStream)new ByteArrayInputStream(attachmentData), attachment.getFilename(), contentType, (long)attachmentData.length, comment);
            this.fileUploadManager.storeResource((AttachmentResource)resource, ceo);
        }
    }
}

