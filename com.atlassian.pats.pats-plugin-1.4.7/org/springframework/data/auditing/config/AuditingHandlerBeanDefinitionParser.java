/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.aop.framework.ProxyFactoryBean
 *  org.springframework.aop.target.LazyInitTargetSource
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.auditing.config;

import javax.annotation.Nonnull;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.config.ParsingUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class AuditingHandlerBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    private static final String AUDITOR_AWARE_REF = "auditor-aware-ref";
    private final String mappingContextBeanName;
    private String resolvedBeanName;

    public AuditingHandlerBeanDefinitionParser(String mappingContextBeanName) {
        Assert.hasText((String)mappingContextBeanName, (String)"MappingContext bean name must not be null!");
        this.mappingContextBeanName = mappingContextBeanName;
    }

    public String getResolvedBeanName() {
        return this.resolvedBeanName;
    }

    @Nonnull
    protected Class<?> getBeanClass(Element element) {
        return AuditingHandler.class;
    }

    protected boolean shouldGenerateId() {
        return true;
    }

    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addConstructorArgReference(this.mappingContextBeanName);
        String auditorAwareRef = element.getAttribute(AUDITOR_AWARE_REF);
        if (StringUtils.hasText((String)auditorAwareRef)) {
            builder.addPropertyValue("auditorAware", (Object)this.createLazyInitTargetSourceBeanDefinition(auditorAwareRef));
        }
        ParsingUtils.setPropertyValue(builder, element, "set-dates", "dateTimeForNow");
        ParsingUtils.setPropertyReference(builder, element, "date-time-provider-ref", "dateTimeProvider");
        ParsingUtils.setPropertyValue(builder, element, "modify-on-creation", "modifyOnCreation");
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        this.resolvedBeanName = super.resolveId(element, definition, parserContext);
        return this.resolvedBeanName;
    }

    private BeanDefinition createLazyInitTargetSourceBeanDefinition(String auditorAwareRef) {
        BeanDefinitionBuilder targetSourceBuilder = BeanDefinitionBuilder.rootBeanDefinition(LazyInitTargetSource.class);
        targetSourceBuilder.addPropertyValue("targetBeanName", (Object)auditorAwareRef);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProxyFactoryBean.class);
        builder.addPropertyValue("targetSource", (Object)targetSourceBuilder.getBeanDefinition());
        return builder.getBeanDefinition();
    }
}

