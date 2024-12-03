/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.crmf.PKMACValue
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.operator.MacCalculator;

abstract class PKMACValueGenerator {
    private PKMACValueGenerator() {
    }

    public static PKMACValue generate(PKMACBuilder builder, char[] password, SubjectPublicKeyInfo keyInfo) throws CRMFException {
        MacCalculator calculator = builder.build(password);
        OutputStream macOut = calculator.getOutputStream();
        try {
            macOut.write(keyInfo.getEncoded("DER"));
            macOut.close();
        }
        catch (IOException e) {
            throw new CRMFException("exception encoding mac input: " + e.getMessage(), e);
        }
        return new PKMACValue(calculator.getAlgorithmIdentifier(), new DERBitString(calculator.getMac()));
    }
}

