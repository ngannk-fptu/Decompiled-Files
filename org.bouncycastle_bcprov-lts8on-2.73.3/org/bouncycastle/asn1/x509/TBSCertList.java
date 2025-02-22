/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.Time;

public class TBSCertList
extends ASN1Object {
    ASN1Integer version;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time thisUpdate;
    Time nextUpdate;
    ASN1Sequence revokedCertificates;
    Extensions crlExtensions;

    public static TBSCertList getInstance(ASN1TaggedObject obj, boolean explicit) {
        return TBSCertList.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static TBSCertList getInstance(Object obj) {
        if (obj instanceof TBSCertList) {
            return (TBSCertList)obj;
        }
        if (obj != null) {
            return new TBSCertList(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TBSCertList(ASN1Sequence seq) {
        if (seq.size() < 3 || seq.size() > 7) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int seqPos = 0;
        this.version = seq.getObjectAt(seqPos) instanceof ASN1Integer ? ASN1Integer.getInstance(seq.getObjectAt(seqPos++)) : null;
        this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqPos++));
        this.issuer = X500Name.getInstance(seq.getObjectAt(seqPos++));
        this.thisUpdate = Time.getInstance(seq.getObjectAt(seqPos++));
        if (seqPos < seq.size() && (seq.getObjectAt(seqPos) instanceof ASN1UTCTime || seq.getObjectAt(seqPos) instanceof ASN1GeneralizedTime || seq.getObjectAt(seqPos) instanceof Time)) {
            this.nextUpdate = Time.getInstance(seq.getObjectAt(seqPos++));
        }
        if (seqPos < seq.size() && !(seq.getObjectAt(seqPos) instanceof ASN1TaggedObject)) {
            this.revokedCertificates = ASN1Sequence.getInstance(seq.getObjectAt(seqPos++));
        }
        if (seqPos < seq.size() && seq.getObjectAt(seqPos) instanceof ASN1TaggedObject) {
            this.crlExtensions = Extensions.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)seq.getObjectAt(seqPos), true));
        }
    }

    public int getVersionNumber() {
        if (this.version == null) {
            return 1;
        }
        return this.version.intValueExact() + 1;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public Time getThisUpdate() {
        return this.thisUpdate;
    }

    public Time getNextUpdate() {
        return this.nextUpdate;
    }

    public CRLEntry[] getRevokedCertificates() {
        if (this.revokedCertificates == null) {
            return new CRLEntry[0];
        }
        CRLEntry[] entries = new CRLEntry[this.revokedCertificates.size()];
        for (int i = 0; i < entries.length; ++i) {
            entries[i] = CRLEntry.getInstance(this.revokedCertificates.getObjectAt(i));
        }
        return entries;
    }

    public Enumeration getRevokedCertificateEnumeration() {
        if (this.revokedCertificates == null) {
            return new EmptyEnumeration();
        }
        return new RevokedCertificatesEnumeration(this.revokedCertificates.getObjects());
    }

    public Extensions getExtensions() {
        return this.crlExtensions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(7);
        if (this.version != null) {
            v.add(this.version);
        }
        v.add(this.signature);
        v.add(this.issuer);
        v.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            v.add(this.nextUpdate);
        }
        if (this.revokedCertificates != null) {
            v.add(this.revokedCertificates);
        }
        if (this.crlExtensions != null) {
            v.add(new DERTaggedObject(0, this.crlExtensions));
        }
        return new DERSequence(v);
    }

    public static class CRLEntry
    extends ASN1Object {
        ASN1Sequence seq;
        Extensions crlEntryExtensions;

        private CRLEntry(ASN1Sequence seq) {
            if (seq.size() < 2 || seq.size() > 3) {
                throw new IllegalArgumentException("Bad sequence size: " + seq.size());
            }
            this.seq = seq;
        }

        public static CRLEntry getInstance(Object o) {
            if (o instanceof CRLEntry) {
                return (CRLEntry)o;
            }
            if (o != null) {
                return new CRLEntry(ASN1Sequence.getInstance(o));
            }
            return null;
        }

        public ASN1Integer getUserCertificate() {
            return ASN1Integer.getInstance(this.seq.getObjectAt(0));
        }

        public Time getRevocationDate() {
            return Time.getInstance(this.seq.getObjectAt(1));
        }

        public Extensions getExtensions() {
            if (this.crlEntryExtensions == null && this.seq.size() == 3) {
                this.crlEntryExtensions = Extensions.getInstance(this.seq.getObjectAt(2));
            }
            return this.crlEntryExtensions;
        }

        @Override
        public ASN1Primitive toASN1Primitive() {
            return this.seq;
        }

        public boolean hasExtensions() {
            return this.seq.size() == 3;
        }
    }

    private static class EmptyEnumeration
    implements Enumeration {
        private EmptyEnumeration() {
        }

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        public Object nextElement() {
            throw new NoSuchElementException("Empty Enumeration");
        }
    }

    private static class RevokedCertificatesEnumeration
    implements Enumeration {
        private final Enumeration en;

        RevokedCertificatesEnumeration(Enumeration en) {
            this.en = en;
        }

        @Override
        public boolean hasMoreElements() {
            return this.en.hasMoreElements();
        }

        public Object nextElement() {
            return CRLEntry.getInstance(this.en.nextElement());
        }
    }
}

