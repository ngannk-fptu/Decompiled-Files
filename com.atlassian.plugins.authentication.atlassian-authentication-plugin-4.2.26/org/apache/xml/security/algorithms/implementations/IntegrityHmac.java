/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.algorithms.implementations;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class IntegrityHmac
extends SignatureAlgorithmSpi {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrityHmac.class);
    private final Mac macAlgorithm;
    private HMACOutputLength hmacOutputLength;

    abstract int getDigestLength();

    public IntegrityHmac() throws XMLSignatureException {
        this(null);
    }

    public IntegrityHmac(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        LOG.debug("Created IntegrityHmacSHA1 using {}", (Object)algorithmID);
        try {
            this.macAlgorithm = provider == null ? Mac.getInstance(algorithmID) : Mac.getInstance(algorithmID, provider);
        }
        catch (NoSuchAlgorithmException ex) {
            Object[] exArgs = new Object[]{algorithmID, ex.getLocalizedMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        throw new XMLSignatureException("empty", new Object[]{"Incorrect method call"});
    }

    @Override
    protected boolean engineVerify(byte[] signature) throws XMLSignatureException {
        try {
            if (this.hmacOutputLength != null && this.hmacOutputLength.length < this.getDigestLength()) {
                LOG.debug("HMACOutputLength must not be less than {}", (Object)this.getDigestLength());
                Object[] exArgs = new Object[]{String.valueOf(this.getDigestLength())};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            }
            byte[] completeResult = this.macAlgorithm.doFinal();
            return MessageDigestAlgorithm.isEqual(completeResult, signature);
        }
        catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitVerify(Key secretKey) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = null;
            if (secretKey != null) {
                supplied = secretKey.getClass().getName();
            }
            String needed = SecretKey.class.getName();
            Object[] exArgs = new Object[]{supplied, needed};
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }
        try {
            this.macAlgorithm.init(secretKey);
        }
        catch (InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            if (this.hmacOutputLength != null && this.hmacOutputLength.length < this.getDigestLength()) {
                LOG.debug("HMACOutputLength must not be less than {}", (Object)this.getDigestLength());
                Object[] exArgs = new Object[]{String.valueOf(this.getDigestLength())};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            }
            return this.macAlgorithm.doFinal();
        }
        catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitSign(Key secretKey) throws XMLSignatureException {
        this.engineInitSign(secretKey, (AlgorithmParameterSpec)null);
    }

    @Override
    protected void engineInitSign(Key secretKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = null;
            if (secretKey != null) {
                supplied = secretKey.getClass().getName();
            }
            String needed = SecretKey.class.getName();
            Object[] exArgs = new Object[]{supplied, needed};
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }
        try {
            if (algorithmParameterSpec == null) {
                this.macAlgorithm.init(secretKey);
            } else {
                this.macAlgorithm.init(secretKey, algorithmParameterSpec);
            }
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitSign(Key secretKey, SecureRandom secureRandom) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
    }

    @Override
    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        }
        catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        }
        catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineUpdate(byte[] buf, int offset, int len) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(buf, offset, len);
        }
        catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected String engineGetJCEAlgorithmString() {
        return this.macAlgorithm.getAlgorithm();
    }

    @Override
    protected String engineGetJCEProviderName() {
        return this.macAlgorithm.getProvider().getName();
    }

    @Override
    protected void engineSetHMACOutputLength(int length) throws XMLSignatureException {
        this.hmacOutputLength = new HMACOutputLength(length);
    }

    @Override
    protected void engineGetContextFromElement(Element element) throws XMLSignatureException {
        String hmacLength;
        if (element == null) {
            throw new IllegalArgumentException("element null");
        }
        Element n = XMLUtils.selectDsNode(element.getFirstChild(), "HMACOutputLength", 0);
        if (n != null && (hmacLength = XMLUtils.getFullTextChildrenFromNode(n)) != null && hmacLength.length() != 0) {
            this.hmacOutputLength = new HMACOutputLength(Integer.parseInt(hmacLength));
        }
    }

    @Override
    protected void engineAddContextToElement(Element element) throws XMLSignatureException {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }
        if (this.hmacOutputLength != null) {
            Document doc = element.getOwnerDocument();
            Element HMElem = XMLUtils.createElementInSignatureSpace(doc, "HMACOutputLength");
            Text HMText = doc.createTextNode("" + this.hmacOutputLength.length);
            HMElem.appendChild(HMText);
            XMLUtils.addReturnToElement(element);
            element.appendChild(HMElem);
            XMLUtils.addReturnToElement(element);
        }
    }

    private static class HMACOutputLength {
        private static final int MIN_LENGTH = 128;
        private static final int MAX_LENGTH = 2048;
        private final int length;

        public HMACOutputLength(int length) throws XMLSignatureException {
            this.length = length;
            if (length < 128) {
                LOG.debug("HMACOutputLength must not be less than {}", (Object)128);
                Object[] exArgs = new Object[]{String.valueOf(128)};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            }
            if (length > 2048) {
                LOG.debug("HMACOutputLength must not be more than {}", (Object)2048);
                Object[] exArgs = new Object[]{String.valueOf(2048)};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMax", exArgs);
            }
        }
    }

    public static class IntegrityHmacMD5
    extends IntegrityHmac {
        public IntegrityHmacMD5() throws XMLSignatureException {
        }

        public IntegrityHmacMD5(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
        }

        @Override
        int getDigestLength() {
            return 128;
        }
    }

    public static class IntegrityHmacRIPEMD160
    extends IntegrityHmac {
        public IntegrityHmacRIPEMD160() throws XMLSignatureException {
        }

        public IntegrityHmacRIPEMD160(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
        }

        @Override
        int getDigestLength() {
            return 160;
        }
    }

    public static class IntegrityHmacSHA512
    extends IntegrityHmac {
        public IntegrityHmacSHA512() throws XMLSignatureException {
        }

        public IntegrityHmacSHA512(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
        }

        @Override
        int getDigestLength() {
            return 512;
        }
    }

    public static class IntegrityHmacSHA384
    extends IntegrityHmac {
        public IntegrityHmacSHA384() throws XMLSignatureException {
        }

        public IntegrityHmacSHA384(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
        }

        @Override
        int getDigestLength() {
            return 384;
        }
    }

    public static class IntegrityHmacSHA256
    extends IntegrityHmac {
        public IntegrityHmacSHA256() throws XMLSignatureException {
        }

        public IntegrityHmacSHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
        }

        @Override
        int getDigestLength() {
            return 256;
        }
    }

    public static class IntegrityHmacSHA224
    extends IntegrityHmac {
        public IntegrityHmacSHA224() throws XMLSignatureException {
        }

        public IntegrityHmacSHA224(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
        }

        @Override
        int getDigestLength() {
            return 224;
        }
    }

    public static class IntegrityHmacSHA1
    extends IntegrityHmac {
        public IntegrityHmacSHA1() throws XMLSignatureException {
        }

        public IntegrityHmacSHA1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
        }

        @Override
        int getDigestLength() {
            return 160;
        }
    }
}

