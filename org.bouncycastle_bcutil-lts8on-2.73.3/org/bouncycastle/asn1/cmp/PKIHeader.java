/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class PKIHeader
extends ASN1Object {
    public static final GeneralName NULL_NAME = new GeneralName(X500Name.getInstance((Object)new DERSequence()));
    public static final int CMP_1999 = 1;
    public static final int CMP_2000 = 2;
    public static final int CMP_2021 = 3;
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

    private PKIHeader(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.pvno = ASN1Integer.getInstance(en.nextElement());
        this.sender = GeneralName.getInstance(en.nextElement());
        this.recipient = GeneralName.getInstance(en.nextElement());
        block11: while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject)en.nextElement();
            if (!tObj.hasContextTag()) {
                throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText((ASN1TaggedObject)tObj));
            }
            switch (tObj.getTagNo()) {
                case 0: {
                    this.messageTime = ASN1GeneralizedTime.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
                case 1: {
                    this.protectionAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
                case 2: {
                    this.senderKID = ASN1OctetString.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
                case 3: {
                    this.recipKID = ASN1OctetString.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
                case 4: {
                    this.transactionID = ASN1OctetString.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
                case 5: {
                    this.senderNonce = ASN1OctetString.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
                case 6: {
                    this.recipNonce = ASN1OctetString.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
                case 7: {
                    this.freeText = PKIFreeText.getInstance(tObj, true);
                    continue block11;
                }
                case 8: {
                    this.generalInfo = ASN1Sequence.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block11;
                }
            }
            throw new IllegalArgumentException("unknown tag number: " + tObj.getTagNo());
        }
    }

    public PKIHeader(int pvno, GeneralName sender, GeneralName recipient) {
        this(new ASN1Integer((long)pvno), sender, recipient);
    }

    private PKIHeader(ASN1Integer pvno, GeneralName sender, GeneralName recipient) {
        this.pvno = pvno;
        this.sender = sender;
        this.recipient = recipient;
    }

    public static PKIHeader getInstance(Object o) {
        if (o instanceof PKIHeader) {
            return (PKIHeader)((Object)o);
        }
        if (o != null) {
            return new PKIHeader(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Integer getPvno() {
        return this.pvno;
    }

    public GeneralName getSender() {
        return this.sender;
    }

    public GeneralName getRecipient() {
        return this.recipient;
    }

    public ASN1GeneralizedTime getMessageTime() {
        return this.messageTime;
    }

    public AlgorithmIdentifier getProtectionAlg() {
        return this.protectionAlg;
    }

    public ASN1OctetString getSenderKID() {
        return this.senderKID;
    }

    public ASN1OctetString getRecipKID() {
        return this.recipKID;
    }

    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }

    public ASN1OctetString getSenderNonce() {
        return this.senderNonce;
    }

    public ASN1OctetString getRecipNonce() {
        return this.recipNonce;
    }

    public PKIFreeText getFreeText() {
        return this.freeText;
    }

    public InfoTypeAndValue[] getGeneralInfo() {
        if (this.generalInfo == null) {
            return null;
        }
        InfoTypeAndValue[] results = new InfoTypeAndValue[this.generalInfo.size()];
        for (int i = 0; i < results.length; ++i) {
            results[i] = InfoTypeAndValue.getInstance(this.generalInfo.getObjectAt(i));
        }
        return results;
    }

    public ASN1Primitive toASN1Primitive() {
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
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, tagNo, obj));
        }
    }
}

