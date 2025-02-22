/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.provider.JCERSAPrivateKey;
import org.bouncycastle.util.Strings;

public class JCERSAPrivateCrtKey
extends JCERSAPrivateKey
implements RSAPrivateCrtKey {
    static final long serialVersionUID = 7834723820638524718L;
    private BigInteger publicExponent;
    private BigInteger primeP;
    private BigInteger primeQ;
    private BigInteger primeExponentP;
    private BigInteger primeExponentQ;
    private BigInteger crtCoefficient;

    JCERSAPrivateCrtKey(RSAPrivateCrtKeyParameters key) {
        super(key);
        this.publicExponent = key.getPublicExponent();
        this.primeP = key.getP();
        this.primeQ = key.getQ();
        this.primeExponentP = key.getDP();
        this.primeExponentQ = key.getDQ();
        this.crtCoefficient = key.getQInv();
    }

    JCERSAPrivateCrtKey(RSAPrivateCrtKeySpec spec) {
        this.modulus = spec.getModulus();
        this.publicExponent = spec.getPublicExponent();
        this.privateExponent = spec.getPrivateExponent();
        this.primeP = spec.getPrimeP();
        this.primeQ = spec.getPrimeQ();
        this.primeExponentP = spec.getPrimeExponentP();
        this.primeExponentQ = spec.getPrimeExponentQ();
        this.crtCoefficient = spec.getCrtCoefficient();
    }

    JCERSAPrivateCrtKey(RSAPrivateCrtKey key) {
        this.modulus = key.getModulus();
        this.publicExponent = key.getPublicExponent();
        this.privateExponent = key.getPrivateExponent();
        this.primeP = key.getPrimeP();
        this.primeQ = key.getPrimeQ();
        this.primeExponentP = key.getPrimeExponentP();
        this.primeExponentQ = key.getPrimeExponentQ();
        this.crtCoefficient = key.getCrtCoefficient();
    }

    JCERSAPrivateCrtKey(PrivateKeyInfo info) throws IOException {
        this(RSAPrivateKey.getInstance(info.parsePrivateKey()));
    }

    JCERSAPrivateCrtKey(RSAPrivateKey key) {
        this.modulus = key.getModulus();
        this.publicExponent = key.getPublicExponent();
        this.privateExponent = key.getPrivateExponent();
        this.primeP = key.getPrime1();
        this.primeQ = key.getPrime2();
        this.primeExponentP = key.getExponent1();
        this.primeExponentQ = key.getExponent2();
        this.crtCoefficient = key.getCoefficient();
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPrivateKey(this.getModulus(), this.getPublicExponent(), this.getPrivateExponent(), this.getPrimeP(), this.getPrimeQ(), this.getPrimeExponentP(), this.getPrimeExponentQ(), this.getCrtCoefficient()));
    }

    @Override
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }

    @Override
    public BigInteger getPrimeP() {
        return this.primeP;
    }

    @Override
    public BigInteger getPrimeQ() {
        return this.primeQ;
    }

    @Override
    public BigInteger getPrimeExponentP() {
        return this.primeExponentP;
    }

    @Override
    public BigInteger getPrimeExponentQ() {
        return this.primeExponentQ;
    }

    @Override
    public BigInteger getCrtCoefficient() {
        return this.crtCoefficient;
    }

    @Override
    public int hashCode() {
        return this.getModulus().hashCode() ^ this.getPublicExponent().hashCode() ^ this.getPrivateExponent().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RSAPrivateCrtKey)) {
            return false;
        }
        RSAPrivateCrtKey key = (RSAPrivateCrtKey)o;
        return this.getModulus().equals(key.getModulus()) && this.getPublicExponent().equals(key.getPublicExponent()) && this.getPrivateExponent().equals(key.getPrivateExponent()) && this.getPrimeP().equals(key.getPrimeP()) && this.getPrimeQ().equals(key.getPrimeQ()) && this.getPrimeExponentP().equals(key.getPrimeExponentP()) && this.getPrimeExponentQ().equals(key.getPrimeExponentQ()) && this.getCrtCoefficient().equals(key.getCrtCoefficient());
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("RSA Private CRT Key").append(nl);
        buf.append("            modulus: ").append(this.getModulus().toString(16)).append(nl);
        buf.append("    public exponent: ").append(this.getPublicExponent().toString(16)).append(nl);
        buf.append("   private exponent: ").append(this.getPrivateExponent().toString(16)).append(nl);
        buf.append("             primeP: ").append(this.getPrimeP().toString(16)).append(nl);
        buf.append("             primeQ: ").append(this.getPrimeQ().toString(16)).append(nl);
        buf.append("     primeExponentP: ").append(this.getPrimeExponentP().toString(16)).append(nl);
        buf.append("     primeExponentQ: ").append(this.getPrimeExponentQ().toString(16)).append(nl);
        buf.append("     crtCoefficient: ").append(this.getCrtCoefficient().toString(16)).append(nl);
        return buf.toString();
    }
}

