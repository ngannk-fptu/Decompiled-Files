/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class StandardAttributeCallback
implements AttributeCallback {
    @Override
    public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
        String name = attribute.getLocalName();
        if ("id".equals(name)) {
            return false;
        }
        if ("depends-on".equals(name)) {
            builder.getBeanDefinition().setDependsOn(StringUtils.tokenizeToStringArray((String)attribute.getValue(), (String)",; "));
            return false;
        }
        if ("lazy-init".equals(name)) {
            builder.setLazyInit(Boolean.valueOf(attribute.getValue()).booleanValue());
            return false;
        }
        return true;
    }
}

