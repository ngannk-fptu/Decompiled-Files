/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessOutputStream;
import org.apache.pdfbox.io.ScratchFile;

public final class COSInputStream
extends FilterInputStream {
    private final List<DecodeResult> decodeResults;

    static COSInputStream create(List<Filter> filters, COSDictionary parameters, InputStream in, ScratchFile scratchFile) throws IOException {
        return COSInputStream.create(filters, parameters, in, scratchFile, DecodeOptions.DEFAULT);
    }

    static COSInputStream create(List<Filter> filters, COSDictionary parameters, InputStream in, ScratchFile scratchFile, DecodeOptions options) throws IOException {
        HashSet<Filter> filterSet;
        InputStream input = in;
        if (filters.isEmpty()) {
            return new COSInputStream(in, Collections.<DecodeResult>emptyList());
        }
        ArrayList<DecodeResult> results = new ArrayList<DecodeResult>(filters.size());
        if (filters.size() > 1 && (filterSet = new HashSet<Filter>(filters)).size() != filters.size()) {
            throw new IOException("Duplicate");
        }
        for (int i = 0; i < filters.size(); ++i) {
            DecodeResult result;
            if (scratchFile != null) {
                final RandomAccess buffer = scratchFile.createBuffer();
                result = filters.get(i).decode(input, new RandomAccessOutputStream(buffer), parameters, i, options);
                results.add(result);
                input = new RandomAccessInputStream(buffer){

                    @Override
                    public void close() throws IOException {
                        buffer.close();
                    }
                };
                continue;
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            result = filters.get(i).decode(input, output, parameters, i, options);
            results.add(result);
            input = new ByteArrayInputStream(output.toByteArray());
        }
        return new COSInputStream(input, results);
    }

    private COSInputStream(InputStream input, List<DecodeResult> decodeResults) {
        super(input);
        this.decodeResults = decodeResults;
    }

    public DecodeResult getDecodeResult() {
        if (this.decodeResults.isEmpty()) {
            return DecodeResult.DEFAULT;
        }
        return this.decodeResults.get(this.decodeResults.size() - 1);
    }
}

