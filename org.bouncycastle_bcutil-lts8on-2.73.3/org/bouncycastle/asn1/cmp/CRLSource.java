/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.DistributionPointName
 *  org.bouncycastle.asn1.x509.GeneralNames
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class CRLSource
extends ASN1Object
implements ASN1Choice {
    private final DistributionPointName dpn;
    private final GeneralNames issuer;

    private CRLSource(ASN1TaggedObject ato) {
        if (ato.hasContextTag(0)) {
            this.dpn = DistributionPointName.getInstance((ASN1TaggedObject)ato, (boolean)true);
            this.issuer = null;
        } else if (ato.hasContextTag(1)) {
            this.dpn = null;
            this.issuer = GeneralNames.getInstance((ASN1TaggedObject)ato, (boolean)true);
        } else {
            throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText((ASN1TaggedObject)ato));
        }
    }

    public CRLSource(DistributionPointName dpn, GeneralNames issuer) {
        if (dpn == null == (issuer == null)) {
            throw new IllegalArgumentException("either dpn or issuer must be set");
        }
        this.dpn = dpn;
        this.issuer = issuer;
    }

    public static CRLSource getInstance(Object o) {
        if (o instanceof CRLSource) {
            return (CRLSource)((Object)o);
        }
        if (o != null) {
            return new CRLSource(ASN1TaggedObject.getInstance((Object)o));
        }
        return null;
    }

    public DistributionPointName getDpn() {
        return this.dpn;
    }

    public GeneralNames getIssuer() {
        return this.issuer;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.dpn != null) {
            return new DERTaggedObject(true, 0, (ASN1Encodable)this.dpn);
        }
        return new DERTaggedObject(true, 1, (ASN1Encodable)this.issuer);
    }
}

