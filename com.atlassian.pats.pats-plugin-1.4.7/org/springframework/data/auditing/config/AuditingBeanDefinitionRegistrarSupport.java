/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.ProxyFactoryBean
 *  org.springframework.aop.target.LazyInitTargetSource
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.auditing.config;

import java.lang.annotation.Annotation;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.data.auditing.config.AnnotationAuditingConfiguration;
import org.springframework.data.auditing.config.AuditingConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AuditingBeanDefinitionRegistrarSupport
implements ImportBeanDefinitionRegistrar {
    private static final String AUDITOR_AWARE = "auditorAware";
    private static final String DATE_TIME_PROVIDER = "dateTimeProvider";
    private static final String MODIFY_ON_CREATE = "modifyOnCreation";
    private static final String SET_DATES = "dateTimeForNow";

    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Assert.notNull((Object)annotationMetadata, (String)"AnnotationMetadata must not be null!");
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null!");
        AbstractBeanDefinition ahbd = this.registerAuditHandlerBeanDefinition(registry, this.getConfiguration(annotationMetadata));
        this.registerAuditListenerBeanDefinition((BeanDefinition)ahbd, registry);
    }

    private AbstractBeanDefinition registerAuditHandlerBeanDefinition(BeanDefinitionRegistry registry, AuditingConfiguration configuration) {
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null!");
        Assert.notNull((Object)configuration, (String)"AuditingConfiguration must not be null!");
        AbstractBeanDefinition ahbd = this.getAuditHandlerBeanDefinitionBuilder(configuration).getBeanDefinition();
        registry.registerBeanDefinition(this.getAuditingHandlerBeanName(), (BeanDefinition)ahbd);
        return ahbd;
    }

    protected BeanDefinitionBuilder getAuditHandlerBeanDefinitionBuilder(AuditingConfiguration configuration) {
        Assert.notNull((Object)configuration, (String)"AuditingConfiguration must not be null!");
        return this.configureDefaultAuditHandlerAttributes(configuration, BeanDefinitionBuilder.rootBeanDefinition(AuditingHandler.class));
    }

    protected BeanDefinitionBuilder configureDefaultAuditHandlerAttributes(AuditingConfiguration configuration, BeanDefinitionBuilder builder) {
        if (StringUtils.hasText((String)configuration.getAuditorAwareRef())) {
            builder.addPropertyValue(AUDITOR_AWARE, (Object)this.createLazyInitTargetSourceBeanDefinition(configuration.getAuditorAwareRef()));
        } else {
            builder.setAutowireMode(2);
        }
        builder.addPropertyValue(SET_DATES, (Object)configuration.isSetDates());
        builder.addPropertyValue(MODIFY_ON_CREATE, (Object)configuration.isModifyOnCreate());
        if (StringUtils.hasText((String)configuration.getDateTimeProviderRef())) {
            builder.addPropertyReference(DATE_TIME_PROVIDER, configuration.getDateTimeProviderRef());
        } else {
            builder.addPropertyValue(DATE_TIME_PROVIDER, (Object)CurrentDateTimeProvider.INSTANCE);
        }
        builder.setRole(2);
        return builder;
    }

    protected AuditingConfiguration getConfiguration(AnnotationMetadata annotationMetadata) {
        return new AnnotationAuditingConfiguration(annotationMetadata, this.getAnnotation());
    }

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract void registerAuditListenerBeanDefinition(BeanDefinition var1, BeanDefinitionRegistry var2);

    protected abstract String getAuditingHandlerBeanName();

    protected void registerInfrastructureBeanWithId(AbstractBeanDefinition definition, String id, BeanDefinitionRegistry registry) {
        definition.setRole(2);
        registry.registerBeanDefinition(id, (BeanDefinition)definition);
    }

    private BeanDefinition createLazyInitTargetSourceBeanDefinition(String auditorAwareRef) {
        BeanDefinitionBuilder targetSourceBuilder = BeanDefinitionBuilder.rootBeanDefinition(LazyInitTargetSource.class);
        targetSourceBuilder.addPropertyValue("targetBeanName", (Object)auditorAwareRef);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProxyFactoryBean.class);
        builder.addPropertyValue("targetSource", (Object)targetSourceBuilder.getBeanDefinition());
        return builder.getBeanDefinition();
    }
}

