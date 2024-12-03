/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.xml.security.algorithms.Algorithm;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.algorithms.implementations.IntegrityHmac;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.algorithms.implementations.SignatureDSA;
import org.apache.xml.security.algorithms.implementations.SignatureECDSA;
import org.apache.xml.security.algorithms.implementations.SignatureEDDSA;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureAlgorithm
extends Algorithm {
    private static final Logger LOG = LoggerFactory.getLogger(SignatureAlgorithm.class);
    private static Map<String, Class<? extends SignatureAlgorithmSpi>> algorithmHash = new ConcurrentHashMap<String, Class<? extends SignatureAlgorithmSpi>>();
    private final SignatureAlgorithmSpi signatureAlgorithmSpi;
    private final String algorithmURI;

    public SignatureAlgorithm(Document doc, String algorithmURI) throws XMLSecurityException {
        this(doc, algorithmURI, null);
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, Provider provider) throws XMLSecurityException {
        this(doc, algorithmURI, provider, null);
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, Provider provider, AlgorithmParameterSpec parameterSpec) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;
        this.signatureAlgorithmSpi = SignatureAlgorithm.getSignatureAlgorithmSpi(algorithmURI, provider);
        if (parameterSpec != null) {
            this.signatureAlgorithmSpi.engineSetParameter(parameterSpec);
            this.signatureAlgorithmSpi.engineAddContextToElement(this.getElement());
        }
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, int hmacOutputLength) throws XMLSecurityException {
        this(doc, algorithmURI, hmacOutputLength, null);
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, int hmacOutputLength, Provider provider) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;
        this.signatureAlgorithmSpi = SignatureAlgorithm.getSignatureAlgorithmSpi(algorithmURI, provider);
        this.signatureAlgorithmSpi.engineSetHMACOutputLength(hmacOutputLength);
        this.signatureAlgorithmSpi.engineAddContextToElement(this.getElement());
    }

    public SignatureAlgorithm(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, true, null);
    }

    public SignatureAlgorithm(Element element, String baseURI, Provider provider) throws XMLSecurityException {
        this(element, baseURI, true, provider);
    }

    public SignatureAlgorithm(Element element, String baseURI, boolean secureValidation) throws XMLSecurityException {
        this(element, baseURI, secureValidation, null);
    }

    public SignatureAlgorithm(Element element, String baseURI, boolean secureValidation, Provider provider) throws XMLSecurityException {
        super(element, baseURI);
        this.algorithmURI = this.getURI();
        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }
        if (secureValidation && ("http://www.w3.org/2001/04/xmldsig-more#hmac-md5".equals(this.algorithmURI) || "http://www.w3.org/2001/04/xmldsig-more#rsa-md5".equals(this.algorithmURI))) {
            Object[] exArgs = new Object[]{this.algorithmURI};
            throw new XMLSecurityException("signature.signatureAlgorithm", exArgs);
        }
        this.signatureAlgorithmSpi = SignatureAlgorithm.getSignatureAlgorithmSpi(this.algorithmURI, provider);
        this.signatureAlgorithmSpi.engineGetContextFromElement(this.getElement());
    }

    private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(String algorithmURI, Provider provider) throws XMLSignatureException {
        try {
            Class<? extends SignatureAlgorithmSpi> implementingClass = algorithmHash.get(algorithmURI);
            LOG.debug("Create URI \"{}\" class \"{}\"", (Object)algorithmURI, implementingClass);
            if (implementingClass == null) {
                Object[] exArgs = new Object[]{algorithmURI};
                throw new XMLSignatureException("algorithms.NoSuchAlgorithmNoEx", exArgs);
            }
            if (provider != null) {
                try {
                    Constructor<? extends SignatureAlgorithmSpi> constructor = implementingClass.getConstructor(Provider.class);
                    return constructor.newInstance(provider);
                }
                catch (NoSuchMethodException e) {
                    LOG.warn("Class \"{}\" does not have a constructor with Provider", implementingClass);
                }
            }
            return JavaUtils.newInstanceWithEmptyConstructor(implementingClass);
        }
        catch (IllegalAccessException | InstantiationException | NullPointerException | InvocationTargetException ex) {
            Object[] exArgs = new Object[]{algorithmURI, ex.getMessage()};
            throw new XMLSignatureException(ex, "algorithms.NoSuchAlgorithm", exArgs);
        }
    }

    public byte[] sign() throws XMLSignatureException {
        return this.signatureAlgorithmSpi.engineSign();
    }

    public String getJCEAlgorithmString() {
        return this.signatureAlgorithmSpi.engineGetJCEAlgorithmString();
    }

    public String getJCEProviderName() {
        return this.signatureAlgorithmSpi.engineGetJCEProviderName();
    }

    public void update(byte[] input) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineUpdate(input);
    }

    public void update(byte input) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineUpdate(input);
    }

    public void update(byte[] buf, int offset, int len) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineUpdate(buf, offset, len);
    }

    public void initSign(Key signingKey) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitSign(signingKey);
    }

    public void initSign(Key signingKey, SecureRandom secureRandom) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitSign(signingKey, secureRandom);
    }

    public void initSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitSign(signingKey, algorithmParameterSpec);
    }

    public void setParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineSetParameter(params);
    }

    public void initVerify(Key verificationKey) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitVerify(verificationKey);
    }

    public boolean verify(byte[] signature) throws XMLSignatureException {
        return this.signatureAlgorithmSpi.engineVerify(signature);
    }

    public final String getURI() {
        return this.getLocalAttribute("Algorithm");
    }

    public static void register(String algorithmURI, String implementingClass) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
        JavaUtils.checkRegisterPermission();
        LOG.debug("Try to register {} {}", (Object)algorithmURI, (Object)implementingClass);
        Class<? extends SignatureAlgorithmSpi> registeredClass = algorithmHash.get(algorithmURI);
        if (registeredClass != null) {
            Object[] exArgs = new Object[]{algorithmURI, registeredClass};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        try {
            Class<?> clazz = ClassLoaderUtils.loadClass(implementingClass, SignatureAlgorithm.class);
            algorithmHash.put(algorithmURI, clazz);
        }
        catch (NullPointerException ex) {
            Object[] exArgs = new Object[]{algorithmURI, ex.getMessage()};
            throw new XMLSignatureException(ex, "algorithms.NoSuchAlgorithm", exArgs);
        }
    }

    public static void register(String algorithmURI, Class<? extends SignatureAlgorithmSpi> implementingClass) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
        JavaUtils.checkRegisterPermission();
        LOG.debug("Try to register {} {}", (Object)algorithmURI, implementingClass);
        Class<? extends SignatureAlgorithmSpi> registeredClass = algorithmHash.get(algorithmURI);
        if (registeredClass != null) {
            Object[] exArgs = new Object[]{algorithmURI, registeredClass};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        algorithmHash.put(algorithmURI, implementingClass);
    }

    public static void registerDefaultAlgorithms() {
        algorithmHash.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", SignatureDSA.class);
        algorithmHash.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", SignatureDSA.SHA256.class);
        algorithmHash.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", SignatureBaseRSA.SignatureRSASHA1.class);
        algorithmHash.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", IntegrityHmac.IntegrityHmacSHA1.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", SignatureBaseRSA.SignatureRSAMD5.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", SignatureBaseRSA.SignatureRSARIPEMD160.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224", SignatureBaseRSA.SignatureRSASHA224.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", SignatureBaseRSA.SignatureRSASHA256.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", SignatureBaseRSA.SignatureRSASHA384.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", SignatureBaseRSA.SignatureRSASHA512.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA1MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA224MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA256MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA384MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA512MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#rsa-pss", SignatureBaseRSA.SignatureRSASSAPSS.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_224MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_256MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_384MGF1.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_512MGF1.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", SignatureECDSA.SignatureECDSASHA1.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224", SignatureECDSA.SignatureECDSASHA224.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", SignatureECDSA.SignatureECDSASHA256.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", SignatureECDSA.SignatureECDSASHA384.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", SignatureECDSA.SignatureECDSASHA512.class);
        algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160", SignatureECDSA.SignatureECDSARIPEMD160.class);
        algorithmHash.put("http://www.w3.org/2021/04/xmldsig-more#eddsa-ed25519", SignatureEDDSA.SignatureEd25519.class);
        algorithmHash.put("http://www.w3.org/2021/04/xmldsig-more#eddsa-ed448", SignatureEDDSA.SignatureEd448.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", IntegrityHmac.IntegrityHmacMD5.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", IntegrityHmac.IntegrityHmacRIPEMD160.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224", IntegrityHmac.IntegrityHmacSHA224.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", IntegrityHmac.IntegrityHmacSHA256.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", IntegrityHmac.IntegrityHmacSHA384.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", IntegrityHmac.IntegrityHmacSHA512.class);
    }

    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }

    @Override
    public String getBaseLocalName() {
        return "SignatureMethod";
    }
}

