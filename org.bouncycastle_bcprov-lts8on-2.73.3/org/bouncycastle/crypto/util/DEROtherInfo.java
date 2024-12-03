/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.util.DerUtil;

public class DEROtherInfo {
    private final DERSequence sequence;

    private DEROtherInfo(DERSequence sequence) {
        this.sequence = sequence;
    }

    public byte[] getEncoded() throws IOException {
        return this.sequence.getEncoded();
    }

    public static final class Builder {
        private final AlgorithmIdentifier algorithmID;
        private final ASN1OctetString partyUInfo;
        private final ASN1OctetString partyVInfo;
        private ASN1TaggedObject suppPubInfo;
        private ASN1TaggedObject suppPrivInfo;

        public Builder(AlgorithmIdentifier algorithmID, byte[] partyUInfo, byte[] partyVInfo) {
            this.algorithmID = algorithmID;
            this.partyUInfo = DerUtil.getOctetString(partyUInfo);
            this.partyVInfo = DerUtil.getOctetString(partyVInfo);
        }

        public Builder withSuppPubInfo(byte[] suppPubInfo) {
            this.suppPubInfo = new DERTaggedObject(false, 0, (ASN1Encodable)DerUtil.getOctetString(suppPubInfo));
            return this;
        }

        public Builder withSuppPrivInfo(byte[] suppPrivInfo) {
            this.suppPrivInfo = new DERTaggedObject(false, 1, (ASN1Encodable)DerUtil.getOctetString(suppPrivInfo));
            return this;
        }

        public DEROtherInfo build() {
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(this.algorithmID);
            v.add(this.partyUInfo);
            v.add(this.partyVInfo);
            if (this.suppPubInfo != null) {
                v.add(this.suppPubInfo);
            }
            if (this.suppPrivInfo != null) {
                v.add(this.suppPrivInfo);
            }
            return new DEROtherInfo(new DERSequence(v));
        }
    }
}

