/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io.pem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;

public class PemReader
extends BufferedReader {
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";

    public PemReader(Reader reader) {
        super(reader);
    }

    public PemObject readPemObject() throws IOException {
        int index;
        String line = this.readLine();
        while (line != null && !line.startsWith(BEGIN)) {
            line = this.readLine();
        }
        if (line != null && (index = (line = line.substring(BEGIN.length())).indexOf(45)) > 0 && line.endsWith("-----") && line.length() - index == 5) {
            String type = line.substring(0, index);
            return this.loadObject(type);
        }
        return null;
    }

    private PemObject loadObject(String type) throws IOException {
        String line;
        String endMarker = END + type;
        StringBuffer buf = new StringBuffer();
        ArrayList<PemHeader> headers = new ArrayList<PemHeader>();
        while ((line = this.readLine()) != null) {
            int index = line.indexOf(58);
            if (index >= 0) {
                String hdr = line.substring(0, index);
                String value = line.substring(index + 1).trim();
                headers.add(new PemHeader(hdr, value));
                continue;
            }
            if (line.indexOf(endMarker) != -1) break;
            buf.append(line.trim());
        }
        if (line == null) {
            throw new IOException(endMarker + " not found");
        }
        return new PemObject(type, headers, Base64.decode(buf.toString()));
    }
}

