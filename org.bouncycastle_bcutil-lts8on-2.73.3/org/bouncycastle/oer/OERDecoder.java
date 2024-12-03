/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 */
package org.bouncycastle.oer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERInputStream;

public class OERDecoder {
    public static ASN1Encodable decode(byte[] src, Element e) throws IOException {
        return OERDecoder.decode(new ByteArrayInputStream(src), e);
    }

    public static ASN1Encodable decode(InputStream src, Element e) throws IOException {
        OERInputStream oerInputStream = new OERInputStream(src);
        return oerInputStream.parse(e);
    }
}

