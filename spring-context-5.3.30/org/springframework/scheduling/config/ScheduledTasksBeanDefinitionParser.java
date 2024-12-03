/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.scheduling.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScheduledTasksBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    private static final String ELEMENT_SCHEDULED = "scheduled";
    private static final long ZERO_INITIAL_DELAY = 0L;

    protected boolean shouldGenerateId() {
        return true;
    }

    protected String getBeanClassName(Element element) {
        return "org.springframework.scheduling.config.ContextLifecycleScheduledTaskRegistrar";
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setLazyInit(false);
        ManagedList cronTaskList = new ManagedList();
        ManagedList fixedDelayTaskList = new ManagedList();
        ManagedList fixedRateTaskList = new ManagedList();
        ManagedList triggerTaskList = new ManagedList();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node child = childNodes.item(i);
            if (!this.isScheduledElement(child, parserContext)) continue;
            Element taskElement = (Element)child;
            String ref = taskElement.getAttribute("ref");
            String method = taskElement.getAttribute("method");
            if (!StringUtils.hasText((String)ref) || !StringUtils.hasText((String)method)) {
                parserContext.getReaderContext().error("Both 'ref' and 'method' are required", (Object)taskElement);
                continue;
            }
            String cronAttribute = taskElement.getAttribute("cron");
            String fixedDelayAttribute = taskElement.getAttribute("fixed-delay");
            String fixedRateAttribute = taskElement.getAttribute("fixed-rate");
            String triggerAttribute = taskElement.getAttribute("trigger");
            String initialDelayAttribute = taskElement.getAttribute("initial-delay");
            boolean hasCronAttribute = StringUtils.hasText((String)cronAttribute);
            boolean hasFixedDelayAttribute = StringUtils.hasText((String)fixedDelayAttribute);
            boolean hasFixedRateAttribute = StringUtils.hasText((String)fixedRateAttribute);
            boolean hasTriggerAttribute = StringUtils.hasText((String)triggerAttribute);
            boolean hasInitialDelayAttribute = StringUtils.hasText((String)initialDelayAttribute);
            if (!(hasCronAttribute || hasFixedDelayAttribute || hasFixedRateAttribute || hasTriggerAttribute)) {
                parserContext.getReaderContext().error("one of the 'cron', 'fixed-delay', 'fixed-rate', or 'trigger' attributes is required", (Object)taskElement);
                continue;
            }
            if (hasInitialDelayAttribute && (hasCronAttribute || hasTriggerAttribute)) {
                parserContext.getReaderContext().error("the 'initial-delay' attribute may not be used with cron and trigger tasks", (Object)taskElement);
                continue;
            }
            String runnableName = this.runnableReference(ref, method, taskElement, parserContext).getBeanName();
            if (hasFixedDelayAttribute) {
                fixedDelayTaskList.add((Object)this.intervalTaskReference(runnableName, initialDelayAttribute, fixedDelayAttribute, taskElement, parserContext));
            }
            if (hasFixedRateAttribute) {
                fixedRateTaskList.add((Object)this.intervalTaskReference(runnableName, initialDelayAttribute, fixedRateAttribute, taskElement, parserContext));
            }
            if (hasCronAttribute) {
                cronTaskList.add((Object)this.cronTaskReference(runnableName, cronAttribute, taskElement, parserContext));
            }
            if (!hasTriggerAttribute) continue;
            String triggerName = new RuntimeBeanReference(triggerAttribute).getBeanName();
            triggerTaskList.add((Object)this.triggerTaskReference(runnableName, triggerName, taskElement, parserContext));
        }
        String schedulerRef = element.getAttribute("scheduler");
        if (StringUtils.hasText((String)schedulerRef)) {
            builder.addPropertyReference("taskScheduler", schedulerRef);
        }
        builder.addPropertyValue("cronTasksList", (Object)cronTaskList);
        builder.addPropertyValue("fixedDelayTasksList", (Object)fixedDelayTaskList);
        builder.addPropertyValue("fixedRateTasksList", (Object)fixedRateTaskList);
        builder.addPropertyValue("triggerTasksList", (Object)triggerTaskList);
    }

    private boolean isScheduledElement(Node node, ParserContext parserContext) {
        return node.getNodeType() == 1 && ELEMENT_SCHEDULED.equals(parserContext.getDelegate().getLocalName(node));
    }

    private RuntimeBeanReference runnableReference(String ref, String method, Element taskElement, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((String)"org.springframework.scheduling.support.ScheduledMethodRunnable");
        builder.addConstructorArgReference(ref);
        builder.addConstructorArgValue((Object)method);
        return this.beanReference(taskElement, parserContext, builder);
    }

    private RuntimeBeanReference intervalTaskReference(String runnableBeanName, String initialDelay, String interval, Element taskElement, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((String)"org.springframework.scheduling.config.IntervalTask");
        builder.addConstructorArgReference(runnableBeanName);
        builder.addConstructorArgValue((Object)interval);
        builder.addConstructorArgValue(StringUtils.hasLength((String)initialDelay) ? initialDelay : Long.valueOf(0L));
        return this.beanReference(taskElement, parserContext, builder);
    }

    private RuntimeBeanReference cronTaskReference(String runnableBeanName, String cronExpression, Element taskElement, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((String)"org.springframework.scheduling.config.CronTask");
        builder.addConstructorArgReference(runnableBeanName);
        builder.addConstructorArgValue((Object)cronExpression);
        return this.beanReference(taskElement, parserContext, builder);
    }

    private RuntimeBeanReference triggerTaskReference(String runnableBeanName, String triggerBeanName, Element taskElement, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((String)"org.springframework.scheduling.config.TriggerTask");
        builder.addConstructorArgReference(runnableBeanName);
        builder.addConstructorArgReference(triggerBeanName);
        return this.beanReference(taskElement, parserContext, builder);
    }

    private RuntimeBeanReference beanReference(Element taskElement, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.getRawBeanDefinition().setSource(parserContext.extractSource((Object)taskElement));
        String generatedName = parserContext.getReaderContext().generateBeanName((BeanDefinition)builder.getRawBeanDefinition());
        parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)builder.getBeanDefinition(), generatedName));
        return new RuntimeBeanReference(generatedName);
    }
}

