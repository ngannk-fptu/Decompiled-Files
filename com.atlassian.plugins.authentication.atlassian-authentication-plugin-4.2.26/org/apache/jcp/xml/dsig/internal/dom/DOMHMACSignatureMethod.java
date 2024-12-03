/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.MacOutputStream;
import org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class DOMHMACSignatureMethod
extends AbstractDOMSignatureMethod {
    private static final String DOM_SIGNATURE_PROVIDER = "org.jcp.xml.dsig.internal.dom.MacProvider";
    private static final Logger LOG = LoggerFactory.getLogger(DOMHMACSignatureMethod.class);
    static final String HMAC_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
    static final String HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    static final String HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    static final String HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    static final String HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
    private Mac hmac;
    private int outputLength;
    private boolean outputLengthSet;
    private SignatureMethodParameterSpec params;

    DOMHMACSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        this.checkParams((SignatureMethodParameterSpec)params);
        this.params = (SignatureMethodParameterSpec)params;
    }

    DOMHMACSignatureMethod(Element smElem) throws MarshalException {
        Element paramsElem = DOMUtils.getFirstChildElement(smElem);
        if (paramsElem != null) {
            this.params = this.unmarshalParams(paramsElem);
        }
        try {
            this.checkParams(this.params);
        }
        catch (InvalidAlgorithmParameterException iape) {
            throw new MarshalException(iape);
        }
    }

    @Override
    void checkParams(SignatureMethodParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null) {
            if (!(params instanceof HMACParameterSpec)) {
                throw new InvalidAlgorithmParameterException("params must be of type HMACParameterSpec");
            }
            this.outputLength = ((HMACParameterSpec)params).getOutputLength();
            this.outputLengthSet = true;
            LOG.debug("Setting outputLength from HMACParameterSpec to: {}", (Object)this.outputLength);
        }
    }

    @Override
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    @Override
    SignatureMethodParameterSpec unmarshalParams(Element paramsElem) throws MarshalException {
        try {
            this.outputLength = Integer.parseInt(paramsElem.getFirstChild().getNodeValue());
        }
        catch (NumberFormatException ex) {
            throw new MarshalException("Invalid output length supplied: " + paramsElem.getFirstChild().getNodeValue());
        }
        this.outputLengthSet = true;
        LOG.debug("unmarshalled outputLength: {}", (Object)this.outputLength);
        return new HMACParameterSpec(this.outputLength);
    }

    @Override
    void marshalParams(Element parent, String prefix) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element hmacElem = DOMUtils.createElement(ownerDoc, "HMACOutputLength", "http://www.w3.org/2000/09/xmldsig#", prefix);
        hmacElem.appendChild(ownerDoc.createTextNode(String.valueOf(this.outputLength)));
        parent.appendChild(hmacElem);
    }

    @Override
    boolean verify(Key key, SignedInfo si, byte[] sig, XMLValidateContext context) throws InvalidKeyException, SignatureException, XMLSignatureException {
        if (key == null || si == null || sig == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("key must be SecretKey");
        }
        if (this.hmac == null) {
            try {
                Provider p = (Provider)context.getProperty(DOM_SIGNATURE_PROVIDER);
                this.hmac = p == null ? Mac.getInstance(this.getJCAAlgorithm()) : Mac.getInstance(this.getJCAAlgorithm(), p);
            }
            catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        if (this.outputLengthSet && this.outputLength < this.getDigestLength()) {
            throw new XMLSignatureException("HMACOutputLength must not be less than " + this.getDigestLength());
        }
        this.hmac.init(key);
        ((DOMSignedInfo)si).canonicalize(context, new MacOutputStream(this.hmac));
        byte[] result = this.hmac.doFinal();
        return MessageDigest.isEqual(sig, result);
    }

    @Override
    byte[] sign(Key key, SignedInfo si, XMLSignContext context) throws InvalidKeyException, XMLSignatureException {
        if (key == null || si == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("key must be SecretKey");
        }
        if (this.hmac == null) {
            try {
                Provider p = (Provider)context.getProperty(DOM_SIGNATURE_PROVIDER);
                this.hmac = p == null ? Mac.getInstance(this.getJCAAlgorithm()) : Mac.getInstance(this.getJCAAlgorithm(), p);
            }
            catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        if (this.outputLengthSet && this.outputLength < this.getDigestLength()) {
            throw new XMLSignatureException("HMACOutputLength must not be less than " + this.getDigestLength());
        }
        this.hmac.init(key);
        ((DOMSignedInfo)si).canonicalize(context, new MacOutputStream(this.hmac));
        return this.hmac.doFinal();
    }

    @Override
    boolean paramsEqual(AlgorithmParameterSpec spec) {
        if (this.getParameterSpec() == spec) {
            return true;
        }
        if (!(spec instanceof HMACParameterSpec)) {
            return false;
        }
        HMACParameterSpec ospec = (HMACParameterSpec)spec;
        return this.outputLength == ospec.getOutputLength();
    }

    @Override
    AbstractDOMSignatureMethod.Type getAlgorithmType() {
        return AbstractDOMSignatureMethod.Type.HMAC;
    }

    abstract int getDigestLength();

    static final class RIPEMD160
    extends DOMHMACSignatureMethod {
        RIPEMD160(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMHMACSignatureMethod.HMAC_RIPEMD160;
        }

        @Override
        String getJCAAlgorithm() {
            return "HMACRIPEMD160";
        }

        @Override
        int getDigestLength() {
            return 160;
        }
    }

    static final class SHA512
    extends DOMHMACSignatureMethod {
        SHA512(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMHMACSignatureMethod.HMAC_SHA512;
        }

        @Override
        String getJCAAlgorithm() {
            return "HmacSHA512";
        }

        @Override
        int getDigestLength() {
            return 512;
        }
    }

    static final class SHA384
    extends DOMHMACSignatureMethod {
        SHA384(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMHMACSignatureMethod.HMAC_SHA384;
        }

        @Override
        String getJCAAlgorithm() {
            return "HmacSHA384";
        }

        @Override
        int getDigestLength() {
            return 384;
        }
    }

    static final class SHA256
    extends DOMHMACSignatureMethod {
        SHA256(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMHMACSignatureMethod.HMAC_SHA256;
        }

        @Override
        String getJCAAlgorithm() {
            return "HmacSHA256";
        }

        @Override
        int getDigestLength() {
            return 256;
        }
    }

    static final class SHA224
    extends DOMHMACSignatureMethod {
        SHA224(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMHMACSignatureMethod.HMAC_SHA224;
        }

        @Override
        String getJCAAlgorithm() {
            return "HmacSHA224";
        }

        @Override
        int getDigestLength() {
            return 224;
        }
    }

    static final class SHA1
    extends DOMHMACSignatureMethod {
        SHA1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
        }

        @Override
        String getJCAAlgorithm() {
            return "HmacSHA1";
        }

        @Override
        int getDigestLength() {
            return 160;
        }
    }
}

