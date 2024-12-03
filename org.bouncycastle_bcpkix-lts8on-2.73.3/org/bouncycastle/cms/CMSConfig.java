/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSSignedHelper;

public class CMSConfig {
    public static void setSigningEncryptionAlgorithmMapping(String oid, String algorithmName) {
        ASN1ObjectIdentifier id = new ASN1ObjectIdentifier(oid);
        CMSSignedHelper.INSTANCE.setSigningEncryptionAlgorithmMapping(id, algorithmName);
    }
}

