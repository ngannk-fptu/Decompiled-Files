/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1IA5String;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERIA5String
extends ASN1IA5String {
    public DERIA5String(String string) {
        this(string, false);
    }

    public DERIA5String(String string, boolean validate) {
        super(string, validate);
    }

    DERIA5String(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

