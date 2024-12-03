/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.impl;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.apache.tomcat.util.http.fileupload.util.Closeable;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.apache.tomcat.util.http.fileupload.util.Streams;

public class FileItemStreamImpl
implements FileItemStream {
    private final FileItemIteratorImpl fileItemIteratorImpl;
    private final String contentType;
    private final String fieldName;
    private final String name;
    private final boolean formField;
    private final InputStream stream;
    private FileItemHeaders headers;

    public FileItemStreamImpl(FileItemIteratorImpl pFileItemIterator, String pName, String pFieldName, String pContentType, boolean pFormField, long pContentLength) throws FileUploadException, IOException {
        MultipartStream.ItemInputStream itemStream;
        this.fileItemIteratorImpl = pFileItemIterator;
        this.name = pName;
        this.fieldName = pFieldName;
        this.contentType = pContentType;
        this.formField = pFormField;
        long fileSizeMax = this.fileItemIteratorImpl.getFileSizeMax();
        if (fileSizeMax != -1L && pContentLength != -1L && pContentLength > fileSizeMax) {
            FileSizeLimitExceededException e = new FileSizeLimitExceededException(String.format("The field %s exceeds its maximum permitted size of %s bytes.", this.fieldName, fileSizeMax), pContentLength, fileSizeMax);
            e.setFileName(pName);
            e.setFieldName(pFieldName);
            throw new FileUploadIOException(e);
        }
        InputStream istream = itemStream = this.fileItemIteratorImpl.getMultiPartStream().newInputStream();
        if (fileSizeMax != -1L) {
            istream = new LimitedInputStream(istream, fileSizeMax){

                @Override
                protected void raiseError(long pSizeMax, long pCount) throws IOException {
                    itemStream.close(true);
                    FileSizeLimitExceededException e = new FileSizeLimitExceededException(String.format("The field %s exceeds its maximum permitted size of %s bytes.", FileItemStreamImpl.this.fieldName, pSizeMax), pCount, pSizeMax);
                    e.setFieldName(FileItemStreamImpl.this.fieldName);
                    e.setFileName(FileItemStreamImpl.this.name);
                    throw new FileUploadIOException(e);
                }
            };
        }
        this.stream = istream;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getName() {
        return Streams.checkFileName(this.name);
    }

    @Override
    public boolean isFormField() {
        return this.formField;
    }

    @Override
    public InputStream openStream() throws IOException {
        if (((Closeable)((Object)this.stream)).isClosed()) {
            throw new FileItemStream.ItemSkippedException();
        }
        return this.stream;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    @Override
    public FileItemHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public void setHeaders(FileItemHeaders pHeaders) {
        this.headers = pHeaders;
    }
}

