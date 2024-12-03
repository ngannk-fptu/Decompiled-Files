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
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.etsi102941.DcDelete;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class CtlDelete
extends ASN1Object
implements ASN1Choice {
    public static final int cert = 0;
    public static final int dc = 1;
    private final int choice;
    private final ASN1Encodable ctlDelete;

    public static CtlDelete cert(HashedId8 value) {
        return new CtlDelete(0, (ASN1Encodable)value);
    }

    public static CtlDelete dc(DcDelete value) {
        return new CtlDelete(1, (ASN1Encodable)value);
    }

    public CtlDelete(int choice, ASN1Encodable value) {
        this.choice = choice;
        switch (choice) {
            case 0: {
                this.ctlDelete = HashedId8.getInstance(value);
                return;
            }
            case 1: {
                this.ctlDelete = DcDelete.getInstance(value);
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + choice);
    }

    private CtlDelete(ASN1TaggedObject value) {
        this(value.getTagNo(), (ASN1Encodable)value.getExplicitBaseObject());
    }

    public static CtlDelete getInstance(Object o) {
        if (o instanceof CtlDelete) {
            return (CtlDelete)((Object)o);
        }
        if (o != null) {
            return new CtlDelete(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getCtlDelete() {
        return this.ctlDelete;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.ctlDelete);
    }
}

