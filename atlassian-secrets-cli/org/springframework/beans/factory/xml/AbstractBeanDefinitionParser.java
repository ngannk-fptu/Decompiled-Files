/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public abstract class AbstractBeanDefinitionParser
implements BeanDefinitionParser {
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";

    @Override
    @Nullable
    public final BeanDefinition parse(Element element, ParserContext parserContext) {
        AbstractBeanDefinition definition = this.parseInternal(element, parserContext);
        if (definition != null && !parserContext.isNested()) {
            try {
                String name;
                String id = this.resolveId(element, definition, parserContext);
                if (!StringUtils.hasText(id)) {
                    parserContext.getReaderContext().error("Id is required for element '" + parserContext.getDelegate().getLocalName(element) + "' when used as a top-level tag", element);
                }
                String[] aliases = null;
                if (this.shouldParseNameAsAliases() && StringUtils.hasLength(name = element.getAttribute(NAME_ATTRIBUTE))) {
                    aliases = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(name));
                }
                BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, aliases);
                this.registerBeanDefinition(holder, parserContext.getRegistry());
                if (this.shouldFireEvents()) {
                    BeanComponentDefinition componentDefinition = new BeanComponentDefinition(holder);
                    this.postProcessComponentDefinition(componentDefinition);
                    parserContext.registerComponent(componentDefinition);
                }
            }
            catch (BeanDefinitionStoreException ex) {
                String msg = ex.getMessage();
                parserContext.getReaderContext().error(msg != null ? msg : ex.toString(), element);
                return null;
            }
        }
        return definition;
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (this.shouldGenerateId()) {
            return parserContext.getReaderContext().generateBeanName(definition);
        }
        String id = element.getAttribute(ID_ATTRIBUTE);
        if (!StringUtils.hasText(id) && this.shouldGenerateIdAsFallback()) {
            id = parserContext.getReaderContext().generateBeanName(definition);
        }
        return id;
    }

    protected void registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definition, registry);
    }

    @Nullable
    protected abstract AbstractBeanDefinition parseInternal(Element var1, ParserContext var2);

    protected boolean shouldGenerateId() {
        return false;
    }

    protected boolean shouldGenerateIdAsFallback() {
        return false;
    }

    protected boolean shouldParseNameAsAliases() {
        return true;
    }

    protected boolean shouldFireEvents() {
        return true;
    }

    protected void postProcessComponentDefinition(BeanComponentDefinition componentDefinition) {
    }
}

