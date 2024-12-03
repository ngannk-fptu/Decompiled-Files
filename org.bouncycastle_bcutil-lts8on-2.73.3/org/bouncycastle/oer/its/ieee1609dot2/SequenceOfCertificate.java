/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;

public class SequenceOfCertificate
extends ASN1Object {
    private final List<Certificate> certificates;

    public SequenceOfCertificate(List<Certificate> certificates) {
        this.certificates = Collections.unmodifiableList(certificates);
    }

    private SequenceOfCertificate(ASN1Sequence sequence) {
        Iterator seq = sequence.iterator();
        ArrayList<Certificate> certificates = new ArrayList<Certificate>();
        while (seq.hasNext()) {
            certificates.add(Certificate.getInstance(seq.next()));
        }
        this.certificates = Collections.unmodifiableList(certificates);
    }

    public static SequenceOfCertificate getInstance(Object src) {
        if (src instanceof SequenceOfCertificate) {
            return (SequenceOfCertificate)((Object)src);
        }
        if (src != null) {
            return new SequenceOfCertificate(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.certificates);
    }

    public List<Certificate> getCertificates() {
        return this.certificates;
    }

    public static class Builder {
        List<Certificate> certificates = new ArrayList<Certificate>();

        public Builder add(Certificate ... certificates) {
            this.certificates.addAll(Arrays.asList(certificates));
            return this;
        }

        public SequenceOfCertificate build() {
            return new SequenceOfCertificate(this.certificates);
        }
    }
}

