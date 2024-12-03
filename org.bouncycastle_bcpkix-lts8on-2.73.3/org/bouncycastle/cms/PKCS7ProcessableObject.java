/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;

public class PKCS7ProcessableObject
implements CMSTypedData {
    private final ASN1ObjectIdentifier type;
    private final ASN1Encodable structure;

    public PKCS7ProcessableObject(ASN1ObjectIdentifier type, ASN1Encodable structure) {
        this.type = type;
        this.structure = structure;
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }

    @Override
    public void write(OutputStream cOut) throws IOException, CMSException {
        if (this.structure instanceof ASN1Sequence) {
            ASN1Sequence s = ASN1Sequence.getInstance((Object)this.structure);
            for (ASN1Encodable enc : s) {
                cOut.write(enc.toASN1Primitive().getEncoded("DER"));
            }
        } else {
            byte[] encoded = this.structure.toASN1Primitive().getEncoded("DER");
            int index = 1;
            while ((encoded[index] & 0xFF) > 127) {
                ++index;
            }
            cOut.write(encoded, ++index, encoded.length - index);
        }
    }

    @Override
    public Object getContent() {
        return this.structure;
    }
}

