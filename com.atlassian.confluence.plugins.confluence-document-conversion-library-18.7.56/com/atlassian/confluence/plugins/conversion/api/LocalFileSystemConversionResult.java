/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException
 *  com.atlassian.confluence.web.rangerequest.RangeRequest
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  javax.ws.rs.core.StreamingOutput
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.plugins.conversion.api;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.api.ConversionData;
import com.atlassian.confluence.plugins.conversion.api.ConversionResult;
import com.atlassian.confluence.plugins.conversion.api.ConversionStatus;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.IOUtils;

public class LocalFileSystemConversionResult
extends ConversionResult {
    public final ConversionStatus conversionStatus;
    private final File file;
    private final Supplier<String> contentTypeSupplier;

    public LocalFileSystemConversionResult(ConversionType conversionType, Attachment attachment, ConversionStatus conversionStatus, String conversionUrl, File file) {
        super(attachment, conversionUrl);
        this.conversionStatus = Objects.requireNonNull(conversionStatus);
        this.file = Objects.requireNonNull(file);
        this.contentTypeSupplier = Suppliers.memoize(this.getContentTypeSupplier(Objects.requireNonNull(conversionType), file));
    }

    private Supplier<String> getContentTypeSupplier(ConversionType conversionType, File file) {
        return () -> {
            String string;
            if (ConversionType.POSTER.equals((Object)conversionType) || ConversionType.THUMBNAIL.equals((Object)conversionType)) {
                return FileFormat.JPG.getDefaultMimeType();
            }
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath() + "mime");
            try {
                string = IOUtils.toString((InputStream)fileInputStream);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        fileInputStream.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return null;
                }
            }
            fileInputStream.close();
            return string;
        };
    }

    @Override
    public ConversionStatus getConversionStatus() {
        return this.conversionStatus;
    }

    @Override
    public ConversionData getConversionData(Optional<String> rangeHeader) throws RangeNotSatisfiableException, FileNotFoundException {
        Objects.requireNonNull(rangeHeader);
        if (!this.file.exists() || this.file.length() == 0L) {
            throw new FileNotFoundException("File does not exist: " + this.file.getAbsolutePath());
        }
        final RangeRequest rangeRequest = rangeHeader.isPresent() ? RangeRequest.parse((String)rangeHeader.get(), (long)this.file.length()) : null;
        return new ConversionData(){

            @Override
            public StreamingOutput getStreamingOutput() {
                return outputStream -> {
                    try (FileInputStream fileInputStream = new FileInputStream(LocalFileSystemConversionResult.this.file);){
                        if (rangeRequest == null) {
                            IOUtils.copy((InputStream)fileInputStream, (OutputStream)outputStream);
                        } else {
                            fileInputStream.getChannel().transferTo(rangeRequest.getOffset(), rangeRequest.getEnd() - rangeRequest.getOffset() + 1L, Channels.newChannel(outputStream));
                        }
                    }
                    finally {
                        outputStream.close();
                    }
                };
            }

            @Override
            public long getContentLength() {
                return rangeRequest == null ? LocalFileSystemConversionResult.this.file.length() : rangeRequest.getEnd() - rangeRequest.getOffset() + 1L;
            }

            @Override
            public Optional<String> getContentRange() {
                return rangeRequest == null ? Optional.empty() : Optional.of("bytes " + rangeRequest.getOffset() + "-" + rangeRequest.getEnd() + "/" + LocalFileSystemConversionResult.this.file.length());
            }

            @Override
            public String getContentType() {
                return (String)LocalFileSystemConversionResult.this.contentTypeSupplier.get();
            }
        };
    }

    @Override
    public Optional<String> getContentType() {
        return Optional.ofNullable((String)this.contentTypeSupplier.get());
    }
}

