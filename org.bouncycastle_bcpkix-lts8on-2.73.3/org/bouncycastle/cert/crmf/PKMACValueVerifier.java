/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cmp.PBMParameter
 *  org.bouncycastle.asn1.crmf.PKMACValue
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Arrays;

class PKMACValueVerifier {
    private final PKMACBuilder builder;

    public PKMACValueVerifier(PKMACBuilder builder) {
        this.builder = builder;
    }

    public boolean isValid(PKMACValue value, char[] password, SubjectPublicKeyInfo keyInfo) throws CRMFException {
        this.builder.setParameters(PBMParameter.getInstance((Object)value.getAlgId().getParameters()));
        MacCalculator calculator = this.builder.build(password);
        OutputStream macOut = calculator.getOutputStream();
        try {
            macOut.write(keyInfo.getEncoded("DER"));
            macOut.close();
        }
        catch (IOException e) {
            throw new CRMFException("exception encoding mac input: " + e.getMessage(), e);
        }
        return Arrays.constantTimeAreEqual((byte[])calculator.getMac(), (byte[])value.getValue().getBytes());
    }
}

