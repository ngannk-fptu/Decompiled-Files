/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.sandbox.DefaultFileOperation
 *  com.atlassian.plugins.conversion.sandbox.FileOperation
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.plugins.conversion.convert.image;

import com.atlassian.confluence.plugins.conversion.convert.image.PdfConversionSupport;
import com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailRequest;
import com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailResponse;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.sandbox.DefaultFileOperation;
import com.atlassian.plugins.conversion.sandbox.FileOperation;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
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

public class SandboxPdfThumbnailTask
implements SandboxTask<SandboxPdfThumbnailRequest, SandboxPdfThumbnailResponse> {
    private final FileOperation fileOperation;

    @VisibleForTesting
    public SandboxPdfThumbnailTask(FileOperation fileOperation) {
        this.fileOperation = fileOperation;
    }

    public SandboxPdfThumbnailTask() {
        this((FileOperation)new DefaultFileOperation());
    }

    public SandboxPdfThumbnailResponse apply(SandboxTaskContext sandboxTaskContext, SandboxPdfThumbnailRequest sandboxThumbnailRequest) {
        FileFormat inFileFormat = sandboxThumbnailRequest.getFileFormat();
        if (FileFormat.PDF != inFileFormat) {
            this.log("Can't find converter for " + sandboxThumbnailRequest.getFileFormat());
            return new SandboxPdfThumbnailResponse(SandboxConversionStatus.ERROR);
        }
        PdfConversionSupport converter = new PdfConversionSupport();
        int pageNumber = this.getValueOrDefault(sandboxThumbnailRequest.getPageNumber());
        int width = sandboxThumbnailRequest.getWidth();
        int height = sandboxThumbnailRequest.getHeight();
        FileFormat inputFileFormat = sandboxThumbnailRequest.getFileFormat();
        File convertedFile = Objects.requireNonNull(sandboxThumbnailRequest.getConvertedFile());
        if (Files.exists(convertedFile.toPath(), new LinkOption[0])) {
            this.log("Converted file param is exist. Will skip conversion process now");
            return new SandboxPdfThumbnailResponse(SandboxConversionStatus.CONVERTED);
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
            return new SandboxPdfThumbnailResponse(SandboxConversionStatus.ERROR);
        }
        this.log("Done thumbnail generation for: " + inputFile.getName() + " with page " + pageNumber);
        return new SandboxPdfThumbnailResponse(SandboxConversionStatus.CONVERTED);
    }

    @VisibleForTesting
    protected PdfConversionSupport getPdfConversionSupport() {
        return new PdfConversionSupport();
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

    public SandboxSerializer<SandboxPdfThumbnailRequest> inputSerializer() {
        return SandboxPdfThumbnailRequest.serializer();
    }

    public SandboxSerializer<SandboxPdfThumbnailResponse> outputSerializer() {
        return SandboxPdfThumbnailResponse.serializer();
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

