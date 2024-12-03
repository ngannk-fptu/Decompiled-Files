/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.scheduling.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class ExecutorBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    protected String getBeanClassName(Element element) {
        return "org.springframework.scheduling.config.TaskExecutorFactoryBean";
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String queueCapacity;
        String keepAliveSeconds = element.getAttribute("keep-alive");
        if (StringUtils.hasText((String)keepAliveSeconds)) {
            builder.addPropertyValue("keepAliveSeconds", (Object)keepAliveSeconds);
        }
        if (StringUtils.hasText((String)(queueCapacity = element.getAttribute("queue-capacity")))) {
            builder.addPropertyValue("queueCapacity", (Object)queueCapacity);
        }
        this.configureRejectionPolicy(element, builder);
        String poolSize = element.getAttribute("pool-size");
        if (StringUtils.hasText((String)poolSize)) {
            builder.addPropertyValue("poolSize", (Object)poolSize);
        }
    }

    private void configureRejectionPolicy(Element element, BeanDefinitionBuilder builder) {
        String rejectionPolicy = element.getAttribute("rejection-policy");
        if (!StringUtils.hasText((String)rejectionPolicy)) {
            return;
        }
        String prefix = "java.util.concurrent.ThreadPoolExecutor.";
        String policyClassName = rejectionPolicy.equals("ABORT") ? prefix + "AbortPolicy" : (rejectionPolicy.equals("CALLER_RUNS") ? prefix + "CallerRunsPolicy" : (rejectionPolicy.equals("DISCARD") ? prefix + "DiscardPolicy" : (rejectionPolicy.equals("DISCARD_OLDEST") ? prefix + "DiscardOldestPolicy" : rejectionPolicy)));
        builder.addPropertyValue("rejectedExecutionHandler", (Object)new RootBeanDefinition(policyClassName));
    }
}

