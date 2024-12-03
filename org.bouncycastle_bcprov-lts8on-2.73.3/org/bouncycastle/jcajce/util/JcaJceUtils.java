/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;

public class JcaJceUtils {
    private JcaJceUtils() {
    }

    public static ASN1Encodable extractParameters(AlgorithmParameters params) throws IOException {
        ASN1Primitive asn1Params;
        try {
            asn1Params = ASN1Primitive.fromByteArray(params.getEncoded("ASN.1"));
        }
        catch (Exception ex) {
            asn1Params = ASN1Primitive.fromByteArray(params.getEncoded());
        }
        return asn1Params;
    }

    public static void loadParameters(AlgorithmParameters params, ASN1Encodable sParams) throws IOException {
        try {
            params.init(sParams.toASN1Primitive().getEncoded(), "ASN.1");
        }
        catch (Exception ex) {
            params.init(sParams.toASN1Primitive().getEncoded());
        }
    }
}

