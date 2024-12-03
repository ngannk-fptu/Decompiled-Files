/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedExternalPayload;

public class EcSignature
extends ASN1Object
implements ASN1Choice {
    public static final int encryptedEcSignature = 0;
    public static final int ecSignature = 1;
    private final int choice;
    private final ASN1Encodable _ecSignature;

    public EcSignature(int choice, ASN1Encodable ecSignature) {
        this.choice = choice;
        this._ecSignature = ecSignature;
    }

    private EcSignature(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this._ecSignature = EtsiTs103097DataEncrypted.getInstance(ato.getExplicitBaseObject());
                return;
            }
            case 1: {
                this._ecSignature = EtsiTs103097DataSignedExternalPayload.getInstance(ato.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + this.choice);
    }

    public static EcSignature getInstance(Object o) {
        if (o instanceof EcSignature) {
            return (EcSignature)((Object)o);
        }
        if (o != null) {
            return new EcSignature(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEcSignature() {
        return this._ecSignature;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this._ecSignature);
    }
}

