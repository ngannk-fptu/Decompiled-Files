/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.abdera.i18n.text.io.PeekAheadInputStream;

public class CharsetSniffingInputStream
extends FilterInputStream {
    protected String encoding;
    protected boolean bomset = false;
    protected final boolean preserve;

    public CharsetSniffingInputStream(InputStream in) {
        this(in, true);
    }

    public CharsetSniffingInputStream(InputStream in, boolean preserveBom) {
        super(!(in instanceof PeekAheadInputStream) ? new PeekAheadInputStream(in, 4) : in);
        this.preserve = preserveBom;
        try {
            this.encoding = this.detectEncoding();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isBomSet() {
        return this.bomset;
    }

    public String getEncoding() {
        return this.encoding;
    }

    protected PeekAheadInputStream getInternal() {
        return (PeekAheadInputStream)this.in;
    }

    private static boolean equals(byte[] a1, int len, byte[] a2) {
        int n = 0;
        int i = 0;
        while (n < len) {
            if (a1[n] != a2[i]) {
                return false;
            }
            ++n;
            ++i;
        }
        return true;
    }

    protected String detectEncoding() throws IOException {
        PeekAheadInputStream pin = (PeekAheadInputStream)this.in;
        byte[] bom = new byte[4];
        pin.peek(bom);
        this.bomset = false;
        for (Encoding enc : Encoding.values()) {
            int bomlen = enc.equals(bom);
            if (bomlen <= 0) continue;
            this.bomset = enc.getBom();
            if (this.bomset && !this.preserve) {
                pin.read(new byte[bomlen]);
            }
            return enc.getEncoding();
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Encoding {
        UTF32be("UTF-32", true, new byte[][]{{0, 0, -2, -1}}),
        UTF32le("UTF-32", true, new byte[][]{{-1, -2, 0, 0}}),
        INVALID(null, true, {-2, -1, 0, 0}, {0, 0, -1, -2}),
        UTF16be("UTF-16", true, new byte[][]{{-2, -1}}),
        UTF16le("UTF-16", true, new byte[][]{{-1, -2}}),
        UTF8("UTF-8", true, new byte[][]{{-17, -69, -65}}),
        UTF32be2("UTF-32be", false, new byte[][]{{0, 0, 0, 60}}),
        UTF32le2("UTF-32le", false, new byte[][]{{60, 0, 0, 0}}),
        UTF16be2("UTF-16be", false, new byte[][]{{0, 60, 0, 63}}),
        UTF16le2("UTF-16le", false, new byte[][]{{60, 0, 63, 0}});

        private final String enc;
        private final byte[][] checks;
        private final boolean bom;

        private Encoding(String name, boolean bom, byte[] ... checks) {
            this.enc = name;
            this.checks = checks;
            this.bom = bom;
        }

        public String getEncoding() {
            return this.enc;
        }

        public boolean getBom() {
            return this.bom;
        }

        public int equals(byte[] bom) {
            for (byte[] check : this.checks) {
                if (!CharsetSniffingInputStream.equals(bom, check.length, check)) continue;
                return check.length;
            }
            return 0;
        }
    }
}

