/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.edec.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCEdDSAPublicKey
implements EdDSAPublicKey {
    static final long serialVersionUID = 1L;
    transient AsymmetricKeyParameter eddsaPublicKey;

    BCEdDSAPublicKey(AsymmetricKeyParameter pubKey) {
        this.eddsaPublicKey = pubKey;
    }

    BCEdDSAPublicKey(SubjectPublicKeyInfo keyInfo) {
        this.populateFromPubKeyInfo(keyInfo);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    BCEdDSAPublicKey(byte[] prefix, byte[] rawData) throws InvalidKeySpecException {
        int prefixLength = prefix.length;
        if (!Utils.isValidPrefix(prefix, rawData)) throw new InvalidKeySpecException("raw key data not recognised");
        if (rawData.length - prefixLength == 57) {
            this.eddsaPublicKey = new Ed448PublicKeyParameters(rawData, prefixLength);
            return;
        } else {
            if (rawData.length - prefixLength != 32) throw new InvalidKeySpecException("raw key data not recognised");
            this.eddsaPublicKey = new Ed25519PublicKeyParameters(rawData, prefixLength);
        }
    }

    @Override
    public byte[] getPointEncoding() {
        if (this.eddsaPublicKey instanceof Ed448PublicKeyParameters) {
            return ((Ed448PublicKeyParameters)this.eddsaPublicKey).getEncoded();
        }
        return ((Ed25519PublicKeyParameters)this.eddsaPublicKey).getEncoded();
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo keyInfo) {
        byte[] encoding = keyInfo.getPublicKeyData().getOctets();
        this.eddsaPublicKey = EdECObjectIdentifiers.id_Ed448.equals(keyInfo.getAlgorithm().getAlgorithm()) ? new Ed448PublicKeyParameters(encoding) : new Ed25519PublicKeyParameters(encoding);
    }

    @Override
    public String getAlgorithm() {
        if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
            return "EdDSA";
        }
        return this.eddsaPublicKey instanceof Ed448PublicKeyParameters ? "Ed448" : "Ed25519";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        if (this.eddsaPublicKey instanceof Ed448PublicKeyParameters) {
            byte[] encoding = new byte[KeyFactorySpi.Ed448Prefix.length + 57];
            System.arraycopy(KeyFactorySpi.Ed448Prefix, 0, encoding, 0, KeyFactorySpi.Ed448Prefix.length);
            ((Ed448PublicKeyParameters)this.eddsaPublicKey).encode(encoding, KeyFactorySpi.Ed448Prefix.length);
            return encoding;
        }
        byte[] encoding = new byte[KeyFactorySpi.Ed25519Prefix.length + 32];
        System.arraycopy(KeyFactorySpi.Ed25519Prefix, 0, encoding, 0, KeyFactorySpi.Ed25519Prefix.length);
        ((Ed25519PublicKeyParameters)this.eddsaPublicKey).encode(encoding, KeyFactorySpi.Ed25519Prefix.length);
        return encoding;
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.eddsaPublicKey;
    }

    public String toString() {
        return Utils.keyToString("Public Key", this.getAlgorithm(), this.eddsaPublicKey);
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

