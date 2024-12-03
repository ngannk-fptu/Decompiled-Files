/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.Extension
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.Utils;
import org.bouncycastle.asn1.x509.Extension;

public class ExtensionReq
extends ASN1Object {
    private final Extension[] extensions;

    public static ExtensionReq getInstance(Object obj) {
        if (obj instanceof ExtensionReq) {
            return (ExtensionReq)((Object)obj);
        }
        if (obj != null) {
            return new ExtensionReq(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static ExtensionReq getInstance(ASN1TaggedObject obj, boolean explicit) {
        return ExtensionReq.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ExtensionReq(Extension Extension2) {
        this.extensions = new Extension[]{Extension2};
    }

    public ExtensionReq(Extension[] extensions) {
        this.extensions = Utils.clone(extensions);
    }

    private ExtensionReq(ASN1Sequence seq) {
        this.extensions = new Extension[seq.size()];
        for (int i = 0; i != seq.size(); ++i) {
            this.extensions[i] = Extension.getInstance((Object)seq.getObjectAt(i));
        }
    }

    public Extension[] getExtensions() {
        return Utils.clone(this.extensions);
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[])this.extensions);
    }
}

