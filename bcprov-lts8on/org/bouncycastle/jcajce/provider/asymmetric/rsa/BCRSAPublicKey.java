/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class BCRSAPublicKey
implements java.security.interfaces.RSAPublicKey {
    static final AlgorithmIdentifier DEFAULT_ALGORITHM_IDENTIFIER = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    static final long serialVersionUID = 2675817738516720772L;
    private BigInteger modulus;
    private BigInteger publicExponent;
    private transient AlgorithmIdentifier algorithmIdentifier;
    private transient RSAKeyParameters rsaPublicKey;

    BCRSAPublicKey(RSAKeyParameters key) {
        this(DEFAULT_ALGORITHM_IDENTIFIER, key);
    }

    BCRSAPublicKey(AlgorithmIdentifier algId, RSAKeyParameters key) {
        this.algorithmIdentifier = algId;
        this.modulus = key.getModulus();
        this.publicExponent = key.getExponent();
        this.rsaPublicKey = key;
    }

    BCRSAPublicKey(RSAPublicKeySpec spec) {
        this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = spec.getModulus();
        this.publicExponent = spec.getPublicExponent();
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    BCRSAPublicKey(java.security.interfaces.RSAPublicKey key) {
        this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = key.getModulus();
        this.publicExponent = key.getPublicExponent();
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    BCRSAPublicKey(SubjectPublicKeyInfo info) {
        this.populateFromPublicKeyInfo(info);
    }

    private void populateFromPublicKeyInfo(SubjectPublicKeyInfo info) {
        try {
            RSAPublicKey pubKey = RSAPublicKey.getInstance(info.parsePublicKey());
            this.algorithmIdentifier = info.getAlgorithm();
            this.modulus = pubKey.getModulus();
            this.publicExponent = pubKey.getPublicExponent();
            this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in RSA public key");
        }
    }

    @Override
    public BigInteger getModulus() {
        return this.modulus;
    }

    @Override
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }

    @Override
    public String getAlgorithm() {
        if (this.algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            return "RSASSA-PSS";
        }
        return "RSA";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(this.algorithmIdentifier, new RSAPublicKey(this.getModulus(), this.getPublicExponent()));
    }

    RSAKeyParameters engineGetKeyParameters() {
        return this.rsaPublicKey;
    }

    public int hashCode() {
        return this.getModulus().hashCode() ^ this.getPublicExponent().hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof java.security.interfaces.RSAPublicKey)) {
            return false;
        }
        java.security.interfaces.RSAPublicKey key = (java.security.interfaces.RSAPublicKey)o;
        return this.getModulus().equals(key.getModulus()) && this.getPublicExponent().equals(key.getPublicExponent());
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("RSA Public Key [").append(RSAUtil.generateKeyFingerprint(this.getModulus())).append("]").append(",[").append(RSAUtil.generateExponentFingerprint(this.getPublicExponent())).append("]").append(nl);
        buf.append("        modulus: ").append(this.getModulus().toString(16)).append(nl);
        buf.append("public exponent: ").append(this.getPublicExponent().toString(16)).append(nl);
        return buf.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            this.algorithmIdentifier = AlgorithmIdentifier.getInstance(in.readObject());
        }
        catch (Exception e) {
            this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        }
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (!this.algorithmIdentifier.equals(DEFAULT_ALGORITHM_IDENTIFIER)) {
            out.writeObject(this.algorithmIdentifier.getEncoded());
        }
    }
}

