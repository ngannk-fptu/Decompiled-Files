/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfparser;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class EndstreamOutputStream
extends BufferedOutputStream {
    private boolean hasCR = false;
    private boolean hasLF = false;
    private int pos = 0;
    private boolean mustFilter = true;

    EndstreamOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (this.pos == 0 && len > 10) {
            this.mustFilter = false;
            for (int i = 0; i < 10; ++i) {
                if (b[i] >= 9 && (b[i] <= 10 || b[i] >= 32 || b[i] == 13)) continue;
                this.mustFilter = true;
                break;
            }
        }
        if (this.mustFilter) {
            if (this.hasCR) {
                this.hasCR = false;
                if (!this.hasLF && len == 1 && b[off] == 10) {
                    return;
                }
                super.write(13);
            }
            if (this.hasLF) {
                super.write(10);
                this.hasLF = false;
            }
            if (len > 0) {
                if (b[off + len - 1] == 13) {
                    this.hasCR = true;
                    --len;
                } else if (b[off + len - 1] == 10) {
                    this.hasLF = true;
                    if (--len > 0 && b[off + len - 1] == 13) {
                        this.hasCR = true;
                        --len;
                    }
                }
            }
        }
        super.write(b, off, len);
        this.pos += len;
    }

    @Override
    public synchronized void flush() throws IOException {
        if (this.hasCR && !this.hasLF) {
            super.write(13);
            ++this.pos;
        }
        this.hasCR = false;
        this.hasLF = false;
        super.flush();
    }
}

