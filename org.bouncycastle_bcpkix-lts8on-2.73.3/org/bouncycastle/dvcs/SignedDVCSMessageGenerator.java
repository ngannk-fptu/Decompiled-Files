/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.dvcs;

import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.dvcs.DVCSException;
import org.bouncycastle.dvcs.DVCSMessage;

public class SignedDVCSMessageGenerator {
    private final CMSSignedDataGenerator signedDataGen;

    public SignedDVCSMessageGenerator(CMSSignedDataGenerator signedDataGen) {
        this.signedDataGen = signedDataGen;
    }

    public CMSSignedData build(DVCSMessage message) throws DVCSException {
        try {
            byte[] encapsulatedData = message.getContent().toASN1Primitive().getEncoded("DER");
            return this.signedDataGen.generate(new CMSProcessableByteArray(message.getContentType(), encapsulatedData), true);
        }
        catch (CMSException e) {
            throw new DVCSException("Could not sign DVCS request", e);
        }
        catch (IOException e) {
            throw new DVCSException("Could not encode DVCS request", e);
        }
    }
}

