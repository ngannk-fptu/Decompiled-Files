/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.opensaml.core.xml.XMLObjectBuilderFactory
 *  org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport
 */
package com.nimbusds.oauth2.sdk.assertions.saml2;

import javax.xml.namespace.QName;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;

final class SAML2Utils {
    public static <T> T buildSAMLObject(Class<T> clazz) {
        try {
            XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            QName defaultElementName = (QName)clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
            return (T)builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalArgumentException("Couldn't create SAML object with class " + clazz.getCanonicalName() + ": " + e.getMessage(), e);
        }
    }

    private SAML2Utils() {
    }
}

