/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.plugins.conversion.sandbox;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.sandbox.ConverterProvider;
import com.atlassian.plugins.conversion.sandbox.DefaultConverterProvider;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionResponse;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

public class SandboxConversionTask
implements SandboxTask<SandboxConversionRequest, SandboxConversionResponse> {
    private static final int THUMB_PAGE = 1;
    private static final int THUMB_WIDTH = 320;
    private static final int THUMB_HEIGHT = 320;
    private static final String MIME_SUFFIX = "mime";
    private final ConverterProvider converterProvider = new DefaultConverterProvider();

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public SandboxConversionResponse apply(SandboxTaskContext context, SandboxConversionRequest request) {
        Optional<AbstractConverter> mayBeConverter = this.converterProvider.getConverter(request.getFileFormat());
        if (!mayBeConverter.isPresent()) {
            this.log("Can't find converter for " + (Object)((Object)request.getFileFormat()));
            return new SandboxConversionResponse(SandboxConversionStatus.ERROR);
        }
        File convertedFile = request.getConvertedFile();
        File errorFile = request.getErrorFile();
        if (Files.exists(convertedFile.toPath(), new LinkOption[0])) {
            return new SandboxConversionResponse(SandboxConversionStatus.CONVERTED);
        }
        if (Files.exists(errorFile.toPath(), new LinkOption[0])) {
            return new SandboxConversionResponse(SandboxConversionStatus.ERROR);
        }
        try (FileInputStream input = new FileInputStream(request.getInputFile());){
            File tempFile = request.getTempFile();
            FileFormat fileFormat = request.getFileFormat();
            switch (request.getConversionType()) {
                case THUMBNAIL: {
                    this.createThumbnail(mayBeConverter.get(), fileFormat, input, tempFile);
                    Files.move(tempFile.toPath(), convertedFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
                    return new SandboxConversionResponse(SandboxConversionStatus.CONVERTED);
                }
                case DOCUMENT: {
                    File mineTypeTempFile = this.getMineTypeFile(request.getTempFile());
                    File mineTypeFile = this.getMineTypeFile(request.getConvertedFile());
                    this.createDocument(mayBeConverter.get(), fileFormat, input, tempFile, mineTypeTempFile);
                    Files.move(tempFile.toPath(), convertedFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
                    Files.move(mineTypeTempFile.toPath(), mineTypeFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
                    return new SandboxConversionResponse(SandboxConversionStatus.CONVERTED);
                }
            }
            return new SandboxConversionResponse(SandboxConversionStatus.CONVERTED);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SandboxSerializer<SandboxConversionRequest> inputSerializer() {
        return SandboxConversionRequest.serializer();
    }

    public SandboxSerializer<SandboxConversionResponse> outputSerializer() {
        return SandboxConversionResponse.serializer();
    }

    private void createDocument(AbstractConverter converter, FileFormat inputFormat, InputStream input, File tempFile, File mimeTypeTempFile) throws Exception {
        FileFormat outputFormat = converter.getBestOutputFormat(inputFormat);
        if (outputFormat != null) {
            try (FileOutputStream mimeTypeOutput = new FileOutputStream(mimeTypeTempFile);){
                IOUtils.write((String)outputFormat.getDefaultMimeType(), (OutputStream)mimeTypeOutput, (Charset)StandardCharsets.UTF_8);
            }
            try (FileOutputStream output = new FileOutputStream(tempFile);){
                converter.convertDocDirect(inputFormat, outputFormat, input, output);
            }
        }
    }

    private void createThumbnail(AbstractConverter converter, FileFormat inputFormat, InputStream input, File tempFile) throws Exception {
        try (FileOutputStream output = new FileOutputStream(tempFile);){
            converter.generateThumbnailDirect(inputFormat, FileFormat.JPG, input, output, 1, 320.0, 320.0);
        }
    }

    private File getMineTypeFile(File convertedFile) {
        return new File(convertedFile.getAbsoluteFile() + MIME_SUFFIX);
    }

    private void log(String message) {
        System.err.println(message);
        System.err.flush();
    }
}

