/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.MacData
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.DigestInfo
 */
package org.bouncycastle.pkcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCSException;

class MacDataGenerator {
    private PKCS12MacCalculatorBuilder builder;

    MacDataGenerator(PKCS12MacCalculatorBuilder builder) {
        this.builder = builder;
    }

    public MacData build(char[] password, byte[] data) throws PKCSException {
        MacCalculator macCalculator;
        try {
            macCalculator = this.builder.build(password);
            OutputStream out = macCalculator.getOutputStream();
            out.write(data);
            out.close();
        }
        catch (Exception e) {
            throw new PKCSException("unable to process data: " + e.getMessage(), e);
        }
        AlgorithmIdentifier algId = macCalculator.getAlgorithmIdentifier();
        DigestInfo dInfo = new DigestInfo(this.builder.getDigestAlgorithmIdentifier(), macCalculator.getMac());
        PKCS12PBEParams params = PKCS12PBEParams.getInstance((Object)algId.getParameters());
        return new MacData(dInfo, params.getIV(), params.getIterations().intValue());
    }
}

