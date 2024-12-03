/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

public interface BeanDefinitionParser {
    @Nullable
    public BeanDefinition parse(Element var1, ParserContext var2);
}

