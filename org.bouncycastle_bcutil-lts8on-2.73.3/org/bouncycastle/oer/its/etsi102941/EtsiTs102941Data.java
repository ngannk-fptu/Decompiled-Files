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
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.EtsiTs102941DataContent;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;

public class EtsiTs102941Data
extends ASN1Object {
    private final Version version;
    private final EtsiTs102941DataContent content;

    public EtsiTs102941Data(Version version, EtsiTs102941DataContent content) {
        this.version = version;
        this.content = content;
    }

    private EtsiTs102941Data(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.version = Version.getInstance(sequence.getObjectAt(0));
        this.content = EtsiTs102941DataContent.getInstance(sequence.getObjectAt(1));
    }

    public static EtsiTs102941Data getInstance(Object o) {
        if (o instanceof EtsiTs102941Data) {
            return (EtsiTs102941Data)((Object)o);
        }
        if (o != null) {
            if (o instanceof Opaque) {
                return new EtsiTs102941Data(ASN1Sequence.getInstance((Object)((Opaque)((Object)o)).getContent()));
            }
            return new EtsiTs102941Data(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public Version getVersion() {
        return this.version;
    }

    public EtsiTs102941DataContent getContent() {
        return this.content;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.version, this.content});
    }
}

