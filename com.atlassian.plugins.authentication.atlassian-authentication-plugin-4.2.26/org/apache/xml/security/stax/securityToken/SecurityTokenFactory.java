/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityToken;

import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;

public abstract class SecurityTokenFactory {
    private static SecurityTokenFactory instance;

    public static synchronized SecurityTokenFactory getInstance() throws XMLSecurityException {
        if (instance == null) {
            String stf = ConfigurationProperties.getProperty("securityTokenFactory");
            if (stf == null) {
                throw new XMLSecurityException("algorithm.ClassDoesNotExist", new Object[]{"null"});
            }
            Class<Object> callingClass = ConfigurationProperties.getCallingClass();
            if (callingClass == null) {
                callingClass = SecurityTokenFactory.class;
            }
            try {
                Class<?> securityTokenFactoryClass = ClassLoaderUtils.loadClass(stf, callingClass);
                instance = (SecurityTokenFactory)JavaUtils.newInstanceWithEmptyConstructor(securityTokenFactoryClass);
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new XMLSecurityException(e, "algorithm.ClassDoesNotExist", new Object[]{stf});
            }
        }
        return instance;
    }

    public abstract InboundSecurityToken getSecurityToken(KeyInfoType var1, SecurityTokenConstants.KeyUsage var2, XMLSecurityProperties var3, InboundSecurityContext var4) throws XMLSecurityException;
}

