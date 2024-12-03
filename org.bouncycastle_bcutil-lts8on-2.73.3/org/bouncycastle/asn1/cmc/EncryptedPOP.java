/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.TaggedRequest;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class EncryptedPOP
extends ASN1Object {
    private final TaggedRequest request;
    private final ContentInfo cms;
    private final AlgorithmIdentifier thePOPAlgID;
    private final AlgorithmIdentifier witnessAlgID;
    private final byte[] witness;

    private EncryptedPOP(ASN1Sequence seq) {
        if (seq.size() != 5) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.request = TaggedRequest.getInstance(seq.getObjectAt(0));
        this.cms = ContentInfo.getInstance(seq.getObjectAt(1));
        this.thePOPAlgID = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(2));
        this.witnessAlgID = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(3));
        this.witness = Arrays.clone((byte[])ASN1OctetString.getInstance((Object)seq.getObjectAt(4)).getOctets());
    }

    public EncryptedPOP(TaggedRequest request, ContentInfo cms, AlgorithmIdentifier thePOPAlgID, AlgorithmIdentifier witnessAlgID, byte[] witness) {
        this.request = request;
        this.cms = cms;
        this.thePOPAlgID = thePOPAlgID;
        this.witnessAlgID = witnessAlgID;
        this.witness = Arrays.clone((byte[])witness);
    }

    public static EncryptedPOP getInstance(Object o) {
        if (o instanceof EncryptedPOP) {
            return (EncryptedPOP)((Object)o);
        }
        if (o != null) {
            return new EncryptedPOP(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public TaggedRequest getRequest() {
        return this.request;
    }

    public ContentInfo getCms() {
        return this.cms;
    }

    public AlgorithmIdentifier getThePOPAlgID() {
        return this.thePOPAlgID;
    }

    public AlgorithmIdentifier getWitnessAlgID() {
        return this.witnessAlgID;
    }

    public byte[] getWitness() {
        return Arrays.clone((byte[])this.witness);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add((ASN1Encodable)this.request);
        v.add((ASN1Encodable)this.cms);
        v.add((ASN1Encodable)this.thePOPAlgID);
        v.add((ASN1Encodable)this.witnessAlgID);
        v.add((ASN1Encodable)new DEROctetString(this.witness));
        return new DERSequence(v);
    }
}

