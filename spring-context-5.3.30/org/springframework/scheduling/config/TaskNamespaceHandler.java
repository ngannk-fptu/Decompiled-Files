/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.scheduling.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.scheduling.config.AnnotationDrivenBeanDefinitionParser;
import org.springframework.scheduling.config.ExecutorBeanDefinitionParser;
import org.springframework.scheduling.config.ScheduledTasksBeanDefinitionParser;
import org.springframework.scheduling.config.SchedulerBeanDefinitionParser;

public class TaskNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
        this.registerBeanDefinitionParser("executor", (BeanDefinitionParser)new ExecutorBeanDefinitionParser());
        this.registerBeanDefinitionParser("scheduled-tasks", (BeanDefinitionParser)new ScheduledTasksBeanDefinitionParser());
        this.registerBeanDefinitionParser("scheduler", (BeanDefinitionParser)new SchedulerBeanDefinitionParser());
    }
}

