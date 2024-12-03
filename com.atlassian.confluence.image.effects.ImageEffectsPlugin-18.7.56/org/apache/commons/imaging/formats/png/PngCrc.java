/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

class PngCrc {
    private final long[] crc_table = new long[256];
    private boolean crc_table_computed;

    PngCrc() {
    }

    private void make_crc_table() {
        for (int n = 0; n < 256; ++n) {
            long c = n;
            for (int k = 0; k < 8; ++k) {
                if ((c & 1L) != 0L) {
                    c = 0xEDB88320L ^ c >> 1;
                    continue;
                }
                c >>= 1;
            }
            this.crc_table[n] = c;
        }
        this.crc_table_computed = true;
    }

    private long update_crc(long crc, byte[] buf) {
        long c = crc;
        if (!this.crc_table_computed) {
            this.make_crc_table();
        }
        for (int n = 0; n < buf.length; ++n) {
            c = this.crc_table[(int)((c ^ (long)buf[n]) & 0xFFL)] ^ c >> 8;
        }
        return c;
    }

    public final int crc(byte[] buf, int len) {
        return (int)(this.update_crc(0xFFFFFFFFL, buf) ^ 0xFFFFFFFFL);
    }

    public final long start_partial_crc(byte[] buf, int len) {
        return this.update_crc(0xFFFFFFFFL, buf);
    }

    public final long continue_partial_crc(long old_crc, byte[] buf, int len) {
        return this.update_crc(old_crc, buf);
    }

    public final long finish_partial_crc(long old_crc) {
        return old_crc ^ 0xFFFFFFFFL;
    }
}

