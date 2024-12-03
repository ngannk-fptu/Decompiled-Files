/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.util.ClassUtils
 */
package com.atlassian.plugin.spring.scanner.runtime.impl;

import com.atlassian.plugin.spring.scanner.runtime.impl.util.AnnotationIndexReader;
import com.atlassian.plugin.spring.scanner.runtime.impl.util.BeanDefinitionChecker;
import java.beans.Introspector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.ClassUtils;

public class ClassIndexBeanDefinitionScanner {
    private static final Logger log = LoggerFactory.getLogger(ClassIndexBeanDefinitionScanner.class);
    private final BeanDefinitionRegistry registry;
    private final String profileName;
    private final Integer autowireDefault;
    private final BundleContext bundleContext;

    public ClassIndexBeanDefinitionScanner(BeanDefinitionRegistry registry, String profileName, Integer autowireDefault, BundleContext bundleContext) {
        this.registry = registry;
        this.profileName = profileName;
        this.autowireDefault = autowireDefault;
        this.bundleContext = bundleContext;
    }

    protected Set<BeanDefinitionHolder> doScan() {
        LinkedHashSet<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<BeanDefinitionHolder>();
        Map<String, BeanDefinition> namesAndDefinitions = this.findCandidateComponents();
        for (Map.Entry<String, BeanDefinition> nameAndDefinition : namesAndDefinitions.entrySet()) {
            if (!BeanDefinitionChecker.needToRegister(nameAndDefinition.getKey(), nameAndDefinition.getValue(), this.registry)) continue;
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(nameAndDefinition.getValue(), nameAndDefinition.getKey());
            beanDefinitions.add(definitionHolder);
            this.registerBeanDefinition(definitionHolder, this.registry);
        }
        return beanDefinitions;
    }

    public Map<String, BeanDefinition> findCandidateComponents() {
        HashMap<String, BeanDefinition> candidates = new HashMap<String, BeanDefinition>();
        TreeSet<String> beanTypeAndNames = new TreeSet<String>();
        String[] profileNames = AnnotationIndexReader.splitProfiles(this.profileName);
        for (String fileToRead : AnnotationIndexReader.getIndexFilesForProfiles(profileNames, "component")) {
            beanTypeAndNames.addAll(AnnotationIndexReader.readAllIndexFilesForProduct(fileToRead, this.bundleContext));
        }
        Set<String> primaryComponentTypeAndNames = this.findPrimaryComponentTypeAndNames(profileNames);
        for (String beanTypeAndName : beanTypeAndNames) {
            String[] typeAndName = beanTypeAndName.split("#");
            String beanClassName = typeAndName[0];
            String beanName = "";
            if (typeAndName.length > 1) {
                beanName = typeAndName[1];
            }
            if (beanName.isEmpty()) {
                beanName = Introspector.decapitalize(ClassUtils.getShortName((String)beanClassName));
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("Found candidate bean '%s' from class '%s'", beanName, beanClassName));
            }
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition((String)beanClassName);
            if (null != this.autowireDefault) {
                beanDefinitionBuilder.setAutowireMode(this.autowireDefault.intValue());
            }
            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            if (primaryComponentTypeAndNames.contains(beanTypeAndName)) {
                beanDefinition.setPrimary(true);
            }
            candidates.put(beanName, (BeanDefinition)beanDefinition);
        }
        return candidates;
    }

    private Set<String> findPrimaryComponentTypeAndNames(String[] profileNames) {
        HashSet<String> beanTypeAndNames = new HashSet<String>();
        for (String fileToRead : AnnotationIndexReader.getIndexFilesForProfiles(profileNames, "primary-component")) {
            beanTypeAndNames.addAll(AnnotationIndexReader.readAllIndexFilesForProduct(fileToRead, this.bundleContext));
        }
        return beanTypeAndNames;
    }

    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition((BeanDefinitionHolder)definitionHolder, (BeanDefinitionRegistry)registry);
    }
}

