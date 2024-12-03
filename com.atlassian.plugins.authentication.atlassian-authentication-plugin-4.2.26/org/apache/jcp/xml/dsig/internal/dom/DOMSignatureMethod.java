/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.SignerOutputStream;
import org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMHMACSignatureMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMRSAPSSSignatureMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.xml.security.algorithms.implementations.SignatureECDSA;
import org.apache.xml.security.utils.JavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public abstract class DOMSignatureMethod
extends AbstractDOMSignatureMethod {
    private static final String DOM_SIGNATURE_PROVIDER = "org.jcp.xml.dsig.internal.dom.SignatureProvider";
    private static final Logger LOG = LoggerFactory.getLogger(DOMSignatureMethod.class);
    private SignatureMethodParameterSpec params;
    private Signature signature;
    static final String RSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
    static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    static final String RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    static final String RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    static final String RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    static final String ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    static final String ECDSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    static final String ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    static final String ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    static final String ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    static final String DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
    static final String ED25519 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed25519";
    static final String ED448 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed448";
    static final String ECDSA_RIPEMD160 = "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
    static final String RSA_SHA1_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
    static final String RSA_SHA224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
    static final String RSA_SHA256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
    static final String RSA_SHA384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
    static final String RSA_SHA512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
    static final String RSA_RIPEMD160_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#ripemd160-rsa-MGF1";

    DOMSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof SignatureMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec");
        }
        this.checkParams((SignatureMethodParameterSpec)params);
        this.params = (SignatureMethodParameterSpec)params;
    }

    DOMSignatureMethod(Element smElem) throws MarshalException {
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

    abstract byte[] postSignFormat(Key var1, byte[] var2) throws IOException;

    abstract byte[] preVerifyFormat(Key var1, byte[] var2) throws IOException;

    static SignatureMethod unmarshal(Element smElem) throws MarshalException {
        String alg = DOMUtils.getAttributeValue(smElem, "Algorithm");
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
            return new SHA1withRSA(smElem);
        }
        if (alg.equals(RSA_SHA224)) {
            return new SHA224withRSA(smElem);
        }
        if (alg.equals(RSA_SHA256)) {
            return new SHA256withRSA(smElem);
        }
        if (alg.equals(RSA_SHA384)) {
            return new SHA384withRSA(smElem);
        }
        if (alg.equals(RSA_SHA512)) {
            return new SHA512withRSA(smElem);
        }
        if (alg.equals(RSA_RIPEMD160)) {
            return new RIPEMD160withRSA(smElem);
        }
        if (alg.equals(RSA_SHA1_MGF1)) {
            return new SHA1withRSAandMGF1(smElem);
        }
        if (alg.equals(RSA_SHA224_MGF1)) {
            return new SHA224withRSAandMGF1(smElem);
        }
        if (alg.equals(RSA_SHA256_MGF1)) {
            return new SHA256withRSAandMGF1(smElem);
        }
        if (alg.equals(RSA_SHA384_MGF1)) {
            return new SHA384withRSAandMGF1(smElem);
        }
        if (alg.equals(RSA_SHA512_MGF1)) {
            return new SHA512withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#rsa-pss")) {
            return new DOMRSAPSSSignatureMethod.RSAPSS(smElem);
        }
        if (alg.equals(RSA_RIPEMD160_MGF1)) {
            return new RIPEMD160withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
            return new SHA1withDSA(smElem);
        }
        if (alg.equals(DSA_SHA256)) {
            return new SHA256withDSA(smElem);
        }
        if (alg.equals(ECDSA_SHA1)) {
            return new SHA1withECDSA(smElem);
        }
        if (alg.equals(ECDSA_SHA224)) {
            return new SHA224withECDSA(smElem);
        }
        if (alg.equals(ECDSA_SHA256)) {
            return new SHA256withECDSA(smElem);
        }
        if (alg.equals(ECDSA_SHA384)) {
            return new SHA384withECDSA(smElem);
        }
        if (alg.equals(ECDSA_SHA512)) {
            return new SHA512withECDSA(smElem);
        }
        if (alg.equals(ECDSA_RIPEMD160)) {
            return new RIPEMD160withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
            return new DOMHMACSignatureMethod.SHA1(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224")) {
            return new DOMHMACSignatureMethod.SHA224(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256")) {
            return new DOMHMACSignatureMethod.SHA256(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384")) {
            return new DOMHMACSignatureMethod.SHA384(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512")) {
            return new DOMHMACSignatureMethod.SHA512(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160")) {
            return new DOMHMACSignatureMethod.RIPEMD160(smElem);
        }
        if (alg.equals(ED25519)) {
            return new EDDSA_ED25519(smElem);
        }
        if (alg.equals(ED448)) {
            return new EDDSA_ED448(smElem);
        }
        throw new MarshalException("unsupported SignatureMethod algorithm: " + alg);
    }

    @Override
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    Signature getSignature(Provider p) throws NoSuchAlgorithmException {
        return p == null ? Signature.getInstance(this.getJCAAlgorithm()) : Signature.getInstance(this.getJCAAlgorithm(), p);
    }

    @Override
    boolean verify(Key key, SignedInfo si, byte[] sig, XMLValidateContext context) throws InvalidKeyException, SignatureException, XMLSignatureException {
        byte[] s;
        if (key == null || si == null || sig == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("key must be PublicKey");
        }
        if (this.signature == null) {
            Provider p = (Provider)context.getProperty(DOM_SIGNATURE_PROVIDER);
            try {
                this.signature = this.getSignature(p);
            }
            catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        this.signature.initVerify((PublicKey)key);
        LOG.debug("Signature provider: {}", (Object)this.signature.getProvider());
        LOG.debug("Verifying with key: {}", (Object)key);
        LOG.debug("JCA Algorithm: {}", (Object)this.getJCAAlgorithm());
        LOG.debug("Signature Bytes length: {}", (Object)sig.length);
        try (SignerOutputStream outputStream = new SignerOutputStream(this.signature);){
            ((DOMSignedInfo)si).canonicalize(context, outputStream);
            s = this.preVerifyFormat(key, sig);
        }
        catch (IOException ioe) {
            throw new XMLSignatureException(ioe);
        }
        return this.signature.verify(s);
    }

    @Override
    byte[] sign(Key key, SignedInfo si, XMLSignContext context) throws InvalidKeyException, XMLSignatureException {
        byte[] byArray;
        if (key == null || si == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
        }
        if (this.signature == null) {
            Provider p = (Provider)context.getProperty(DOM_SIGNATURE_PROVIDER);
            try {
                this.signature = this.getSignature(p);
            }
            catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        this.signature.initSign((PrivateKey)key);
        LOG.debug("Signature provider: {}", (Object)this.signature.getProvider());
        LOG.debug("Signing with key: {}", (Object)key);
        LOG.debug("JCA Algorithm: {}", (Object)this.getJCAAlgorithm());
        SignerOutputStream outputStream = new SignerOutputStream(this.signature);
        Throwable throwable = null;
        try {
            ((DOMSignedInfo)si).canonicalize(context, outputStream);
            byArray = this.postSignFormat(key, this.signature.sign());
        }
        catch (Throwable throwable2) {
            try {
                try {
                    throwable = throwable2;
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    DOMSignatureMethod.$closeResource(throwable, outputStream);
                    throw throwable3;
                }
            }
            catch (IOException | SignatureException ex) {
                throw new XMLSignatureException(ex);
            }
        }
        DOMSignatureMethod.$closeResource(throwable, outputStream);
        return byArray;
    }

    static final class EDDSA_ED448
    extends AbstractEDDSASignatureMethod {
        EDDSA_ED448(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        EDDSA_ED448(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ED448;
        }

        @Override
        String getJCAAlgorithm() {
            return "Ed448";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.EDDSA;
        }
    }

    static final class EDDSA_ED25519
    extends AbstractEDDSASignatureMethod {
        EDDSA_ED25519(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        EDDSA_ED25519(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ED25519;
        }

        @Override
        String getJCAAlgorithm() {
            return "Ed25519";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.EDDSA;
        }
    }

    static final class RIPEMD160withECDSA
    extends AbstractECDSASignatureMethod {
        RIPEMD160withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ECDSA_RIPEMD160;
        }

        @Override
        String getJCAAlgorithm() {
            return "RIPEMD160withECDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "RIPEMD160withECDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.ECDSA;
        }
    }

    static final class SHA512withECDSA
    extends AbstractECDSASignatureMethod {
        SHA512withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ECDSA_SHA512;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA512withECDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "SHA512withECDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.ECDSA;
        }
    }

    static final class SHA384withECDSA
    extends AbstractECDSASignatureMethod {
        SHA384withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ECDSA_SHA384;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA384withECDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "SHA384withECDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.ECDSA;
        }
    }

    static final class SHA256withECDSA
    extends AbstractECDSASignatureMethod {
        SHA256withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ECDSA_SHA256;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA256withECDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "SHA256withECDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.ECDSA;
        }
    }

    static final class SHA224withECDSA
    extends AbstractECDSASignatureMethod {
        SHA224withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ECDSA_SHA224;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA224withECDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "SHA224withECDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.ECDSA;
        }
    }

    static final class SHA1withECDSA
    extends AbstractECDSASignatureMethod {
        SHA1withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.ECDSA_SHA1;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA1withECDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "SHA1withECDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.ECDSA;
        }
    }

    static final class SHA256withDSA
    extends AbstractDSASignatureMethod {
        SHA256withDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.DSA_SHA256;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA256withDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "SHA256withDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.DSA;
        }
    }

    static final class SHA1withDSA
    extends AbstractDSASignatureMethod {
        SHA1withDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA1withDSAinP1363Format";
        }

        @Override
        String getJCAFallbackAlgorithm() {
            return "SHA1withDSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.DSA;
        }
    }

    static final class RIPEMD160withRSAandMGF1
    extends AbstractRSASignatureMethod {
        RIPEMD160withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_RIPEMD160_MGF1;
        }

        @Override
        String getJCAAlgorithm() {
            return "RIPEMD160withRSAandMGF1";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA512withRSAandMGF1
    extends AbstractRSASignatureMethod {
        SHA512withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA512_MGF1;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA512withRSAandMGF1";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA384withRSAandMGF1
    extends AbstractRSASignatureMethod {
        SHA384withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA384_MGF1;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA384withRSAandMGF1";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA256withRSAandMGF1
    extends AbstractRSASignatureMethod {
        SHA256withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA256_MGF1;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA256withRSAandMGF1";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA224withRSAandMGF1
    extends AbstractRSASignatureMethod {
        SHA224withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA224_MGF1;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA224withRSAandMGF1";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA1withRSAandMGF1
    extends AbstractRSASignatureMethod {
        SHA1withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA1_MGF1;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA1withRSAandMGF1";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class RIPEMD160withRSA
    extends AbstractRSASignatureMethod {
        RIPEMD160withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_RIPEMD160;
        }

        @Override
        String getJCAAlgorithm() {
            return "RIPEMD160withRSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA512withRSA
    extends AbstractRSASignatureMethod {
        SHA512withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA512;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA512withRSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA384withRSA
    extends AbstractRSASignatureMethod {
        SHA384withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA384;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA384withRSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA256withRSA
    extends AbstractRSASignatureMethod {
        SHA256withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA256;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA256withRSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA224withRSA
    extends AbstractRSASignatureMethod {
        SHA224withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_SHA224;
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA224withRSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static final class SHA1withRSA
    extends AbstractRSASignatureMethod {
        SHA1withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }

        @Override
        String getJCAAlgorithm() {
            return "SHA1withRSA";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    static abstract class AbstractEDDSASignatureMethod
    extends DOMSignatureMethod {
        AbstractEDDSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractEDDSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        byte[] postSignFormat(Key key, byte[] sig) {
            return sig;
        }

        @Override
        byte[] preVerifyFormat(Key key, byte[] sig) {
            return sig;
        }
    }

    static abstract class AbstractECDSASignatureMethod
    extends AbstractP1363FormatSignatureMethod {
        AbstractECDSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractECDSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        byte[] postSignFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                int rawLen = -1;
                if (key instanceof ECPrivateKey) {
                    ECPrivateKey ecKey = (ECPrivateKey)key;
                    rawLen = (ecKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
                }
                return SignatureECDSA.convertASN1toXMLDSIG(sig, rawLen);
            }
            return sig;
        }

        @Override
        byte[] preVerifyFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                return SignatureECDSA.convertXMLDSIGtoASN1(sig);
            }
            return sig;
        }
    }

    static abstract class AbstractDSASignatureMethod
    extends AbstractP1363FormatSignatureMethod {
        AbstractDSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractDSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        byte[] postSignFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                int size = ((DSAKey)((Object)key)).getParams().getQ().bitLength();
                return JavaUtils.convertDsaASN1toXMLDSIG(sig, size / 8);
            }
            return sig;
        }

        @Override
        byte[] preVerifyFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                int size = ((DSAKey)((Object)key)).getParams().getQ().bitLength();
                return JavaUtils.convertDsaXMLDSIGtoASN1(sig, size / 8);
            }
            return sig;
        }
    }

    static abstract class AbstractP1363FormatSignatureMethod
    extends DOMSignatureMethod {
        boolean asn1;

        AbstractP1363FormatSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractP1363FormatSignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        abstract String getJCAFallbackAlgorithm();

        @Override
        Signature getSignature(Provider p) throws NoSuchAlgorithmException {
            try {
                return p == null ? Signature.getInstance(this.getJCAAlgorithm()) : Signature.getInstance(this.getJCAAlgorithm(), p);
            }
            catch (NoSuchAlgorithmException nsae) {
                Signature s = p == null ? Signature.getInstance(this.getJCAFallbackAlgorithm()) : Signature.getInstance(this.getJCAFallbackAlgorithm(), p);
                this.asn1 = true;
                return s;
            }
        }
    }

    static abstract class AbstractRSASignatureMethod
    extends DOMSignatureMethod {
        AbstractRSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractRSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        byte[] postSignFormat(Key key, byte[] sig) {
            return sig;
        }

        @Override
        byte[] preVerifyFormat(Key key, byte[] sig) {
            return sig;
        }
    }
}

