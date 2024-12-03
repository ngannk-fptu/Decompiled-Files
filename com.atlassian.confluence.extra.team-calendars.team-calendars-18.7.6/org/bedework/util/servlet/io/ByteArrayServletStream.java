/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 */
package org.bedework.util.servlet.io;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.bedework.util.servlet.io.PooledBufferedOutputStream;

public class ByteArrayServletStream
extends ServletOutputStream {
    PooledBufferedOutputStream pbos;

    public ByteArrayServletStream(PooledBufferedOutputStream pbos) {
        this.pbos = pbos;
    }

    public void write(int param) throws IOException {
        this.pbos.write(param);
    }

    public void close() {
        if (this.pbos != null) {
            try {
                this.pbos.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        try {
            super.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

