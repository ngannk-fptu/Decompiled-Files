/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.util.Arrays;

public class PKIHeaderBuilder {
    private final ASN1Integer pvno;
    private final GeneralName sender;
    private final GeneralName recipient;
    private ASN1GeneralizedTime messageTime;
    private AlgorithmIdentifier protectionAlg;
    private ASN1OctetString senderKID;
    private ASN1OctetString recipKID;
    private ASN1OctetString transactionID;
    private ASN1OctetString senderNonce;
    private ASN1OctetString recipNonce;
    private PKIFreeText freeText;
    private ASN1Sequence generalInfo;

    public PKIHeaderBuilder(int pvno, GeneralName sender, GeneralName recipient) {
        this(new ASN1Integer((long)pvno), sender, recipient);
    }

    private PKIHeaderBuilder(ASN1Integer pvno, GeneralName sender, GeneralName recipient) {
        this.pvno = pvno;
        this.sender = sender;
        this.recipient = recipient;
    }

    private static ASN1Sequence makeGeneralInfoSeq(InfoTypeAndValue generalInfo) {
        return new DERSequence((ASN1Encodable)generalInfo);
    }

    private static ASN1Sequence makeGeneralInfoSeq(InfoTypeAndValue[] generalInfos) {
        DERSequence genInfoSeq = null;
        if (generalInfos != null) {
            genInfoSeq = new DERSequence((ASN1Encodable[])generalInfos);
        }
        return genInfoSeq;
    }

    public PKIHeaderBuilder setMessageTime(ASN1GeneralizedTime time) {
        this.messageTime = time;
        return this;
    }

    public PKIHeaderBuilder setProtectionAlg(AlgorithmIdentifier aid) {
        this.protectionAlg = aid;
        return this;
    }

    public PKIHeaderBuilder setSenderKID(byte[] kid) {
        return this.setSenderKID((ASN1OctetString)(kid == null ? null : this.createClonedOctetString(kid)));
    }

    public PKIHeaderBuilder setSenderKID(ASN1OctetString kid) {
        this.senderKID = kid;
        return this;
    }

    public PKIHeaderBuilder setRecipKID(byte[] kid) {
        return this.setRecipKID((ASN1OctetString)(kid == null ? null : this.createClonedOctetString(kid)));
    }

    public PKIHeaderBuilder setRecipKID(ASN1OctetString kid) {
        this.recipKID = kid;
        return this;
    }

    public PKIHeaderBuilder setTransactionID(byte[] tid) {
        return this.setTransactionID((ASN1OctetString)(tid == null ? null : this.createClonedOctetString(tid)));
    }

    public PKIHeaderBuilder setTransactionID(ASN1OctetString tid) {
        this.transactionID = tid;
        return this;
    }

    public PKIHeaderBuilder setSenderNonce(byte[] nonce) {
        return this.setSenderNonce((ASN1OctetString)(nonce == null ? null : this.createClonedOctetString(nonce)));
    }

    public PKIHeaderBuilder setSenderNonce(ASN1OctetString nonce) {
        this.senderNonce = nonce;
        return this;
    }

    public PKIHeaderBuilder setRecipNonce(byte[] nonce) {
        return this.setRecipNonce((ASN1OctetString)(nonce == null ? null : this.createClonedOctetString(nonce)));
    }

    public PKIHeaderBuilder setRecipNonce(ASN1OctetString nonce) {
        this.recipNonce = nonce;
        return this;
    }

    public PKIHeaderBuilder setFreeText(PKIFreeText text) {
        this.freeText = text;
        return this;
    }

    public PKIHeaderBuilder setGeneralInfo(InfoTypeAndValue genInfo) {
        return this.setGeneralInfo(PKIHeaderBuilder.makeGeneralInfoSeq(genInfo));
    }

    public PKIHeaderBuilder setGeneralInfo(InfoTypeAndValue[] genInfos) {
        return this.setGeneralInfo(PKIHeaderBuilder.makeGeneralInfoSeq(genInfos));
    }

    public PKIHeaderBuilder setGeneralInfo(ASN1Sequence seqOfInfoTypeAndValue) {
        this.generalInfo = seqOfInfoTypeAndValue;
        return this;
    }

    public PKIHeader build() {
        ASN1EncodableVector v = new ASN1EncodableVector(12);
        v.add((ASN1Encodable)this.pvno);
        v.add((ASN1Encodable)this.sender);
        v.add((ASN1Encodable)this.recipient);
        this.addOptional(v, 0, (ASN1Encodable)this.messageTime);
        this.addOptional(v, 1, (ASN1Encodable)this.protectionAlg);
        this.addOptional(v, 2, (ASN1Encodable)this.senderKID);
        this.addOptional(v, 3, (ASN1Encodable)this.recipKID);
        this.addOptional(v, 4, (ASN1Encodable)this.transactionID);
        this.addOptional(v, 5, (ASN1Encodable)this.senderNonce);
        this.addOptional(v, 6, (ASN1Encodable)this.recipNonce);
        this.addOptional(v, 7, (ASN1Encodable)this.freeText);
        this.addOptional(v, 8, (ASN1Encodable)this.generalInfo);
        this.messageTime = null;
        this.protectionAlg = null;
        this.senderKID = null;
        this.recipKID = null;
        this.transactionID = null;
        this.senderNonce = null;
        this.recipNonce = null;
        this.freeText = null;
        this.generalInfo = null;
        return PKIHeader.getInstance(new DERSequence(v));
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, tagNo, obj));
        }
    }

    private DEROctetString createClonedOctetString(byte[] value) {
        return new DEROctetString(Arrays.clone((byte[])value));
    }
}

