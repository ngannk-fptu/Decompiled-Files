/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.config;

import java.util.Arrays;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.UnmarshallerRepositoryPopulatorFactoryBean;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class ResourceReaderRepositoryPopulatorBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    @Nonnull
    protected String getBeanClassName(Element element) {
        String name = element.getLocalName();
        if ("unmarshaller-populator".equals(name)) {
            return UnmarshallerRepositoryPopulatorFactoryBean.class.getName();
        }
        if ("jackson2-populator".equals(name)) {
            return Jackson2RepositoryPopulatorFactoryBean.class.getName();
        }
        throw new IllegalStateException("Unsupported populator type " + name + "!");
    }

    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String localName = element.getLocalName();
        builder.addPropertyValue("resources", (Object)element.getAttribute("locations"));
        if ("unmarshaller-populator".equals(localName)) {
            ResourceReaderRepositoryPopulatorBeanDefinitionParser.parseXmlPopulator(element, builder);
        } else if (Arrays.asList("jackson-populator", "jackson2-populator").contains(localName)) {
            ResourceReaderRepositoryPopulatorBeanDefinitionParser.parseJsonPopulator(element, builder);
        }
    }

    private static void parseJsonPopulator(Element element, BeanDefinitionBuilder builder) {
        String objectMapperRef = element.getAttribute("object-mapper-ref");
        if (StringUtils.hasText((String)objectMapperRef)) {
            builder.addPropertyReference("mapper", objectMapperRef);
        }
    }

    private static void parseXmlPopulator(Element element, BeanDefinitionBuilder builder) {
        String unmarshallerRefName = element.getAttribute("unmarshaller-ref");
        if (StringUtils.hasText((String)unmarshallerRefName)) {
            builder.addPropertyReference("unmarshaller", unmarshallerRefName);
        }
    }

    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }
}

