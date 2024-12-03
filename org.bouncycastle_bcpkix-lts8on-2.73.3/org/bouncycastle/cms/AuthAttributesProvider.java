/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Set
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Set;

interface AuthAttributesProvider {
    public ASN1Set getAuthAttributes();

    public boolean isAead();
}

