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
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.SignerOutputStream;
import org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.jcp.xml.dsig.internal.dom.RSAPSSParameterSpec;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class DOMRSAPSSSignatureMethod
extends AbstractDOMSignatureMethod {
    private static final String DOM_SIGNATURE_PROVIDER = "org.jcp.xml.dsig.internal.dom.SignatureProvider";
    private static final Logger LOG = LoggerFactory.getLogger(DOMRSAPSSSignatureMethod.class);
    private final SignatureMethodParameterSpec params;
    private Signature signature;
    static final String RSA_PSS = "http://www.w3.org/2007/05/xmldsig-more#rsa-pss";
    private int trailerField = 1;
    private int saltLength = 32;
    private String digestName = "SHA-256";

    DOMRSAPSSSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof SignatureMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec");
        }
        if (params == null) {
            params = this.getDefaultParameterSpec();
        }
        this.checkParams((SignatureMethodParameterSpec)params);
        this.params = (SignatureMethodParameterSpec)params;
    }

    DOMRSAPSSSignatureMethod(Element smElem) throws MarshalException {
        Element paramsElem = DOMUtils.getFirstChildElement(smElem);
        this.params = paramsElem != null ? this.unmarshalParams(paramsElem) : this.getDefaultParameterSpec();
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
            if (!(params instanceof RSAPSSParameterSpec)) {
                throw new InvalidAlgorithmParameterException("params must be of type RSAPSSParameterSpec");
            }
            if (((RSAPSSParameterSpec)params).getTrailerField() > 0) {
                this.trailerField = ((RSAPSSParameterSpec)params).getTrailerField();
                LOG.debug("Setting trailerField from RSAPSSParameterSpec to: {}", (Object)this.trailerField);
            }
            if (((RSAPSSParameterSpec)params).getSaltLength() > 0) {
                this.saltLength = ((RSAPSSParameterSpec)params).getSaltLength();
                LOG.debug("Setting saltLength from RSAPSSParameterSpec to: {}", (Object)this.saltLength);
            }
            if (((RSAPSSParameterSpec)params).getDigestName() != null) {
                this.digestName = ((RSAPSSParameterSpec)params).getDigestName();
                LOG.debug("Setting digestName from RSAPSSParameterSpec to: {}", (Object)this.digestName);
            }
        }
    }

    @Override
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    @Override
    void marshalParams(Element parent, String prefix) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element rsaPssParamsElement = ownerDoc.createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:RSAPSSParams");
        rsaPssParamsElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:pss", "http://www.w3.org/2007/05/xmldsig-more#");
        Element digestMethodElement = DOMUtils.createElement(rsaPssParamsElement.getOwnerDocument(), "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", prefix);
        try {
            digestMethodElement.setAttributeNS(null, "Algorithm", SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.fromDigestAlgorithm(this.digestName).getXmlDigestAlgorithm());
        }
        catch (XMLSignatureException | DOMException e) {
            throw new MarshalException("Invalid digest name supplied: " + this.digestName);
        }
        rsaPssParamsElement.appendChild(digestMethodElement);
        Element saltLengthElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:SaltLength");
        Text saltLengthText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.saltLength));
        saltLengthElement.appendChild(saltLengthText);
        rsaPssParamsElement.appendChild(saltLengthElement);
        Element trailerFieldElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:TrailerField");
        Text trailerFieldText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.trailerField));
        trailerFieldElement.appendChild(trailerFieldText);
        rsaPssParamsElement.appendChild(trailerFieldElement);
        parent.appendChild(rsaPssParamsElement);
    }

    @Override
    SignatureMethodParameterSpec unmarshalParams(Element paramsElem) throws MarshalException {
        if (paramsElem != null) {
            SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm digestAlgorithm;
            Element saltLengthNode = XMLUtils.selectNode(paramsElem.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", "SaltLength", 0);
            Element trailerFieldNode = XMLUtils.selectNode(paramsElem.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", "TrailerField", 0);
            int trailerField = 1;
            if (trailerFieldNode != null) {
                try {
                    trailerField = Integer.parseInt(trailerFieldNode.getTextContent());
                }
                catch (NumberFormatException ex) {
                    throw new MarshalException("Invalid trailer field supplied: " + trailerFieldNode.getTextContent());
                }
            }
            String xmlAlgorithm = XMLUtils.selectDsNode(paramsElem.getFirstChild(), "DigestMethod", 0).getAttribute("Algorithm");
            try {
                digestAlgorithm = SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.fromXmlDigestAlgorithm(xmlAlgorithm);
            }
            catch (XMLSignatureException e) {
                throw new MarshalException("Invalid digest algorithm supplied: " + xmlAlgorithm);
            }
            String digestName = digestAlgorithm.getDigestAlgorithm();
            RSAPSSParameterSpec params = new RSAPSSParameterSpec();
            params.setTrailerField(trailerField);
            try {
                int saltLength = saltLengthNode == null ? digestAlgorithm.getSaltLength() : Integer.parseInt(saltLengthNode.getTextContent());
                params.setSaltLength(saltLength);
            }
            catch (NumberFormatException ex) {
                throw new MarshalException("Invalid salt length supplied: " + saltLengthNode.getTextContent());
            }
            params.setDigestName(digestName);
            return params;
        }
        return this.getDefaultParameterSpec();
    }

    @Override
    boolean verify(Key key, SignedInfo si, byte[] sig, XMLValidateContext context) throws InvalidKeyException, SignatureException, javax.xml.crypto.dsig.XMLSignatureException {
        boolean bl;
        if (key == null || si == null || sig == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("key must be PublicKey");
        }
        if (this.signature == null) {
            try {
                Provider p = (Provider)context.getProperty(DOM_SIGNATURE_PROVIDER);
                this.signature = p == null ? Signature.getInstance(this.getJCAAlgorithm()) : Signature.getInstance(this.getJCAAlgorithm(), p);
            }
            catch (NoSuchAlgorithmException nsae) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(nsae);
            }
        }
        this.signature.initVerify((PublicKey)key);
        try {
            this.signature.setParameter(new PSSParameterSpec(this.digestName, "MGF1", new MGF1ParameterSpec(this.digestName), this.saltLength, this.trailerField));
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new javax.xml.crypto.dsig.XMLSignatureException(e);
        }
        LOG.debug("Signature provider: {}", (Object)this.signature.getProvider());
        LOG.debug("Verifying with key: {}", (Object)key);
        LOG.debug("JCA Algorithm: {}", (Object)this.getJCAAlgorithm());
        LOG.debug("Signature Bytes length: {}", (Object)sig.length);
        SignerOutputStream outputStream = new SignerOutputStream(this.signature);
        Throwable throwable = null;
        try {
            ((DOMSignedInfo)si).canonicalize(context, outputStream);
            bl = this.signature.verify(sig);
        }
        catch (Throwable throwable2) {
            try {
                try {
                    throwable = throwable2;
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    DOMRSAPSSSignatureMethod.$closeResource(throwable, outputStream);
                    throw throwable3;
                }
            }
            catch (IOException ioe) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(ioe);
            }
        }
        DOMRSAPSSSignatureMethod.$closeResource(throwable, outputStream);
        return bl;
    }

    @Override
    byte[] sign(Key key, SignedInfo si, XMLSignContext context) throws InvalidKeyException, javax.xml.crypto.dsig.XMLSignatureException {
        byte[] byArray;
        if (key == null || si == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
        }
        if (this.signature == null) {
            try {
                Provider p = (Provider)context.getProperty(DOM_SIGNATURE_PROVIDER);
                this.signature = p == null ? Signature.getInstance(this.getJCAAlgorithm()) : Signature.getInstance(this.getJCAAlgorithm(), p);
            }
            catch (NoSuchAlgorithmException nsae) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(nsae);
            }
        }
        this.signature.initSign((PrivateKey)key);
        try {
            this.signature.setParameter(new PSSParameterSpec(this.digestName, "MGF1", new MGF1ParameterSpec(this.digestName), this.saltLength, this.trailerField));
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new javax.xml.crypto.dsig.XMLSignatureException(e);
        }
        LOG.debug("Signature provider: {}", (Object)this.signature.getProvider());
        LOG.debug("Signing with key: {}", (Object)key);
        LOG.debug("JCA Algorithm: {}", (Object)this.getJCAAlgorithm());
        SignerOutputStream outputStream = new SignerOutputStream(this.signature);
        Throwable throwable = null;
        try {
            ((DOMSignedInfo)si).canonicalize(context, outputStream);
            byArray = this.signature.sign();
        }
        catch (Throwable throwable2) {
            try {
                try {
                    throwable = throwable2;
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    DOMRSAPSSSignatureMethod.$closeResource(throwable, outputStream);
                    throw throwable3;
                }
            }
            catch (IOException | SignatureException e) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(e);
            }
        }
        DOMRSAPSSSignatureMethod.$closeResource(throwable, outputStream);
        return byArray;
    }

    @Override
    boolean paramsEqual(AlgorithmParameterSpec spec) {
        return this.getParameterSpec().equals(spec);
    }

    private SignatureMethodParameterSpec getDefaultParameterSpec() {
        RSAPSSParameterSpec params = new RSAPSSParameterSpec();
        params.setTrailerField(this.trailerField);
        params.setSaltLength(this.saltLength);
        params.setDigestName(this.digestName);
        return params;
    }

    private static /* synthetic */ /* end resource */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable throwable) {
                x0.addSuppressed(throwable);
            }
        } else {
            x1.close();
        }
    }

    static final class RSAPSS
    extends DOMRSAPSSSignatureMethod {
        RSAPSS(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RSAPSS(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMRSAPSSSignatureMethod.RSA_PSS;
        }

        @Override
        String getJCAAlgorithm() {
            return "RSASSA-PSS";
        }

        @Override
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }
}

