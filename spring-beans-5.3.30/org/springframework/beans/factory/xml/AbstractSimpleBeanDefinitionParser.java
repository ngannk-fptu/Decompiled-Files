/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Conventions
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public abstract class AbstractSimpleBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        NamedNodeMap attributes = element.getAttributes();
        for (int x = 0; x < attributes.getLength(); ++x) {
            Attr attribute = (Attr)attributes.item(x);
            if (!this.isEligibleAttribute(attribute, parserContext)) continue;
            String propertyName = this.extractPropertyName(attribute.getLocalName());
            Assert.state((boolean)StringUtils.hasText((String)propertyName), (String)"Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");
            builder.addPropertyValue(propertyName, attribute.getValue());
        }
        this.postProcess(builder, element);
    }

    protected boolean isEligibleAttribute(Attr attribute, ParserContext parserContext) {
        String fullName = attribute.getName();
        return !fullName.equals("xmlns") && !fullName.startsWith("xmlns:") && this.isEligibleAttribute(parserContext.getDelegate().getLocalName(attribute));
    }

    protected boolean isEligibleAttribute(String attributeName) {
        return !"id".equals(attributeName);
    }

    protected String extractPropertyName(String attributeName) {
        return Conventions.attributeNameToPropertyName((String)attributeName);
    }

    protected void postProcess(BeanDefinitionBuilder beanDefinition, Element element) {
    }
}

