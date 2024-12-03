/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

class PEMUtil {
    private final Boundaries[] _supportedBoundaries;

    PEMUtil(String type) {
        this._supportedBoundaries = new Boundaries[]{new Boundaries(type), new Boundaries("X509 " + type), new Boundaries("PKCS7")};
    }

    private String readLine(InputStream in) throws IOException {
        int c;
        StringBuffer l = new StringBuffer();
        while (true) {
            if ((c = in.read()) != 13 && c != 10 && c >= 0) {
                l.append((char)c);
                continue;
            }
            if (c < 0 || l.length() != 0) break;
        }
        if (c < 0) {
            if (l.length() == 0) {
                return null;
            }
            return l.toString();
        }
        if (c == 13) {
            in.mark(1);
            c = in.read();
            if (c == 10) {
                in.mark(1);
            }
            if (c > 0) {
                in.reset();
            }
        }
        return l.toString();
    }

    private Boundaries getBoundaries(String line) {
        for (int i = 0; i != this._supportedBoundaries.length; ++i) {
            Boundaries boundary = this._supportedBoundaries[i];
            if (!boundary.isTheExpectedHeader(line) && !boundary.isTheExpectedFooter(line)) continue;
            return boundary;
        }
        return null;
    }

    ASN1Sequence readPEMObject(InputStream in, boolean isFirst) throws IOException {
        String line;
        StringBuffer pemBuf = new StringBuffer();
        Boundaries header = null;
        while (header == null && (line = this.readLine(in)) != null) {
            header = this.getBoundaries(line);
            if (header == null || header.isTheExpectedHeader(line)) continue;
            throw new IOException("malformed PEM data: found footer where header was expected");
        }
        if (header == null) {
            if (!isFirst) {
                return null;
            }
            throw new IOException("malformed PEM data: no header found");
        }
        Boundaries footer = null;
        while (footer == null && (line = this.readLine(in)) != null) {
            footer = this.getBoundaries(line);
            if (footer != null) {
                if (header.isTheExpectedFooter(line)) continue;
                throw new IOException("malformed PEM data: header/footer mismatch");
            }
            pemBuf.append(line);
        }
        if (footer == null) {
            throw new IOException("malformed PEM data: no footer found");
        }
        if (pemBuf.length() != 0) {
            try {
                return ASN1Sequence.getInstance(Base64.decode(pemBuf.toString()));
            }
            catch (Exception e) {
                throw new IOException("malformed PEM data encountered");
            }
        }
        return null;
    }

    private static class Boundaries {
        private final String _header;
        private final String _footer;

        private Boundaries(String type) {
            this._header = "-----BEGIN " + type + "-----";
            this._footer = "-----END " + type + "-----";
        }

        public boolean isTheExpectedHeader(String line) {
            return line.startsWith(this._header);
        }

        public boolean isTheExpectedFooter(String line) {
            return line.startsWith(this._footer);
        }
    }
}

