/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import org.apache.poi.poifs.filesystem.POIFSStream;
import org.apache.poi.poifs.property.DocumentProperty;

public final class DocumentOutputStream
extends OutputStream {
    private int _document_size = 0;
    private boolean _closed = false;
    private final POIFSDocument _document;
    private final DocumentProperty _property;
    private UnsynchronizedByteArrayOutputStream _buffer = new UnsynchronizedByteArrayOutputStream(4096);
    private POIFSStream _stream;
    private OutputStream _stream_output;
    private final long _limit;

    public DocumentOutputStream(DocumentEntry document) throws IOException {
        this(document, -1L);
    }

    public DocumentOutputStream(DirectoryEntry parent, String name) throws IOException {
        this(DocumentOutputStream.createDocument(parent, name), -1L);
    }

    DocumentOutputStream(DocumentEntry document, long limit) throws IOException {
        this(DocumentOutputStream.getDocument(document), limit);
    }

    DocumentOutputStream(POIFSDocument document, long limit) throws IOException {
        this._document = document;
        this._document.free();
        this._property = document.getDocumentProperty();
        this._limit = limit;
    }

    private static POIFSDocument getDocument(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage, " + document + " not a Document Node");
        }
        return new POIFSDocument((DocumentNode)document);
    }

    private static DocumentEntry createDocument(DirectoryEntry parent, String name) throws IOException {
        if (!(parent instanceof DirectoryNode)) {
            throw new IOException("Cannot open internal directory storage, " + parent + " not a Directory Node");
        }
        return parent.createDocument(name, (InputStream)new UnsynchronizedByteArrayInputStream(new byte[0]));
    }

    private void checkBufferSize() throws IOException {
        if (this._buffer.size() > 4096) {
            byte[] data = this._buffer.toByteArray();
            this._buffer = null;
            this.write(data, 0, data.length);
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.write(new byte[]{(byte)b}, 0, 1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (this._closed) {
            throw new IOException("cannot perform requested operation on a closed stream");
        }
        if (this._limit > -1L && this.size() + (long)len > this._limit) {
            throw new IOException("tried to write too much data");
        }
        if (this._buffer != null) {
            this._buffer.write(b, off, len);
            this.checkBufferSize();
        } else {
            if (this._stream == null) {
                this._stream = new POIFSStream(this._document.getFileSystem());
                this._stream_output = this._stream.getOutputStream();
            }
            this._stream_output.write(b, off, len);
            this._document_size += len;
        }
    }

    @Override
    public void close() throws IOException {
        if (this._buffer != null) {
            this._document.replaceContents(this._buffer.toInputStream());
        } else {
            this._stream_output.close();
            this._property.updateSize(this._document_size);
            this._property.setStartBlock(this._stream.getStartBlock());
        }
        this._closed = true;
    }

    public long size() {
        return (long)this._document_size + (this._buffer == null ? 0L : (long)this._buffer.size());
    }
}

