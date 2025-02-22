/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;

public class Target
extends ASN1Object
implements ASN1Choice {
    public static final int targetName = 0;
    public static final int targetGroup = 1;
    private GeneralName targName;
    private GeneralName targGroup;

    public static Target getInstance(Object obj) {
        if (obj == null || obj instanceof Target) {
            return (Target)obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new Target((ASN1TaggedObject)obj);
        }
        throw new IllegalArgumentException("unknown object in factory: " + obj.getClass());
    }

    private Target(ASN1TaggedObject tagObj) {
        switch (tagObj.getTagNo()) {
            case 0: {
                this.targName = GeneralName.getInstance(tagObj, true);
                break;
            }
            case 1: {
                this.targGroup = GeneralName.getInstance(tagObj, true);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag: " + tagObj.getTagNo());
            }
        }
    }

    public Target(int type, GeneralName name) {
        this(new DERTaggedObject(type, name));
    }

    public GeneralName getTargetGroup() {
        return this.targGroup;
    }

    public GeneralName getTargetName() {
        return this.targName;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.targName != null) {
            return new DERTaggedObject(true, 0, (ASN1Encodable)this.targName);
        }
        return new DERTaggedObject(true, 1, (ASN1Encodable)this.targGroup);
    }
}

