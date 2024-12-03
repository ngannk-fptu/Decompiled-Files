/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import java.util.Locale;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class ServiceAttributeCallback
implements AttributeCallback {
    private static final char UNDERSCORE_CHAR = '_';
    private static final char DASH_CHAR = '-';
    private static final String AUTOEXPORT = "auto-export";
    private static final String AUTOEXPORT_PROP = "interfaceDetector";
    private static final String INTERFACE = "interface";
    private static final String INTERFACES_PROP = "interfaces";
    private static final String CCL_PROP = "exportContextClassLoader";
    private static final String CONTEXT_CLASSLOADER = "context-class-loader";
    private static final String REF = "ref";

    @Override
    public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder bldr) {
        String name = attribute.getLocalName();
        if (INTERFACE.equals(name)) {
            bldr.addPropertyValue(INTERFACES_PROP, (Object)attribute.getValue());
            return false;
        }
        if (REF.equals(name)) {
            return false;
        }
        if (AUTOEXPORT.equals(name)) {
            String label = attribute.getValue().toUpperCase(Locale.ENGLISH).replace('-', '_');
            bldr.addPropertyValue(AUTOEXPORT_PROP, (Object)Enum.valueOf(DefaultInterfaceDetector.class, label));
            return false;
        }
        if (CONTEXT_CLASSLOADER.equals(name)) {
            String value = attribute.getValue().toUpperCase(Locale.ENGLISH).replace('-', '_');
            bldr.addPropertyValue(CCL_PROP, (Object)ExportContextClassLoaderEnum.valueOf(value));
            return false;
        }
        return true;
    }
}

