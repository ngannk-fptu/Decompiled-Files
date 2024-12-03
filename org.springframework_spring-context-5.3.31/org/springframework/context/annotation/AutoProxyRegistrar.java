/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.config.AopConfigUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotatedTypeMetadata
 *  org.springframework.core.type.AnnotationMetadata
 */
package org.springframework.context.annotation;

import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

public class AutoProxyRegistrar
implements ImportBeanDefinitionRegistrar {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean candidateFound = false;
        Set annTypes = importingClassMetadata.getAnnotationTypes();
        for (String annType : annTypes) {
            AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)importingClassMetadata, annType);
            if (candidate == null) continue;
            Object mode = candidate.get((Object)"mode");
            Object proxyTargetClass = candidate.get((Object)"proxyTargetClass");
            if (mode == null || proxyTargetClass == null || AdviceMode.class != mode.getClass() || Boolean.class != proxyTargetClass.getClass()) continue;
            candidateFound = true;
            if (mode != AdviceMode.PROXY) continue;
            AopConfigUtils.registerAutoProxyCreatorIfNecessary((BeanDefinitionRegistry)registry);
            if (!((Boolean)proxyTargetClass).booleanValue()) continue;
            AopConfigUtils.forceAutoProxyCreatorToUseClassProxying((BeanDefinitionRegistry)registry);
            return;
        }
        if (!candidateFound && this.logger.isInfoEnabled()) {
            String name = this.getClass().getSimpleName();
            this.logger.info((Object)String.format("%s was imported but no annotations were found having both 'mode' and 'proxyTargetClass' attributes of type AdviceMode and boolean respectively. This means that auto proxy creator registration and configuration may not have occurred as intended, and components may not be proxied as expected. Check to ensure that %s has been @Import'ed on the same class where these annotations are declared; otherwise remove the import of %s altogether.", name, name, name));
        }
    }
}

