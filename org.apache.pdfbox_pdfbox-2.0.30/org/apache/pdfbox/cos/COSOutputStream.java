/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessOutputStream;
import org.apache.pdfbox.io.ScratchFile;

public final class COSOutputStream
extends FilterOutputStream {
    private final List<Filter> filters;
    private final COSDictionary parameters;
    private final ScratchFile scratchFile;
    private RandomAccess buffer;

    COSOutputStream(List<Filter> filters, COSDictionary parameters, OutputStream output, ScratchFile scratchFile) throws IOException {
        super(output);
        this.filters = filters;
        this.parameters = parameters;
        this.scratchFile = scratchFile;
        this.buffer = filters.isEmpty() ? null : scratchFile.createBuffer();
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (this.buffer != null) {
            this.buffer.write(b);
        } else {
            super.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.buffer != null) {
            this.buffer.write(b, off, len);
        } else {
            super.write(b, off, len);
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (this.buffer != null) {
            this.buffer.write(b);
        } else {
            super.write(b);
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.buffer == null) {
            super.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void close() throws IOException {
        try {
            if (this.buffer == null) return;
            try {
                int i = this.filters.size() - 1;
                while (i >= 0) {
                    block17: {
                        RandomAccessInputStream unfilteredIn = new RandomAccessInputStream(this.buffer);
                        try {
                            if (i == 0) {
                                this.filters.get(i).encode(unfilteredIn, this.out, this.parameters, i);
                                break block17;
                            }
                            RandomAccess filteredBuffer = this.scratchFile.createBuffer();
                            try {
                                RandomAccessOutputStream filteredOut = new RandomAccessOutputStream(filteredBuffer);
                                try {
                                    this.filters.get(i).encode(unfilteredIn, filteredOut, this.parameters, i);
                                }
                                finally {
                                    filteredOut.close();
                                }
                                RandomAccess tmpSwap = filteredBuffer;
                                filteredBuffer = this.buffer;
                                this.buffer = tmpSwap;
                            }
                            finally {
                                filteredBuffer.close();
                            }
                        }
                        finally {
                            unfilteredIn.close();
                        }
                    }
                    --i;
                }
                return;
            }
            finally {
                this.buffer.close();
                this.buffer = null;
            }
        }
        finally {
            super.close();
        }
    }
}

