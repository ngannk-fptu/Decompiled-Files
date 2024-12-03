/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.dom.DOMStructure;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMDigestMethod
extends DOMStructure
implements DigestMethod {
    static final String SHA224 = "http://www.w3.org/2001/04/xmldsig-more#sha224";
    static final String SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    static final String WHIRLPOOL = "http://www.w3.org/2007/05/xmldsig-more#whirlpool";
    static final String SHA3_224 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224";
    static final String SHA3_256 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256";
    static final String SHA3_384 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384";
    static final String SHA3_512 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512";
    private DigestMethodParameterSpec params;

    DOMDigestMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof DigestMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type DigestMethodParameterSpec");
        }
        this.checkParams((DigestMethodParameterSpec)params);
        this.params = (DigestMethodParameterSpec)params;
    }

    DOMDigestMethod(Element dmElem) throws MarshalException {
        Element paramsElem = DOMUtils.getFirstChildElement(dmElem);
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

    static DigestMethod unmarshal(Element dmElem) throws MarshalException {
        String alg = DOMUtils.getAttributeValue(dmElem, "Algorithm");
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
            return new SHA1(dmElem);
        }
        if (alg.equals(SHA224)) {
            return new SHA224(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
            return new SHA256(dmElem);
        }
        if (alg.equals(SHA384)) {
            return new SHA384(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
            return new SHA512(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmlenc#ripemd160")) {
            return new RIPEMD160(dmElem);
        }
        if (alg.equals(WHIRLPOOL)) {
            return new WHIRLPOOL(dmElem);
        }
        if (alg.equals(SHA3_224)) {
            return new SHA3_224(dmElem);
        }
        if (alg.equals(SHA3_256)) {
            return new SHA3_256(dmElem);
        }
        if (alg.equals(SHA3_384)) {
            return new SHA3_384(dmElem);
        }
        if (alg.equals(SHA3_512)) {
            return new SHA3_512(dmElem);
        }
        throw new MarshalException("unsupported DigestMethod algorithm: " + alg);
    }

    void checkParams(DigestMethodParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + this.getMessageDigestAlgorithm() + " DigestMethod algorithm");
        }
    }

    @Override
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    DigestMethodParameterSpec unmarshalParams(Element paramsElem) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + this.getMessageDigestAlgorithm() + " DigestMethod algorithm");
    }

    @Override
    public void marshal(Node parent, String prefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element dmElem = DOMUtils.createElement(ownerDoc, "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", prefix);
        DOMUtils.setAttribute(dmElem, "Algorithm", this.getAlgorithm());
        if (this.params != null) {
            this.marshalParams(dmElem, prefix);
        }
        parent.appendChild(dmElem);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DigestMethod)) {
            return false;
        }
        DigestMethod odm = (DigestMethod)o;
        boolean paramsEqual = this.params == null ? odm.getParameterSpec() == null : this.params.equals(odm.getParameterSpec());
        return this.getAlgorithm().equals(odm.getAlgorithm()) && paramsEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.params != null) {
            result = 31 * result + this.params.hashCode();
        }
        result = 31 * result + this.getAlgorithm().hashCode();
        return result;
    }

    void marshalParams(Element parent, String prefix) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + this.getMessageDigestAlgorithm() + " DigestMethod algorithm");
    }

    abstract String getMessageDigestAlgorithm();

    static final class SHA3_512
    extends DOMDigestMethod {
        SHA3_512(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_512(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMDigestMethod.SHA3_512;
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA3-512";
        }
    }

    static final class SHA3_384
    extends DOMDigestMethod {
        SHA3_384(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_384(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMDigestMethod.SHA3_384;
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA3-384";
        }
    }

    static final class SHA3_256
    extends DOMDigestMethod {
        SHA3_256(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_256(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMDigestMethod.SHA3_256;
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA3-256";
        }
    }

    static final class SHA3_224
    extends DOMDigestMethod {
        SHA3_224(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_224(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMDigestMethod.SHA3_224;
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA3-224";
        }
    }

    static final class WHIRLPOOL
    extends DOMDigestMethod {
        WHIRLPOOL(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        WHIRLPOOL(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMDigestMethod.WHIRLPOOL;
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "WHIRLPOOL";
        }
    }

    static final class RIPEMD160
    extends DOMDigestMethod {
        RIPEMD160(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#ripemd160";
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "RIPEMD160";
        }
    }

    static final class SHA512
    extends DOMDigestMethod {
        SHA512(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#sha512";
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA-512";
        }
    }

    static final class SHA384
    extends DOMDigestMethod {
        SHA384(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMDigestMethod.SHA384;
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA-384";
        }
    }

    static final class SHA256
    extends DOMDigestMethod {
        SHA256(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#sha256";
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA-256";
        }
    }

    static final class SHA224
    extends DOMDigestMethod {
        SHA224(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return DOMDigestMethod.SHA224;
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA-224";
        }
    }

    static final class SHA1
    extends DOMDigestMethod {
        SHA1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override
        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#sha1";
        }

        @Override
        String getMessageDigestAlgorithm() {
            return "SHA-1";
        }
    }
}

