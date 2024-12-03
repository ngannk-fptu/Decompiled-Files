/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConfluenceXmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.DefaultXMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.DefaultXmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.DefaultXmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.DefaultXmlStreamWriterTemplate;
import com.atlassian.confluence.xml.XhtmlEntityResolver;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.xml.stream.XMLEventFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XmlFactoryContextConfig {
    XmlFactoryContextConfig() {
    }

    @Bean
    XhtmlEntityResolver xhtmlEntityResolver() {
        return new XhtmlEntityResolver();
    }

    @Bean
    @AvailableToPlugins
    DefaultXmlEventReaderFactory xmlEventReaderFactory() {
        return new DefaultXmlEventReaderFactory(this.xhtmlEntityResolver());
    }

    @Bean
    @AvailableToPlugins
    DefaultXmlOutputFactoryProvider xmlOutputFactoryProvider() {
        return new DefaultXmlOutputFactoryProvider(this.xmlOutputFactory(), this.xmlFragmentOutputFactory());
    }

    @Bean
    @AvailableToPlugins
    ConfluenceXmlOutputFactory xmlOutputFactory() {
        return ConfluenceXmlOutputFactory.create();
    }

    @Bean
    @AvailableToPlugins
    ConfluenceXmlOutputFactory xmlFragmentOutputFactory() {
        return ConfluenceXmlOutputFactory.createFragmentXmlOutputFactory();
    }

    @Bean
    XMLEventFactory xmlEventFactory() {
        return XMLEventFactory.newInstance();
    }

    @Bean
    @AvailableToPlugins
    DefaultXMLEventFactoryProvider xmlEventFactoryProvider() {
        return new DefaultXMLEventFactoryProvider(this.xmlEventFactory());
    }

    @Bean
    @AvailableToPlugins
    DefaultXmlStreamWriterTemplate xmlStreamWriterTemplate() {
        return new DefaultXmlStreamWriterTemplate(this.xmlFragmentOutputFactory());
    }
}

