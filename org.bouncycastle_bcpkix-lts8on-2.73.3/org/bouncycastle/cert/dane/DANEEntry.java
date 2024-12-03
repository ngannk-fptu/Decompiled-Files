/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cert.dane;

import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;

public class DANEEntry {
    public static final int CERT_USAGE_CA = 0;
    public static final int CERT_USAGE_PKIX_VALIDATE = 1;
    public static final int CERT_USAGE_TRUST_ANCHOR = 2;
    public static final int CERT_USAGE_ACCEPT = 3;
    static final int CERT_USAGE = 0;
    static final int SELECTOR = 1;
    static final int MATCHING_TYPE = 2;
    private final String domainName;
    private final byte[] flags;
    private final X509CertificateHolder certHolder;

    DANEEntry(String domainName, byte[] flags, X509CertificateHolder certHolder) {
        this.flags = flags;
        this.domainName = domainName;
        this.certHolder = certHolder;
    }

    public DANEEntry(String domainName, byte[] data) throws IOException {
        this(domainName, Arrays.copyOfRange((byte[])data, (int)0, (int)3), new X509CertificateHolder(Arrays.copyOfRange((byte[])data, (int)3, (int)data.length)));
    }

    public byte[] getFlags() {
        return Arrays.clone((byte[])this.flags);
    }

    public X509CertificateHolder getCertificate() {
        return this.certHolder;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public byte[] getRDATA() throws IOException {
        byte[] certEnc = this.certHolder.getEncoded();
        byte[] data = new byte[this.flags.length + certEnc.length];
        System.arraycopy(this.flags, 0, data, 0, this.flags.length);
        System.arraycopy(certEnc, 0, data, this.flags.length, certEnc.length);
        return data;
    }

    public static boolean isValidCertificate(byte[] data) {
        return (data[0] >= 0 || data[0] <= 3) && data[1] == 0 && data[2] == 0;
    }
}

