/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.ReaderContext
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.beans.factory.xml.XmlReaderContext
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationUtils;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

public class RepositoryBeanDefinitionParser
implements BeanDefinitionParser {
    private final RepositoryConfigurationExtension extension;

    public RepositoryBeanDefinitionParser(RepositoryConfigurationExtension extension) {
        Assert.notNull((Object)extension, (String)"Extension must not be null!");
        this.extension = extension;
    }

    @Nullable
    public BeanDefinition parse(Element element, ParserContext parser) {
        XmlReaderContext readerContext = parser.getReaderContext();
        try {
            ResourceLoader resourceLoader = ConfigurationUtils.getRequiredResourceLoader(readerContext);
            Environment environment = readerContext.getEnvironment();
            BeanDefinitionRegistry registry = parser.getRegistry();
            XmlRepositoryConfigurationSource configSource = new XmlRepositoryConfigurationSource(element, parser, environment);
            RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configSource, resourceLoader, environment);
            RepositoryConfigurationUtils.exposeRegistration(this.extension, registry, configSource);
            for (BeanComponentDefinition definition : delegate.registerRepositoriesIn(registry, this.extension)) {
                readerContext.fireComponentRegistered((ComponentDefinition)definition);
            }
        }
        catch (RuntimeException e) {
            this.handleError(e, element, (ReaderContext)readerContext);
        }
        return null;
    }

    private void handleError(Exception e, Element source, ReaderContext reader) {
        reader.error(e.getMessage(), reader.extractSource((Object)source), (Throwable)e);
    }

    protected static boolean hasBean(Class<?> type, BeanDefinitionRegistry registry) {
        String name = String.format("%s%s0", type.getName(), "#");
        return registry.containsBeanDefinition(name);
    }
}

