/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 */
package org.eclipse.gemini.blueprint.blueprint.config.internal;

import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ReferenceParsingUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class BlueprintReferenceAttributeCallback
implements AttributeCallback {
    private static final String AVAILABILITY = "availability";
    private static final String SERVICE_BEAN_NAME_PROP = "serviceBeanName";
    private static final String COMPONENT_NAME = "component-name";

    @Override
    public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
        String name = attribute.getLocalName();
        String value = attribute.getValue();
        if (AVAILABILITY.equals(name)) {
            builder.addPropertyValue(AVAILABILITY, (Object)ReferenceParsingUtil.determineAvailability(value));
            return false;
        }
        if (COMPONENT_NAME.equals(name)) {
            builder.addPropertyValue(SERVICE_BEAN_NAME_PROP, (Object)value);
            return false;
        }
        return true;
    }
}

