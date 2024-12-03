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

public class BlueprintAttributeCallback
implements AttributeCallback {
    private static final String ACTIVATION_ATTR = "activation";
    private static final String LAZY_ACTIVATION = "lazy";

    @Override
    public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
        String name = attribute.getLocalName();
        String value = attribute.getValue();
        if (ACTIVATION_ATTR.equals(name) && StringUtils.hasText((String)value)) {
            if (LAZY_ACTIVATION.equalsIgnoreCase(value)) {
                builder.setLazyInit(true);
            } else {
                builder.setLazyInit(false);
            }
            return false;
        }
        return true;
    }
}

