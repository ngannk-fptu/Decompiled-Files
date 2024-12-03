/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class SignerIdentifier
extends ASN1Object
implements ASN1Choice {
    public static final int digest = 0;
    public static final int certificate = 1;
    public static final int self = 2;
    private final int choice;
    private final ASN1Encodable signerIdentifier;

    public SignerIdentifier(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.signerIdentifier = value;
    }

    private SignerIdentifier(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.signerIdentifier = HashedId8.getInstance(ato.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.signerIdentifier = SequenceOfCertificate.getInstance(ato.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.signerIdentifier = DERNull.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static SignerIdentifier getInstance(Object src) {
        if (src instanceof SignerIdentifier) {
            return (SignerIdentifier)((Object)src);
        }
        if (src != null) {
            return new SignerIdentifier(ASN1TaggedObject.getInstance((Object)src, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public static SignerIdentifier digest(HashedId8 id) {
        return new SignerIdentifier(0, (ASN1Encodable)id);
    }

    public static SignerIdentifier certificate(SequenceOfCertificate sequenceOfCertificate) {
        return new SignerIdentifier(1, (ASN1Encodable)sequenceOfCertificate);
    }

    public static SignerIdentifier self() {
        return new SignerIdentifier(2, (ASN1Encodable)DERNull.INSTANCE);
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.signerIdentifier);
    }

    public ASN1Encodable getSignerIdentifier() {
        return this.signerIdentifier;
    }
}

