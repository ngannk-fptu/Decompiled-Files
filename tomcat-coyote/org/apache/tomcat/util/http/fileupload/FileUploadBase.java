/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import org.apache.tomcat.util.http.fileupload.ProgressListener;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.apache.tomcat.util.http.fileupload.util.FileItemHeadersImpl;
import org.apache.tomcat.util.http.fileupload.util.Streams;

public abstract class FileUploadBase {
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String MULTIPART = "multipart/";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String MULTIPART_MIXED = "multipart/mixed";
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

    public FileItemIterator getItemIterator(RequestContext ctx) throws FileUploadException, IOException {
        try {
            return new FileItemIteratorImpl(this, ctx);
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
            FileItemFactory fileItemFactory = Objects.requireNonNull(this.getFileItemFactory(), "No FileItemFactory has been set.");
            byte[] buffer = new byte[8192];
            while (iter.hasNext()) {
                if ((long)items.size() == this.fileCountMax) {
                    throw new FileCountLimitExceededException(ATTACHMENT, this.getFileCountMax());
                }
                FileItemStream item = iter.next();
                String fileName = item.getName();
                FileItem fileItem = fileItemFactory.createItem(item.getFieldName(), item.getContentType(), item.isFormField(), fileName);
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
        catch (FileUploadException e) {
            throw e;
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
            List mappedItems = itemsMap.computeIfAbsent(fieldName, k -> new ArrayList());
            mappedItems.add(fileItem);
        }
        return itemsMap;
    }

    public byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(contentType, new char[]{';', ','});
        String boundaryStr = params.get("boundary");
        if (boundaryStr == null) {
            return null;
        }
        byte[] boundary = boundaryStr.getBytes(StandardCharsets.ISO_8859_1);
        return boundary;
    }

    public String getFileName(FileItemHeaders headers) {
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

    public String getFieldName(FileItemHeaders headers) {
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

    public FileItemHeaders getParsedHeaders(String headerPart) {
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
                header.append(' ').append(headerPart, nonWs, end);
                start = end + 2;
            }
            this.parseHeaderLine(headers, header.toString());
        }
        return headers;
    }

    protected FileItemHeadersImpl newFileItemHeaders() {
        return new FileItemHeadersImpl();
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
        String headerValue = header.substring(colonOffset + 1).trim();
        headers.addHeader(headerName, headerValue);
    }

    public ProgressListener getProgressListener() {
        return this.listener;
    }

    public void setProgressListener(ProgressListener pListener) {
        this.listener = pListener;
    }
}

