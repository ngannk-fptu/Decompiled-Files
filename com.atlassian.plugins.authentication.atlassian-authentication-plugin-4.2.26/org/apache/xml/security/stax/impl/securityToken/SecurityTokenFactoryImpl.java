/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.securityToken;

import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import org.apache.xml.security.binding.xmldsig.DSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.KeyValueType;
import org.apache.xml.security.binding.xmldsig.RSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.X509DataType;
import org.apache.xml.security.binding.xmldsig.X509IssuerSerialType;
import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.impl.securityToken.AbstractInboundSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.DsaKeyValueSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.ECKeyValueSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.KeyNameSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.RsaKeyValueSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509IssuerSerialSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SKISecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SubjectNameSecurityToken;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenFactory;
import org.apache.xml.security.utils.RFC2253Parser;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;

public class SecurityTokenFactoryImpl
extends SecurityTokenFactory {
    @Override
    public InboundSecurityToken getSecurityToken(KeyInfoType keyInfoType, SecurityTokenConstants.KeyUsage keyUsage, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
        if (keyInfoType != null) {
            X509DataType x509DataType = (X509DataType)XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_X509Data);
            if (x509DataType != null) {
                return SecurityTokenFactoryImpl.getSecurityToken(x509DataType, securityProperties, inboundSecurityContext, keyUsage);
            }
            KeyValueType keyValueType = (KeyValueType)XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_KeyValue);
            if (keyValueType != null) {
                return SecurityTokenFactoryImpl.getSecurityToken(keyValueType, securityProperties, inboundSecurityContext, keyUsage);
            }
            String keyName = (String)XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_KeyName);
            if (keyName != null) {
                KeyNameSecurityToken token = this.getSecurityToken(keyName, securityProperties, inboundSecurityContext, keyUsage);
                return token;
            }
        }
        if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() != null) {
            return this.getDefaultSecurityToken(securityProperties, inboundSecurityContext, keyUsage);
        }
        if (SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() != null) {
            return this.getDefaultSecurityToken(securityProperties, inboundSecurityContext, keyUsage);
        }
        throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
    }

    private InboundSecurityToken getDefaultSecurityToken(XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) {
        AbstractInboundSecurityToken token = new AbstractInboundSecurityToken(inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_NoKeyInfo, false){

            @Override
            public SecurityTokenConstants.TokenType getTokenType() {
                return SecurityTokenConstants.DefaultToken;
            }
        };
        SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
        return token;
    }

    private KeyNameSecurityToken getSecurityToken(String keyName, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) throws XMLSecurityException {
        KeyNameSecurityToken token = new KeyNameSecurityToken(keyName, inboundSecurityContext);
        if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null) {
            Map<String, Key> keyNameMap = securityProperties.getKeyNameMap();
            Key key = keyNameMap.get(keyName);
            if (key == null) {
                throw new XMLSecurityException("stax.keyNotFoundForName", new Object[]{keyName});
            }
            if (key instanceof PublicKey) {
                token.setPublicKey((PublicKey)key);
            } else {
                throw new XMLSecurityException("stax.keyTypeNotSupported", new Object[]{key.getClass().getSimpleName()});
            }
        }
        SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
        return token;
    }

    private static InboundSecurityToken getSecurityToken(KeyValueType keyValueType, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) throws XMLSecurityException {
        RSAKeyValueType rsaKeyValueType = (RSAKeyValueType)XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_RSAKeyValue);
        if (rsaKeyValueType != null) {
            RsaKeyValueSecurityToken token = new RsaKeyValueSecurityToken(rsaKeyValueType, inboundSecurityContext);
            SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        DSAKeyValueType dsaKeyValueType = (DSAKeyValueType)XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_DSAKeyValue);
        if (dsaKeyValueType != null) {
            DsaKeyValueSecurityToken token = new DsaKeyValueSecurityToken(dsaKeyValueType, inboundSecurityContext);
            SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        ECKeyValueType ecKeyValueType = (ECKeyValueType)XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig11_ECKeyValue);
        if (ecKeyValueType != null) {
            ECKeyValueSecurityToken token = new ECKeyValueSecurityToken(ecKeyValueType, inboundSecurityContext);
            SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        throw new XMLSecurityException("stax.unsupportedKeyValue");
    }

    private static InboundSecurityToken getSecurityToken(X509DataType x509DataType, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) throws XMLSecurityException {
        byte[] certBytes = (byte[])XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509Certificate);
        if (certBytes != null) {
            X509Certificate cert = SecurityTokenFactoryImpl.getCertificateFromBytes(certBytes);
            SecurityTokenConstants.TokenType tokenType = SecurityTokenConstants.X509V3Token;
            if (cert.getVersion() == 1) {
                tokenType = SecurityTokenConstants.X509V1Token;
            }
            X509SecurityToken token = new X509SecurityToken(tokenType, inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_X509KeyIdentifier, true);
            token.setX509Certificates(new X509Certificate[]{cert});
            SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        X509IssuerSerialType issuerSerialType = (X509IssuerSerialType)XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509IssuerSerial);
        if (issuerSerialType != null) {
            if (issuerSerialType.getX509IssuerName() == null || issuerSerialType.getX509SerialNumber() == null || SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null || SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() == null) {
                throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
            }
            X509IssuerSerialSecurityToken token = new X509IssuerSerialSecurityToken(SecurityTokenConstants.X509V3Token, inboundSecurityContext, IDGenerator.generateID(null));
            token.setIssuerName(issuerSerialType.getX509IssuerName());
            token.setSerialNumber(issuerSerialType.getX509SerialNumber());
            SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        byte[] skiBytes = (byte[])XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509SKI);
        if (skiBytes != null) {
            if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null || SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() == null) {
                throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
            }
            X509SKISecurityToken token = new X509SKISecurityToken(SecurityTokenConstants.X509V3Token, inboundSecurityContext, IDGenerator.generateID(null));
            token.setSkiBytes(skiBytes);
            SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        String subjectName = (String)XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509SubjectName);
        if (subjectName != null) {
            if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null || SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() == null) {
                throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
            }
            String normalizedSubjectName = RFC2253Parser.normalize(subjectName);
            X509SubjectNameSecurityToken token = new X509SubjectNameSecurityToken(SecurityTokenConstants.X509V3Token, inboundSecurityContext, IDGenerator.generateID(null));
            token.setSubjectName(normalizedSubjectName);
            SecurityTokenFactoryImpl.setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
    }

    private static void setTokenKey(XMLSecurityProperties securityProperties, SecurityTokenConstants.KeyUsage keyUsage, AbstractInboundSecurityToken token) {
        Key key = null;
        if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage)) {
            key = securityProperties.getSignatureVerificationKey();
        } else if (SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage)) {
            key = securityProperties.getDecryptionKey();
        }
        if (key instanceof PublicKey && !SecurityTokenConstants.KeyValueToken.equals(token.getTokenType())) {
            token.setPublicKey((PublicKey)key);
        } else {
            token.setSecretKey("", key);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static X509Certificate getCertificateFromBytes(byte[] data) throws XMLSecurityException {
        try (UnsyncByteArrayInputStream in = new UnsyncByteArrayInputStream(data);){
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate x509Certificate = (X509Certificate)factory.generateCertificate(in);
            return x509Certificate;
        }
        catch (IOException | CertificateException e) {
            throw new XMLSecurityException(e);
        }
    }
}

