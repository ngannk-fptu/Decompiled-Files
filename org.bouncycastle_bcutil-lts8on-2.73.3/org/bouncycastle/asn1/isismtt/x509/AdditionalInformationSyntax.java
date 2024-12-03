/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.x500.DirectoryString
 */
package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.DirectoryString;

public class AdditionalInformationSyntax
extends ASN1Object {
    private DirectoryString information;

    public static AdditionalInformationSyntax getInstance(Object obj) {
        if (obj instanceof AdditionalInformationSyntax) {
            return (AdditionalInformationSyntax)((Object)obj);
        }
        if (obj != null) {
            return new AdditionalInformationSyntax(DirectoryString.getInstance((Object)obj));
        }
        return null;
    }

    private AdditionalInformationSyntax(DirectoryString information) {
        this.information = information;
    }

    public AdditionalInformationSyntax(String information) {
        this(new DirectoryString(information));
    }

    public DirectoryString getInformation() {
        return this.information;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.information.toASN1Primitive();
    }
}

