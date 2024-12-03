/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ServiceSpecificPermissions;

public class PsidSsp
extends ASN1Object {
    private final Psid psid;
    private final ServiceSpecificPermissions ssp;

    public PsidSsp(Psid psid, ServiceSpecificPermissions ssp) {
        this.psid = psid;
        this.ssp = ssp;
    }

    private PsidSsp(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.psid = Psid.getInstance(seq.getObjectAt(0));
        this.ssp = OEROptional.getValue(ServiceSpecificPermissions.class, seq.getObjectAt(1));
    }

    public static PsidSsp getInstance(Object nextElement) {
        if (nextElement instanceof PsidSsp) {
            return (PsidSsp)((Object)nextElement);
        }
        if (nextElement != null) {
            return new PsidSsp(ASN1Sequence.getInstance((Object)nextElement));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Psid getPsid() {
        return this.psid;
    }

    public ServiceSpecificPermissions getSsp() {
        return this.ssp;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.psid, OEROptional.getInstance((Object)this.ssp)});
    }

    public static class Builder {
        private Psid psid;
        private ServiceSpecificPermissions ssp;

        public Builder setPsid(Psid psid) {
            this.psid = psid;
            return this;
        }

        public Builder setSsp(ServiceSpecificPermissions ssp) {
            this.ssp = ssp;
            return this;
        }

        public PsidSsp createPsidSsp() {
            return new PsidSsp(this.psid, this.ssp);
        }
    }
}

