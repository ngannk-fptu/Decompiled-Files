/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.beans.factory.xml.XmlReaderContext
 *  org.springframework.context.annotation.AnnotationConfigUtils
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.ClassUtils
 */
package com.atlassian.plugin.spring.scanner.runtime.impl;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.runtime.impl.ClassIndexBeanDefinitionScanner;
import com.atlassian.plugin.spring.scanner.runtime.impl.ComponentImportBeanFactoryPostProcessor;
import com.atlassian.plugin.spring.scanner.runtime.impl.DevModeBeanInitialisationLoggerBeanPostProcessor;
import com.atlassian.plugin.spring.scanner.runtime.impl.ServiceExporterBeanPostProcessor;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

public class AtlassianScannerBeanDefinitionParser
implements BeanDefinitionParser {
    private static final String PROFILE_ATTRIBUTE = "profile";
    public static final String JAVAX_INJECT_CLASSNAME = "javax.inject.Inject";
    private static final Logger log = LoggerFactory.getLogger(AtlassianScannerBeanDefinitionParser.class);

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String profileName = null;
        Integer autowireDefault = null;
        if (element.hasAttribute(PROFILE_ATTRIBUTE)) {
            profileName = element.getAttribute(PROFILE_ATTRIBUTE);
        }
        if (element.hasAttribute("autowire")) {
            autowireDefault = parserContext.getDelegate().getAutowireMode(element.getAttribute("autowire"));
        }
        BundleContext targetPluginBundleContext = this.getBundleContext(parserContext);
        this.checkScannerRuntimeIsNotEmbeddedInBundle(targetPluginBundleContext);
        ClassIndexBeanDefinitionScanner scanner = new ClassIndexBeanDefinitionScanner(parserContext.getReaderContext().getRegistry(), profileName, autowireDefault, targetPluginBundleContext);
        Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan();
        this.registerComponents(parserContext.getReaderContext(), beanDefinitions, element, profileName);
        return null;
    }

    protected void registerComponents(XmlReaderContext readerContext, Set<BeanDefinitionHolder> beanDefinitions, Element element, String profileName) {
        Object source = readerContext.extractSource((Object)element);
        CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
        for (BeanDefinitionHolder beanDefHolder : beanDefinitions) {
            compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition(beanDefHolder));
        }
        LinkedHashSet<BeanDefinitionHolder> processorDefinitions = new LinkedHashSet<BeanDefinitionHolder>(AnnotationConfigUtils.registerAnnotationConfigProcessors((BeanDefinitionRegistry)readerContext.getRegistry(), (Object)source));
        BeanDefinitionHolder javaxInject = this.getJavaxInjectPostProcessor(readerContext.getRegistry(), source);
        if (null != javaxInject) {
            processorDefinitions.add(javaxInject);
        }
        processorDefinitions.add(this.getComponentImportPostProcessor(readerContext.getRegistry(), source, profileName));
        processorDefinitions.add(this.getServiceExportPostProcessor(readerContext.getRegistry(), source, profileName));
        processorDefinitions.add(this.getDevModeBeanInitialisationLoggerPostProcessor(readerContext.getRegistry(), source));
        for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
            compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition(processorDefinition));
        }
        readerContext.fireComponentRegistered((ComponentDefinition)compositeDef);
    }

    private BundleContext getBundleContext(ParserContext parserContext) {
        ResourceLoader resourceLoader = parserContext.getReaderContext().getResourceLoader();
        if (!(resourceLoader instanceof ConfigurableOsgiBundleApplicationContext)) {
            throw new IllegalStateException("Could not access BundleContext from ResourceLoader: expected resourceLoader to be an instance of " + ConfigurableOsgiBundleApplicationContext.class.getName() + ": got " + resourceLoader.getClass().getName());
        }
        BundleContext bundleContext = ((ConfigurableOsgiBundleApplicationContext)resourceLoader).getBundleContext();
        if (bundleContext == null) {
            throw new IllegalStateException("Could not access BundleContext from ResourceLoader: ConfigurableOsgiBundleApplicationContext.getBundleContext returned null");
        }
        return bundleContext;
    }

    private void checkScannerRuntimeIsNotEmbeddedInBundle(BundleContext targetPluginBundleContext) {
        Bundle bundleContainingScannerAnnotationLibsAsSeenByPlugin;
        long scannerRuntimeBundleId;
        Bundle pluginInvokingScannerRuntime = targetPluginBundleContext.getBundle();
        if (pluginInvokingScannerRuntime == null) {
            throw new IllegalStateException("Cannot execute atlassian-spring-scanner-runtime from a plugin that is not in a valid state: bundleContext.getBundle() returned null for plugin bundle.");
        }
        String howToFixScope = "Use 'mvn dependency:tree' and ensure the atlassian-spring-scanner-annotation dependency in your plugin has <scope>provided</scope>, not 'runtime' or 'compile', and you have NO dependency on atlassian-spring-scanner-runtime.";
        Bundle bundleContainingThisScannerRuntime = FrameworkUtil.getBundle(AtlassianScannerBeanDefinitionParser.class);
        if (bundleContainingThisScannerRuntime == null) {
            throw new IllegalStateException("Incorrect use of atlassian-spring-scanner-runtime: atlassian-spring-scanner-runtime classes do not appear to be coming from a bundle classloader. Use 'mvn dependency:tree' and ensure the atlassian-spring-scanner-annotation dependency in your plugin has <scope>provided</scope>, not 'runtime' or 'compile', and you have NO dependency on atlassian-spring-scanner-runtime.");
        }
        Bundle bundleContainingScannerAnnotationLibsAsSeenByRuntime = FrameworkUtil.getBundle(ComponentImport.class);
        if (bundleContainingScannerAnnotationLibsAsSeenByRuntime == null) {
            throw new IllegalStateException("Incorrect use of atlassian-spring-scanner-runtime: atlassian-spring-scanner-annotation classes do not appear to be coming from a bundle classloader. Use 'mvn dependency:tree' and ensure the atlassian-spring-scanner-annotation dependency in your plugin has <scope>provided</scope>, not 'runtime' or 'compile', and you have NO dependency on atlassian-spring-scanner-runtime.");
        }
        long invokingPluginBundleId = pluginInvokingScannerRuntime.getBundleId();
        if (invokingPluginBundleId == (scannerRuntimeBundleId = bundleContainingThisScannerRuntime.getBundleId())) {
            throw new IllegalStateException("Incorrect use of atlassian-spring-scanner-runtime: atlassian-spring-scanner-runtime classes are embedded inside the target plugin '" + pluginInvokingScannerRuntime.getSymbolicName() + "'; embedding scanner-runtime is not supported since scanner version 2.0. " + "Use 'mvn dependency:tree' and ensure the atlassian-spring-scanner-annotation dependency in your plugin has <scope>provided</scope>, not 'runtime' or 'compile', and you have NO dependency on atlassian-spring-scanner-runtime.");
        }
        try {
            bundleContainingScannerAnnotationLibsAsSeenByPlugin = FrameworkUtil.getBundle((Class)pluginInvokingScannerRuntime.loadClass(ComponentImport.class.getName()));
        }
        catch (ClassNotFoundException e) {
            return;
        }
        long scannerAnnotationBundleId = bundleContainingScannerAnnotationLibsAsSeenByRuntime.getBundleId();
        if (bundleContainingScannerAnnotationLibsAsSeenByPlugin == null || bundleContainingScannerAnnotationLibsAsSeenByPlugin.getBundleId() != scannerAnnotationBundleId) {
            throw new IllegalStateException("Cannot execute atlassian-spring-scanner-runtime: plugin has an extra copy of atlassian-spring-scanner-annotation classes, perhaps embedded inside the target plugin '" + pluginInvokingScannerRuntime.getSymbolicName() + "'; embedding scanner-annotations is not supported since scanner version 2.0. " + "Use 'mvn dependency:tree' and ensure the atlassian-spring-scanner-annotation dependency in your plugin has <scope>provided</scope>, not 'runtime' or 'compile', and you have NO dependency on atlassian-spring-scanner-runtime.");
        }
    }

    private BeanDefinitionHolder getJavaxInjectPostProcessor(BeanDefinitionRegistry registry, Object source) {
        if (ClassUtils.isPresent((String)JAVAX_INJECT_CLASSNAME, (ClassLoader)this.getClass().getClassLoader())) {
            try {
                Class<?> injectClass = this.getClass().getClassLoader().loadClass(JAVAX_INJECT_CLASSNAME);
                HashMap properties = new HashMap();
                properties.put("autowiredAnnotationType", injectClass);
                RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
                def.setSource(source);
                def.setRole(2);
                def.setPropertyValues(new MutablePropertyValues(properties));
                return this.registerBeanPostProcessor(registry, def, "javaxInjectBeanPostProcessor");
            }
            catch (ClassNotFoundException e) {
                log.error("Unable to load class 'javax.inject.Inject' for javax component purposes.  Not sure how this is possible.  Skipping...");
            }
        }
        return null;
    }

    private BeanDefinitionHolder registerBeanPostProcessor(BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {
        definition.setRole(2);
        registry.registerBeanDefinition(beanName, (BeanDefinition)definition);
        return new BeanDefinitionHolder((BeanDefinition)definition, beanName);
    }

    private BeanDefinitionHolder getComponentImportPostProcessor(BeanDefinitionRegistry registry, Object source, String profileName) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("profileName", profileName);
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(ComponentImportBeanFactoryPostProcessor.class);
        rootBeanDefinition.setAutowireMode(3);
        rootBeanDefinition.setSource(source);
        rootBeanDefinition.setPropertyValues(new MutablePropertyValues(properties));
        return this.registerBeanPostProcessor(registry, rootBeanDefinition, "componentImportBeanFactoryPostProcessor");
    }

    private BeanDefinitionHolder getServiceExportPostProcessor(BeanDefinitionRegistry registry, Object source, String profileName) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("profileName", profileName);
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(ServiceExporterBeanPostProcessor.class);
        rootBeanDefinition.setSource(source);
        rootBeanDefinition.setAutowireMode(3);
        rootBeanDefinition.setPropertyValues(new MutablePropertyValues(properties));
        return this.registerBeanPostProcessor(registry, rootBeanDefinition, "serviceExportBeanPostProcessor");
    }

    private BeanDefinitionHolder getDevModeBeanInitialisationLoggerPostProcessor(BeanDefinitionRegistry registry, Object source) {
        RootBeanDefinition def = new RootBeanDefinition(DevModeBeanInitialisationLoggerBeanPostProcessor.class);
        def.setSource(source);
        def.setAutowireMode(3);
        return this.registerBeanPostProcessor(registry, def, "devModeBeanInitialisationLoggerBeanPostProcessor");
    }
}

