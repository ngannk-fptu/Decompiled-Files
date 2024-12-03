/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.EncodableDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class HKDF {
    private static final String versionLabel = "HPKE-v1";
    private final HKDFBytesGenerator kdf;
    private final int hashLength;

    HKDF(short kdfId) {
        EncodableDigest hash;
        switch (kdfId) {
            case 1: {
                hash = new SHA256Digest();
                break;
            }
            case 2: {
                hash = new SHA384Digest();
                break;
            }
            case 3: {
                hash = new SHA512Digest();
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid kdf id");
            }
        }
        this.kdf = new HKDFBytesGenerator((Digest)((Object)hash));
        this.hashLength = hash.getDigestSize();
    }

    int getHashSize() {
        return this.hashLength;
    }

    protected byte[] LabeledExtract(byte[] salt, byte[] suiteID, String label, byte[] ikm) {
        if (salt == null) {
            salt = new byte[this.hashLength];
        }
        byte[] labeledIKM = Arrays.concatenate(versionLabel.getBytes(), suiteID, label.getBytes(), ikm);
        return this.kdf.extractPRK(salt, labeledIKM);
    }

    protected byte[] LabeledExpand(byte[] prk, byte[] suiteID, String label, byte[] info, int L) {
        if (L > 65536) {
            throw new IllegalArgumentException("Expand length cannot be larger than 2^16");
        }
        byte[] labeledInfo = Arrays.concatenate(Pack.shortToBigEndian((short)L), versionLabel.getBytes(), suiteID, label.getBytes());
        this.kdf.init(HKDFParameters.skipExtractParameters(prk, Arrays.concatenate(labeledInfo, info)));
        byte[] rv = new byte[L];
        this.kdf.generateBytes(rv, 0, rv.length);
        return rv;
    }
}

