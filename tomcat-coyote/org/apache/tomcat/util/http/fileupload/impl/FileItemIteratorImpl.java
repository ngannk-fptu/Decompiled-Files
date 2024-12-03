/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.apache.tomcat.util.http.fileupload.impl.FileItemStreamImpl;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;

public class FileItemIteratorImpl
implements FileItemIterator {
    private final FileUploadBase fileUploadBase;
    private final RequestContext ctx;
    private long sizeMax;
    private long fileSizeMax;
    private MultipartStream multiPartStream;
    private MultipartStream.ProgressNotifier progressNotifier;
    private byte[] multiPartBoundary;
    private FileItemStreamImpl currentItem;
    private String currentFieldName;
    private boolean skipPreamble;
    private boolean itemValid;
    private boolean eof;

    @Override
    public long getSizeMax() {
        return this.sizeMax;
    }

    @Override
    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    @Override
    public long getFileSizeMax() {
        return this.fileSizeMax;
    }

    @Override
    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    public FileItemIteratorImpl(FileUploadBase fileUploadBase, RequestContext requestContext) throws FileUploadException, IOException {
        this.fileUploadBase = fileUploadBase;
        this.sizeMax = fileUploadBase.getSizeMax();
        this.fileSizeMax = fileUploadBase.getFileSizeMax();
        this.ctx = Objects.requireNonNull(requestContext, "requestContext");
        this.skipPreamble = true;
        this.findNextItem();
    }

    protected void init(FileUploadBase fileUploadBase, RequestContext pRequestContext) throws FileUploadException, IOException {
        InputStream input;
        String contentType = this.ctx.getContentType();
        if (null == contentType || !contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/")) {
            throw new InvalidContentTypeException(String.format("the request doesn't contain a %s or %s stream, content type header is %s", "multipart/form-data", "multipart/mixed", contentType));
        }
        long requestSize = ((UploadContext)this.ctx).contentLength();
        if (this.sizeMax >= 0L) {
            if (requestSize != -1L && requestSize > this.sizeMax) {
                throw new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", requestSize, this.sizeMax), requestSize, this.sizeMax);
            }
            input = new LimitedInputStream(this.ctx.getInputStream(), this.sizeMax){

                @Override
                protected void raiseError(long pSizeMax, long pCount) throws IOException {
                    SizeLimitExceededException ex = new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", pCount, pSizeMax), pCount, pSizeMax);
                    throw new FileUploadIOException(ex);
                }
            };
        } else {
            input = this.ctx.getInputStream();
        }
        String charEncoding = fileUploadBase.getHeaderEncoding();
        if (charEncoding == null) {
            charEncoding = this.ctx.getCharacterEncoding();
        }
        this.multiPartBoundary = fileUploadBase.getBoundary(contentType);
        if (this.multiPartBoundary == null) {
            IOUtils.closeQuietly(input);
            throw new FileUploadException("the request was rejected because no multipart boundary was found");
        }
        this.progressNotifier = new MultipartStream.ProgressNotifier(fileUploadBase.getProgressListener(), requestSize);
        try {
            this.multiPartStream = new MultipartStream(input, this.multiPartBoundary, this.progressNotifier);
        }
        catch (IllegalArgumentException iae) {
            IOUtils.closeQuietly(input);
            throw new InvalidContentTypeException(String.format("The boundary specified in the %s header is too long", "Content-type"), iae);
        }
        this.multiPartStream.setHeaderEncoding(charEncoding);
    }

    public MultipartStream getMultiPartStream() throws FileUploadException, IOException {
        if (this.multiPartStream == null) {
            this.init(this.fileUploadBase, this.ctx);
        }
        return this.multiPartStream;
    }

    private boolean findNextItem() throws FileUploadException, IOException {
        if (this.eof) {
            return false;
        }
        if (this.currentItem != null) {
            this.currentItem.close();
            this.currentItem = null;
        }
        MultipartStream multi = this.getMultiPartStream();
        while (true) {
            boolean nextPart;
            if (!(nextPart = this.skipPreamble ? multi.skipPreamble() : multi.readBoundary())) {
                if (this.currentFieldName == null) {
                    this.eof = true;
                    return false;
                }
                multi.setBoundary(this.multiPartBoundary);
                this.currentFieldName = null;
                continue;
            }
            FileItemHeaders headers = this.fileUploadBase.getParsedHeaders(multi.readHeaders());
            if (this.currentFieldName == null) {
                String fieldName = this.fileUploadBase.getFieldName(headers);
                if (fieldName != null) {
                    String subContentType = headers.getHeader("Content-type");
                    if (subContentType != null && subContentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/mixed")) {
                        this.currentFieldName = fieldName;
                        byte[] subBoundary = this.fileUploadBase.getBoundary(subContentType);
                        multi.setBoundary(subBoundary);
                        this.skipPreamble = true;
                        continue;
                    }
                    String fileName = this.fileUploadBase.getFileName(headers);
                    this.currentItem = new FileItemStreamImpl(this, fileName, fieldName, headers.getHeader("Content-type"), fileName == null, this.getContentLength(headers));
                    this.currentItem.setHeaders(headers);
                    this.progressNotifier.noteItem();
                    this.itemValid = true;
                    return true;
                }
            } else {
                String fileName = this.fileUploadBase.getFileName(headers);
                if (fileName != null) {
                    this.currentItem = new FileItemStreamImpl(this, fileName, this.currentFieldName, headers.getHeader("Content-type"), false, this.getContentLength(headers));
                    this.currentItem.setHeaders(headers);
                    this.progressNotifier.noteItem();
                    this.itemValid = true;
                    return true;
                }
            }
            multi.discardBodyData();
        }
    }

    private long getContentLength(FileItemHeaders pHeaders) {
        try {
            return Long.parseLong(pHeaders.getHeader("Content-length"));
        }
        catch (Exception e) {
            return -1L;
        }
    }

    @Override
    public boolean hasNext() throws FileUploadException, IOException {
        if (this.eof) {
            return false;
        }
        if (this.itemValid) {
            return true;
        }
        try {
            return this.findNextItem();
        }
        catch (FileUploadIOException e) {
            throw (FileUploadException)e.getCause();
        }
    }

    @Override
    public FileItemStream next() throws FileUploadException, IOException {
        if (this.eof || !this.itemValid && !this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.itemValid = false;
        return this.currentItem;
    }

    @Override
    public List<FileItem> getFileItems() throws FileUploadException, IOException {
        ArrayList<FileItem> items = new ArrayList<FileItem>();
        while (this.hasNext()) {
            FileItemStream fis = this.next();
            FileItem fi = this.fileUploadBase.getFileItemFactory().createItem(fis.getFieldName(), fis.getContentType(), fis.isFormField(), fis.getName());
            items.add(fi);
        }
        return items;
    }
}

