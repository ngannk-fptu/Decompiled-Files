/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.bouncycastle.util.Strings;

public class DistributionPoint
extends ASN1Object {
    DistributionPointName distributionPoint;
    ReasonFlags reasons;
    GeneralNames cRLIssuer;

    public static DistributionPoint getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DistributionPoint.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static DistributionPoint getInstance(Object obj) {
        if (obj == null || obj instanceof DistributionPoint) {
            return (DistributionPoint)obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new DistributionPoint((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("Invalid DistributionPoint: " + obj.getClass().getName());
    }

    public DistributionPoint(ASN1Sequence seq) {
        block5: for (int i = 0; i != seq.size(); ++i) {
            ASN1TaggedObject t = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            switch (t.getTagNo()) {
                case 0: {
                    this.distributionPoint = DistributionPointName.getInstance(t, true);
                    continue block5;
                }
                case 1: {
                    this.reasons = new ReasonFlags(ASN1BitString.getInstance(t, false));
                    continue block5;
                }
                case 2: {
                    this.cRLIssuer = GeneralNames.getInstance(t, false);
                    continue block5;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag encountered in structure: " + t.getTagNo());
                }
            }
        }
    }

    public DistributionPoint(DistributionPointName distributionPoint, ReasonFlags reasons, GeneralNames cRLIssuer) {
        this.distributionPoint = distributionPoint;
        this.reasons = reasons;
        this.cRLIssuer = cRLIssuer;
    }

    public DistributionPointName getDistributionPoint() {
        return this.distributionPoint;
    }

    public ReasonFlags getReasons() {
        return this.reasons;
    }

    public GeneralNames getCRLIssuer() {
        return this.cRLIssuer;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.distributionPoint != null) {
            v.add(new DERTaggedObject(0, this.distributionPoint));
        }
        if (this.reasons != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.reasons));
        }
        if (this.cRLIssuer != null) {
            v.add(new DERTaggedObject(false, 2, (ASN1Encodable)this.cRLIssuer));
        }
        return new DERSequence(v);
    }

    public String toString() {
        String sep = Strings.lineSeparator();
        StringBuffer buf = new StringBuffer();
        buf.append("DistributionPoint: [");
        buf.append(sep);
        if (this.distributionPoint != null) {
            this.appendObject(buf, sep, "distributionPoint", this.distributionPoint.toString());
        }
        if (this.reasons != null) {
            this.appendObject(buf, sep, "reasons", this.reasons.toString());
        }
        if (this.cRLIssuer != null) {
            this.appendObject(buf, sep, "cRLIssuer", this.cRLIssuer.toString());
        }
        buf.append("]");
        buf.append(sep);
        return buf.toString();
    }

    private void appendObject(StringBuffer buf, String sep, String name, String value) {
        String indent = "    ";
        buf.append(indent);
        buf.append(name);
        buf.append(":");
        buf.append(sep);
        buf.append(indent);
        buf.append(indent);
        buf.append(value);
        buf.append(sep);
    }
}

