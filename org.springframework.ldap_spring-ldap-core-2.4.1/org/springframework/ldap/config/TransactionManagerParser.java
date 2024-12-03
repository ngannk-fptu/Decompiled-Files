/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.ldap.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.ldap.config.ParserUtils;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceAndDataSourceTransactionManager;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceAndHibernateTransactionManager;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManager;
import org.springframework.ldap.transaction.compensating.support.DefaultTempEntryRenamingStrategy;
import org.springframework.ldap.transaction.compensating.support.DifferentSubtreeTempEntryRenamingStrategy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class TransactionManagerParser
implements BeanDefinitionParser {
    private static final String ATT_CONTEXT_SOURCE_REF = "context-source-ref";
    private static final String ATT_DATA_SOURCE_REF = "data-source-ref";
    private static final String ATT_SESSION_FACTORY_REF = "session-factory-ref";
    private static final String ATT_TEMP_SUFFIX = "temp-suffix";
    private static final String ATT_SUBTREE_NODE = "subtree-node";
    private static final String DEFAULT_ID = "transactionManager";

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder;
        String contextSourceRef = ParserUtils.getString(element, ATT_CONTEXT_SOURCE_REF, "contextSource");
        String dataSourceRef = element.getAttribute(ATT_DATA_SOURCE_REF);
        String sessionFactoryRef = element.getAttribute(ATT_SESSION_FACTORY_REF);
        if (StringUtils.hasText((String)dataSourceRef) && StringUtils.hasText((String)sessionFactoryRef)) {
            throw new IllegalArgumentException(String.format("Only one of %s and %s can be specified", ATT_DATA_SOURCE_REF, ATT_SESSION_FACTORY_REF));
        }
        if (StringUtils.hasText((String)dataSourceRef)) {
            builder = BeanDefinitionBuilder.rootBeanDefinition(ContextSourceAndDataSourceTransactionManager.class);
            builder.addPropertyReference("dataSource", dataSourceRef);
        } else if (StringUtils.hasText((String)sessionFactoryRef)) {
            builder = BeanDefinitionBuilder.rootBeanDefinition(ContextSourceAndHibernateTransactionManager.class);
            builder.addPropertyReference("sessionFactory", sessionFactoryRef);
        } else {
            builder = BeanDefinitionBuilder.rootBeanDefinition(ContextSourceTransactionManager.class);
        }
        builder.addPropertyReference("contextSource", contextSourceRef);
        Element defaultStrategyChild = DomUtils.getChildElementByTagName((Element)element, (String)"default-renaming-strategy");
        Element differentSubtreeChild = DomUtils.getChildElementByTagName((Element)element, (String)"different-subtree-renaming-strategy");
        if (defaultStrategyChild != null) {
            builder.addPropertyValue("renamingStrategy", (Object)this.parseDefaultRenamingStrategy(defaultStrategyChild));
        }
        if (differentSubtreeChild != null) {
            builder.addPropertyValue("renamingStrategy", (Object)this.parseDifferentSubtreeRenamingStrategy(differentSubtreeChild));
        }
        String id = ParserUtils.getString(element, "id", DEFAULT_ID);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)beanDefinition, id));
        return beanDefinition;
    }

    private BeanDefinition parseDifferentSubtreeRenamingStrategy(Element element) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DifferentSubtreeTempEntryRenamingStrategy.class);
        String subtreeNode = element.getAttribute(ATT_SUBTREE_NODE);
        Assert.hasText((String)subtreeNode, (String)"subtree-node must be specified");
        builder.addConstructorArgValue((Object)subtreeNode);
        return builder.getBeanDefinition();
    }

    public BeanDefinition parseDefaultRenamingStrategy(Element element) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DefaultTempEntryRenamingStrategy.class);
        builder.addPropertyValue("tempSuffix", (Object)ParserUtils.getString(element, ATT_TEMP_SUFFIX, "_temp"));
        return builder.getBeanDefinition();
    }
}

