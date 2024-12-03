/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.placeholder;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.editor.placeholder.EditorPlaceholderMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.placeholder.EditorPlaceholderUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.placeholder.PlaceholderMarshallingFactory;
import com.atlassian.confluence.content.render.xhtml.placeholder.PlaceholderMarshallingFactoryImpl;
import com.atlassian.confluence.content.render.xhtml.storage.placeholder.StoragePlaceholderMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.placeholder.StoragePlaceholderUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.view.placeholder.ViewPlaceholderMarshaller;
import com.atlassian.confluence.xhtml.api.Placeholder;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PlaceholderMarshallerContextConfig {
    @Resource
    private XmlStreamWriterTemplate xmlStreamWriterTemplate;
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;

    PlaceholderMarshallerContextConfig() {
    }

    @Bean
    @AvailableToPlugins(interfaces={PlaceholderMarshallingFactory.class})
    PlaceholderMarshallingFactory placeholderMarshallingFactory() {
        return new PlaceholderMarshallingFactoryImpl(this.viewPlaceholderMarshaller(), this.editorPlaceholderMarshaller(), this.storagePlaceholderMarshaller(), this.editorPlaceholderUnmarshaller(), this.storagePlaceholderUnmarshaller());
    }

    @Bean
    Marshaller<Placeholder> viewPlaceholderMarshaller() {
        return new ViewPlaceholderMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    Marshaller<Placeholder> editorPlaceholderMarshaller() {
        return new EditorPlaceholderMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    Marshaller<Placeholder> storagePlaceholderMarshaller() {
        return new StoragePlaceholderMarshaller(this.xmlFragmentOutputFactory);
    }

    @Bean
    Unmarshaller<Placeholder> editorPlaceholderUnmarshaller() {
        return new EditorPlaceholderUnmarshaller(this.xmlEventReaderFactory);
    }

    @Bean
    Unmarshaller<Placeholder> storagePlaceholderUnmarshaller() {
        return new StoragePlaceholderUnmarshaller();
    }
}

