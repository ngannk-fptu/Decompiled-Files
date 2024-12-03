/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.ByteArrayOutputStream
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.Spool;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class ByteArraySpool
implements Spool {
    private int initialBufferSize = 10240;

    public int getInitialBufferSize() {
        return this.initialBufferSize;
    }

    public void setInitialBufferSize(int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
    }

    @Override
    public InputStream spool(InputStream is) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream(this.initialBufferSize);
        IOUtils.copy((InputStream)is, (OutputStream)buf);
        return new ByteArrayInputStream(buf.toByteArray());
    }
}

