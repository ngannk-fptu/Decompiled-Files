/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.encryption.AgreementMethod;
import org.apache.xml.security.encryption.CipherData;
import org.apache.xml.security.encryption.CipherReference;
import org.apache.xml.security.encryption.CipherValue;
import org.apache.xml.security.encryption.DocumentSerializer;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.EncryptionMethod;
import org.apache.xml.security.encryption.EncryptionProperties;
import org.apache.xml.security.encryption.EncryptionProperty;
import org.apache.xml.security.encryption.Reference;
import org.apache.xml.security.encryption.ReferenceList;
import org.apache.xml.security.encryption.Serializer;
import org.apache.xml.security.encryption.TransformSerializer;
import org.apache.xml.security.encryption.Transforms;
import org.apache.xml.security.encryption.XMLCipherInput;
import org.apache.xml.security.encryption.XMLCipherUtil;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.keyresolver.implementations.EncryptedKeyResolver;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.transforms.InvalidTransformException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLCipher {
    private static final Logger LOG = LoggerFactory.getLogger(XMLCipher.class);
    public static final String TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    public static final String AES_128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    public static final String AES_256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    public static final String AES_192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
    public static final String AES_128_GCM = "http://www.w3.org/2009/xmlenc11#aes128-gcm";
    public static final String AES_192_GCM = "http://www.w3.org/2009/xmlenc11#aes192-gcm";
    public static final String AES_256_GCM = "http://www.w3.org/2009/xmlenc11#aes256-gcm";
    public static final String SEED_128 = "http://www.w3.org/2007/05/xmldsig-more#seed128-cbc";
    public static final String CAMELLIA_128 = "http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc";
    public static final String CAMELLIA_192 = "http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc";
    public static final String CAMELLIA_256 = "http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc";
    public static final String RSA_v1dot5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    public static final String RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    public static final String RSA_OAEP_11 = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
    public static final String DIFFIE_HELLMAN = "http://www.w3.org/2001/04/xmlenc#dh";
    public static final String TRIPLEDES_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
    public static final String AES_128_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
    public static final String AES_256_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
    public static final String AES_192_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
    public static final String CAMELLIA_128_KeyWrap = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia128";
    public static final String CAMELLIA_192_KeyWrap = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia192";
    public static final String CAMELLIA_256_KeyWrap = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia256";
    public static final String SEED_128_KeyWrap = "http://www.w3.org/2007/05/xmldsig-more#kw-seed128";
    public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String RIPEMD_160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
    public static final String XML_DSIG = "http://www.w3.org/2000/09/xmldsig#";
    public static final String N14C_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String N14C_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String EXCL_XML_N14C = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String EXCL_XML_N14C_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String PHYSICAL_XML_N14C = "http://santuario.apache.org/c14n/physical";
    public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
    public static final int ENCRYPT_MODE = 1;
    public static final int DECRYPT_MODE = 2;
    public static final int UNWRAP_MODE = 4;
    public static final int WRAP_MODE = 3;
    private static final String ENC_ALGORITHMS = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\nhttp://www.w3.org/2007/05/xmldsig-more#seed128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia192-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia256-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia128\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia192\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia256\nhttp://www.w3.org/2007/05/xmldsig-more#kw-seed128\n";
    private static final Set<String> SUPPORTED_ALGORITHMS;
    private static final boolean HAVE_FUNCTIONAL_IDENTITY_TRANSFORMER;
    private Cipher contextCipher;
    private int cipherMode = Integer.MIN_VALUE;
    private final String algorithm;
    private final String requestedJCEProvider;
    private Document contextDocument;
    private final Factory factory;
    private final Serializer serializer;
    private Key key;
    private Key kek;
    private EncryptedKey ek;
    private EncryptedData ed;
    private boolean secureValidation = true;
    private String digestAlg;
    private List<KeyResolverSpi> internalKeyResolvers;

    private XMLCipher(String transformation, String provider, String digestMethod, Serializer serializer) throws XMLEncryptionException {
        LOG.debug("Constructing XMLCipher...");
        this.factory = new Factory();
        this.algorithm = transformation;
        this.requestedJCEProvider = provider;
        this.digestAlg = digestMethod;
        this.serializer = serializer;
        if (transformation != null) {
            this.contextCipher = this.constructCipher(transformation, digestMethod);
        }
    }

    private static Serializer createSerializer(boolean secureValidation) throws XMLEncryptionException {
        return XMLCipher.createSerializer(null, secureValidation);
    }

    private static Serializer createSerializer(String canonAlg, boolean secureValidation) throws XMLEncryptionException {
        String c14nAlg = canonAlg != null ? canonAlg : PHYSICAL_XML_N14C;
        try {
            if (HAVE_FUNCTIONAL_IDENTITY_TRANSFORMER) {
                return new TransformSerializer(c14nAlg, secureValidation);
            }
            return new DocumentSerializer(c14nAlg, secureValidation);
        }
        catch (TransformerConfigurationException | InvalidCanonicalizerException e) {
            throw new XMLEncryptionException(e);
        }
    }

    private static boolean isValidEncryptionAlgorithm(String algorithm) {
        return SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    private static void validateTransformation(String transformation) {
        if (null == transformation) {
            throw new NullPointerException("Transformation unexpectedly null...");
        }
        if (!XMLCipher.isValidEncryptionAlgorithm(transformation)) {
            LOG.warn("Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\nhttp://www.w3.org/2007/05/xmldsig-more#seed128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia192-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia256-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia128\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia192\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia256\nhttp://www.w3.org/2007/05/xmldsig-more#kw-seed128\n");
        }
    }

    public static XMLCipher getInstance(String transformation) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation");
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, null, null, XMLCipher.createSerializer(true));
    }

    public static XMLCipher getInstance(Serializer serializer, String transformation) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation");
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, null, null, serializer);
    }

    public static XMLCipher getInstance(String transformation, String canon) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation and c14n algorithm");
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, null, null, XMLCipher.createSerializer(canon, true));
    }

    public static XMLCipher getInstance(String transformation, String canon, String digestMethod) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation and c14n algorithm");
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, null, digestMethod, XMLCipher.createSerializer(canon, true));
    }

    public static XMLCipher getProviderInstance(String transformation, String provider) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation and provider");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, provider, null, XMLCipher.createSerializer(true));
    }

    public static XMLCipher getProviderInstance(String transformation, String provider, String canon) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation, provider and c14n algorithm");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, provider, null, XMLCipher.createSerializer(canon, true));
    }

    public static XMLCipher getProviderInstance(String transformation, String provider, String canon, String digestMethod) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation, provider and c14n algorithm");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, provider, digestMethod, XMLCipher.createSerializer(canon, true));
    }

    public static XMLCipher getProviderInstance(Serializer serializer, String transformation, String provider, String digestMethod) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation, provider and c14n algorithm");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        XMLCipher.validateTransformation(transformation);
        return new XMLCipher(transformation, provider, digestMethod, serializer);
    }

    public static XMLCipher getInstance() throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with no arguments");
        return new XMLCipher(null, null, null, XMLCipher.createSerializer(true));
    }

    public static XMLCipher getProviderInstance(String provider) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with provider");
        return new XMLCipher(null, provider, null, XMLCipher.createSerializer(true));
    }

    public void init(int opmode, Key key) throws XMLEncryptionException {
        LOG.debug("Initializing XMLCipher...");
        this.ek = null;
        this.ed = null;
        switch (opmode) {
            case 1: {
                LOG.debug("opmode = ENCRYPT_MODE");
                this.ed = this.createEncryptedData(1, "NO VALUE YET");
                break;
            }
            case 2: {
                LOG.debug("opmode = DECRYPT_MODE");
                break;
            }
            case 3: {
                LOG.debug("opmode = WRAP_MODE");
                this.ek = this.createEncryptedKey(1, "NO VALUE YET");
                break;
            }
            case 4: {
                LOG.debug("opmode = UNWRAP_MODE");
                break;
            }
            default: {
                LOG.error("Mode unexpectedly invalid");
                throw new XMLEncryptionException("Invalid mode in init");
            }
        }
        this.cipherMode = opmode;
        this.key = key;
    }

    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }

    public void registerInternalKeyResolver(KeyResolverSpi keyResolver) {
        if (this.internalKeyResolvers == null) {
            this.internalKeyResolvers = new ArrayList<KeyResolverSpi>();
        }
        this.internalKeyResolvers.add(keyResolver);
    }

    public EncryptedData getEncryptedData() {
        LOG.debug("Returning EncryptedData");
        return this.ed;
    }

    public EncryptedKey getEncryptedKey() {
        LOG.debug("Returning EncryptedKey");
        return this.ek;
    }

    public void setKEK(Key kek) {
        this.kek = kek;
    }

    public Element martial(EncryptedData encryptedData) {
        return this.factory.toElement(encryptedData);
    }

    public Element martial(Document context, EncryptedData encryptedData) {
        this.contextDocument = context;
        return this.factory.toElement(encryptedData);
    }

    public Element martial(EncryptedKey encryptedKey) {
        return this.factory.toElement(encryptedKey);
    }

    public Element martial(Document context, EncryptedKey encryptedKey) {
        this.contextDocument = context;
        return this.factory.toElement(encryptedKey);
    }

    public Element martial(ReferenceList referenceList) {
        return this.factory.toElement(referenceList);
    }

    public Element martial(Document context, ReferenceList referenceList) {
        this.contextDocument = context;
        return this.factory.toElement(referenceList);
    }

    private Document encryptElement(Element element) throws Exception {
        LOG.debug("Encrypting element...");
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        this.encryptData(this.contextDocument, element, false);
        Element encryptedElement = this.factory.toElement(this.ed);
        Node sourceParent = element.getParentNode();
        sourceParent.replaceChild(encryptedElement, element);
        return this.contextDocument;
    }

    private Document encryptElementContent(Element element) throws Exception {
        LOG.debug("Encrypting element content...");
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        this.encryptData(this.contextDocument, element, true);
        Element encryptedElement = this.factory.toElement(this.ed);
        XMLCipher.removeContent(element);
        element.appendChild(encryptedElement);
        return this.contextDocument;
    }

    public Document doFinal(Document context, Document source) throws Exception {
        LOG.debug("Processing source document...");
        if (null == source) {
            throw new XMLEncryptionException("empty", "Source document unexpectedly null...");
        }
        return this.doFinal(context, source.getDocumentElement());
    }

    public Document doFinal(Document context, Element element) throws Exception {
        LOG.debug("Processing source element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Source element unexpectedly null...");
        }
        this.contextDocument = context;
        Document result = null;
        switch (this.cipherMode) {
            case 2: {
                result = this.decryptElement(element);
                break;
            }
            case 1: {
                result = this.encryptElement(element);
                break;
            }
            case 3: 
            case 4: {
                break;
            }
            default: {
                throw new XMLEncryptionException(new IllegalStateException());
            }
        }
        return result;
    }

    public Document doFinal(Document context, Element element, boolean content) throws Exception {
        LOG.debug("Processing source element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Source element unexpectedly null...");
        }
        this.contextDocument = context;
        Document result = null;
        switch (this.cipherMode) {
            case 2: {
                if (content) {
                    result = this.decryptElementContent(element);
                    break;
                }
                result = this.decryptElement(element);
                break;
            }
            case 1: {
                if (content) {
                    result = this.encryptElementContent(element);
                    break;
                }
                result = this.encryptElement(element);
                break;
            }
            case 3: 
            case 4: {
                break;
            }
            default: {
                throw new XMLEncryptionException(new IllegalStateException());
            }
        }
        return result;
    }

    public EncryptedData encryptData(Document context, Element element) throws Exception {
        return this.encryptData(context, element, false);
    }

    public EncryptedData encryptData(Document context, String type, InputStream serializedData) throws Exception {
        LOG.debug("Encrypting element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == serializedData) {
            throw new XMLEncryptionException("empty", "Serialized data unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        return this.encryptData(context, null, type, serializedData);
    }

    public EncryptedData encryptData(Document context, Element element, boolean contentMode) throws Exception {
        LOG.debug("Encrypting element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        String type = contentMode ? "http://www.w3.org/2001/04/xmlenc#Content" : "http://www.w3.org/2001/04/xmlenc#Element";
        return this.encryptData(context, element, type, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private EncryptedData encryptData(Document context, Element element, String type, InputStream serializedData) throws Exception {
        this.contextDocument = context;
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        if (element != null && element.getParentNode() == null) {
            throw new XMLEncryptionException("empty", "The element can't be serialized as it has no parent");
        }
        byte[] serializedOctets = null;
        if (serializedData == null) {
            if ("http://www.w3.org/2001/04/xmlenc#Content".equals(type)) {
                if (element == null) {
                    throw new XMLEncryptionException("empty", "Cannot encrypt null element");
                }
                NodeList children = element.getChildNodes();
                if (null == children) throw new XMLEncryptionException("empty", "Element has no content.");
                serializedOctets = this.serializer.serializeToByteArray(children);
            } else {
                serializedOctets = this.serializer.serializeToByteArray(element);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Serialized octets:\n" + new String(serializedOctets, StandardCharsets.UTF_8));
            }
        }
        byte[] encryptedBytes = null;
        Cipher c = this.contextCipher == null ? this.constructCipher(this.algorithm, null) : this.contextCipher;
        int ivLen = JCEMapper.getIVLengthFromURI(this.algorithm) / 8;
        byte[] iv = XMLSecurityConstants.generateBytes(ivLen);
        try {
            AlgorithmParameterSpec paramSpec = this.constructBlockCipherParameters(this.algorithm, iv);
            c.init(this.cipherMode, this.key, paramSpec);
        }
        catch (InvalidKeyException ike) {
            throw new XMLEncryptionException(ike);
        }
        try {
            if (serializedData != null) {
                byte[] buf = new byte[8192];
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                    int numBytes;
                    while ((numBytes = serializedData.read(buf)) != -1) {
                        byte[] data = c.update(buf, 0, numBytes);
                        baos.write(data);
                    }
                    baos.write(c.doFinal());
                    encryptedBytes = baos.toByteArray();
                }
            } else {
                encryptedBytes = c.doFinal(serializedOctets);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Expected cipher.outputSize = " + Integer.toString(c.getOutputSize(serializedOctets.length)));
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Actual cipher.outputSize = " + Integer.toString(encryptedBytes.length));
            }
        }
        catch (UnsupportedEncodingException | IllegalStateException | BadPaddingException | IllegalBlockSizeException e) {
            throw new XMLEncryptionException(e);
        }
        if (c.getIV() != null) {
            iv = c.getIV();
        }
        byte[] finalEncryptedBytes = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, finalEncryptedBytes, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, finalEncryptedBytes, iv.length, encryptedBytes.length);
        String base64EncodedEncryptedOctets = XMLUtils.encodeToString(finalEncryptedBytes);
        LOG.debug("Encrypted octets:\n{}", (Object)base64EncodedEncryptedOctets);
        LOG.debug("Encrypted octets length = {}", (Object)base64EncodedEncryptedOctets.length());
        try {
            CipherData cd = this.ed.getCipherData();
            CipherValue cv = cd.getCipherValue();
            cv.setValue(base64EncodedEncryptedOctets);
            if (type != null) {
                this.ed.setType(new URI(type).toString());
            }
            EncryptionMethod method = this.factory.newEncryptionMethod(new URI(this.algorithm).toString());
            method.setDigestAlgorithm(this.digestAlg);
            this.ed.setEncryptionMethod(method);
            return this.ed;
        }
        catch (URISyntaxException ex) {
            throw new XMLEncryptionException(ex);
        }
    }

    private AlgorithmParameterSpec constructBlockCipherParameters(String algorithm, byte[] iv) {
        return XMLCipherUtil.constructBlockCipherParameters(algorithm, iv);
    }

    public EncryptedData loadEncryptedData(Document context, Element element) throws XMLEncryptionException {
        LOG.debug("Loading encrypted element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        this.contextDocument = context;
        this.ed = this.factory.newEncryptedData(element);
        return this.ed;
    }

    public EncryptedKey loadEncryptedKey(Document context, Element element) throws XMLEncryptionException {
        LOG.debug("Loading encrypted key...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (this.cipherMode != 4 && this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in UNWRAP_MODE or DECRYPT_MODE...");
        }
        this.contextDocument = context;
        this.ek = this.factory.newEncryptedKey(element);
        return this.ek;
    }

    public EncryptedKey loadEncryptedKey(Element element) throws XMLEncryptionException {
        return this.loadEncryptedKey(element.getOwnerDocument(), element);
    }

    public EncryptedKey encryptKey(Document doc, Key key) throws XMLEncryptionException {
        return this.encryptKey(doc, key, null, null);
    }

    public EncryptedKey encryptKey(Document doc, Key key, String mgfAlgorithm, byte[] oaepParams) throws XMLEncryptionException {
        return this.encryptKey(doc, key, mgfAlgorithm, oaepParams, null);
    }

    public EncryptedKey encryptKey(Document doc, Key key, String mgfAlgorithm, byte[] oaepParams, SecureRandom random) throws XMLEncryptionException {
        LOG.debug("Encrypting key ...");
        if (null == key) {
            throw new XMLEncryptionException("empty", "Key unexpectedly null...");
        }
        if (this.cipherMode != 3) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in WRAP_MODE...");
        }
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        this.contextDocument = doc;
        byte[] encryptedBytes = null;
        Cipher c = this.contextCipher == null ? this.constructCipher(this.algorithm, null) : this.contextCipher;
        try {
            OAEPParameterSpec oaepParameters = this.constructOAEPParameters(this.algorithm, this.digestAlg, mgfAlgorithm, oaepParams);
            if (random != null) {
                if (oaepParameters == null) {
                    c.init(3, this.key, random);
                } else {
                    c.init(3, this.key, oaepParameters, random);
                }
            } else if (oaepParameters == null) {
                c.init(3, this.key);
            } else {
                c.init(3, this.key, oaepParameters);
            }
            encryptedBytes = c.wrap(key);
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException e) {
            throw new XMLEncryptionException(e);
        }
        String base64EncodedEncryptedOctets = XMLUtils.encodeToString(encryptedBytes);
        LOG.debug("Encrypted key octets:\n{}", (Object)base64EncodedEncryptedOctets);
        LOG.debug("Encrypted key octets length = {}", (Object)base64EncodedEncryptedOctets.length());
        CipherValue cv = this.ek.getCipherData().getCipherValue();
        cv.setValue(base64EncodedEncryptedOctets);
        try {
            EncryptionMethod method = this.factory.newEncryptionMethod(new URI(this.algorithm).toString());
            method.setDigestAlgorithm(this.digestAlg);
            method.setMGFAlgorithm(mgfAlgorithm);
            method.setOAEPparams(oaepParams);
            this.ek.setEncryptionMethod(method);
        }
        catch (URISyntaxException ex) {
            throw new XMLEncryptionException(ex);
        }
        return this.ek;
    }

    public Key decryptKey(EncryptedKey encryptedKey, String algorithm) throws XMLEncryptionException {
        Key ret;
        LOG.debug("Decrypting key from previously loaded EncryptedKey...");
        if (this.cipherMode != 4) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in UNWRAP_MODE...");
        }
        if (algorithm == null) {
            throw new XMLEncryptionException("empty", "Cannot decrypt a key without knowing the algorithm");
        }
        if (this.key == null) {
            LOG.debug("Trying to find a KEK via key resolvers");
            KeyInfo ki = encryptedKey.getKeyInfo();
            if (ki != null) {
                ki.setSecureValidation(this.secureValidation);
                try {
                    String keyWrapAlg = encryptedKey.getEncryptionMethod().getAlgorithm();
                    String keyType = JCEMapper.getJCEKeyAlgorithmFromURI(keyWrapAlg);
                    this.key = "RSA".equals(keyType) || "EC".equals(keyType) ? ki.getPrivateKey() : ki.getSecretKey();
                }
                catch (Exception e) {
                    LOG.debug(e.getMessage(), (Throwable)e);
                }
            }
            if (this.key == null) {
                LOG.error("XMLCipher::decryptKey unable to resolve a KEK");
                throw new XMLEncryptionException("empty", "Unable to decrypt without a KEK");
            }
        }
        XMLCipherInput cipherInput = new XMLCipherInput(encryptedKey);
        cipherInput.setSecureValidation(this.secureValidation);
        byte[] encryptedBytes = cipherInput.getBytes();
        String jceKeyAlgorithm = JCEMapper.getJCEKeyAlgorithmFromURI(algorithm);
        LOG.debug("JCE Key Algorithm: {}", (Object)jceKeyAlgorithm);
        Cipher c = this.contextCipher == null ? this.constructCipher(encryptedKey.getEncryptionMethod().getAlgorithm(), encryptedKey.getEncryptionMethod().getDigestAlgorithm()) : this.contextCipher;
        try {
            EncryptionMethod encMethod = encryptedKey.getEncryptionMethod();
            OAEPParameterSpec oaepParameters = this.constructOAEPParameters(encMethod.getAlgorithm(), encMethod.getDigestAlgorithm(), encMethod.getMGFAlgorithm(), encMethod.getOAEPparams());
            if (oaepParameters == null) {
                c.init(4, this.key);
            } else {
                c.init(4, this.key, oaepParameters);
            }
            ret = c.unwrap(encryptedBytes, jceKeyAlgorithm, 3);
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new XMLEncryptionException(e);
        }
        LOG.debug("Decryption of key type {} OK", (Object)algorithm);
        return ret;
    }

    private OAEPParameterSpec constructOAEPParameters(String encryptionAlgorithm, String digestAlgorithm, String mgfAlgorithm, byte[] oaepParams) {
        if (RSA_OAEP.equals(encryptionAlgorithm) || RSA_OAEP_11.equals(encryptionAlgorithm)) {
            String jceDigestAlgorithm = "SHA-1";
            if (digestAlgorithm != null) {
                jceDigestAlgorithm = JCEMapper.translateURItoJCEID(digestAlgorithm);
            }
            PSource.PSpecified pSource = PSource.PSpecified.DEFAULT;
            if (oaepParams != null) {
                pSource = new PSource.PSpecified(oaepParams);
            }
            MGF1ParameterSpec mgfParameterSpec = new MGF1ParameterSpec("SHA-1");
            if (RSA_OAEP_11.equals(encryptionAlgorithm)) {
                if ("http://www.w3.org/2009/xmlenc11#mgf1sha224".equals(mgfAlgorithm)) {
                    mgfParameterSpec = new MGF1ParameterSpec("SHA-224");
                } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha256".equals(mgfAlgorithm)) {
                    mgfParameterSpec = new MGF1ParameterSpec("SHA-256");
                } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha384".equals(mgfAlgorithm)) {
                    mgfParameterSpec = new MGF1ParameterSpec("SHA-384");
                } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha512".equals(mgfAlgorithm)) {
                    mgfParameterSpec = new MGF1ParameterSpec("SHA-512");
                }
            }
            return new OAEPParameterSpec(jceDigestAlgorithm, "MGF1", mgfParameterSpec, pSource);
        }
        return null;
    }

    private Cipher constructCipher(String algorithm, String digestAlgorithm) throws XMLEncryptionException {
        Cipher c;
        String jceAlgorithm = JCEMapper.translateURItoJCEID(algorithm);
        LOG.debug("JCE Algorithm = {}", (Object)jceAlgorithm);
        try {
            c = this.requestedJCEProvider == null ? Cipher.getInstance(jceAlgorithm) : Cipher.getInstance(jceAlgorithm, this.requestedJCEProvider);
        }
        catch (NoSuchAlgorithmException nsae) {
            c = this.constructCipher(algorithm, digestAlgorithm, nsae);
        }
        catch (NoSuchProviderException | NoSuchPaddingException e) {
            throw new XMLEncryptionException(e);
        }
        return c;
    }

    private Cipher constructCipher(String algorithm, String digestAlgorithm, Exception nsae) throws XMLEncryptionException {
        if (!RSA_OAEP.equals(algorithm)) {
            throw new XMLEncryptionException(nsae);
        }
        if (digestAlgorithm == null || SHA1.equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", this.requestedJCEProvider);
            }
            catch (Exception ex) {
                throw new XMLEncryptionException(ex);
            }
        }
        if ("http://www.w3.org/2001/04/xmldsig-more#sha224".equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-224andMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-224andMGF1Padding", this.requestedJCEProvider);
            }
            catch (Exception ex) {
                throw new XMLEncryptionException(ex);
            }
        }
        if (SHA256.equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", this.requestedJCEProvider);
            }
            catch (Exception ex) {
                throw new XMLEncryptionException(ex);
            }
        }
        if ("http://www.w3.org/2001/04/xmldsig-more#sha384".equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-384AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-384AndMGF1Padding", this.requestedJCEProvider);
            }
            catch (Exception ex) {
                throw new XMLEncryptionException(ex);
            }
        }
        if (SHA512.equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-512AndMGF1Padding", this.requestedJCEProvider);
            }
            catch (Exception ex) {
                throw new XMLEncryptionException(ex);
            }
        }
        throw new XMLEncryptionException(nsae);
    }

    public Key decryptKey(EncryptedKey encryptedKey) throws XMLEncryptionException {
        return this.decryptKey(encryptedKey, this.ed.getEncryptionMethod().getAlgorithm());
    }

    private static void removeContent(Node node) {
        while (node.hasChildNodes()) {
            node.removeChild(node.getFirstChild());
        }
    }

    private Document decryptElement(Element element) throws XMLEncryptionException {
        LOG.debug("Decrypting element...");
        if (element == null) {
            throw new XMLEncryptionException("empty", "Cannot decrypt null element");
        }
        if (element.getParentNode() == null) {
            throw new XMLEncryptionException("empty", "The element can't be serialized as it has no parent");
        }
        if (this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        byte[] octets = this.decryptToByteArray(element);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Decrypted octets:\n" + new String(octets));
        }
        Node sourceParent = element.getParentNode();
        try {
            Node decryptedNode = this.serializer.deserialize(octets, sourceParent);
            if (sourceParent != null && 9 == sourceParent.getNodeType()) {
                this.contextDocument.removeChild(this.contextDocument.getDocumentElement());
                this.contextDocument.appendChild(decryptedNode);
            } else if (sourceParent != null) {
                sourceParent.replaceChild(decryptedNode, element);
            }
        }
        catch (IOException ex) {
            throw new XMLEncryptionException(ex);
        }
        return this.contextDocument;
    }

    private Document decryptElementContent(Element element) throws XMLEncryptionException {
        Element e = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
        if (null == e) {
            throw new XMLEncryptionException("empty", "No EncryptedData child element.");
        }
        return this.decryptElement(e);
    }

    public byte[] decryptToByteArray(Element element) throws XMLEncryptionException {
        Cipher c;
        LOG.debug("Decrypting to ByteArray...");
        if (this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        EncryptedData encryptedData = this.factory.newEncryptedData(element);
        String encMethodAlgorithm = encryptedData.getEncryptionMethod().getAlgorithm();
        if (this.key == null) {
            KeyInfo ki = encryptedData.getKeyInfo();
            if (ki != null) {
                try {
                    EncryptedKeyResolver resolver = new EncryptedKeyResolver(encMethodAlgorithm, this.kek, this.internalKeyResolvers);
                    ki.registerInternalKeyResolver(resolver);
                    ki.setSecureValidation(this.secureValidation);
                    this.key = ki.getSecretKey();
                }
                catch (KeyResolverException kre) {
                    LOG.debug(kre.getMessage(), (Throwable)kre);
                }
            }
            if (this.key == null) {
                LOG.error("XMLCipher::decryptElement unable to resolve a decryption key");
                throw new XMLEncryptionException("empty", "encryption.nokey");
            }
        }
        XMLCipherInput cipherInput = new XMLCipherInput(encryptedData);
        cipherInput.setSecureValidation(this.secureValidation);
        byte[] encryptedBytes = cipherInput.getBytes();
        String jceAlgorithm = JCEMapper.translateURItoJCEID(encMethodAlgorithm);
        LOG.debug("JCE Algorithm = {}", (Object)jceAlgorithm);
        try {
            c = this.requestedJCEProvider == null ? Cipher.getInstance(jceAlgorithm) : Cipher.getInstance(jceAlgorithm, this.requestedJCEProvider);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new XMLEncryptionException(e);
        }
        int ivLen = JCEMapper.getIVLengthFromURI(encMethodAlgorithm) / 8;
        byte[] ivBytes = new byte[ivLen];
        System.arraycopy(encryptedBytes, 0, ivBytes, 0, ivLen);
        String blockCipherAlg = this.algorithm;
        if (blockCipherAlg == null) {
            blockCipherAlg = encMethodAlgorithm;
        }
        AlgorithmParameterSpec paramSpec = this.constructBlockCipherParameters(blockCipherAlg, ivBytes);
        try {
            c.init(this.cipherMode, this.key, paramSpec);
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new XMLEncryptionException(e);
        }
        try {
            return c.doFinal(encryptedBytes, ivLen, encryptedBytes.length - ivLen);
        }
        catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new XMLEncryptionException(e);
        }
    }

    public EncryptedData createEncryptedData(int type, String value) throws XMLEncryptionException {
        EncryptedData result = null;
        CipherData data = null;
        if (2 == type) {
            CipherReference cipherReference = this.factory.newCipherReference(value);
            data = this.factory.newCipherData(type);
            data.setCipherReference(cipherReference);
            result = this.factory.newEncryptedData(data);
        } else if (1 == type) {
            CipherValue cipherValue = this.factory.newCipherValue(value);
            data = this.factory.newCipherData(type);
            data.setCipherValue(cipherValue);
            result = this.factory.newEncryptedData(data);
        }
        return result;
    }

    public EncryptedKey createEncryptedKey(int type, String value) throws XMLEncryptionException {
        EncryptedKey result = null;
        CipherData data = null;
        if (2 == type) {
            CipherReference cipherReference = this.factory.newCipherReference(value);
            data = this.factory.newCipherData(type);
            data.setCipherReference(cipherReference);
            result = this.factory.newEncryptedKey(data);
        } else if (1 == type) {
            CipherValue cipherValue = this.factory.newCipherValue(value);
            data = this.factory.newCipherData(type);
            data.setCipherValue(cipherValue);
            result = this.factory.newEncryptedKey(data);
        }
        return result;
    }

    public AgreementMethod createAgreementMethod(String algorithm) {
        return this.factory.newAgreementMethod(algorithm);
    }

    public CipherData createCipherData(int type) {
        return this.factory.newCipherData(type);
    }

    public CipherReference createCipherReference(String uri) {
        return this.factory.newCipherReference(uri);
    }

    public CipherValue createCipherValue(String value) {
        return this.factory.newCipherValue(value);
    }

    public EncryptionMethod createEncryptionMethod(String algorithm) {
        return this.factory.newEncryptionMethod(algorithm);
    }

    public EncryptionProperties createEncryptionProperties() {
        return this.factory.newEncryptionProperties();
    }

    public EncryptionProperty createEncryptionProperty() {
        return this.factory.newEncryptionProperty();
    }

    public ReferenceList createReferenceList(int type) {
        return this.factory.newReferenceList(type);
    }

    public Transforms createTransforms() {
        return this.factory.newTransforms();
    }

    public Transforms createTransforms(Document doc) {
        return this.factory.newTransforms(doc);
    }

    private static boolean haveFunctionalIdentityTransformer() {
        String xml = "<a:e1 xmlns:a=\"a\" xmlns:b=\"b\"><a xmlns=\"a\" xmlns:b=\"b\"/></a:e1>";
        try {
            DOMResult domResult = new DOMResult();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.newTransformer().transform(new StreamSource(new ByteArrayInputStream("<a:e1 xmlns:a=\"a\" xmlns:b=\"b\"><a xmlns=\"a\" xmlns:b=\"b\"/></a:e1>".getBytes(StandardCharsets.UTF_8))), domResult);
            boolean result = false;
            if (domResult.getNode().getFirstChild().getFirstChild().hasAttributes() && domResult.getNode().getFirstChild().getFirstChild().getAttributes().getLength() >= 1) {
                result = "http://www.w3.org/2000/xmlns/".equals(domResult.getNode().getFirstChild().getFirstChild().getAttributes().item(1).getNamespaceURI());
            }
            LOG.debug("Have functional IdentityTransformer: {}", (Object)result);
            return result;
        }
        catch (Exception e) {
            LOG.debug(e.getMessage(), (Throwable)e);
            return false;
        }
    }

    static {
        HAVE_FUNCTIONAL_IDENTITY_TRANSFORMER = XMLCipher.haveFunctionalIdentityTransformer();
        HashSet<String> encryptionAlgorithms = new HashSet<String>();
        encryptionAlgorithms.add(TRIPLEDES);
        encryptionAlgorithms.add(AES_128);
        encryptionAlgorithms.add(AES_256);
        encryptionAlgorithms.add(AES_192);
        encryptionAlgorithms.add(AES_128_GCM);
        encryptionAlgorithms.add(AES_192_GCM);
        encryptionAlgorithms.add(AES_256_GCM);
        encryptionAlgorithms.add(SEED_128);
        encryptionAlgorithms.add(CAMELLIA_128);
        encryptionAlgorithms.add(CAMELLIA_192);
        encryptionAlgorithms.add(CAMELLIA_256);
        encryptionAlgorithms.add(RSA_v1dot5);
        encryptionAlgorithms.add(RSA_OAEP);
        encryptionAlgorithms.add(RSA_OAEP_11);
        encryptionAlgorithms.add(TRIPLEDES_KeyWrap);
        encryptionAlgorithms.add(AES_128_KeyWrap);
        encryptionAlgorithms.add(AES_256_KeyWrap);
        encryptionAlgorithms.add(AES_192_KeyWrap);
        encryptionAlgorithms.add(CAMELLIA_128_KeyWrap);
        encryptionAlgorithms.add(CAMELLIA_192_KeyWrap);
        encryptionAlgorithms.add(CAMELLIA_256_KeyWrap);
        encryptionAlgorithms.add(SEED_128_KeyWrap);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(encryptionAlgorithms);
    }

    private class Factory {
        private Factory() {
        }

        AgreementMethod newAgreementMethod(String algorithm) {
            return new AgreementMethodImpl(algorithm);
        }

        CipherData newCipherData(int type) {
            return new CipherDataImpl(type);
        }

        CipherReference newCipherReference(String uri) {
            return new CipherReferenceImpl(uri);
        }

        CipherValue newCipherValue(String value) {
            return new CipherValueImpl(value);
        }

        EncryptedData newEncryptedData(CipherData data) {
            return new EncryptedDataImpl(data);
        }

        EncryptedKey newEncryptedKey(CipherData data) {
            return new EncryptedKeyImpl(data);
        }

        EncryptionMethod newEncryptionMethod(String algorithm) {
            return new EncryptionMethodImpl(algorithm);
        }

        EncryptionProperties newEncryptionProperties() {
            return new EncryptionPropertiesImpl();
        }

        EncryptionProperty newEncryptionProperty() {
            return new EncryptionPropertyImpl();
        }

        ReferenceList newReferenceList(int type) {
            return new ReferenceListImpl(type);
        }

        Transforms newTransforms() {
            return new TransformsImpl();
        }

        Transforms newTransforms(Document doc) {
            return new TransformsImpl(doc);
        }

        CipherData newCipherData(Element element) throws XMLEncryptionException {
            if (null == element) {
                throw new NullPointerException("element is null");
            }
            int type = 0;
            Element e = null;
            if (element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").getLength() > 0) {
                type = 1;
                e = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").item(0);
            } else if (element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").getLength() > 0) {
                type = 2;
                e = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").item(0);
            }
            CipherData result = this.newCipherData(type);
            if (type == 1) {
                result.setCipherValue(this.newCipherValue(e));
            } else if (type == 2) {
                result.setCipherReference(this.newCipherReference(e));
            }
            return result;
        }

        CipherReference newCipherReference(Element element) throws XMLEncryptionException {
            Attr uriAttr = element.getAttributeNodeNS(null, "URI");
            CipherReferenceImpl result = new CipherReferenceImpl(uriAttr);
            NodeList transformsElements = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "Transforms");
            Element transformsElement = (Element)transformsElements.item(0);
            if (transformsElement != null) {
                LOG.debug("Creating a DSIG based Transforms element");
                try {
                    result.setTransforms(new TransformsImpl(transformsElement));
                }
                catch (XMLSecurityException e) {
                    throw new XMLEncryptionException(e);
                }
            }
            return result;
        }

        CipherValue newCipherValue(Element element) {
            String value = XMLUtils.getFullTextChildrenFromNode(element);
            return this.newCipherValue(value);
        }

        EncryptedData newEncryptedData(Element element) throws XMLEncryptionException {
            Element encryptionPropertiesElement;
            Element keyInfoElement;
            EncryptedData result = null;
            NodeList dataElements = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
            Element dataElement = (Element)dataElements.item(dataElements.getLength() - 1);
            CipherData data = this.newCipherData(dataElement);
            result = this.newEncryptedData(data);
            result.setId(element.getAttributeNS(null, "Id"));
            result.setType(element.getAttributeNS(null, "Type"));
            result.setMimeType(element.getAttributeNS(null, "MimeType"));
            result.setEncoding(element.getAttributeNS(null, "Encoding"));
            Element encryptionMethodElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
            if (null != encryptionMethodElement) {
                result.setEncryptionMethod(this.newEncryptionMethod(encryptionMethodElement));
            }
            if (null != (keyInfoElement = (Element)element.getElementsByTagNameNS(XMLCipher.XML_DSIG, "KeyInfo").item(0))) {
                KeyInfo ki = this.newKeyInfo(keyInfoElement);
                result.setKeyInfo(ki);
            }
            if (null != (encryptionPropertiesElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0))) {
                result.setEncryptionProperties(this.newEncryptionProperties(encryptionPropertiesElement));
            }
            return result;
        }

        EncryptedKey newEncryptedKey(Element element) throws XMLEncryptionException {
            Element carriedNameElement;
            Element referenceListElement;
            Element encryptionPropertiesElement;
            Element keyInfoElement;
            EncryptedKey result = null;
            NodeList dataElements = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
            Element dataElement = (Element)dataElements.item(dataElements.getLength() - 1);
            CipherData data = this.newCipherData(dataElement);
            result = this.newEncryptedKey(data);
            result.setId(element.getAttributeNS(null, "Id"));
            result.setType(element.getAttributeNS(null, "Type"));
            result.setMimeType(element.getAttributeNS(null, "MimeType"));
            result.setEncoding(element.getAttributeNS(null, "Encoding"));
            result.setRecipient(element.getAttributeNS(null, "Recipient"));
            Element encryptionMethodElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
            if (null != encryptionMethodElement) {
                result.setEncryptionMethod(this.newEncryptionMethod(encryptionMethodElement));
            }
            if (null != (keyInfoElement = (Element)element.getElementsByTagNameNS(XMLCipher.XML_DSIG, "KeyInfo").item(0))) {
                KeyInfo ki = this.newKeyInfo(keyInfoElement);
                result.setKeyInfo(ki);
            }
            if (null != (encryptionPropertiesElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0))) {
                result.setEncryptionProperties(this.newEncryptionProperties(encryptionPropertiesElement));
            }
            if (null != (referenceListElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "ReferenceList").item(0))) {
                result.setReferenceList(this.newReferenceList(referenceListElement));
            }
            if (null != (carriedNameElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName").item(0))) {
                result.setCarriedName(carriedNameElement.getFirstChild().getNodeValue());
            }
            return result;
        }

        KeyInfo newKeyInfo(Element element) throws XMLEncryptionException {
            try {
                KeyInfo ki = new KeyInfo(element, null);
                ki.setSecureValidation(XMLCipher.this.secureValidation);
                if (XMLCipher.this.internalKeyResolvers != null) {
                    int size = XMLCipher.this.internalKeyResolvers.size();
                    for (int i = 0; i < size; ++i) {
                        ki.registerInternalKeyResolver((KeyResolverSpi)XMLCipher.this.internalKeyResolvers.get(i));
                    }
                }
                return ki;
            }
            catch (XMLSecurityException xse) {
                throw new XMLEncryptionException(xse, "KeyInfo.error");
            }
        }

        EncryptionMethod newEncryptionMethod(Element element) {
            Element mgfElement;
            Element digestElement;
            Element oaepParamsElement;
            String encAlgorithm = element.getAttributeNS(null, "Algorithm");
            EncryptionMethod result = this.newEncryptionMethod(encAlgorithm);
            Element keySizeElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeySize").item(0);
            if (null != keySizeElement) {
                result.setKeySize(Integer.parseInt(keySizeElement.getFirstChild().getNodeValue()));
            }
            if (null != (oaepParamsElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OAEPparams").item(0))) {
                String oaepParams = oaepParamsElement.getFirstChild().getNodeValue();
                result.setOAEPparams(XMLUtils.decode(oaepParams.getBytes(StandardCharsets.UTF_8)));
            }
            if ((digestElement = (Element)element.getElementsByTagNameNS(XMLCipher.XML_DSIG, "DigestMethod").item(0)) != null) {
                String digestAlgorithm = digestElement.getAttributeNS(null, "Algorithm");
                result.setDigestAlgorithm(digestAlgorithm);
            }
            if ((mgfElement = (Element)element.getElementsByTagNameNS("http://www.w3.org/2009/xmlenc11#", "MGF").item(0)) != null && !XMLCipher.RSA_OAEP.equals(XMLCipher.this.algorithm)) {
                String mgfAlgorithm = mgfElement.getAttributeNS(null, "Algorithm");
                result.setMGFAlgorithm(mgfAlgorithm);
            }
            return result;
        }

        EncryptionProperties newEncryptionProperties(Element element) {
            EncryptionProperties result = this.newEncryptionProperties();
            result.setId(element.getAttributeNS(null, "Id"));
            NodeList encryptionPropertyList = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");
            int length = encryptionPropertyList.getLength();
            for (int i = 0; i < length; ++i) {
                Node n = encryptionPropertyList.item(i);
                if (null == n) continue;
                result.addEncryptionProperty(this.newEncryptionProperty((Element)n));
            }
            return result;
        }

        EncryptionProperty newEncryptionProperty(Element element) {
            EncryptionProperty result = this.newEncryptionProperty();
            result.setTarget(element.getAttributeNS(null, "Target"));
            result.setId(element.getAttributeNS(null, "Id"));
            return result;
        }

        ReferenceList newReferenceList(Element element) {
            ReferenceListImpl result;
            block6: {
                NodeList list;
                int type;
                block5: {
                    type = 0;
                    if (null != element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference").item(0)) {
                        type = 1;
                    } else if (null != element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference").item(0)) {
                        type = 2;
                    }
                    result = new ReferenceListImpl(type);
                    list = null;
                    if (1 != type) break block5;
                    list = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference");
                    int drLength = list.getLength();
                    for (int i = 0; i < drLength; ++i) {
                        String uri = ((Element)list.item(i)).getAttributeNS(null, "URI");
                        result.add(result.newDataReference(uri));
                    }
                    break block6;
                }
                if (2 != type) break block6;
                list = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference");
                int krLength = list.getLength();
                for (int i = 0; i < krLength; ++i) {
                    String uri = ((Element)list.item(i)).getAttributeNS(null, "URI");
                    result.add(result.newKeyReference(uri));
                }
            }
            return result;
        }

        Element toElement(EncryptedData encryptedData) {
            return ((EncryptedDataImpl)encryptedData).toElement();
        }

        Element toElement(EncryptedKey encryptedKey) {
            return ((EncryptedKeyImpl)encryptedKey).toElement();
        }

        Element toElement(ReferenceList referenceList) {
            return ((ReferenceListImpl)referenceList).toElement();
        }

        private class ReferenceListImpl
        implements ReferenceList {
            private Class<?> sentry;
            private List<Reference> references;

            public ReferenceListImpl(int type) {
                if (type == 1) {
                    this.sentry = DataReference.class;
                } else if (type == 2) {
                    this.sentry = KeyReference.class;
                } else {
                    throw new IllegalArgumentException();
                }
                this.references = new LinkedList<Reference>();
            }

            @Override
            public void add(Reference reference) {
                if (!reference.getClass().equals(this.sentry)) {
                    throw new IllegalArgumentException();
                }
                this.references.add(reference);
            }

            @Override
            public void remove(Reference reference) {
                if (!reference.getClass().equals(this.sentry)) {
                    throw new IllegalArgumentException();
                }
                this.references.remove(reference);
            }

            @Override
            public int size() {
                return this.references.size();
            }

            @Override
            public boolean isEmpty() {
                return this.references.isEmpty();
            }

            @Override
            public Iterator<Reference> getReferences() {
                return this.references.iterator();
            }

            Element toElement() {
                Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "ReferenceList");
                for (Reference reference : this.references) {
                    result.appendChild(((ReferenceImpl)reference).toElement());
                }
                return result;
            }

            @Override
            public Reference newDataReference(String uri) {
                return new DataReference(uri);
            }

            @Override
            public Reference newKeyReference(String uri) {
                return new KeyReference(uri);
            }

            private class KeyReference
            extends ReferenceImpl {
                KeyReference(String uri) {
                    super(uri);
                }

                @Override
                public String getType() {
                    return "KeyReference";
                }
            }

            private class DataReference
            extends ReferenceImpl {
                DataReference(String uri) {
                    super(uri);
                }

                @Override
                public String getType() {
                    return "DataReference";
                }
            }

            private abstract class ReferenceImpl
            implements Reference {
                private String uri;
                private List<Element> referenceInformation;

                ReferenceImpl(String uri) {
                    this.uri = uri;
                    this.referenceInformation = new LinkedList<Element>();
                }

                @Override
                public abstract String getType();

                @Override
                public String getURI() {
                    return this.uri;
                }

                @Override
                public Iterator<Element> getElementRetrievalInformation() {
                    return this.referenceInformation.iterator();
                }

                @Override
                public void setURI(String uri) {
                    this.uri = uri;
                }

                @Override
                public void removeElementRetrievalInformation(Element node) {
                    this.referenceInformation.remove(node);
                }

                @Override
                public void addElementRetrievalInformation(Element node) {
                    this.referenceInformation.add(node);
                }

                public Element toElement() {
                    String tagName = this.getType();
                    Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", tagName);
                    result.setAttributeNS(null, "URI", this.uri);
                    return result;
                }
            }
        }

        private class TransformsImpl
        extends org.apache.xml.security.transforms.Transforms
        implements Transforms {
            public TransformsImpl() {
                super(XMLCipher.this.contextDocument);
            }

            public TransformsImpl(Document doc) {
                if (doc == null) {
                    throw new RuntimeException("Document is null");
                }
                this.setDocument(doc);
                this.setElement(this.createElementForFamilyLocal(this.getBaseNamespace(), this.getBaseLocalName()));
            }

            public TransformsImpl(Element element) throws XMLSignatureException, InvalidTransformException, XMLSecurityException, TransformationException {
                super(element, "");
            }

            public Element toElement() {
                if (this.getDocument() == null) {
                    this.setDocument(XMLCipher.this.contextDocument);
                }
                return this.getElement();
            }

            @Override
            public org.apache.xml.security.transforms.Transforms getDSTransforms() {
                return this;
            }

            @Override
            public String getBaseNamespace() {
                return "http://www.w3.org/2001/04/xmlenc#";
            }
        }

        private class EncryptionPropertyImpl
        implements EncryptionProperty {
            private String target;
            private String id;
            private Map<String, String> attributeMap = new HashMap<String, String>();
            private List<Element> encryptionInformation = new LinkedList<Element>();

            @Override
            public String getTarget() {
                return this.target;
            }

            @Override
            public void setTarget(String target) {
                if (target == null || target.length() == 0) {
                    this.target = null;
                } else if (target.charAt(0) == '#') {
                    this.target = target;
                } else {
                    URI tmpTarget = null;
                    try {
                        tmpTarget = new URI(target);
                    }
                    catch (URISyntaxException ex) {
                        throw (IllegalArgumentException)new IllegalArgumentException().initCause(ex);
                    }
                    this.target = tmpTarget.toString();
                }
            }

            @Override
            public String getId() {
                return this.id;
            }

            @Override
            public void setId(String id) {
                this.id = id;
            }

            @Override
            public String getAttribute(String attribute) {
                return this.attributeMap.get(attribute);
            }

            @Override
            public void setAttribute(String attribute, String value) {
                this.attributeMap.put(attribute, value);
            }

            @Override
            public Iterator<Element> getEncryptionInformation() {
                return this.encryptionInformation.iterator();
            }

            @Override
            public void addEncryptionInformation(Element info) {
                this.encryptionInformation.add(info);
            }

            @Override
            public void removeEncryptionInformation(Element info) {
                this.encryptionInformation.remove(info);
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "EncryptionProperty");
                if (null != this.target) {
                    result.setAttributeNS(null, "Target", this.target);
                }
                if (null != this.id) {
                    result.setAttributeNS(null, "Id", this.id);
                }
                if (!this.attributeMap.isEmpty()) {
                    for (Map.Entry entry : this.attributeMap.entrySet()) {
                        result.setAttributeNS("http://www.w3.org/XML/1998/namespace", (String)entry.getKey(), (String)entry.getValue());
                    }
                }
                if (!this.encryptionInformation.isEmpty()) {
                    for (Element element : this.encryptionInformation) {
                        result.appendChild(element);
                    }
                }
                return result;
            }
        }

        private class EncryptionPropertiesImpl
        implements EncryptionProperties {
            private String id;
            private List<EncryptionProperty> encryptionProperties = new LinkedList<EncryptionProperty>();

            @Override
            public String getId() {
                return this.id;
            }

            @Override
            public void setId(String id) {
                this.id = id;
            }

            @Override
            public Iterator<EncryptionProperty> getEncryptionProperties() {
                return this.encryptionProperties.iterator();
            }

            @Override
            public void addEncryptionProperty(EncryptionProperty property) {
                this.encryptionProperties.add(property);
            }

            @Override
            public void removeEncryptionProperty(EncryptionProperty property) {
                this.encryptionProperties.remove(property);
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "EncryptionProperties");
                if (null != this.id) {
                    result.setAttributeNS(null, "Id", this.id);
                }
                Iterator<EncryptionProperty> itr = this.getEncryptionProperties();
                while (itr.hasNext()) {
                    result.appendChild(((EncryptionPropertyImpl)itr.next()).toElement());
                }
                return result;
            }
        }

        private class EncryptionMethodImpl
        implements EncryptionMethod {
            private String algorithm;
            private int keySize = Integer.MIN_VALUE;
            private byte[] oaepParams;
            private List<Element> encryptionMethodInformation;
            private String digestAlgorithm;
            private String mgfAlgorithm;

            public EncryptionMethodImpl(String algorithm) {
                URI tmpAlgorithm = null;
                try {
                    tmpAlgorithm = new URI(algorithm);
                }
                catch (URISyntaxException ex) {
                    throw (IllegalArgumentException)new IllegalArgumentException().initCause(ex);
                }
                this.algorithm = tmpAlgorithm.toString();
                this.encryptionMethodInformation = new LinkedList<Element>();
            }

            @Override
            public String getAlgorithm() {
                return this.algorithm;
            }

            @Override
            public int getKeySize() {
                return this.keySize;
            }

            @Override
            public void setKeySize(int size) {
                this.keySize = size;
            }

            @Override
            public byte[] getOAEPparams() {
                return this.oaepParams;
            }

            @Override
            public void setOAEPparams(byte[] params) {
                this.oaepParams = params;
            }

            @Override
            public void setDigestAlgorithm(String digestAlgorithm) {
                this.digestAlgorithm = digestAlgorithm;
            }

            @Override
            public String getDigestAlgorithm() {
                return this.digestAlgorithm;
            }

            @Override
            public void setMGFAlgorithm(String mgfAlgorithm) {
                this.mgfAlgorithm = mgfAlgorithm;
            }

            @Override
            public String getMGFAlgorithm() {
                return this.mgfAlgorithm;
            }

            @Override
            public Iterator<Element> getEncryptionMethodInformation() {
                return this.encryptionMethodInformation.iterator();
            }

            @Override
            public void addEncryptionMethodInformation(Element info) {
                this.encryptionMethodInformation.add(info);
            }

            @Override
            public void removeEncryptionMethodInformation(Element info) {
                this.encryptionMethodInformation.remove(info);
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "EncryptionMethod");
                result.setAttributeNS(null, "Algorithm", this.algorithm);
                if (this.keySize > 0) {
                    result.appendChild(XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "KeySize").appendChild(XMLCipher.this.contextDocument.createTextNode(String.valueOf(this.keySize))));
                }
                if (null != this.oaepParams) {
                    Element oaepElement = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "OAEPparams");
                    oaepElement.appendChild(XMLCipher.this.contextDocument.createTextNode(XMLUtils.encodeToString(this.oaepParams)));
                    result.appendChild(oaepElement);
                }
                if (this.digestAlgorithm != null) {
                    Element digestElement = XMLUtils.createElementInSignatureSpace(XMLCipher.this.contextDocument, "DigestMethod");
                    digestElement.setAttributeNS(null, "Algorithm", this.digestAlgorithm);
                    digestElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix(XMLCipher.XML_DSIG), XMLCipher.XML_DSIG);
                    result.appendChild(digestElement);
                }
                if (this.mgfAlgorithm != null) {
                    Element mgfElement = XMLUtils.createElementInEncryption11Space(XMLCipher.this.contextDocument, "MGF");
                    mgfElement.setAttributeNS(null, "Algorithm", this.mgfAlgorithm);
                    mgfElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix("http://www.w3.org/2009/xmlenc11#"), "http://www.w3.org/2009/xmlenc11#");
                    result.appendChild(mgfElement);
                }
                Iterator<Element> itr = this.encryptionMethodInformation.iterator();
                while (itr.hasNext()) {
                    result.appendChild(itr.next());
                }
                return result;
            }
        }

        private abstract class EncryptedTypeImpl {
            private String id;
            private String type;
            private String mimeType;
            private String encoding;
            private EncryptionMethod encryptionMethod;
            private KeyInfo keyInfo;
            private CipherData cipherData;
            private EncryptionProperties encryptionProperties;

            protected EncryptedTypeImpl(CipherData data) {
                this.cipherData = data;
            }

            public String getId() {
                return this.id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getType() {
                return this.type;
            }

            public void setType(String type) {
                if (type == null || type.length() == 0) {
                    this.type = null;
                } else {
                    URI tmpType = null;
                    try {
                        tmpType = new URI(type);
                    }
                    catch (URISyntaxException ex) {
                        throw (IllegalArgumentException)new IllegalArgumentException().initCause(ex);
                    }
                    this.type = tmpType.toString();
                }
            }

            public String getMimeType() {
                return this.mimeType;
            }

            public void setMimeType(String type) {
                this.mimeType = type;
            }

            public String getEncoding() {
                return this.encoding;
            }

            public void setEncoding(String encoding) {
                if (encoding == null || encoding.length() == 0) {
                    this.encoding = null;
                } else {
                    URI tmpEncoding = null;
                    try {
                        tmpEncoding = new URI(encoding);
                    }
                    catch (URISyntaxException ex) {
                        throw (IllegalArgumentException)new IllegalArgumentException().initCause(ex);
                    }
                    this.encoding = tmpEncoding.toString();
                }
            }

            public EncryptionMethod getEncryptionMethod() {
                return this.encryptionMethod;
            }

            public void setEncryptionMethod(EncryptionMethod method) {
                this.encryptionMethod = method;
            }

            public KeyInfo getKeyInfo() {
                return this.keyInfo;
            }

            public void setKeyInfo(KeyInfo info) {
                this.keyInfo = info;
            }

            public CipherData getCipherData() {
                return this.cipherData;
            }

            public EncryptionProperties getEncryptionProperties() {
                return this.encryptionProperties;
            }

            public void setEncryptionProperties(EncryptionProperties properties) {
                this.encryptionProperties = properties;
            }
        }

        private class EncryptedKeyImpl
        extends EncryptedTypeImpl
        implements EncryptedKey {
            private String keyRecipient;
            private ReferenceList referenceList;
            private String carriedName;

            public EncryptedKeyImpl(CipherData data) {
                super(data);
            }

            @Override
            public String getRecipient() {
                return this.keyRecipient;
            }

            @Override
            public void setRecipient(String recipient) {
                this.keyRecipient = recipient;
            }

            @Override
            public ReferenceList getReferenceList() {
                return this.referenceList;
            }

            @Override
            public void setReferenceList(ReferenceList list) {
                this.referenceList = list;
            }

            @Override
            public String getCarriedName() {
                return this.carriedName;
            }

            @Override
            public void setCarriedName(String name) {
                this.carriedName = name;
            }

            Element toElement() {
                Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedKey");
                if (null != super.getId()) {
                    result.setAttributeNS(null, "Id", super.getId());
                }
                if (null != super.getType()) {
                    result.setAttributeNS(null, "Type", super.getType());
                }
                if (null != super.getMimeType()) {
                    result.setAttributeNS(null, "MimeType", super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    result.setAttributeNS(null, "Encoding", super.getEncoding());
                }
                if (null != this.getRecipient()) {
                    result.setAttributeNS(null, "Recipient", this.getRecipient());
                }
                if (null != super.getEncryptionMethod()) {
                    result.appendChild(((EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    result.appendChild(super.getKeyInfo().getElement().cloneNode(true));
                }
                result.appendChild(((CipherDataImpl)super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    result.appendChild(((EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
                }
                if (this.referenceList != null && !this.referenceList.isEmpty()) {
                    result.appendChild(((ReferenceListImpl)this.getReferenceList()).toElement());
                }
                if (null != this.carriedName) {
                    Element element = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName");
                    Text node = XMLCipher.this.contextDocument.createTextNode(this.carriedName);
                    element.appendChild(node);
                    result.appendChild(element);
                }
                return result;
            }
        }

        private class EncryptedDataImpl
        extends EncryptedTypeImpl
        implements EncryptedData {
            public EncryptedDataImpl(CipherData data) {
                super(data);
            }

            Element toElement() {
                Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
                if (null != super.getId()) {
                    result.setAttributeNS(null, "Id", super.getId());
                }
                if (null != super.getType()) {
                    result.setAttributeNS(null, "Type", super.getType());
                }
                if (null != super.getMimeType()) {
                    result.setAttributeNS(null, "MimeType", super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    result.setAttributeNS(null, "Encoding", super.getEncoding());
                }
                if (null != super.getEncryptionMethod()) {
                    result.appendChild(((EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    result.appendChild(super.getKeyInfo().getElement().cloneNode(true));
                }
                result.appendChild(((CipherDataImpl)super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    result.appendChild(((EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
                }
                return result;
            }
        }

        private class CipherValueImpl
        implements CipherValue {
            private String cipherValue;

            public CipherValueImpl(String value) {
                this.cipherValue = value;
            }

            @Override
            public String getValue() {
                return this.cipherValue;
            }

            @Override
            public void setValue(String value) {
                this.cipherValue = value;
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "CipherValue");
                result.appendChild(XMLCipher.this.contextDocument.createTextNode(this.cipherValue));
                return result;
            }
        }

        private class CipherReferenceImpl
        implements CipherReference {
            private String referenceURI;
            private Transforms referenceTransforms;
            private Attr referenceNode;

            public CipherReferenceImpl(String uri) {
                this.referenceURI = uri;
                this.referenceNode = null;
            }

            public CipherReferenceImpl(Attr uri) {
                this.referenceURI = uri.getNodeValue();
                this.referenceNode = uri;
            }

            @Override
            public String getURI() {
                return this.referenceURI;
            }

            @Override
            public Attr getURIAsAttr() {
                return this.referenceNode;
            }

            @Override
            public Transforms getTransforms() {
                return this.referenceTransforms;
            }

            @Override
            public void setTransforms(Transforms transforms) {
                this.referenceTransforms = transforms;
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "CipherReference");
                result.setAttributeNS(null, "URI", this.referenceURI);
                if (null != this.referenceTransforms) {
                    result.appendChild(((TransformsImpl)this.referenceTransforms).toElement());
                }
                return result;
            }
        }

        private class CipherDataImpl
        implements CipherData {
            private static final String valueMessage = "Data type is reference type.";
            private static final String referenceMessage = "Data type is value type.";
            private CipherValue cipherValue;
            private CipherReference cipherReference;
            private int cipherType = Integer.MIN_VALUE;

            public CipherDataImpl(int type) {
                this.cipherType = type;
            }

            @Override
            public CipherValue getCipherValue() {
                return this.cipherValue;
            }

            @Override
            public void setCipherValue(CipherValue value) throws XMLEncryptionException {
                if (this.cipherType == 2) {
                    throw new XMLEncryptionException(new UnsupportedOperationException(valueMessage));
                }
                this.cipherValue = value;
            }

            @Override
            public CipherReference getCipherReference() {
                return this.cipherReference;
            }

            @Override
            public void setCipherReference(CipherReference reference) throws XMLEncryptionException {
                if (this.cipherType == 1) {
                    throw new XMLEncryptionException(new UnsupportedOperationException(referenceMessage));
                }
                this.cipherReference = reference;
            }

            @Override
            public int getDataType() {
                return this.cipherType;
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "CipherData");
                if (this.cipherType == 1) {
                    result.appendChild(((CipherValueImpl)this.cipherValue).toElement());
                } else if (this.cipherType == 2) {
                    result.appendChild(((CipherReferenceImpl)this.cipherReference).toElement());
                }
                return result;
            }
        }

        private class AgreementMethodImpl
        implements AgreementMethod {
            private byte[] kaNonce;
            private List<Element> agreementMethodInformation = new LinkedList<Element>();
            private KeyInfo originatorKeyInfo;
            private KeyInfo recipientKeyInfo;
            private String algorithmURI;

            public AgreementMethodImpl(String algorithm) {
                URI tmpAlgorithm = null;
                try {
                    tmpAlgorithm = new URI(algorithm);
                }
                catch (URISyntaxException ex) {
                    throw (IllegalArgumentException)new IllegalArgumentException().initCause(ex);
                }
                this.algorithmURI = tmpAlgorithm.toString();
            }

            @Override
            public byte[] getKANonce() {
                return this.kaNonce;
            }

            @Override
            public void setKANonce(byte[] kanonce) {
                this.kaNonce = kanonce;
            }

            @Override
            public Iterator<Element> getAgreementMethodInformation() {
                return this.agreementMethodInformation.iterator();
            }

            @Override
            public void addAgreementMethodInformation(Element info) {
                this.agreementMethodInformation.add(info);
            }

            @Override
            public void revoveAgreementMethodInformation(Element info) {
                this.agreementMethodInformation.remove(info);
            }

            @Override
            public KeyInfo getOriginatorKeyInfo() {
                return this.originatorKeyInfo;
            }

            @Override
            public void setOriginatorKeyInfo(KeyInfo keyInfo) {
                this.originatorKeyInfo = keyInfo;
            }

            @Override
            public KeyInfo getRecipientKeyInfo() {
                return this.recipientKeyInfo;
            }

            @Override
            public void setRecipientKeyInfo(KeyInfo keyInfo) {
                this.recipientKeyInfo = keyInfo;
            }

            @Override
            public String getAlgorithm() {
                return this.algorithmURI;
            }
        }
    }
}

