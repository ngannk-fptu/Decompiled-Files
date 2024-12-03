/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.ManagedMap
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.transaction.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.config.TxNamespaceHandler;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

class TxAdviceBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    private static final String METHOD_ELEMENT = "method";
    private static final String METHOD_NAME_ATTRIBUTE = "name";
    private static final String ATTRIBUTES_ELEMENT = "attributes";
    private static final String TIMEOUT_ATTRIBUTE = "timeout";
    private static final String READ_ONLY_ATTRIBUTE = "read-only";
    private static final String PROPAGATION_ATTRIBUTE = "propagation";
    private static final String ISOLATION_ATTRIBUTE = "isolation";
    private static final String ROLLBACK_FOR_ATTRIBUTE = "rollback-for";
    private static final String NO_ROLLBACK_FOR_ATTRIBUTE = "no-rollback-for";

    TxAdviceBeanDefinitionParser() {
    }

    protected Class<?> getBeanClass(Element element) {
        return TransactionInterceptor.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyReference("transactionManager", TxNamespaceHandler.getTransactionManagerName(element));
        List txAttributes = DomUtils.getChildElementsByTagName((Element)element, (String)ATTRIBUTES_ELEMENT);
        if (txAttributes.size() > 1) {
            parserContext.getReaderContext().error("Element <attributes> is allowed at most once inside element <advice>", (Object)element);
        } else if (txAttributes.size() == 1) {
            Element attributeSourceElement = (Element)txAttributes.get(0);
            RootBeanDefinition attributeSourceDefinition = this.parseAttributeSource(attributeSourceElement, parserContext);
            builder.addPropertyValue("transactionAttributeSource", (Object)attributeSourceDefinition);
        } else {
            builder.addPropertyValue("transactionAttributeSource", (Object)new RootBeanDefinition("org.springframework.transaction.annotation.AnnotationTransactionAttributeSource"));
        }
    }

    private RootBeanDefinition parseAttributeSource(Element attrEle, ParserContext parserContext) {
        List methods = DomUtils.getChildElementsByTagName((Element)attrEle, (String)METHOD_ELEMENT);
        ManagedMap transactionAttributeMap = new ManagedMap(methods.size());
        transactionAttributeMap.setSource(parserContext.extractSource((Object)attrEle));
        for (Element methodEle : methods) {
            String name = methodEle.getAttribute(METHOD_NAME_ATTRIBUTE);
            TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource((Object)methodEle));
            RuleBasedTransactionAttribute attribute = new RuleBasedTransactionAttribute();
            String propagation = methodEle.getAttribute(PROPAGATION_ATTRIBUTE);
            String isolation = methodEle.getAttribute(ISOLATION_ATTRIBUTE);
            String timeout = methodEle.getAttribute(TIMEOUT_ATTRIBUTE);
            String readOnly = methodEle.getAttribute(READ_ONLY_ATTRIBUTE);
            if (StringUtils.hasText((String)propagation)) {
                attribute.setPropagationBehaviorName("PROPAGATION_" + propagation);
            }
            if (StringUtils.hasText((String)isolation)) {
                attribute.setIsolationLevelName("ISOLATION_" + isolation);
            }
            if (StringUtils.hasText((String)timeout)) {
                attribute.setTimeoutString(timeout);
            }
            if (StringUtils.hasText((String)readOnly)) {
                attribute.setReadOnly(Boolean.parseBoolean(methodEle.getAttribute(READ_ONLY_ATTRIBUTE)));
            }
            ArrayList<RollbackRuleAttribute> rollbackRules = new ArrayList<RollbackRuleAttribute>(1);
            if (methodEle.hasAttribute(ROLLBACK_FOR_ATTRIBUTE)) {
                String rollbackForValue = methodEle.getAttribute(ROLLBACK_FOR_ATTRIBUTE);
                this.addRollbackRuleAttributesTo(rollbackRules, rollbackForValue);
            }
            if (methodEle.hasAttribute(NO_ROLLBACK_FOR_ATTRIBUTE)) {
                String noRollbackForValue = methodEle.getAttribute(NO_ROLLBACK_FOR_ATTRIBUTE);
                this.addNoRollbackRuleAttributesTo(rollbackRules, noRollbackForValue);
            }
            attribute.setRollbackRules(rollbackRules);
            transactionAttributeMap.put((Object)nameHolder, (Object)attribute);
        }
        RootBeanDefinition attributeSourceDefinition = new RootBeanDefinition(NameMatchTransactionAttributeSource.class);
        attributeSourceDefinition.setSource(parserContext.extractSource((Object)attrEle));
        attributeSourceDefinition.getPropertyValues().add("nameMap", (Object)transactionAttributeMap);
        return attributeSourceDefinition;
    }

    private void addRollbackRuleAttributesTo(List<RollbackRuleAttribute> rollbackRules, String rollbackForValue) {
        String[] exceptionTypeNames;
        for (String typeName : exceptionTypeNames = StringUtils.commaDelimitedListToStringArray((String)rollbackForValue)) {
            rollbackRules.add(new RollbackRuleAttribute(StringUtils.trimWhitespace((String)typeName)));
        }
    }

    private void addNoRollbackRuleAttributesTo(List<RollbackRuleAttribute> rollbackRules, String noRollbackForValue) {
        String[] exceptionTypeNames;
        for (String typeName : exceptionTypeNames = StringUtils.commaDelimitedListToStringArray((String)noRollbackForValue)) {
            rollbackRules.add(new NoRollbackRuleAttribute(StringUtils.trimWhitespace((String)typeName)));
        }
    }
}

