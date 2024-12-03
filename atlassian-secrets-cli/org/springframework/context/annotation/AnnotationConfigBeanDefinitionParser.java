/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

public class AnnotationConfigBeanDefinitionParser
implements BeanDefinitionParser {
    @Override
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils.registerAnnotationConfigProcessors(parserContext.getRegistry(), source);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);
        for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
            parserContext.registerComponent(new BeanComponentDefinition(processorDefinition));
        }
        parserContext.popAndRegisterContainingComponent();
        return null;
    }
}

