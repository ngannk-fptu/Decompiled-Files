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
package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941CrlRequest;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941DeltaCtlRequest;
import org.bouncycastle.oer.its.etsi103097.extension.ExtId;

public class Extension
extends ASN1Object {
    public static final ExtId etsiTs102941CrlRequestId = new ExtId(1L);
    public static final ExtId etsiTs102941DeltaCtlRequestId = new ExtId(2L);
    private final ExtId id;
    private final ASN1Encodable content;

    protected Extension(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.id = ExtId.getInstance(sequence.getObjectAt(0));
        if (this.id.equals((Object)etsiTs102941CrlRequestId)) {
            this.content = EtsiTs102941CrlRequest.getInstance(sequence.getObjectAt(1));
        } else if (this.id.equals((Object)etsiTs102941DeltaCtlRequestId)) {
            this.content = EtsiTs102941DeltaCtlRequest.getInstance(sequence.getObjectAt(1));
        } else {
            throw new IllegalArgumentException("id not 1 (EtsiTs102941CrlRequest) or 2 (EtsiTs102941DeltaCtlRequest)");
        }
    }

    public Extension(ExtId id, ASN1Encodable content) {
        this.id = id;
        if (id.getExtId().intValue() != 1 && id.getExtId().intValue() != 2) {
            throw new IllegalArgumentException("id not 1 (EtsiTs102941CrlRequest) or 2 (EtsiTs102941DeltaCtlRequest)");
        }
        this.content = content;
    }

    public static Extension etsiTs102941CrlRequest(EtsiTs102941CrlRequest request) {
        return new Extension(etsiTs102941CrlRequestId, (ASN1Encodable)request);
    }

    public static Extension etsiTs102941DeltaCtlRequest(EtsiTs102941DeltaCtlRequest request) {
        return new Extension(etsiTs102941DeltaCtlRequestId, (ASN1Encodable)request);
    }

    public static Extension getInstance(Object o) {
        if (o instanceof Extension) {
            return (Extension)((Object)o);
        }
        if (o != null) {
            return new Extension(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.id, this.content});
    }

    public ExtId getId() {
        return this.id;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }
}

