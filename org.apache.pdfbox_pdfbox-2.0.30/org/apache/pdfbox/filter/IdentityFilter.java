/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.IOUtils;

final class IdentityFilter
extends Filter {
    IdentityFilter() {
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        IOUtils.copy(encoded, decoded);
        decoded.flush();
        return new DecodeResult(parameters);
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        IOUtils.copy(input, encoded);
        encoded.flush();
    }
}

