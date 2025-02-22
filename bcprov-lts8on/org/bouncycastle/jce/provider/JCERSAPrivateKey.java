/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class JCERSAPrivateKey
implements RSAPrivateKey,
PKCS12BagAttributeCarrier {
    static final long serialVersionUID = 5110188922551353628L;
    private static BigInteger ZERO = BigInteger.valueOf(0L);
    protected BigInteger modulus;
    protected BigInteger privateExponent;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected JCERSAPrivateKey() {
    }

    JCERSAPrivateKey(RSAKeyParameters key) {
        this.modulus = key.getModulus();
        this.privateExponent = key.getExponent();
    }

    JCERSAPrivateKey(RSAPrivateKeySpec spec) {
        this.modulus = spec.getModulus();
        this.privateExponent = spec.getPrivateExponent();
    }

    JCERSAPrivateKey(RSAPrivateKey key) {
        this.modulus = key.getModulus();
        this.privateExponent = key.getPrivateExponent();
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
        return "RSA";
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new org.bouncycastle.asn1.pkcs.RSAPrivateKey(this.getModulus(), ZERO, this.getPrivateExponent(), ZERO, ZERO, ZERO, ZERO, ZERO));
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
        this.modulus = (BigInteger)in.readObject();
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.attrCarrier.readObject(in);
        this.privateExponent = (BigInteger)in.readObject();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.modulus);
        this.attrCarrier.writeObject(out);
        out.writeObject(this.privateExponent);
    }
}

