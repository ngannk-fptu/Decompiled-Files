/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.ASCII85InputStream;
import org.apache.pdfbox.filter.ASCII85OutputStream;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.IOUtils;

final class ASCII85Filter
extends Filter {
    ASCII85Filter() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        ASCII85InputStream is = null;
        try {
            is = new ASCII85InputStream(encoded);
            IOUtils.copy(is, decoded);
            decoded.flush();
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(is);
            throw throwable;
        }
        IOUtils.closeQuietly(is);
        return new DecodeResult(parameters);
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        ASCII85OutputStream os = new ASCII85OutputStream(encoded);
        IOUtils.copy(input, os);
        os.close();
        encoded.flush();
    }
}

