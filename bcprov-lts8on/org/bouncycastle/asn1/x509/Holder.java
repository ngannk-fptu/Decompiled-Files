/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;

public class Holder
extends ASN1Object {
    public static final int V1_CERTIFICATE_HOLDER = 0;
    public static final int V2_CERTIFICATE_HOLDER = 1;
    IssuerSerial baseCertificateID;
    GeneralNames entityName;
    ObjectDigestInfo objectDigestInfo;
    private int version = 1;

    public static Holder getInstance(Object obj) {
        if (obj instanceof Holder) {
            return (Holder)obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new Holder(ASN1TaggedObject.getInstance(obj));
        }
        if (obj != null) {
            return new Holder(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private Holder(ASN1TaggedObject tagObj) {
        switch (tagObj.getTagNo()) {
            case 0: {
                this.baseCertificateID = IssuerSerial.getInstance(tagObj, true);
                break;
            }
            case 1: {
                this.entityName = GeneralNames.getInstance(tagObj, true);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag in Holder");
            }
        }
        this.version = 0;
    }

    private Holder(ASN1Sequence seq) {
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        block5: for (int i = 0; i != seq.size(); ++i) {
            ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            switch (tObj.getTagNo()) {
                case 0: {
                    this.baseCertificateID = IssuerSerial.getInstance(tObj, false);
                    continue block5;
                }
                case 1: {
                    this.entityName = GeneralNames.getInstance(tObj, false);
                    continue block5;
                }
                case 2: {
                    this.objectDigestInfo = ObjectDigestInfo.getInstance(tObj, false);
                    continue block5;
                }
                default: {
                    throw new IllegalArgumentException("unknown tag in Holder");
                }
            }
        }
        this.version = 1;
    }

    public Holder(IssuerSerial baseCertificateID) {
        this(baseCertificateID, 1);
    }

    public Holder(IssuerSerial baseCertificateID, int version) {
        this.baseCertificateID = baseCertificateID;
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    public Holder(GeneralNames entityName) {
        this(entityName, 1);
    }

    public Holder(GeneralNames entityName, int version) {
        this.entityName = entityName;
        this.version = version;
    }

    public Holder(ObjectDigestInfo objectDigestInfo) {
        this.objectDigestInfo = objectDigestInfo;
    }

    public IssuerSerial getBaseCertificateID() {
        return this.baseCertificateID;
    }

    public GeneralNames getEntityName() {
        return this.entityName;
    }

    public ObjectDigestInfo getObjectDigestInfo() {
        return this.objectDigestInfo;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.version == 1) {
            ASN1EncodableVector v = new ASN1EncodableVector(3);
            if (this.baseCertificateID != null) {
                v.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.baseCertificateID));
            }
            if (this.entityName != null) {
                v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.entityName));
            }
            if (this.objectDigestInfo != null) {
                v.add(new DERTaggedObject(false, 2, (ASN1Encodable)this.objectDigestInfo));
            }
            return new DERSequence(v);
        }
        if (this.entityName != null) {
            return new DERTaggedObject(true, 1, (ASN1Encodable)this.entityName);
        }
        return new DERTaggedObject(true, 0, (ASN1Encodable)this.baseCertificateID);
    }
}

