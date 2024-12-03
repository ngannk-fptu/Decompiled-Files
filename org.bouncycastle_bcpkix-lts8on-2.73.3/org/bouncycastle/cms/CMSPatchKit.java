/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.SignerInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.SignerInformation;

public class CMSPatchKit {
    public static SignerInformation createNonDERSignerInfo(SignerInformation original) {
        return new DLSignerInformation(original);
    }

    public static SignerInformation createWithSignatureAlgorithm(SignerInformation original, AlgorithmIdentifier signatureAlgorithm) {
        return new ModEncAlgSignerInformation(original, signatureAlgorithm);
    }

    private static class DLSignerInformation
    extends SignerInformation {
        protected DLSignerInformation(SignerInformation baseInfo) {
            super(baseInfo);
        }

        @Override
        public byte[] getEncodedSignedAttributes() throws IOException {
            return this.signedAttributeSet.getEncoded("DL");
        }
    }

    private static class ModEncAlgSignerInformation
    extends SignerInformation {
        protected ModEncAlgSignerInformation(SignerInformation baseInfo, AlgorithmIdentifier signatureAlgorithm) {
            super(baseInfo, ModEncAlgSignerInformation.editEncAlg(baseInfo.info, signatureAlgorithm));
        }

        private static SignerInfo editEncAlg(SignerInfo info, AlgorithmIdentifier signatureAlgorithm) {
            return new SignerInfo(info.getSID(), info.getDigestAlgorithm(), info.getAuthenticatedAttributes(), signatureAlgorithm, info.getEncryptedDigest(), info.getUnauthenticatedAttributes());
        }
    }
}

