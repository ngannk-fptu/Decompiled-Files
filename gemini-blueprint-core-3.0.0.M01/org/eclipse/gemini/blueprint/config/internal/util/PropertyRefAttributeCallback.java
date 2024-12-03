/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class PropertyRefAttributeCallback
implements AttributeCallback {
    private static final String PROPERTY_REF = "-ref";

    @Override
    public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
        String name = attribute.getLocalName();
        if (name.endsWith(PROPERTY_REF)) {
            String propertyName = name.substring(0, name.length() - PROPERTY_REF.length());
            builder.addPropertyReference(propertyName, attribute.getValue());
            return false;
        }
        return true;
    }
}

