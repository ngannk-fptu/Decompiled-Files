/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.disk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.http.fileupload.DeferredFileOutputStream;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import org.apache.tomcat.util.http.fileupload.util.Streams;

public class DiskFileItem
implements FileItem {
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final String UID = UUID.randomUUID().toString().replace('-', '_');
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private String fieldName;
    private final String contentType;
    private boolean isFormField;
    private final String fileName;
    private long size = -1L;
    private final int sizeThreshold;
    private final File repository;
    private byte[] cachedContent;
    private transient DeferredFileOutputStream dfos;
    private transient File tempFile;
    private FileItemHeaders headers;
    private String defaultCharset = "ISO-8859-1";

    public DiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository) {
        this.fieldName = fieldName;
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fileName = fileName;
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.isInMemory()) {
            return new FileInputStream(this.dfos.getFile());
        }
        if (this.cachedContent == null) {
            this.cachedContent = this.dfos.getData();
        }
        return new ByteArrayInputStream(this.cachedContent);
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    public String getCharSet() {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(this.getContentType(), ';');
        return params.get("charset");
    }

    @Override
    public String getName() {
        return Streams.checkFileName(this.fileName);
    }

    @Override
    public boolean isInMemory() {
        if (this.cachedContent != null) {
            return true;
        }
        return this.dfos.isInMemory();
    }

    @Override
    public long getSize() {
        if (this.size >= 0L) {
            return this.size;
        }
        if (this.cachedContent != null) {
            return this.cachedContent.length;
        }
        if (this.dfos.isInMemory()) {
            return this.dfos.getData().length;
        }
        return this.dfos.getFile().length();
    }

    @Override
    public byte[] get() throws UncheckedIOException {
        if (this.isInMemory()) {
            if (this.cachedContent == null && this.dfos != null) {
                this.cachedContent = this.dfos.getData();
            }
            return this.cachedContent != null ? (byte[])this.cachedContent.clone() : new byte[]{};
        }
        byte[] fileData = new byte[(int)this.getSize()];
        try (InputStream fis = Files.newInputStream(this.dfos.getFile().toPath(), new OpenOption[0]);){
            IOUtils.readFully(fis, fileData);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return fileData;
    }

    @Override
    public String getString(String charset) throws UnsupportedEncodingException, IOException {
        return new String(this.get(), charset);
    }

    @Override
    public String getString() {
        try {
            byte[] rawData = this.get();
            String charset = this.getCharSet();
            if (charset == null) {
                charset = this.defaultCharset;
            }
            return new String(rawData, charset);
        }
        catch (IOException e) {
            return "";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(File file) throws Exception {
        if (this.isInMemory()) {
            try (OutputStream fout = Files.newOutputStream(file.toPath(), new OpenOption[0]);){
                fout.write(this.get());
            }
        }
        File outputFile = this.getStoreLocation();
        if (outputFile == null) {
            throw new FileUploadException("Cannot write uploaded file to disk!");
        }
        this.size = outputFile.length();
        if (file.exists() && !file.delete()) {
            throw new FileUploadException("Cannot write uploaded file to disk!");
        }
        if (!outputFile.renameTo(file)) {
            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(outputFile));
                out = new BufferedOutputStream(new FileOutputStream(file));
                IOUtils.copy(in, out);
                out.close();
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
                throw throwable;
            }
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public void delete() {
        this.cachedContent = null;
        File outputFile = this.getStoreLocation();
        if (outputFile != null && !this.isInMemory() && outputFile.exists() && !outputFile.delete()) {
            String desc = "Cannot delete " + outputFile.toString();
            throw new UncheckedIOException(desc, new IOException(desc));
        }
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean isFormField() {
        return this.isFormField;
    }

    @Override
    public void setFormField(boolean state) {
        this.isFormField = state;
    }

    @Override
    public OutputStream getOutputStream() {
        if (this.dfos == null) {
            File outputFile = this.getTempFile();
            this.dfos = new DeferredFileOutputStream(this.sizeThreshold, outputFile);
        }
        return this.dfos;
    }

    public File getStoreLocation() {
        if (this.dfos == null) {
            return null;
        }
        if (this.isInMemory()) {
            return null;
        }
        return this.dfos.getFile();
    }

    protected void finalize() {
        if (this.dfos == null || this.dfos.isInMemory()) {
            return;
        }
        File outputFile = this.dfos.getFile();
        if (outputFile != null && outputFile.exists()) {
            outputFile.delete();
        }
    }

    protected File getTempFile() {
        if (this.tempFile == null) {
            File tempDir = this.repository;
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"));
            }
            String tempFileName = String.format("upload_%s_%s.tmp", UID, DiskFileItem.getUniqueId());
            this.tempFile = new File(tempDir, tempFileName);
        }
        return this.tempFile;
    }

    private static String getUniqueId() {
        int limit = 100000000;
        int current = COUNTER.getAndIncrement();
        String id = Integer.toString(current);
        if (current < 100000000) {
            id = ("00000000" + id).substring(id.length());
        }
        return id;
    }

    public String toString() {
        return String.format("name=%s, StoreLocation=%s, size=%s bytes, isFormField=%s, FieldName=%s", this.getName(), this.getStoreLocation(), this.getSize(), this.isFormField(), this.getFieldName());
    }

    @Override
    public FileItemHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public void setHeaders(FileItemHeaders pHeaders) {
        this.headers = pHeaders;
    }

    public String getDefaultCharset() {
        return this.defaultCharset;
    }

    public void setDefaultCharset(String charset) {
        this.defaultCharset = charset;
    }
}

