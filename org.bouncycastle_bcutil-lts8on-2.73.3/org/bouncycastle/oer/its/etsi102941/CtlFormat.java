/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.DeltaCtl;
import org.bouncycastle.oer.its.etsi102941.FullCtl;
import org.bouncycastle.oer.its.etsi102941.SequenceOfCtlCommand;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedRcaCtl;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CtlFormat
extends ASN1Object {
    private final Version version;
    private final Time32 nextUpdate;
    private final ASN1Boolean isFullCtl;
    private final UINT8 ctlSequence;
    private final SequenceOfCtlCommand ctlCommands;

    public CtlFormat(Version version, Time32 nextUpdate, ASN1Boolean isFullCtl, UINT8 ctlSequence, SequenceOfCtlCommand ctlCommands) {
        this.version = version;
        this.nextUpdate = nextUpdate;
        this.isFullCtl = isFullCtl;
        this.ctlSequence = ctlSequence;
        this.ctlCommands = ctlCommands;
    }

    protected CtlFormat(ASN1Sequence seq) {
        if (seq.size() != 5) {
            throw new IllegalArgumentException("expected sequence size of 5");
        }
        this.version = Version.getInstance(seq.getObjectAt(0));
        this.nextUpdate = Time32.getInstance(seq.getObjectAt(1));
        this.isFullCtl = ASN1Boolean.getInstance((Object)seq.getObjectAt(2));
        this.ctlSequence = UINT8.getInstance(seq.getObjectAt(3));
        this.ctlCommands = SequenceOfCtlCommand.getInstance(seq.getObjectAt(4));
    }

    public static CtlFormat getInstance(Object o) {
        if (o instanceof CtlFormat) {
            return (CtlFormat)((Object)o);
        }
        if (o != null) {
            return new CtlFormat(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public Version getVersion() {
        return this.version;
    }

    public Time32 getNextUpdate() {
        return this.nextUpdate;
    }

    public ASN1Boolean getIsFullCtl() {
        return this.isFullCtl;
    }

    public UINT8 getCtlSequence() {
        return this.ctlSequence;
    }

    public SequenceOfCtlCommand getCtlCommands() {
        return this.ctlCommands;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Version version;
        private Time32 nextUpdate;
        private ASN1Boolean isFullCtl;
        private UINT8 ctlSequence;
        private SequenceOfCtlCommand ctlCommands;

        public Builder setVersion(Version version) {
            this.version = version;
            return this;
        }

        public Builder setNextUpdate(Time32 nextUpdate) {
            this.nextUpdate = nextUpdate;
            return this;
        }

        public Builder setIsFullCtl(ASN1Boolean isFullCtl) {
            this.isFullCtl = isFullCtl;
            return this;
        }

        public Builder setCtlSequence(UINT8 ctlSequence) {
            this.ctlSequence = ctlSequence;
            return this;
        }

        public Builder setCtlSequence(ASN1Integer ctlSequence) {
            this.ctlSequence = new UINT8(ctlSequence.getValue());
            return this;
        }

        public Builder setCtlCommands(SequenceOfCtlCommand ctlCommands) {
            this.ctlCommands = ctlCommands;
            return this;
        }

        public CtlFormat createCtlFormat() {
            return new CtlFormat(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
        }

        public DeltaCtl createDeltaCtl() {
            if (this.isFullCtl != null && ASN1Boolean.TRUE.equals((ASN1Primitive)this.isFullCtl)) {
                throw new IllegalArgumentException("isFullCtl must be false for DeltaCtl");
            }
            return new DeltaCtl(this.version, this.nextUpdate, this.ctlSequence, this.ctlCommands);
        }

        public FullCtl createFullCtl() {
            return new FullCtl(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
        }

        public ToBeSignedRcaCtl createToBeSignedRcaCtl() {
            return new ToBeSignedRcaCtl(this.version, this.nextUpdate, this.isFullCtl, this.ctlSequence, this.ctlCommands);
        }
    }
}

