/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.cos;

import java.io.Closeable;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSOutputStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessOutputStream;
import org.apache.pdfbox.io.ScratchFile;

public class COSStream
extends COSDictionary
implements Closeable {
    private RandomAccess randomAccess;
    private final ScratchFile scratchFile;
    private boolean isWriting;
    private static final Log LOG = LogFactory.getLog(COSStream.class);

    public COSStream() {
        this(ScratchFile.getMainMemoryOnlyInstance());
    }

    public COSStream(ScratchFile scratchFile) {
        this.setInt(COSName.LENGTH, 0);
        this.scratchFile = scratchFile != null ? scratchFile : ScratchFile.getMainMemoryOnlyInstance();
    }

    private void checkClosed() throws IOException {
        if (this.randomAccess != null && this.randomAccess.isClosed()) {
            throw new IOException("COSStream has been closed and cannot be read. Perhaps its enclosing PDDocument has been closed?");
        }
    }

    @Deprecated
    public InputStream getFilteredStream() throws IOException {
        return this.createRawInputStream();
    }

    private void ensureRandomAccessExists(boolean forInputStream) throws IOException {
        if (this.randomAccess == null) {
            if (forInputStream && LOG.isDebugEnabled()) {
                LOG.debug((Object)"Create InputStream called without data being written before to stream.");
            }
            this.randomAccess = this.scratchFile.createBuffer();
        }
    }

    public InputStream createRawInputStream() throws IOException {
        this.checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot read while there is an open stream writer");
        }
        this.ensureRandomAccessExists(true);
        return new RandomAccessInputStream(this.randomAccess);
    }

    @Deprecated
    public InputStream getUnfilteredStream() throws IOException {
        return this.createInputStream();
    }

    public COSInputStream createInputStream() throws IOException {
        return this.createInputStream(DecodeOptions.DEFAULT);
    }

    public COSInputStream createInputStream(DecodeOptions options) throws IOException {
        this.checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot read while there is an open stream writer");
        }
        this.ensureRandomAccessExists(true);
        RandomAccessInputStream input = new RandomAccessInputStream(this.randomAccess);
        return COSInputStream.create(this.getFilterList(), this, input, this.scratchFile, options);
    }

    @Deprecated
    public OutputStream createUnfilteredStream() throws IOException {
        return this.createOutputStream();
    }

    public OutputStream createOutputStream() throws IOException {
        return this.createOutputStream(null);
    }

    public OutputStream createOutputStream(COSBase filters) throws IOException {
        this.checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot have more than one open stream writer.");
        }
        if (filters != null) {
            this.setItem(COSName.FILTER, filters);
        }
        IOUtils.closeQuietly(this.randomAccess);
        this.randomAccess = this.scratchFile.createBuffer();
        RandomAccessOutputStream randomOut = new RandomAccessOutputStream(this.randomAccess);
        COSOutputStream cosOut = new COSOutputStream(this.getFilterList(), this, randomOut, this.scratchFile);
        this.isWriting = true;
        return new FilterOutputStream(cosOut){

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                this.out.write(b, off, len);
            }

            @Override
            public void close() throws IOException {
                super.close();
                COSStream.this.setInt(COSName.LENGTH, (int)COSStream.this.randomAccess.length());
                COSStream.this.isWriting = false;
            }
        };
    }

    @Deprecated
    public OutputStream createFilteredStream() throws IOException {
        return this.createRawOutputStream();
    }

    public OutputStream createRawOutputStream() throws IOException {
        this.checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot have more than one open stream writer.");
        }
        IOUtils.closeQuietly(this.randomAccess);
        this.randomAccess = this.scratchFile.createBuffer();
        RandomAccessOutputStream out = new RandomAccessOutputStream(this.randomAccess);
        this.isWriting = true;
        return new FilterOutputStream(out){

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                this.out.write(b, off, len);
            }

            @Override
            public void close() throws IOException {
                super.close();
                COSStream.this.setInt(COSName.LENGTH, (int)COSStream.this.randomAccess.length());
                COSStream.this.isWriting = false;
            }
        };
    }

    private List<Filter> getFilterList() throws IOException {
        ArrayList<Filter> filterList;
        COSBase filters = this.getFilters();
        if (filters instanceof COSName) {
            filterList = new ArrayList<Filter>(1);
            filterList.add(FilterFactory.INSTANCE.getFilter((COSName)filters));
        } else if (filters instanceof COSArray) {
            COSArray filterArray = (COSArray)filters;
            filterList = new ArrayList(filterArray.size());
            for (int i = 0; i < filterArray.size(); ++i) {
                COSBase base = filterArray.get(i);
                if (!(base instanceof COSName)) {
                    throw new IOException("Forbidden type in filter array: " + (base == null ? "null" : base.getClass().getName()));
                }
                filterList.add(FilterFactory.INSTANCE.getFilter((COSName)base));
            }
        } else {
            filterList = new ArrayList();
        }
        return filterList;
    }

    public long getLength() {
        if (this.isWriting) {
            throw new IllegalStateException("There is an open OutputStream associated with this COSStream. It must be closed before querying the length of this COSStream.");
        }
        return this.getInt(COSName.LENGTH, 0);
    }

    public COSBase getFilters() {
        return this.getDictionaryObject(COSName.FILTER);
    }

    @Deprecated
    public void setFilters(COSBase filters) throws IOException {
        this.setItem(COSName.FILTER, filters);
    }

    @Deprecated
    public String getString() {
        return this.toTextString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toTextString() {
        byte[] array;
        COSInputStream input = null;
        try {
            input = this.createInputStream();
            array = IOUtils.toByteArray(input);
        }
        catch (IOException e) {
            LOG.debug((Object)"An exception occurred trying to get the content - returning empty string instead", (Throwable)e);
            String string = "";
            return string;
        }
        finally {
            IOUtils.closeQuietly(input);
        }
        COSString string = new COSString(array);
        return string.getString();
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromStream(this);
    }

    @Override
    public void close() throws IOException {
        if (this.randomAccess != null) {
            this.randomAccess.close();
        }
    }
}

