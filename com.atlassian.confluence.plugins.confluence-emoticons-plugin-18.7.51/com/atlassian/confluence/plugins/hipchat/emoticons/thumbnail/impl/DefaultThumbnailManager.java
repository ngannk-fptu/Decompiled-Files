/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.SavableAttachment
 *  com.atlassian.plugins.conversion.convert.ConversionException
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.image.ImagingConverter
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail.impl;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail.FormatDetector;
import com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail.ThumbnailManager;
import com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail.ThumbnailSize;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.ImagingConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultThumbnailManager
implements ThumbnailManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultThumbnailManager.class);
    private static final Integer PERMITS_SIZE = Integer.getInteger("emoticon.thumbnail.generator.permits.size", Runtime.getRuntime().availableProcessors());
    private final Semaphore semaphore = new Semaphore(PERMITS_SIZE);
    private final ImagingConverter imagingConverter = new ImagingConverter();

    private void generateThumbnail(FileFormat fileFormat, InputStream inStream, OutputStream outStream, double maxWidth, double maxHeight) throws ConversionException {
        this.imagingConverter.generateThumbnailDirect(fileFormat, fileFormat, inStream, outStream, 1, maxWidth, maxHeight);
    }

    private SavableAttachment createSavableAttachment(String fileName, long fileSize, String contentType, InputStream inputStream) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileSize(fileSize);
        attachment.setMediaType(contentType);
        return new SavableAttachment(attachment, null, inputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<SavableAttachment> generateThumbnails(SavableAttachment originalAttachment) throws ConversionException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy((InputStream)originalAttachment.getAttachmentData(), (OutputStream)byteArrayOutputStream);
            ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            String fileName = originalAttachment.getAttachment().getFileName();
            String mediaType = originalAttachment.getAttachment().getMediaType();
            FileFormat fileFormat = DefaultThumbnailManager.validate(mediaType, bufferedInputStream);
            ArrayList<SavableAttachment> thumbnailAttachmentList = new ArrayList<SavableAttachment>();
            for (ThumbnailSize thumbnailSize : ThumbnailSize.values()) {
                String thumbnailFileName = thumbnailSize.getSize() + "-" + fileName;
                ByteArrayOutputStream thumbnailByteArrayOutputStream = new ByteArrayOutputStream();
                this.semaphore.acquireUninterruptibly();
                try {
                    this.generateThumbnail(fileFormat, bufferedInputStream, thumbnailByteArrayOutputStream, thumbnailSize.getWidth(), thumbnailSize.getHeight());
                }
                finally {
                    bufferedInputStream.reset();
                    this.semaphore.release();
                }
                thumbnailAttachmentList.add(this.createSavableAttachment(thumbnailFileName, thumbnailByteArrayOutputStream.size(), mediaType, new ByteArrayInputStream(thumbnailByteArrayOutputStream.toByteArray())));
            }
            originalAttachment.setAttachmentData((InputStream)bufferedInputStream);
            return thumbnailAttachmentList;
        }
        catch (IOException ex) {
            throw new ConversionException("Could not access file part input stream ", (Throwable)ex);
        }
    }

    private static FileFormat validate(String mediaType, InputStream bufferedInputStream) throws IOException, ConversionException {
        byte[] bytes = new byte[12];
        bufferedInputStream.mark(12);
        bufferedInputStream.read(bytes, 0, 12);
        bufferedInputStream.reset();
        FormatDetector.detect(bytes).orElseThrow(() -> new ConversionException("Could not detect support format for thumbnail"));
        FileFormat fileFormat = FileFormat.fromMimeType((String)mediaType);
        if (fileFormat == null) {
            throw new ConversionException("Could not detect FileFormat for content type " + mediaType);
        }
        return fileFormat;
    }
}

