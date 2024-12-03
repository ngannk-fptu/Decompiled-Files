/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERGeneralizedTime
extends ASN1GeneralizedTime {
    public DERGeneralizedTime(byte[] time) {
        super(time);
    }

    public DERGeneralizedTime(Date time) {
        super(time);
    }

    public DERGeneralizedTime(String time) {
        super(time);
    }

    private byte[] getDERTime() {
        if (this.contents[this.contents.length - 1] == 90) {
            if (!this.hasMinutes()) {
                byte[] derTime = new byte[this.contents.length + 4];
                System.arraycopy(this.contents, 0, derTime, 0, this.contents.length - 1);
                System.arraycopy(Strings.toByteArray("0000Z"), 0, derTime, this.contents.length - 1, 5);
                return derTime;
            }
            if (!this.hasSeconds()) {
                byte[] derTime = new byte[this.contents.length + 2];
                System.arraycopy(this.contents, 0, derTime, 0, this.contents.length - 1);
                System.arraycopy(Strings.toByteArray("00Z"), 0, derTime, this.contents.length - 1, 3);
                return derTime;
            }
            if (this.hasFractionalSeconds()) {
                int ind;
                for (ind = this.contents.length - 2; ind > 0 && this.contents[ind] == 48; --ind) {
                }
                if (this.contents[ind] == 46) {
                    byte[] derTime = new byte[ind + 1];
                    System.arraycopy(this.contents, 0, derTime, 0, ind);
                    derTime[ind] = 90;
                    return derTime;
                }
                byte[] derTime = new byte[ind + 2];
                System.arraycopy(this.contents, 0, derTime, 0, ind + 1);
                derTime[ind + 1] = 90;
                return derTime;
            }
            return this.contents;
        }
        return this.contents;
    }

    @Override
    int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.getDERTime().length);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingDL(withTag, 24, this.getDERTime());
    }

    @Override
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }
}

