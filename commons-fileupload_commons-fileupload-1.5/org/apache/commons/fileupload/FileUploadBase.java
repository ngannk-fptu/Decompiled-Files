/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.io.IOUtils
 */
package org.apache.commons.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileCountLimitExceededException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.fileupload.util.Closeable;
import org.apache.commons.fileupload.util.FileItemHeadersImpl;
import org.apache.commons.fileupload.util.LimitedInputStream;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

public abstract class FileUploadBase {
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String MULTIPART = "multipart/";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String MULTIPART_MIXED = "multipart/mixed";
    @Deprecated
    public static final int MAX_HEADER_SIZE = 1024;
    private long sizeMax = -1L;
    private long fileSizeMax = -1L;
    private long fileCountMax = -1L;
    private String headerEncoding;
    private ProgressListener listener;

    public static final boolean isMultipartContent(RequestContext ctx) {
        String contentType = ctx.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART);
    }

    @Deprecated
    public static boolean isMultipartContent(HttpServletRequest req) {
        return ServletFileUpload.isMultipartContent(req);
    }

    public abstract FileItemFactory getFileItemFactory();

    public abstract void setFileItemFactory(FileItemFactory var1);

    public long getSizeMax() {
        return this.sizeMax;
    }

    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    public long getFileSizeMax() {
        return this.fileSizeMax;
    }

    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    public long getFileCountMax() {
        return this.fileCountMax;
    }

    public void setFileCountMax(long fileCountMax) {
        this.fileCountMax = fileCountMax;
    }

    public String getHeaderEncoding() {
        return this.headerEncoding;
    }

    public void setHeaderEncoding(String encoding) {
        this.headerEncoding = encoding;
    }

    @Deprecated
    public List<FileItem> parseRequest(HttpServletRequest req) throws FileUploadException {
        return this.parseRequest(new ServletRequestContext(req));
    }

    public FileItemIterator getItemIterator(RequestContext ctx) throws FileUploadException, IOException {
        try {
            return new FileItemIteratorImpl(ctx);
        }
        catch (FileUploadIOException e) {
            throw (FileUploadException)e.getCause();
        }
    }

    public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
        ArrayList<FileItem> items = new ArrayList<FileItem>();
        boolean successful = false;
        try {
            FileItemIterator iter = this.getItemIterator(ctx);
            FileItemFactory fac = this.getFileItemFactory();
            byte[] buffer = new byte[8192];
            if (fac == null) {
                throw new NullPointerException("No FileItemFactory has been set.");
            }
            while (iter.hasNext()) {
                if ((long)items.size() == this.fileCountMax) {
                    throw new FileCountLimitExceededException(ATTACHMENT, this.getFileCountMax());
                }
                FileItemStream item = iter.next();
                String fileName = ((FileItemIteratorImpl.FileItemStreamImpl)item).name;
                FileItem fileItem = fac.createItem(item.getFieldName(), item.getContentType(), item.isFormField(), fileName);
                items.add(fileItem);
                try {
                    Streams.copy(item.openStream(), fileItem.getOutputStream(), true, buffer);
                }
                catch (FileUploadIOException e) {
                    throw (FileUploadException)e.getCause();
                }
                catch (IOException e) {
                    throw new IOFileUploadException(String.format("Processing of %s request failed. %s", MULTIPART_FORM_DATA, e.getMessage()), e);
                }
                FileItemHeaders fih = item.getHeaders();
                fileItem.setHeaders(fih);
            }
            successful = true;
            ArrayList<FileItem> arrayList = items;
            return arrayList;
        }
        catch (FileUploadIOException e) {
            throw (FileUploadException)e.getCause();
        }
        catch (IOException e) {
            throw new FileUploadException(e.getMessage(), e);
        }
        finally {
            if (!successful) {
                for (FileItem fileItem : items) {
                    try {
                        fileItem.delete();
                    }
                    catch (Exception exception) {}
                }
            }
        }
    }

    public Map<String, List<FileItem>> parseParameterMap(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = this.parseRequest(ctx);
        HashMap<String, List<FileItem>> itemsMap = new HashMap<String, List<FileItem>>(items.size());
        for (FileItem fileItem : items) {
            String fieldName = fileItem.getFieldName();
            ArrayList<FileItem> mappedItems = (ArrayList<FileItem>)itemsMap.get(fieldName);
            if (mappedItems == null) {
                mappedItems = new ArrayList<FileItem>();
                itemsMap.put(fieldName, mappedItems);
            }
            mappedItems.add(fileItem);
        }
        return itemsMap;
    }

    protected byte[] getBoundary(String contentType) {
        byte[] boundary;
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(contentType, new char[]{';', ','});
        String boundaryStr = params.get("boundary");
        if (boundaryStr == null) {
            return null;
        }
        try {
            boundary = boundaryStr.getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            boundary = boundaryStr.getBytes();
        }
        return boundary;
    }

    @Deprecated
    protected String getFileName(Map<String, String> headers) {
        return this.getFileName(this.getHeader(headers, CONTENT_DISPOSITION));
    }

    protected String getFileName(FileItemHeaders headers) {
        return this.getFileName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFileName(String pContentDisposition) {
        String cdl;
        String fileName = null;
        if (pContentDisposition != null && ((cdl = pContentDisposition.toLowerCase(Locale.ENGLISH)).startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT))) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            Map<String, String> params = parser.parse(pContentDisposition, ';');
            if (params.containsKey("filename")) {
                fileName = params.get("filename");
                fileName = fileName != null ? fileName.trim() : "";
            }
        }
        return fileName;
    }

    protected String getFieldName(FileItemHeaders headers) {
        return this.getFieldName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFieldName(String pContentDisposition) {
        String fieldName = null;
        if (pContentDisposition != null && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            Map<String, String> params = parser.parse(pContentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }
        return fieldName;
    }

    @Deprecated
    protected String getFieldName(Map<String, String> headers) {
        return this.getFieldName(this.getHeader(headers, CONTENT_DISPOSITION));
    }

    @Deprecated
    protected FileItem createItem(Map<String, String> headers, boolean isFormField) throws FileUploadException {
        return this.getFileItemFactory().createItem(this.getFieldName(headers), this.getHeader(headers, CONTENT_TYPE), isFormField, this.getFileName(headers));
    }

    protected FileItemHeaders getParsedHeaders(String headerPart) {
        int end;
        int len = headerPart.length();
        FileItemHeadersImpl headers = this.newFileItemHeaders();
        int start = 0;
        while (start != (end = this.parseEndOfLine(headerPart, start))) {
            StringBuilder header = new StringBuilder(headerPart.substring(start, end));
            start = end + 2;
            while (start < len) {
                char c;
                int nonWs;
                for (nonWs = start; nonWs < len && ((c = headerPart.charAt(nonWs)) == ' ' || c == '\t'); ++nonWs) {
                }
                if (nonWs == start) break;
                end = this.parseEndOfLine(headerPart, nonWs);
                header.append(" ").append(headerPart.substring(nonWs, end));
                start = end + 2;
            }
            this.parseHeaderLine(headers, header.toString());
        }
        return headers;
    }

    protected FileItemHeadersImpl newFileItemHeaders() {
        return new FileItemHeadersImpl();
    }

    @Deprecated
    protected Map<String, String> parseHeaders(String headerPart) {
        FileItemHeaders headers = this.getParsedHeaders(headerPart);
        HashMap<String, String> result = new HashMap<String, String>();
        Iterator<String> iter = headers.getHeaderNames();
        while (iter.hasNext()) {
            String headerName = iter.next();
            Iterator<String> iter2 = headers.getHeaders(headerName);
            StringBuilder headerValue = new StringBuilder(iter2.next());
            while (iter2.hasNext()) {
                headerValue.append(",").append(iter2.next());
            }
            result.put(headerName, headerValue.toString());
        }
        return result;
    }

    private int parseEndOfLine(String headerPart, int end) {
        int index = end;
        while (true) {
            int offset;
            if ((offset = headerPart.indexOf(13, index)) == -1 || offset + 1 >= headerPart.length()) {
                throw new IllegalStateException("Expected headers to be terminated by an empty line.");
            }
            if (headerPart.charAt(offset + 1) == '\n') {
                return offset;
            }
            index = offset + 1;
        }
    }

    private void parseHeaderLine(FileItemHeadersImpl headers, String header) {
        int colonOffset = header.indexOf(58);
        if (colonOffset == -1) {
            return;
        }
        String headerName = header.substring(0, colonOffset).trim();
        String headerValue = header.substring(header.indexOf(58) + 1).trim();
        headers.addHeader(headerName, headerValue);
    }

    @Deprecated
    protected final String getHeader(Map<String, String> headers, String name) {
        return headers.get(name.toLowerCase(Locale.ENGLISH));
    }

    public ProgressListener getProgressListener() {
        return this.listener;
    }

    public void setProgressListener(ProgressListener pListener) {
        this.listener = pListener;
    }

    public static class FileSizeLimitExceededException
    extends SizeException {
        private static final long serialVersionUID = 8150776562029630058L;
        private String fileName;
        private String fieldName;

        public FileSizeLimitExceededException(String message, long actual, long permitted) {
            super(message, actual, permitted);
        }

        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String pFileName) {
            this.fileName = pFileName;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public void setFieldName(String pFieldName) {
            this.fieldName = pFieldName;
        }
    }

    public static class SizeLimitExceededException
    extends SizeException {
        private static final long serialVersionUID = -2474893167098052828L;

        @Deprecated
        public SizeLimitExceededException() {
            this(null, 0L, 0L);
        }

        @Deprecated
        public SizeLimitExceededException(String message) {
            this(message, 0L, 0L);
        }

        public SizeLimitExceededException(String message, long actual, long permitted) {
            super(message, actual, permitted);
        }
    }

    @Deprecated
    public static class UnknownSizeException
    extends FileUploadException {
        private static final long serialVersionUID = 7062279004812015273L;

        public UnknownSizeException() {
        }

        public UnknownSizeException(String message) {
            super(message);
        }
    }

    protected static abstract class SizeException
    extends FileUploadException {
        private static final long serialVersionUID = -8776225574705254126L;
        private final long actual;
        private final long permitted;

        protected SizeException(String message, long actual, long permitted) {
            super(message);
            this.actual = actual;
            this.permitted = permitted;
        }

        public long getActualSize() {
            return this.actual;
        }

        public long getPermittedSize() {
            return this.permitted;
        }
    }

    public static class IOFileUploadException
    extends FileUploadException {
        private static final long serialVersionUID = 1749796615868477269L;
        private final IOException cause;

        public IOFileUploadException(String pMsg, IOException pException) {
            super(pMsg);
            this.cause = pException;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }

    public static class InvalidContentTypeException
    extends FileUploadException {
        private static final long serialVersionUID = -9073026332015646668L;

        public InvalidContentTypeException() {
        }

        public InvalidContentTypeException(String message) {
            super(message);
        }

        public InvalidContentTypeException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class FileUploadIOException
    extends IOException {
        private static final long serialVersionUID = -7047616958165584154L;
        private final FileUploadException cause;

        public FileUploadIOException(FileUploadException pCause) {
            this.cause = pCause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }

    private class FileItemIteratorImpl
    implements FileItemIterator {
        private final MultipartStream multi;
        private final MultipartStream.ProgressNotifier notifier;
        private final byte[] boundary;
        private FileItemStreamImpl currentItem;
        private String currentFieldName;
        private boolean skipPreamble;
        private boolean itemValid;
        private boolean eof;

        FileItemIteratorImpl(RequestContext ctx) throws FileUploadException, IOException {
            InputStream input;
            long requestSize;
            if (ctx == null) {
                throw new NullPointerException("ctx parameter");
            }
            String contentType = ctx.getContentType();
            if (null == contentType || !contentType.toLowerCase(Locale.ENGLISH).startsWith(FileUploadBase.MULTIPART)) {
                throw new InvalidContentTypeException(String.format("the request doesn't contain a %s or %s stream, content type header is %s", FileUploadBase.MULTIPART_FORM_DATA, FileUploadBase.MULTIPART_MIXED, contentType));
            }
            int contentLengthInt = ctx.getContentLength();
            long l = requestSize = UploadContext.class.isAssignableFrom(ctx.getClass()) ? ((UploadContext)ctx).contentLength() : (long)contentLengthInt;
            if (FileUploadBase.this.sizeMax >= 0L) {
                if (requestSize != -1L && requestSize > FileUploadBase.this.sizeMax) {
                    throw new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", requestSize, FileUploadBase.this.sizeMax), requestSize, FileUploadBase.this.sizeMax);
                }
                input = new LimitedInputStream(ctx.getInputStream(), FileUploadBase.this.sizeMax){

                    @Override
                    protected void raiseError(long pSizeMax, long pCount) throws IOException {
                        SizeLimitExceededException ex = new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", pCount, pSizeMax), pCount, pSizeMax);
                        throw new FileUploadIOException(ex);
                    }
                };
            } else {
                input = ctx.getInputStream();
            }
            String charEncoding = FileUploadBase.this.headerEncoding;
            if (charEncoding == null) {
                charEncoding = ctx.getCharacterEncoding();
            }
            this.boundary = FileUploadBase.this.getBoundary(contentType);
            if (this.boundary == null) {
                IOUtils.closeQuietly((InputStream)input);
                throw new FileUploadException("the request was rejected because no multipart boundary was found");
            }
            this.notifier = new MultipartStream.ProgressNotifier(FileUploadBase.this.listener, requestSize);
            try {
                this.multi = new MultipartStream(input, this.boundary, this.notifier);
            }
            catch (IllegalArgumentException iae) {
                IOUtils.closeQuietly((InputStream)input);
                throw new InvalidContentTypeException(String.format("The boundary specified in the %s header is too long", FileUploadBase.CONTENT_TYPE), iae);
            }
            this.multi.setHeaderEncoding(charEncoding);
            this.skipPreamble = true;
            this.findNextItem();
        }

        private boolean findNextItem() throws IOException {
            if (this.eof) {
                return false;
            }
            if (this.currentItem != null) {
                this.currentItem.close();
                this.currentItem = null;
            }
            while (true) {
                boolean nextPart;
                if (!(nextPart = this.skipPreamble ? this.multi.skipPreamble() : this.multi.readBoundary())) {
                    if (this.currentFieldName == null) {
                        this.eof = true;
                        return false;
                    }
                    this.multi.setBoundary(this.boundary);
                    this.currentFieldName = null;
                    continue;
                }
                FileItemHeaders headers = FileUploadBase.this.getParsedHeaders(this.multi.readHeaders());
                if (this.currentFieldName == null) {
                    String fieldName = FileUploadBase.this.getFieldName(headers);
                    if (fieldName != null) {
                        String subContentType = headers.getHeader(FileUploadBase.CONTENT_TYPE);
                        if (subContentType != null && subContentType.toLowerCase(Locale.ENGLISH).startsWith(FileUploadBase.MULTIPART_MIXED)) {
                            this.currentFieldName = fieldName;
                            byte[] subBoundary = FileUploadBase.this.getBoundary(subContentType);
                            this.multi.setBoundary(subBoundary);
                            this.skipPreamble = true;
                            continue;
                        }
                        String fileName = FileUploadBase.this.getFileName(headers);
                        this.currentItem = new FileItemStreamImpl(fileName, fieldName, headers.getHeader(FileUploadBase.CONTENT_TYPE), fileName == null, this.getContentLength(headers));
                        this.currentItem.setHeaders(headers);
                        this.notifier.noteItem();
                        this.itemValid = true;
                        return true;
                    }
                } else {
                    String fileName = FileUploadBase.this.getFileName(headers);
                    if (fileName != null) {
                        this.currentItem = new FileItemStreamImpl(fileName, this.currentFieldName, headers.getHeader(FileUploadBase.CONTENT_TYPE), false, this.getContentLength(headers));
                        this.currentItem.setHeaders(headers);
                        this.notifier.noteItem();
                        this.itemValid = true;
                        return true;
                    }
                }
                this.multi.discardBodyData();
            }
        }

        private long getContentLength(FileItemHeaders pHeaders) {
            try {
                return Long.parseLong(pHeaders.getHeader(FileUploadBase.CONTENT_LENGTH));
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

        class FileItemStreamImpl
        implements FileItemStream {
            private final String contentType;
            private final String fieldName;
            private final String name;
            private final boolean formField;
            private final InputStream stream;
            private boolean opened;
            private FileItemHeaders headers;

            FileItemStreamImpl(String pName, String pFieldName, String pContentType, boolean pFormField, long pContentLength) throws IOException {
                MultipartStream.ItemInputStream itemStream;
                this.name = pName;
                this.fieldName = pFieldName;
                this.contentType = pContentType;
                this.formField = pFormField;
                if (FileUploadBase.this.fileSizeMax != -1L && pContentLength != -1L && pContentLength > FileUploadBase.this.fileSizeMax) {
                    FileSizeLimitExceededException e = new FileSizeLimitExceededException(String.format("The field %s exceeds its maximum permitted size of %s bytes.", this.fieldName, FileUploadBase.this.fileSizeMax), pContentLength, FileUploadBase.this.fileSizeMax);
                    e.setFileName(pName);
                    e.setFieldName(pFieldName);
                    throw new FileUploadIOException(e);
                }
                InputStream istream = itemStream = FileItemIteratorImpl.this.multi.newInputStream();
                if (FileUploadBase.this.fileSizeMax != -1L) {
                    istream = new LimitedInputStream(istream, FileUploadBase.this.fileSizeMax){

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
                if (this.opened) {
                    throw new IllegalStateException("The stream was already opened.");
                }
                if (((Closeable)((Object)this.stream)).isClosed()) {
                    throw new FileItemStream.ItemSkippedException();
                }
                return this.stream;
            }

            void close() throws IOException {
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
    }
}

