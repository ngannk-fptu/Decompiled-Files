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
import org.bouncycastle.oer.its.etsi102941.CtlDelete;
import org.bouncycastle.oer.its.etsi102941.CtlEntry;

public class CtlCommand
extends ASN1Object
implements ASN1Choice {
    private final int choice;
    private final ASN1Encodable ctlCommand;
    public static final int add = 0;
    public static final int delete = 1;

    public CtlCommand(int choice, ASN1Encodable ctlCommand) {
        this.choice = choice;
        this.ctlCommand = ctlCommand;
    }

    private CtlCommand(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.ctlCommand = CtlEntry.getInstance(ato.getExplicitBaseObject());
                return;
            }
            case 1: {
                this.ctlCommand = CtlDelete.getInstance(ato.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + this.choice);
    }

    public static CtlCommand getInstance(Object o) {
        if (o instanceof CtlCommand) {
            return (CtlCommand)((Object)o);
        }
        if (o != null) {
            return new CtlCommand(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public static CtlCommand add(CtlEntry add) {
        return new CtlCommand(0, (ASN1Encodable)add);
    }

    public static CtlCommand delete(CtlDelete delete) {
        return new CtlCommand(1, (ASN1Encodable)delete);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getCtlCommand() {
        return this.ctlCommand;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.ctlCommand);
    }
}

