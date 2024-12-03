/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.JaxbProvider;
import com.sun.jersey.json.impl.NameUtil;
import com.sun.jersey.json.impl.SupportedJaxbProvider;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;

public final class JSONHelper {
    private static JaxbProvider jaxbProvider;

    private JSONHelper() {
    }

    public static String getRootElementName(Class<Object> clazz) {
        XmlRootElement e = clazz.getAnnotation(XmlRootElement.class);
        if (e == null) {
            return JSONHelper.getVariableName(clazz.getSimpleName());
        }
        if ("##default".equals(e.name())) {
            return JSONHelper.getVariableName(clazz.getSimpleName());
        }
        return e.name();
    }

    private static String getVariableName(String baseName) {
        return NameUtil.toMixedCaseName(NameUtil.toWordList(baseName), false);
    }

    public static JaxbProvider getJaxbProvider(JAXBContext jaxbContext) {
        for (SupportedJaxbProvider provider : SupportedJaxbProvider.values()) {
            try {
                Class<?> jaxbContextClass = JSONHelper.getJaxbContextClass(jaxbContext);
                Class<?> clazz = null;
                clazz = SupportedJaxbProvider.JAXB_JDK.equals(provider) ? ClassLoader.getSystemClassLoader().loadClass(SupportedJaxbProvider.JAXB_JDK.getJaxbContextClassName()) : Class.forName(provider.getJaxbContextClassName());
                if (!clazz.isAssignableFrom(jaxbContextClass)) continue;
                jaxbProvider = provider;
                return jaxbProvider;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new IllegalStateException("No JAXB provider found for the following JAXB context: " + (jaxbContext == null ? null : jaxbContext.getClass()));
    }

    private static Class<?> getJaxbContextClass(JAXBContext jaxbContext) throws ClassNotFoundException {
        if (jaxbContext != null) {
            return jaxbContext.getClass();
        }
        return ClassLoader.getSystemClassLoader().loadClass(SupportedJaxbProvider.JAXB_JDK.getJaxbContextClassName());
    }

    public static boolean isNaturalNotationEnabled() {
        try {
            Class.forName("com.sun.xml.bind.annotation.OverrideAnnotationOf");
            return true;
        }
        catch (ClassNotFoundException e) {
            if (jaxbProvider == SupportedJaxbProvider.JAXB_RI) {
                return false;
            }
            try {
                ClassLoader.getSystemClassLoader().loadClass("com.sun.xml.internal.bind.annotation.OverrideAnnotationOf");
                return true;
            }
            catch (ClassNotFoundException e2) {
                if (jaxbProvider == SupportedJaxbProvider.JAXB_JDK) {
                    return false;
                }
                return jaxbProvider == null || jaxbProvider == SupportedJaxbProvider.MOXY;
            }
        }
    }

    public static Map<String, Object> createPropertiesForJaxbContext(Map<String, Object> properties) {
        HashMap<String, Object> jaxbProperties = new HashMap<String, Object>(properties.size() + 1);
        String retainReferenceToInfo = "retainReferenceToInfo";
        jaxbProperties.putAll(properties);
        jaxbProperties.put("retainReferenceToInfo", Boolean.TRUE);
        return jaxbProperties;
    }
}

