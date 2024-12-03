/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.eclipse.gemini.blueprint.blueprint.config;

import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

class BlueprintBeanBeanDefinitionParser
implements BeanDefinitionParser {
    BlueprintBeanBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BlueprintParser parser = new BlueprintParser();
        return parser.parse(element, parserContext);
    }
}

