/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;

public class V2TBSCertListGenerator {
    private ASN1Integer version = new ASN1Integer(1L);
    private AlgorithmIdentifier signature;
    private X500Name issuer;
    private Time thisUpdate;
    private Time nextUpdate = null;
    private Extensions extensions = null;
    private ASN1EncodableVector crlentries = new ASN1EncodableVector();
    private static final ASN1Sequence[] reasons = new ASN1Sequence[11];

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(X500Name issuer) {
        this.issuer = issuer;
    }

    public void setThisUpdate(ASN1UTCTime thisUpdate) {
        this.thisUpdate = new Time(thisUpdate);
    }

    public void setNextUpdate(ASN1UTCTime nextUpdate) {
        this.nextUpdate = new Time(nextUpdate);
    }

    public void setThisUpdate(Time thisUpdate) {
        this.thisUpdate = thisUpdate;
    }

    public void setNextUpdate(Time nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public void addCRLEntry(ASN1Sequence crlEntry) {
        this.crlentries.add(crlEntry);
    }

    public void addCRLEntry(ASN1Integer userCertificate, ASN1UTCTime revocationDate, int reason) {
        this.addCRLEntry(userCertificate, new Time(revocationDate), reason);
    }

    public void addCRLEntry(ASN1Integer userCertificate, Time revocationDate, int reason) {
        this.addCRLEntry(userCertificate, revocationDate, reason, null);
    }

    public void addCRLEntry(ASN1Integer userCertificate, Time revocationDate, int reason, ASN1GeneralizedTime invalidityDate) {
        if (reason != 0) {
            ASN1EncodableVector v = new ASN1EncodableVector(2);
            if (reason < reasons.length) {
                if (reason < 0) {
                    throw new IllegalArgumentException("invalid reason value: " + reason);
                }
                v.add(reasons[reason]);
            } else {
                v.add(V2TBSCertListGenerator.createReasonExtension(reason));
            }
            if (invalidityDate != null) {
                v.add(V2TBSCertListGenerator.createInvalidityDateExtension(invalidityDate));
            }
            this.internalAddCRLEntry(userCertificate, revocationDate, new DERSequence(v));
        } else if (invalidityDate != null) {
            this.internalAddCRLEntry(userCertificate, revocationDate, new DERSequence(V2TBSCertListGenerator.createInvalidityDateExtension(invalidityDate)));
        } else {
            this.addCRLEntry(userCertificate, revocationDate, null);
        }
    }

    private void internalAddCRLEntry(ASN1Integer userCertificate, Time revocationDate, ASN1Sequence extensions) {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(userCertificate);
        v.add(revocationDate);
        if (extensions != null) {
            v.add(extensions);
        }
        this.addCRLEntry(new DERSequence(v));
    }

    public void addCRLEntry(ASN1Integer userCertificate, Time revocationDate, Extensions extensions) {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(userCertificate);
        v.add(revocationDate);
        if (extensions != null) {
            v.add(extensions);
        }
        this.addCRLEntry(new DERSequence(v));
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public TBSCertList generateTBSCertList() {
        if (this.signature == null || this.issuer == null || this.thisUpdate == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 TBSCertList generator");
        }
        return new TBSCertList(this.generateTBSCertStructure());
    }

    public ASN1Sequence generatePreTBSCertList() {
        if (this.signature != null) {
            throw new IllegalStateException("signature should not be set in PreTBSCertList generator");
        }
        if (this.issuer == null || this.thisUpdate == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 PreTBSCertList generator");
        }
        return this.generateTBSCertStructure();
    }

    private ASN1Sequence generateTBSCertStructure() {
        ASN1EncodableVector v = new ASN1EncodableVector(7);
        v.add(this.version);
        if (this.signature != null) {
            v.add(this.signature);
        }
        v.add(this.issuer);
        v.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            v.add(this.nextUpdate);
        }
        if (this.crlentries.size() != 0) {
            v.add(new DERSequence(this.crlentries));
        }
        if (this.extensions != null) {
            v.add(new DERTaggedObject(0, this.extensions));
        }
        return new DERSequence(v);
    }

    private static ASN1Sequence createReasonExtension(int reasonCode) {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        CRLReason crlReason = CRLReason.lookup(reasonCode);
        try {
            v.add(Extension.reasonCode);
            v.add(new DEROctetString(crlReason.getEncoded()));
        }
        catch (IOException e) {
            throw new IllegalArgumentException("error encoding reason: " + e);
        }
        return new DERSequence(v);
    }

    private static ASN1Sequence createInvalidityDateExtension(ASN1GeneralizedTime invalidityDate) {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        try {
            v.add(Extension.invalidityDate);
            v.add(new DEROctetString(invalidityDate.getEncoded()));
        }
        catch (IOException e) {
            throw new IllegalArgumentException("error encoding reason: " + e);
        }
        return new DERSequence(v);
    }

    static {
        V2TBSCertListGenerator.reasons[0] = V2TBSCertListGenerator.createReasonExtension(0);
        V2TBSCertListGenerator.reasons[1] = V2TBSCertListGenerator.createReasonExtension(1);
        V2TBSCertListGenerator.reasons[2] = V2TBSCertListGenerator.createReasonExtension(2);
        V2TBSCertListGenerator.reasons[3] = V2TBSCertListGenerator.createReasonExtension(3);
        V2TBSCertListGenerator.reasons[4] = V2TBSCertListGenerator.createReasonExtension(4);
        V2TBSCertListGenerator.reasons[5] = V2TBSCertListGenerator.createReasonExtension(5);
        V2TBSCertListGenerator.reasons[6] = V2TBSCertListGenerator.createReasonExtension(6);
        V2TBSCertListGenerator.reasons[7] = V2TBSCertListGenerator.createReasonExtension(7);
        V2TBSCertListGenerator.reasons[8] = V2TBSCertListGenerator.createReasonExtension(8);
        V2TBSCertListGenerator.reasons[9] = V2TBSCertListGenerator.createReasonExtension(9);
        V2TBSCertListGenerator.reasons[10] = V2TBSCertListGenerator.createReasonExtension(10);
    }
}

