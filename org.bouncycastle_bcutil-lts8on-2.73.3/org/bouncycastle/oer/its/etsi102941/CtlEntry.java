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
import org.bouncycastle.oer.its.etsi102941.AaEntry;
import org.bouncycastle.oer.its.etsi102941.DcEntry;
import org.bouncycastle.oer.its.etsi102941.EaEntry;
import org.bouncycastle.oer.its.etsi102941.RootCaEntry;
import org.bouncycastle.oer.its.etsi102941.TlmEntry;

public class CtlEntry
extends ASN1Object
implements ASN1Choice {
    public static final int rca = 0;
    public static final int ea = 1;
    public static final int aa = 2;
    public static final int dc = 3;
    public static final int tlm = 4;
    private final int choice;
    private final ASN1Encodable ctlEntry;

    public CtlEntry(int choice, ASN1Encodable ctlEntry) {
        this.choice = choice;
        this.ctlEntry = ctlEntry;
    }

    private CtlEntry(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.ctlEntry = RootCaEntry.getInstance(ato.getExplicitBaseObject());
                return;
            }
            case 1: {
                this.ctlEntry = EaEntry.getInstance(ato.getExplicitBaseObject());
                return;
            }
            case 2: {
                this.ctlEntry = AaEntry.getInstance(ato.getExplicitBaseObject());
                return;
            }
            case 3: {
                this.ctlEntry = DcEntry.getInstance(ato.getExplicitBaseObject());
                return;
            }
            case 4: {
                this.ctlEntry = TlmEntry.getInstance(ato.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + this.choice);
    }

    public static CtlEntry getInstance(Object o) {
        if (o instanceof CtlEntry) {
            return (CtlEntry)((Object)o);
        }
        if (o != null) {
            return new CtlEntry(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public static CtlEntry rca(RootCaEntry rca) {
        return new CtlEntry(0, (ASN1Encodable)rca);
    }

    public static CtlEntry ea(EaEntry ea) {
        return new CtlEntry(1, (ASN1Encodable)ea);
    }

    public static CtlEntry aa(AaEntry aa) {
        return new CtlEntry(2, (ASN1Encodable)aa);
    }

    public static CtlEntry dc(DcEntry dc) {
        return new CtlEntry(3, (ASN1Encodable)dc);
    }

    public static CtlEntry tlm(TlmEntry tlm) {
        return new CtlEntry(4, (ASN1Encodable)tlm);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getCtlEntry() {
        return this.ctlEntry;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.ctlEntry);
    }
}

