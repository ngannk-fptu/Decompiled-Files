/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

public class PEMUtil {
    private final String _header1;
    private final String _header2;
    private final String _footer1;
    private final String _footer2;

    PEMUtil(String type) {
        this._header1 = "-----BEGIN " + type + "-----";
        this._header2 = "-----BEGIN X509 " + type + "-----";
        this._footer1 = "-----END " + type + "-----";
        this._footer2 = "-----END X509 " + type + "-----";
    }

    private String readLine(InputStream in) throws IOException {
        int c;
        StringBuffer l = new StringBuffer();
        while (true) {
            if ((c = in.read()) != 13 && c != 10 && c >= 0) {
                if (c == 13) continue;
                l.append((char)c);
                continue;
            }
            if (c < 0 || l.length() != 0) break;
        }
        if (c < 0) {
            return null;
        }
        return l.toString();
    }

    ASN1Sequence readPEMObject(InputStream in) throws IOException {
        String line;
        StringBuffer pemBuf = new StringBuffer();
        while ((line = this.readLine(in)) != null && !line.startsWith(this._header1) && !line.startsWith(this._header2)) {
        }
        while ((line = this.readLine(in)) != null && !line.startsWith(this._footer1) && !line.startsWith(this._footer2)) {
            pemBuf.append(line);
        }
        if (pemBuf.length() != 0) {
            ASN1Primitive o = new ASN1InputStream(Base64.decode(pemBuf.toString())).readObject();
            if (!(o instanceof ASN1Sequence)) {
                throw new IOException("malformed PEM data encountered");
            }
            return (ASN1Sequence)o;
        }
        return null;
    }
}

