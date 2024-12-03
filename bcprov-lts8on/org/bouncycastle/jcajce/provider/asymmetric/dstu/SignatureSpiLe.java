/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.IOException;
import java.security.SignatureException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpi;

public class SignatureSpiLe
extends SignatureSpi {
    void reverseBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; ++i) {
            byte tmp = bytes[i];
            bytes[i] = bytes[bytes.length - 1 - i];
            bytes[bytes.length - 1 - i] = tmp;
        }
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        byte[] signature = ASN1OctetString.getInstance(super.engineSign()).getOctets();
        this.reverseBytes(signature);
        try {
            return new DEROctetString(signature).getEncoded();
        }
        catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        byte[] bytes = null;
        try {
            bytes = ((ASN1OctetString)ASN1OctetString.fromByteArray(sigBytes)).getOctets();
        }
        catch (IOException e) {
            throw new SignatureException("error decoding signature bytes.");
        }
        this.reverseBytes(bytes);
        try {
            return super.engineVerify(new DEROctetString(bytes).getEncoded());
        }
        catch (SignatureException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }
}

