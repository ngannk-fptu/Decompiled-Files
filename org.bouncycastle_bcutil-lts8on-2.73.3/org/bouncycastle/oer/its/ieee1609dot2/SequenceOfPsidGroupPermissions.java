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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.PsidGroupPermissions;

public class SequenceOfPsidGroupPermissions
extends ASN1Object {
    private final List<PsidGroupPermissions> psidGroupPermissions;

    public SequenceOfPsidGroupPermissions(List<PsidGroupPermissions> groupPermissions) {
        this.psidGroupPermissions = Collections.unmodifiableList(groupPermissions);
    }

    private SequenceOfPsidGroupPermissions(ASN1Sequence seq) {
        ArrayList<PsidGroupPermissions> l = new ArrayList<PsidGroupPermissions>();
        Iterator it = seq.iterator();
        while (it.hasNext()) {
            l.add(PsidGroupPermissions.getInstance(it.next()));
        }
        this.psidGroupPermissions = Collections.unmodifiableList(l);
    }

    public static SequenceOfPsidGroupPermissions getInstance(Object obj) {
        if (obj instanceof SequenceOfPsidGroupPermissions) {
            return (SequenceOfPsidGroupPermissions)((Object)obj);
        }
        if (obj != null) {
            return new SequenceOfPsidGroupPermissions(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public List<PsidGroupPermissions> getPsidGroupPermissions() {
        return this.psidGroupPermissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[])this.psidGroupPermissions.toArray(new PsidGroupPermissions[0]));
    }

    public static class Builder {
        private final List<PsidGroupPermissions> groupPermissions = new ArrayList<PsidGroupPermissions>();

        public Builder setGroupPermissions(List<PsidGroupPermissions> groupPermissions) {
            this.groupPermissions.addAll(groupPermissions);
            return this;
        }

        public Builder addGroupPermission(PsidGroupPermissions ... permissions) {
            this.groupPermissions.addAll(Arrays.asList(permissions));
            return this;
        }

        public SequenceOfPsidGroupPermissions createSequenceOfPsidGroupPermissions() {
            return new SequenceOfPsidGroupPermissions(this.groupPermissions);
        }
    }
}

