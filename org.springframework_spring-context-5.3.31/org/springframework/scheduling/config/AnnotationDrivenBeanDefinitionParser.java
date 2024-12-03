/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.scheduling.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class AnnotationDrivenBeanDefinitionParser
implements BeanDefinitionParser {
    private static final String ASYNC_EXECUTION_ASPECT_CLASS_NAME = "org.springframework.scheduling.aspectj.AnnotationAsyncExecutionAspect";

    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder;
        Object source = parserContext.extractSource((Object)element);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String mode = element.getAttribute("mode");
        if ("aspectj".equals(mode)) {
            this.registerAsyncExecutionAspect(element, parserContext);
        } else if (registry.containsBeanDefinition("org.springframework.context.annotation.internalAsyncAnnotationProcessor")) {
            parserContext.getReaderContext().error("Only one AsyncAnnotationBeanPostProcessor may exist within the context.", source);
        } else {
            String exceptionHandler;
            builder = BeanDefinitionBuilder.genericBeanDefinition((String)"org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor");
            builder.getRawBeanDefinition().setSource(source);
            String executor = element.getAttribute("executor");
            if (StringUtils.hasText((String)executor)) {
                builder.addPropertyReference("executor", executor);
            }
            if (StringUtils.hasText((String)(exceptionHandler = element.getAttribute("exception-handler")))) {
                builder.addPropertyReference("exceptionHandler", exceptionHandler);
            }
            if (Boolean.parseBoolean(element.getAttribute("proxy-target-class"))) {
                builder.addPropertyValue("proxyTargetClass", (Object)true);
            }
            AnnotationDrivenBeanDefinitionParser.registerPostProcessor(parserContext, builder, "org.springframework.context.annotation.internalAsyncAnnotationProcessor");
        }
        if (registry.containsBeanDefinition("org.springframework.context.annotation.internalScheduledAnnotationProcessor")) {
            parserContext.getReaderContext().error("Only one ScheduledAnnotationBeanPostProcessor may exist within the context.", source);
        } else {
            builder = BeanDefinitionBuilder.genericBeanDefinition((String)"org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor");
            builder.getRawBeanDefinition().setSource(source);
            String scheduler = element.getAttribute("scheduler");
            if (StringUtils.hasText((String)scheduler)) {
                builder.addPropertyReference("scheduler", scheduler);
            }
            AnnotationDrivenBeanDefinitionParser.registerPostProcessor(parserContext, builder, "org.springframework.context.annotation.internalScheduledAnnotationProcessor");
        }
        parserContext.popAndRegisterContainingComponent();
        return null;
    }

    private void registerAsyncExecutionAspect(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.scheduling.config.internalAsyncExecutionAspect")) {
            String exceptionHandler;
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((String)ASYNC_EXECUTION_ASPECT_CLASS_NAME);
            builder.setFactoryMethod("aspectOf");
            String executor = element.getAttribute("executor");
            if (StringUtils.hasText((String)executor)) {
                builder.addPropertyReference("executor", executor);
            }
            if (StringUtils.hasText((String)(exceptionHandler = element.getAttribute("exception-handler")))) {
                builder.addPropertyReference("exceptionHandler", exceptionHandler);
            }
            parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)builder.getBeanDefinition(), "org.springframework.scheduling.config.internalAsyncExecutionAspect"));
        }
    }

    private static void registerPostProcessor(ParserContext parserContext, BeanDefinitionBuilder builder, String beanName) {
        builder.setRole(2);
        parserContext.getRegistry().registerBeanDefinition(beanName, (BeanDefinition)builder.getBeanDefinition());
        BeanDefinitionHolder holder = new BeanDefinitionHolder((BeanDefinition)builder.getBeanDefinition(), beanName);
        parserContext.registerComponent((ComponentDefinition)new BeanComponentDefinition(holder));
    }
}

