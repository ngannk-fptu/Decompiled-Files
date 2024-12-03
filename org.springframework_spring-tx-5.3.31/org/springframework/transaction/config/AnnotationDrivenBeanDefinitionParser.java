/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.config.AopNamespaceUtils
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.transaction.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.transaction.config.TxNamespaceHandler;
import org.springframework.transaction.event.TransactionalEventListenerFactory;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

class AnnotationDrivenBeanDefinitionParser
implements BeanDefinitionParser {
    AnnotationDrivenBeanDefinitionParser() {
    }

    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        this.registerTransactionalEventListenerFactory(parserContext);
        String mode = element.getAttribute("mode");
        if ("aspectj".equals(mode)) {
            this.registerTransactionAspect(element, parserContext);
            if (ClassUtils.isPresent((String)"javax.transaction.Transactional", (ClassLoader)this.getClass().getClassLoader())) {
                this.registerJtaTransactionAspect(element, parserContext);
            }
        } else {
            AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
        }
        return null;
    }

    private void registerTransactionAspect(Element element, ParserContext parserContext) {
        String txAspectBeanName = "org.springframework.transaction.config.internalTransactionAspect";
        String txAspectClassName = "org.springframework.transaction.aspectj.AnnotationTransactionAspect";
        if (!parserContext.getRegistry().containsBeanDefinition(txAspectBeanName)) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClassName(txAspectClassName);
            def.setFactoryMethodName("aspectOf");
            AnnotationDrivenBeanDefinitionParser.registerTransactionManager(element, (BeanDefinition)def);
            parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)def, txAspectBeanName));
        }
    }

    private void registerJtaTransactionAspect(Element element, ParserContext parserContext) {
        String txAspectBeanName = "org.springframework.transaction.config.internalJtaTransactionAspect";
        String txAspectClassName = "org.springframework.transaction.aspectj.JtaAnnotationTransactionAspect";
        if (!parserContext.getRegistry().containsBeanDefinition(txAspectBeanName)) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClassName(txAspectClassName);
            def.setFactoryMethodName("aspectOf");
            AnnotationDrivenBeanDefinitionParser.registerTransactionManager(element, (BeanDefinition)def);
            parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)def, txAspectBeanName));
        }
    }

    private static void registerTransactionManager(Element element, BeanDefinition def) {
        def.getPropertyValues().add("transactionManagerBeanName", (Object)TxNamespaceHandler.getTransactionManagerName(element));
    }

    private void registerTransactionalEventListenerFactory(ParserContext parserContext) {
        RootBeanDefinition def = new RootBeanDefinition();
        def.setBeanClass(TransactionalEventListenerFactory.class);
        parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)def, "org.springframework.transaction.config.internalTransactionalEventListenerFactory"));
    }

    private static class AopAutoProxyConfigurer {
        private AopAutoProxyConfigurer() {
        }

        public static void configureAutoProxyCreator(Element element, ParserContext parserContext) {
            AopNamespaceUtils.registerAutoProxyCreatorIfNecessary((ParserContext)parserContext, (Element)element);
            String txAdvisorBeanName = "org.springframework.transaction.config.internalTransactionAdvisor";
            if (!parserContext.getRegistry().containsBeanDefinition(txAdvisorBeanName)) {
                Object eleSource = parserContext.extractSource((Object)element);
                RootBeanDefinition sourceDef = new RootBeanDefinition("org.springframework.transaction.annotation.AnnotationTransactionAttributeSource");
                sourceDef.setSource(eleSource);
                sourceDef.setRole(2);
                String sourceName = parserContext.getReaderContext().registerWithGeneratedName((BeanDefinition)sourceDef);
                RootBeanDefinition interceptorDef = new RootBeanDefinition(TransactionInterceptor.class);
                interceptorDef.setSource(eleSource);
                interceptorDef.setRole(2);
                AnnotationDrivenBeanDefinitionParser.registerTransactionManager(element, (BeanDefinition)interceptorDef);
                interceptorDef.getPropertyValues().add("transactionAttributeSource", (Object)new RuntimeBeanReference(sourceName));
                String interceptorName = parserContext.getReaderContext().registerWithGeneratedName((BeanDefinition)interceptorDef);
                RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryTransactionAttributeSourceAdvisor.class);
                advisorDef.setSource(eleSource);
                advisorDef.setRole(2);
                advisorDef.getPropertyValues().add("transactionAttributeSource", (Object)new RuntimeBeanReference(sourceName));
                advisorDef.getPropertyValues().add("adviceBeanName", (Object)interceptorName);
                if (element.hasAttribute("order")) {
                    advisorDef.getPropertyValues().add("order", (Object)element.getAttribute("order"));
                }
                parserContext.getRegistry().registerBeanDefinition(txAdvisorBeanName, (BeanDefinition)advisorDef);
                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)sourceDef, sourceName));
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)interceptorDef, interceptorName));
                compositeDef.addNestedComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)advisorDef, txAdvisorBeanName));
                parserContext.registerComponent((ComponentDefinition)compositeDef);
            }
        }
    }
}

