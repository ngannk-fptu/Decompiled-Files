/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.BigIntegers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class PrivateKeyInfo
extends ASN1Object {
    private ASN1Integer version;
    private AlgorithmIdentifier privateKeyAlgorithm;
    private ASN1OctetString privateKey;
    private ASN1Set attributes;
    private ASN1BitString publicKey;

    public static PrivateKeyInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return PrivateKeyInfo.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static PrivateKeyInfo getInstance(Object obj) {
        if (obj instanceof PrivateKeyInfo) {
            return (PrivateKeyInfo)obj;
        }
        if (obj != null) {
            return new PrivateKeyInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private static int getVersionValue(ASN1Integer version) {
        int versionValue = version.intValueExact();
        if (versionValue < 0 || versionValue > 1) {
            throw new IllegalArgumentException("invalid version for private key info");
        }
        return versionValue;
    }

    public PrivateKeyInfo(AlgorithmIdentifier privateKeyAlgorithm, ASN1Encodable privateKey) throws IOException {
        this(privateKeyAlgorithm, privateKey, null, null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier privateKeyAlgorithm, ASN1Encodable privateKey, ASN1Set attributes) throws IOException {
        this(privateKeyAlgorithm, privateKey, attributes, null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier privateKeyAlgorithm, ASN1Encodable privateKey, ASN1Set attributes, byte[] publicKey) throws IOException {
        this.version = new ASN1Integer(publicKey != null ? BigIntegers.ONE : BigIntegers.ZERO);
        this.privateKeyAlgorithm = privateKeyAlgorithm;
        this.privateKey = new DEROctetString(privateKey);
        this.attributes = attributes;
        this.publicKey = publicKey == null ? null : new DERBitString(publicKey);
    }

    private PrivateKeyInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.version = ASN1Integer.getInstance(e.nextElement());
        int versionValue = PrivateKeyInfo.getVersionValue(this.version);
        this.privateKeyAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
        this.privateKey = ASN1OctetString.getInstance(e.nextElement());
        int lastTag = -1;
        block4: while (e.hasMoreElements()) {
            ASN1TaggedObject tagged = (ASN1TaggedObject)e.nextElement();
            int tag = tagged.getTagNo();
            if (tag <= lastTag) {
                throw new IllegalArgumentException("invalid optional field in private key info");
            }
            lastTag = tag;
            switch (tag) {
                case 0: {
                    this.attributes = ASN1Set.getInstance(tagged, false);
                    continue block4;
                }
                case 1: {
                    if (versionValue < 1) {
                        throw new IllegalArgumentException("'publicKey' requires version v2(1) or later");
                    }
                    this.publicKey = ASN1BitString.getInstance(tagged, false);
                    continue block4;
                }
            }
            throw new IllegalArgumentException("unknown optional field in private key info");
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ASN1Set getAttributes() {
        return this.attributes;
    }

    public AlgorithmIdentifier getPrivateKeyAlgorithm() {
        return this.privateKeyAlgorithm;
    }

    public ASN1OctetString getPrivateKey() {
        return new DEROctetString(this.privateKey.getOctets());
    }

    public int getPrivateKeyLength() {
        return this.privateKey.getOctetsLength();
    }

    public ASN1Encodable parsePrivateKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.privateKey.getOctets());
    }

    public boolean hasPublicKey() {
        return this.publicKey != null;
    }

    public ASN1Encodable parsePublicKey() throws IOException {
        return this.publicKey == null ? null : ASN1Primitive.fromByteArray(this.publicKey.getOctets());
    }

    public ASN1BitString getPublicKeyData() {
        return this.publicKey;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add(this.version);
        v.add(this.privateKeyAlgorithm);
        v.add(this.privateKey);
        if (this.attributes != null) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.attributes));
        }
        if (this.publicKey != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.publicKey));
        }
        return new DERSequence(v);
    }
}

