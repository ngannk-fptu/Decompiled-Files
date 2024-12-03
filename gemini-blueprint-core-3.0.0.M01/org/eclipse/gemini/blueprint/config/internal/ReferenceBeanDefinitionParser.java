/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 */
package org.eclipse.gemini.blueprint.config.internal;

import org.eclipse.gemini.blueprint.config.internal.AbstractReferenceDefinitionParser;
import org.eclipse.gemini.blueprint.config.internal.OsgiDefaultsDefinition;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class ReferenceBeanDefinitionParser
extends AbstractReferenceDefinitionParser {
    private static final String TIMEOUT_PROP = "timeout";
    protected static final String TIMEOUT = "timeout";

    @Override
    protected Class getBeanClass(Element element) {
        return OsgiServiceProxyFactoryBean.class;
    }

    @Override
    protected void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks, OsgiDefaultsDefinition defaults) {
        TimeoutAttributeCallback timeoutCallback = new TimeoutAttributeCallback();
        super.parseAttributes(element, builder, ParserUtils.mergeCallbacks(callbacks, new AttributeCallback[]{timeoutCallback}), defaults);
        if (!timeoutCallback.isTimeoutSpecified) {
            this.applyDefaultTimeout(builder, defaults);
        }
    }

    protected void applyDefaultTimeout(BeanDefinitionBuilder builder, OsgiDefaultsDefinition defaults) {
        builder.addPropertyValue("timeout", (Object)new TypedStringValue(defaults.getTimeout()));
    }

    static class TimeoutAttributeCallback
    implements AttributeCallback {
        boolean isTimeoutSpecified = false;

        TimeoutAttributeCallback() {
        }

        @Override
        public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
            String name = attribute.getLocalName();
            if ("timeout".equals(name)) {
                this.isTimeoutSpecified = true;
            }
            return true;
        }
    }
}

