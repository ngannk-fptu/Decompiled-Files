/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.jcajce.interfaces.XDHPrivateKey;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCXDHPrivateKey
implements XDHPrivateKey {
    static final long serialVersionUID = 1L;
    transient AsymmetricKeyParameter xdhPrivateKey;
    transient AsymmetricKeyParameter xdhPublicKey;
    transient int hashCode;
    private final boolean hasPublicKey;
    private final byte[] attributes;

    BCXDHPrivateKey(AsymmetricKeyParameter privKey) {
        this.hasPublicKey = true;
        this.attributes = null;
        this.xdhPrivateKey = privKey;
        this.xdhPublicKey = this.xdhPrivateKey instanceof X448PrivateKeyParameters ? ((X448PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey() : ((X25519PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
        this.hashCode = this.calculateHashCode();
    }

    BCXDHPrivateKey(PrivateKeyInfo keyInfo) throws IOException {
        this.hasPublicKey = keyInfo.hasPublicKey();
        this.attributes = keyInfo.getAttributes() != null ? keyInfo.getAttributes().getEncoded() : null;
        this.populateFromPrivateKeyInfo(keyInfo);
    }

    private void populateFromPrivateKeyInfo(PrivateKeyInfo keyInfo) throws IOException {
        int privateKeyLength = keyInfo.getPrivateKeyLength();
        byte[] encoding = privateKeyLength == 32 || privateKeyLength == 56 ? keyInfo.getPrivateKey().getOctets() : ASN1OctetString.getInstance(keyInfo.parsePrivateKey()).getOctets();
        if (EdECObjectIdentifiers.id_X448.equals(keyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
            this.xdhPrivateKey = new X448PrivateKeyParameters(encoding);
            this.xdhPublicKey = ((X448PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
        } else {
            this.xdhPrivateKey = new X25519PrivateKeyParameters(encoding);
            this.xdhPublicKey = ((X25519PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
        }
        this.hashCode = this.calculateHashCode();
    }

    @Override
    public String getAlgorithm() {
        if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
            return "XDH";
        }
        return this.xdhPrivateKey instanceof X448PrivateKeyParameters ? "X448" : "X25519";
    }

    @Override
    public String getFormat() {
        return "PKCS#8";
    }

    @Override
    public byte[] getEncoded() {
        try {
            PrivateKeyInfo privateKeyInfo = this.getPrivateKeyInfo();
            if (privateKeyInfo == null) {
                return null;
            }
            return privateKeyInfo.getEncoded();
        }
        catch (IOException e) {
            return null;
        }
    }

    private PrivateKeyInfo getPrivateKeyInfo() {
        try {
            ASN1Set attrSet = ASN1Set.getInstance(this.attributes);
            PrivateKeyInfo privInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.xdhPrivateKey, attrSet);
            if (this.hasPublicKey && !Properties.isOverrideSet("org.bouncycastle.pkcs8.v1_info_only")) {
                return privInfo;
            }
            return new PrivateKeyInfo(privInfo.getPrivateKeyAlgorithm(), privInfo.parsePrivateKey(), attrSet);
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public XDHPublicKey getPublicKey() {
        return new BCXDHPublicKey(this.xdhPublicKey);
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.xdhPrivateKey;
    }

    public String toString() {
        return Utils.keyToString("Private Key", this.getAlgorithm(), this.xdhPublicKey);
    }

    public boolean equals(Object o) {
        PrivateKeyInfo otherInfo;
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrivateKey)) {
            return false;
        }
        PrivateKey other = (PrivateKey)o;
        PrivateKeyInfo info = this.getPrivateKeyInfo();
        PrivateKeyInfo privateKeyInfo = otherInfo = other instanceof BCXDHPrivateKey ? ((BCXDHPrivateKey)other).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(other.getEncoded());
        if (info == null || otherInfo == null) {
            return false;
        }
        try {
            boolean algEquals = Arrays.constantTimeAreEqual(info.getPrivateKeyAlgorithm().getEncoded(), otherInfo.getPrivateKeyAlgorithm().getEncoded());
            boolean keyEquals = Arrays.constantTimeAreEqual(info.getPrivateKey().getEncoded(), otherInfo.getPrivateKey().getEncoded());
            return algEquals & keyEquals;
        }
        catch (IOException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int calculateHashCode() {
        byte[] publicData = this.xdhPublicKey instanceof X448PublicKeyParameters ? ((X448PublicKeyParameters)this.xdhPublicKey).getEncoded() : ((X25519PublicKeyParameters)this.xdhPublicKey).getEncoded();
        int result = this.getAlgorithm().hashCode();
        result = 31 * result + Arrays.hashCode(publicData);
        return result;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] enc = (byte[])in.readObject();
        this.populateFromPrivateKeyInfo(PrivateKeyInfo.getInstance(enc));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

