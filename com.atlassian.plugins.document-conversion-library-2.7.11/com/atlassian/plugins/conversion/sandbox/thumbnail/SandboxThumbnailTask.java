/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.plugins.conversion.sandbox.thumbnail;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.sandbox.ConverterProvider;
import com.atlassian.plugins.conversion.sandbox.DefaultConverterProvider;
import com.atlassian.plugins.conversion.sandbox.DefaultFileOperation;
import com.atlassian.plugins.conversion.sandbox.FileOperation;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailRequest;
import com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailResponse;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.util.Objects;
import java.util.Optional;

public class SandboxThumbnailTask
implements SandboxTask<SandboxThumbnailRequest, SandboxThumbnailResponse> {
    private final ConverterProvider converterProvider;
    private final FileOperation fileOperation;

    @VisibleForTesting
    public SandboxThumbnailTask(ConverterProvider converterProvider, FileOperation fileOperation) {
        this.converterProvider = converterProvider;
        this.fileOperation = fileOperation;
    }

    public SandboxThumbnailTask() {
        this(new DefaultConverterProvider(), new DefaultFileOperation());
    }

    public SandboxThumbnailResponse apply(SandboxTaskContext sandboxTaskContext, SandboxThumbnailRequest sandboxThumbnailRequest) {
        Optional<AbstractConverter> mayBeConverter = this.converterProvider.getConverter(sandboxThumbnailRequest.getFileFormat());
        if (!mayBeConverter.isPresent()) {
            this.log("Can't find converter for " + (Object)((Object)sandboxThumbnailRequest.getFileFormat()));
            return new SandboxThumbnailResponse(SandboxConversionStatus.ERROR);
        }
        AbstractConverter converter = mayBeConverter.get();
        int pageNumber = this.getValueOrDefault(sandboxThumbnailRequest.getPageNumber());
        int width = sandboxThumbnailRequest.getWidth();
        int height = sandboxThumbnailRequest.getHeight();
        FileFormat inputFileFormat = sandboxThumbnailRequest.getFileFormat();
        File convertedFile = Objects.requireNonNull(sandboxThumbnailRequest.getConvertedFile());
        if (Files.exists(convertedFile.toPath(), new LinkOption[0])) {
            this.log("Converted file param is exist. Will skip conversion process now");
            return new SandboxThumbnailResponse(SandboxConversionStatus.CONVERTED);
        }
        File inputFile = Objects.requireNonNull(sandboxThumbnailRequest.getInputFile());
        File tempFile = Objects.requireNonNull(sandboxThumbnailRequest.getTempFile());
        this.log("Doing thumbnail generation for: " + inputFile.getAbsolutePath() + " with page " + pageNumber);
        this.log("With input params. Page Number = " + pageNumber + ", ,maxWidth = " + width + ", maxHeight = " + height);
        this.log("---With input file is " + inputFile);
        this.log("---With temp file is " + tempFile);
        this.log("---With converted file is " + convertedFile);
        try {
            try (InputStream inputStream = this.newFileInputStream(inputFile);
                 OutputStream outputStream = this.newFileOutputStream(tempFile);){
                converter.generateThumbnailDirect(inputFileFormat, FileFormat.JPG, inputStream, outputStream, pageNumber, width, height);
            }
            this.fileOperation.move(tempFile.toPath(), convertedFile.toPath());
        }
        catch (Exception ex) {
            this.log("Exception when generate thumbnail: " + this.getStackTrace(ex));
            return new SandboxThumbnailResponse(SandboxConversionStatus.ERROR);
        }
        this.log("Done thumbnail generation for: " + inputFile.getName() + " with page " + pageNumber);
        return new SandboxThumbnailResponse(SandboxConversionStatus.CONVERTED);
    }

    @VisibleForTesting
    protected InputStream newFileInputStream(File inputFile) throws IOException {
        return Files.newInputStream(inputFile.toPath(), new OpenOption[0]);
    }

    @VisibleForTesting
    protected OutputStream newFileOutputStream(File outputFile) throws IOException {
        return Files.newOutputStream(outputFile.toPath(), new OpenOption[0]);
    }

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    public SandboxSerializer<SandboxThumbnailRequest> inputSerializer() {
        return SandboxThumbnailRequest.serializer();
    }

    public SandboxSerializer<SandboxThumbnailResponse> outputSerializer() {
        return SandboxThumbnailResponse.serializer();
    }

    private int getValueOrDefault(int input) {
        if (input <= 0) {
            return 0;
        }
        return input;
    }

    private void log(String message) {
        System.err.println(message);
        System.err.flush();
    }
}

