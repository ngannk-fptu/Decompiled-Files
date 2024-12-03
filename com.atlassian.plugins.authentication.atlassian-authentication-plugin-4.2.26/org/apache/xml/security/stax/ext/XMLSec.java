/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 */
package org.apache.xml.security.stax.ext;

import java.net.URISyntaxException;
import java.net.URL;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashSet;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import org.apache.xml.security.binding.xmldsig.ObjectFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConfigurationException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.xml.sax.SAXException;

public class XMLSec {
    public static void init() {
    }

    public static OutboundXMLSec getOutboundXMLSec(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        if (securityProperties == null) {
            throw new XMLSecurityConfigurationException("stax.missingSecurityProperties");
        }
        securityProperties = XMLSec.validateAndApplyDefaultsToOutboundSecurityProperties(securityProperties);
        return new OutboundXMLSec(securityProperties);
    }

    public static InboundXMLSec getInboundWSSec(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        if (securityProperties == null) {
            throw new XMLSecurityConfigurationException("stax.missingSecurityProperties");
        }
        securityProperties = XMLSec.validateAndApplyDefaultsToInboundSecurityProperties(securityProperties);
        return new InboundXMLSec(securityProperties);
    }

    public static XMLSecurityProperties validateAndApplyDefaultsToOutboundSecurityProperties(XMLSecurityProperties securityProperties) throws XMLSecurityConfigurationException {
        if (securityProperties.getActions() == null || securityProperties.getActions().isEmpty()) {
            throw new XMLSecurityConfigurationException("stax.noOutputAction");
        }
        if (new HashSet<XMLSecurityConstants.Action>(securityProperties.getActions()).size() != securityProperties.getActions().size()) {
            throw new XMLSecurityConfigurationException("stax.duplicateActions");
        }
        if (!securityProperties.isSignatureGenerateIds() && !securityProperties.getIdAttributeNS().equals(XMLSecurityConstants.ATT_NULL_Id)) {
            throw new XMLSecurityConfigurationException("stax.idsetbutnotgenerated");
        }
        if (securityProperties.getSignatureSecureParts() != null && securityProperties.getSignatureSecureParts().size() > 1 && !securityProperties.isSignatureGenerateIds()) {
            throw new XMLSecurityConfigurationException("stax.idgenerationdisablewithmultipleparts");
        }
        for (XMLSecurityConstants.Action action : securityProperties.getActions()) {
            if (XMLSecurityConstants.SIGNATURE.equals(action)) {
                if (securityProperties.getSignatureAlgorithm() == null) {
                    if (securityProperties.getSignatureKey() instanceof RSAPrivateKey) {
                        securityProperties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
                    } else if (securityProperties.getSignatureKey() instanceof DSAPrivateKey) {
                        securityProperties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#dsa-sha1");
                    } else if (securityProperties.getSignatureKey() instanceof SecretKey) {
                        securityProperties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#hmac-sha1");
                    }
                }
                if (securityProperties.getSignatureDigestAlgorithm() == null) {
                    securityProperties.setSignatureDigestAlgorithm("http://www.w3.org/2000/09/xmldsig#sha1");
                }
                if (securityProperties.getSignatureCanonicalizationAlgorithm() == null) {
                    securityProperties.setSignatureCanonicalizationAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
                }
                if (!securityProperties.getSignatureKeyIdentifiers().isEmpty()) continue;
                securityProperties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_IssuerSerial);
                continue;
            }
            if (!XMLSecurityConstants.ENCRYPTION.equals(action)) continue;
            if (securityProperties.getEncryptionKeyTransportAlgorithm() == null) {
                securityProperties.setEncryptionKeyTransportAlgorithm("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p");
            }
            if (securityProperties.getEncryptionSymAlgorithm() == null) {
                securityProperties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#aes256-cbc");
            }
            if (securityProperties.getEncryptionKeyIdentifier() != null) continue;
            securityProperties.setEncryptionKeyIdentifier(SecurityTokenConstants.KeyIdentifier_IssuerSerial);
        }
        return new XMLSecurityProperties(securityProperties);
    }

    public static XMLSecurityProperties validateAndApplyDefaultsToInboundSecurityProperties(XMLSecurityProperties securityProperties) throws XMLSecurityConfigurationException {
        return new XMLSecurityProperties(securityProperties);
    }

    static {
        try {
            URL resource = ClassLoaderUtils.getResource("security-config.xml", XMLSec.class);
            if (resource == null) {
                throw new RuntimeException("security-config.xml not found in classpath");
            }
            Init.init(resource.toURI(), XMLSec.class);
            try {
                XMLSecurityConstants.setJaxbContext(JAXBContext.newInstance((Class[])new Class[]{org.apache.xml.security.binding.xmlenc.ObjectFactory.class, org.apache.xml.security.binding.xmlenc11.ObjectFactory.class, ObjectFactory.class, org.apache.xml.security.binding.xmldsig11.ObjectFactory.class, org.apache.xml.security.binding.excc14n.ObjectFactory.class, org.apache.xml.security.binding.xop.ObjectFactory.class}));
                Schema schema = XMLSecurityUtils.loadXMLSecuritySchemas();
                XMLSecurityConstants.setJaxbSchemas(schema);
            }
            catch (JAXBException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
        catch (URISyntaxException | XMLSecurityException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

