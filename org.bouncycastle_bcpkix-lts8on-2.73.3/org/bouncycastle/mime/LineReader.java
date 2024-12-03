/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Strings;

class LineReader {
    private final InputStream src;
    private int lastC = -1;

    LineReader(InputStream src) {
        this.src = src;
    }

    String readLine() throws IOException {
        int c;
        int ch;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        if (this.lastC != -1) {
            if (this.lastC == 13) {
                return "";
            }
            ch = this.lastC;
            this.lastC = -1;
        } else {
            ch = this.src.read();
        }
        while (ch >= 0 && ch != 13 && ch != 10) {
            bOut.write(ch);
            ch = this.src.read();
        }
        if (ch == 13 && (c = this.src.read()) != 10 && c >= 0) {
            this.lastC = c;
        }
        if (ch < 0) {
            return null;
        }
        return Strings.fromUTF8ByteArray((byte[])bOut.toByteArray());
    }
}

