/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cmp.PKIBody
 *  org.bouncycastle.asn1.cmp.PKIHeader
 *  org.bouncycastle.asn1.cmp.PKIMessage
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.cert.CertIOException;

public class GeneralPKIMessage {
    private final PKIMessage pkiMessage;

    private static PKIMessage parseBytes(byte[] encoding) throws IOException {
        try {
            return PKIMessage.getInstance((Object)ASN1Primitive.fromByteArray((byte[])encoding));
        }
        catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public GeneralPKIMessage(byte[] encoding) throws IOException {
        this(GeneralPKIMessage.parseBytes(encoding));
    }

    public GeneralPKIMessage(PKIMessage pkiMessage) {
        this.pkiMessage = pkiMessage;
    }

    public PKIHeader getHeader() {
        return this.pkiMessage.getHeader();
    }

    public PKIBody getBody() {
        return this.pkiMessage.getBody();
    }

    public boolean hasProtection() {
        return this.pkiMessage.getProtection() != null;
    }

    public PKIMessage toASN1Structure() {
        return this.pkiMessage;
    }
}

