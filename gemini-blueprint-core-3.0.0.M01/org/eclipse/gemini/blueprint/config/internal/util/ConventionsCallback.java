/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.core.Conventions
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.Conventions;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class ConventionsCallback
implements AttributeCallback {
    @Override
    public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
        String name = attribute.getLocalName();
        String propertyName = Conventions.attributeNameToPropertyName((String)name);
        builder.addPropertyValue(propertyName, (Object)attribute.getValue());
        return true;
    }
}

