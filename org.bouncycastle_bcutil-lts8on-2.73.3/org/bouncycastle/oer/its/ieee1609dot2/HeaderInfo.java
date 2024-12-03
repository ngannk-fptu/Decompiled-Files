/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.ContributedExtensionBlocks;
import org.bouncycastle.oer.its.ieee1609dot2.MissingCrlIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.PduFunctionalType;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfHashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ThreeDLocation;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;

public class HeaderInfo
extends ASN1Object {
    private final Psid psid;
    private final Time64 generationTime;
    private final Time64 expiryTime;
    private final ThreeDLocation generationLocation;
    private final HashedId3 p2pcdLearningRequest;
    private final MissingCrlIdentifier missingCrlIdentifier;
    private final EncryptionKey encryptionKey;
    private final SequenceOfHashedId3 inlineP2pcdRequest;
    private final Certificate requestedCertificate;
    private final PduFunctionalType pduFunctionalType;
    private final ContributedExtensionBlocks contributedExtensions;

    private HeaderInfo(ASN1Sequence sequence) {
        if (sequence.size() != 11 && sequence.size() != 7) {
            throw new IllegalArgumentException("expected sequence size of 11 or 7");
        }
        Iterator it = sequence.iterator();
        this.psid = Psid.getInstance(it.next());
        this.generationTime = OEROptional.getValue(Time64.class, it.next());
        this.expiryTime = OEROptional.getValue(Time64.class, it.next());
        this.generationLocation = OEROptional.getValue(ThreeDLocation.class, it.next());
        this.p2pcdLearningRequest = OEROptional.getValue(HashedId3.class, it.next());
        this.missingCrlIdentifier = OEROptional.getValue(MissingCrlIdentifier.class, it.next());
        this.encryptionKey = OEROptional.getValue(EncryptionKey.class, it.next());
        if (sequence.size() > 7) {
            this.inlineP2pcdRequest = OEROptional.getValue(SequenceOfHashedId3.class, it.next());
            this.requestedCertificate = OEROptional.getValue(Certificate.class, it.next());
            this.pduFunctionalType = OEROptional.getValue(PduFunctionalType.class, it.next());
            this.contributedExtensions = OEROptional.getValue(ContributedExtensionBlocks.class, it.next());
        } else {
            this.inlineP2pcdRequest = null;
            this.requestedCertificate = null;
            this.pduFunctionalType = null;
            this.contributedExtensions = null;
        }
    }

    public HeaderInfo(Psid psid, Time64 generationTime, Time64 expiryTime, ThreeDLocation generationLocation, HashedId3 p2pcdLearningRequest, MissingCrlIdentifier missingCrlIdentifier, EncryptionKey encryptionKey, SequenceOfHashedId3 inlineP2pcdRequest, Certificate requestedCertificate, PduFunctionalType pduFunctionalType, ContributedExtensionBlocks contributedExtensions) {
        this.psid = psid;
        this.generationTime = generationTime;
        this.expiryTime = expiryTime;
        this.generationLocation = generationLocation;
        this.p2pcdLearningRequest = p2pcdLearningRequest;
        this.missingCrlIdentifier = missingCrlIdentifier;
        this.encryptionKey = encryptionKey;
        this.inlineP2pcdRequest = inlineP2pcdRequest;
        this.requestedCertificate = requestedCertificate;
        this.pduFunctionalType = pduFunctionalType;
        this.contributedExtensions = contributedExtensions;
    }

    public static HeaderInfo getInstance(Object o) {
        if (o instanceof HeaderInfo) {
            return (HeaderInfo)((Object)o);
        }
        if (o != null) {
            return new HeaderInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public Psid getPsid() {
        return this.psid;
    }

    public Time64 getGenerationTime() {
        return this.generationTime;
    }

    public Time64 getExpiryTime() {
        return this.expiryTime;
    }

    public ThreeDLocation getGenerationLocation() {
        return this.generationLocation;
    }

    public HashedId3 getP2pcdLearningRequest() {
        return this.p2pcdLearningRequest;
    }

    public MissingCrlIdentifier getMissingCrlIdentifier() {
        return this.missingCrlIdentifier;
    }

    public EncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public SequenceOfHashedId3 getInlineP2pcdRequest() {
        return this.inlineP2pcdRequest;
    }

    public Certificate getRequestedCertificate() {
        return this.requestedCertificate;
    }

    public PduFunctionalType getPduFunctionalType() {
        return this.pduFunctionalType;
    }

    public ContributedExtensionBlocks getContributedExtensions() {
        return this.contributedExtensions;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.psid, OEROptional.getInstance((Object)this.generationTime), OEROptional.getInstance((Object)this.expiryTime), OEROptional.getInstance((Object)this.generationLocation), OEROptional.getInstance((Object)this.p2pcdLearningRequest), OEROptional.getInstance((Object)this.missingCrlIdentifier), OEROptional.getInstance((Object)this.encryptionKey), OEROptional.getInstance((Object)this.inlineP2pcdRequest), OEROptional.getInstance((Object)this.requestedCertificate), OEROptional.getInstance((Object)this.pduFunctionalType), OEROptional.getInstance((Object)this.contributedExtensions)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Psid psid;
        private Time64 generationTime;
        private Time64 expiryTime;
        private ThreeDLocation generationLocation;
        private HashedId3 p2pcdLearningRequest;
        private MissingCrlIdentifier missingCrlIdentifier;
        private EncryptionKey encryptionKey;
        private SequenceOfHashedId3 inlineP2pcdRequest;
        private Certificate requestedCertificate;
        private PduFunctionalType pduFunctionalType;
        private ContributedExtensionBlocks contributedExtensions;

        public Builder setPsid(Psid psid) {
            this.psid = psid;
            return this;
        }

        public Builder setGenerationTime(Time64 generationTime) {
            this.generationTime = generationTime;
            return this;
        }

        public Builder setExpiryTime(Time64 expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public Builder setGenerationLocation(ThreeDLocation generationLocation) {
            this.generationLocation = generationLocation;
            return this;
        }

        public Builder setP2pcdLearningRequest(HashedId3 p2pcdLearningRequest) {
            this.p2pcdLearningRequest = p2pcdLearningRequest;
            return this;
        }

        public Builder setEncryptionKey(EncryptionKey encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        public Builder setMissingCrlIdentifier(MissingCrlIdentifier missingCrlIdentifier) {
            this.missingCrlIdentifier = missingCrlIdentifier;
            return this;
        }

        public Builder setInlineP2pcdRequest(SequenceOfHashedId3 inlineP2pcdRequest) {
            this.inlineP2pcdRequest = inlineP2pcdRequest;
            return this;
        }

        public Builder setRequestedCertificate(Certificate requestedCertificate) {
            this.requestedCertificate = requestedCertificate;
            return this;
        }

        public Builder setPduFunctionalType(PduFunctionalType pduFunctionalType) {
            this.pduFunctionalType = pduFunctionalType;
            return this;
        }

        public Builder setContributedExtensions(ContributedExtensionBlocks contributedExtensions) {
            this.contributedExtensions = contributedExtensions;
            return this;
        }

        public HeaderInfo createHeaderInfo() {
            return new HeaderInfo(this.psid, this.generationTime, this.expiryTime, this.generationLocation, this.p2pcdLearningRequest, this.missingCrlIdentifier, this.encryptionKey, this.inlineP2pcdRequest, this.requestedCertificate, this.pduFunctionalType, this.contributedExtensions);
        }
    }
}

