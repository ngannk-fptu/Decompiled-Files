/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class XMSSPrivateKey
extends ASN1Object {
    private final int version;
    private final int index;
    private final byte[] secretKeySeed;
    private final byte[] secretKeyPRF;
    private final byte[] publicSeed;
    private final byte[] root;
    private final int maxIndex;
    private final byte[] bdsState;

    public XMSSPrivateKey(int n, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
        this.version = 0;
        this.index = n;
        this.secretKeySeed = Arrays.clone(byArray);
        this.secretKeyPRF = Arrays.clone(byArray2);
        this.publicSeed = Arrays.clone(byArray3);
        this.root = Arrays.clone(byArray4);
        this.bdsState = Arrays.clone(byArray5);
        this.maxIndex = -1;
    }

    public XMSSPrivateKey(int n, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, int n2) {
        this.version = 1;
        this.index = n;
        this.secretKeySeed = Arrays.clone(byArray);
        this.secretKeyPRF = Arrays.clone(byArray2);
        this.publicSeed = Arrays.clone(byArray3);
        this.root = Arrays.clone(byArray4);
        this.bdsState = Arrays.clone(byArray5);
        this.maxIndex = n2;
    }

    private XMSSPrivateKey(ASN1Sequence aSN1Sequence) {
        ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        if (!aSN1Integer.hasValue(0) && !aSN1Integer.hasValue(1)) {
            throw new IllegalArgumentException("unknown version of sequence");
        }
        this.version = aSN1Integer.intValueExact();
        if (aSN1Sequence.size() != 2 && aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("key sequence wrong size");
        }
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(1));
        this.index = ASN1Integer.getInstance(aSN1Sequence2.getObjectAt(0)).intValueExact();
        this.secretKeySeed = Arrays.clone(DEROctetString.getInstance(aSN1Sequence2.getObjectAt(1)).getOctets());
        this.secretKeyPRF = Arrays.clone(DEROctetString.getInstance(aSN1Sequence2.getObjectAt(2)).getOctets());
        this.publicSeed = Arrays.clone(DEROctetString.getInstance(aSN1Sequence2.getObjectAt(3)).getOctets());
        this.root = Arrays.clone(DEROctetString.getInstance(aSN1Sequence2.getObjectAt(4)).getOctets());
        if (aSN1Sequence2.size() == 6) {
            ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Sequence2.getObjectAt(5));
            if (aSN1TaggedObject.getTagNo() != 0) {
                throw new IllegalArgumentException("unknown tag in XMSSPrivateKey");
            }
            this.maxIndex = ASN1Integer.getInstance(aSN1TaggedObject, false).intValueExact();
        } else if (aSN1Sequence2.size() == 5) {
            this.maxIndex = -1;
        } else {
            throw new IllegalArgumentException("keySeq should be 5 or 6 in length");
        }
        this.bdsState = (byte[])(aSN1Sequence.size() == 3 ? Arrays.clone(DEROctetString.getInstance(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(2)), true).getOctets()) : null);
    }

    public static XMSSPrivateKey getInstance(Object object) {
        if (object instanceof XMSSPrivateKey) {
            return (XMSSPrivateKey)object;
        }
        if (object != null) {
            return new XMSSPrivateKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public int getVersion() {
        return this.version;
    }

    public int getIndex() {
        return this.index;
    }

    public int getMaxIndex() {
        return this.maxIndex;
    }

    public byte[] getSecretKeySeed() {
        return Arrays.clone(this.secretKeySeed);
    }

    public byte[] getSecretKeyPRF() {
        return Arrays.clone(this.secretKeyPRF);
    }

    public byte[] getPublicSeed() {
        return Arrays.clone(this.publicSeed);
    }

    public byte[] getRoot() {
        return Arrays.clone(this.root);
    }

    public byte[] getBdsState() {
        return Arrays.clone(this.bdsState);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (this.maxIndex >= 0) {
            aSN1EncodableVector.add(new ASN1Integer(1L));
        } else {
            aSN1EncodableVector.add(new ASN1Integer(0L));
        }
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        aSN1EncodableVector2.add(new ASN1Integer(this.index));
        aSN1EncodableVector2.add(new DEROctetString(this.secretKeySeed));
        aSN1EncodableVector2.add(new DEROctetString(this.secretKeyPRF));
        aSN1EncodableVector2.add(new DEROctetString(this.publicSeed));
        aSN1EncodableVector2.add(new DEROctetString(this.root));
        if (this.maxIndex >= 0) {
            aSN1EncodableVector2.add(new DERTaggedObject(false, 0, new ASN1Integer(this.maxIndex)));
        }
        aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector2));
        aSN1EncodableVector.add(new DERTaggedObject(true, 0, new DEROctetString(this.bdsState)));
        return new DERSequence(aSN1EncodableVector);
    }
}

