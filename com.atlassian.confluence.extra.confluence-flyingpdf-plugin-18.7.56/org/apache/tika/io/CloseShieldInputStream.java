/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.InputStream;
import org.apache.tika.io.ClosedInputStream;
import org.apache.tika.io.ProxyInputStream;

public class CloseShieldInputStream
extends ProxyInputStream {
    public CloseShieldInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() {
        this.in = new ClosedInputStream();
    }
}

