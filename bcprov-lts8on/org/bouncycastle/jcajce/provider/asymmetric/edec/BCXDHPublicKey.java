/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.edec.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCXDHPublicKey
implements XDHPublicKey {
    static final long serialVersionUID = 1L;
    transient AsymmetricKeyParameter xdhPublicKey;

    BCXDHPublicKey(AsymmetricKeyParameter pubKey) {
        this.xdhPublicKey = pubKey;
    }

    BCXDHPublicKey(SubjectPublicKeyInfo keyInfo) {
        this.populateFromPubKeyInfo(keyInfo);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    BCXDHPublicKey(byte[] prefix, byte[] rawData) throws InvalidKeySpecException {
        int prefixLength = prefix.length;
        if (!Utils.isValidPrefix(prefix, rawData)) throw new InvalidKeySpecException("raw key data not recognised");
        if (rawData.length - prefixLength == 56) {
            this.xdhPublicKey = new X448PublicKeyParameters(rawData, prefixLength);
            return;
        } else {
            if (rawData.length - prefixLength != 32) throw new InvalidKeySpecException("raw key data not recognised");
            this.xdhPublicKey = new X25519PublicKeyParameters(rawData, prefixLength);
        }
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo keyInfo) {
        byte[] encoding = keyInfo.getPublicKeyData().getOctets();
        this.xdhPublicKey = EdECObjectIdentifiers.id_X448.equals(keyInfo.getAlgorithm().getAlgorithm()) ? new X448PublicKeyParameters(encoding) : new X25519PublicKeyParameters(encoding);
    }

    @Override
    public String getAlgorithm() {
        if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
            return "XDH";
        }
        return this.xdhPublicKey instanceof X448PublicKeyParameters ? "X448" : "X25519";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        if (this.xdhPublicKey instanceof X448PublicKeyParameters) {
            byte[] encoding = new byte[KeyFactorySpi.x448Prefix.length + 56];
            System.arraycopy(KeyFactorySpi.x448Prefix, 0, encoding, 0, KeyFactorySpi.x448Prefix.length);
            ((X448PublicKeyParameters)this.xdhPublicKey).encode(encoding, KeyFactorySpi.x448Prefix.length);
            return encoding;
        }
        byte[] encoding = new byte[KeyFactorySpi.x25519Prefix.length + 32];
        System.arraycopy(KeyFactorySpi.x25519Prefix, 0, encoding, 0, KeyFactorySpi.x25519Prefix.length);
        ((X25519PublicKeyParameters)this.xdhPublicKey).encode(encoding, KeyFactorySpi.x25519Prefix.length);
        return encoding;
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.xdhPublicKey;
    }

    @Override
    public BigInteger getU() {
        byte[] keyData = this.getUEncoding();
        Arrays.reverseInPlace(keyData);
        return new BigInteger(1, keyData);
    }

    @Override
    public byte[] getUEncoding() {
        if (this.xdhPublicKey instanceof X448PublicKeyParameters) {
            return ((X448PublicKeyParameters)this.xdhPublicKey).getEncoded();
        }
        return ((X25519PublicKeyParameters)this.xdhPublicKey).getEncoded();
    }

    public String toString() {
        return Utils.keyToString("Public Key", this.getAlgorithm(), this.xdhPublicKey);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PublicKey)) {
            return false;
        }
        PublicKey other = (PublicKey)o;
        return Arrays.areEqual(other.getEncoded(), this.getEncoded());
    }

    public int hashCode() {
        return Arrays.hashCode(this.getEncoded());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] enc = (byte[])in.readObject();
        this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(enc));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

