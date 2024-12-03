/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.util.Strings;

public class BCRSAPrivateKey
implements RSAPrivateKey,
PKCS12BagAttributeCarrier {
    static final long serialVersionUID = 5110188922551353628L;
    private static BigInteger ZERO = BigInteger.valueOf(0L);
    protected BigInteger modulus;
    protected BigInteger privateExponent;
    private byte[] algorithmIdentifierEnc = BCRSAPrivateKey.getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
    protected transient AlgorithmIdentifier algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
    protected transient RSAKeyParameters rsaPrivateKey;
    protected transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    BCRSAPrivateKey(RSAKeyParameters key) {
        this.modulus = key.getModulus();
        this.privateExponent = key.getExponent();
        this.rsaPrivateKey = key;
    }

    BCRSAPrivateKey(AlgorithmIdentifier algID, RSAKeyParameters key) {
        this.algorithmIdentifier = algID;
        this.algorithmIdentifierEnc = BCRSAPrivateKey.getEncoding(algID);
        this.modulus = key.getModulus();
        this.privateExponent = key.getExponent();
        this.rsaPrivateKey = key;
    }

    BCRSAPrivateKey(RSAPrivateKeySpec spec) {
        this.modulus = spec.getModulus();
        this.privateExponent = spec.getPrivateExponent();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    BCRSAPrivateKey(RSAPrivateKey key) {
        this.modulus = key.getModulus();
        this.privateExponent = key.getPrivateExponent();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    BCRSAPrivateKey(AlgorithmIdentifier algID, org.bouncycastle.asn1.pkcs.RSAPrivateKey key) {
        this.algorithmIdentifier = algID;
        this.algorithmIdentifierEnc = BCRSAPrivateKey.getEncoding(algID);
        this.modulus = key.getModulus();
        this.privateExponent = key.getPrivateExponent();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    @Override
    public BigInteger getModulus() {
        return this.modulus;
    }

    @Override
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
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
        return "PKCS#8";
    }

    RSAKeyParameters engineGetKeyParameters() {
        return this.rsaPrivateKey;
    }

    @Override
    public byte[] getEncoded() {
        return KeyUtil.getEncodedPrivateKeyInfo(this.algorithmIdentifier, new org.bouncycastle.asn1.pkcs.RSAPrivateKey(this.getModulus(), ZERO, this.getPrivateExponent(), ZERO, ZERO, ZERO, ZERO, ZERO));
    }

    public boolean equals(Object o) {
        if (!(o instanceof RSAPrivateKey)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        RSAPrivateKey key = (RSAPrivateKey)o;
        return this.getModulus().equals(key.getModulus()) && this.getPrivateExponent().equals(key.getPrivateExponent());
    }

    public int hashCode() {
        return this.getModulus().hashCode() ^ this.getPrivateExponent().hashCode();
    }

    @Override
    public void setBagAttribute(ASN1ObjectIdentifier oid, ASN1Encodable attribute) {
        this.attrCarrier.setBagAttribute(oid, attribute);
    }

    @Override
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier oid) {
        return this.attrCarrier.getBagAttribute(oid);
    }

    @Override
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.algorithmIdentifierEnc == null) {
            this.algorithmIdentifierEnc = BCRSAPrivateKey.getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
        }
        this.algorithmIdentifier = AlgorithmIdentifier.getInstance(this.algorithmIdentifierEnc);
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("RSA Private Key [").append(RSAUtil.generateKeyFingerprint(this.getModulus())).append("],[]").append(nl);
        buf.append("            modulus: ").append(this.getModulus().toString(16)).append(nl);
        return buf.toString();
    }

    private static byte[] getEncoding(AlgorithmIdentifier algorithmIdentifier) {
        try {
            return algorithmIdentifier.getEncoded();
        }
        catch (IOException e) {
            return null;
        }
    }
}

