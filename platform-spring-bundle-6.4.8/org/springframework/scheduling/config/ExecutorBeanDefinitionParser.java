/*
 * Decompiled with CFR 0.152.
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
    @Override
    protected String getBeanClassName(Element element) {
        return "org.springframework.scheduling.config.TaskExecutorFactoryBean";
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String queueCapacity;
        String keepAliveSeconds = element.getAttribute("keep-alive");
        if (StringUtils.hasText(keepAliveSeconds)) {
            builder.addPropertyValue("keepAliveSeconds", keepAliveSeconds);
        }
        if (StringUtils.hasText(queueCapacity = element.getAttribute("queue-capacity"))) {
            builder.addPropertyValue("queueCapacity", queueCapacity);
        }
        this.configureRejectionPolicy(element, builder);
        String poolSize = element.getAttribute("pool-size");
        if (StringUtils.hasText(poolSize)) {
            builder.addPropertyValue("poolSize", poolSize);
        }
    }

    private void configureRejectionPolicy(Element element, BeanDefinitionBuilder builder) {
        String rejectionPolicy = element.getAttribute("rejection-policy");
        if (!StringUtils.hasText(rejectionPolicy)) {
            return;
        }
        String prefix = "java.util.concurrent.ThreadPoolExecutor.";
        String policyClassName = rejectionPolicy.equals("ABORT") ? prefix + "AbortPolicy" : (rejectionPolicy.equals("CALLER_RUNS") ? prefix + "CallerRunsPolicy" : (rejectionPolicy.equals("DISCARD") ? prefix + "DiscardPolicy" : (rejectionPolicy.equals("DISCARD_OLDEST") ? prefix + "DiscardOldestPolicy" : rejectionPolicy)));
        builder.addPropertyValue("rejectedExecutionHandler", new RootBeanDefinition(policyClassName));
    }
}

