/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.plugins.conversion.convert.ConversionException
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.image.AbstractConverter
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.conversion.impl.FileSystemConversionState;
import com.atlassian.confluence.plugins.conversion.impl.runnable.ConversionRunnable;
import com.atlassian.confluence.plugins.conversion.impl.runnable.CouldNotReserveMemoryForConversionException;
import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryReserveService;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class JVMConversionRunnable
extends ConversionRunnable {
    private static final Marker MARKER_JVM = MarkerFactory.getMarker((String)"JVMConversion");
    private static final int THUMB_PAGE = 1;
    private static final int THUMB_WIDTH = 320;
    private static final int THUMB_HEIGHT = 320;
    private final AttachmentManager attachmentManager;
    private final ConversionType conversionType;
    private final AbstractConverter[] converters;
    private final FileFormat inFileFormat;
    private final MemoryReserveService memoryReserveService;

    public JVMConversionRunnable(FileSystemConversionState conversionState, Attachment attachment, FileFormat inFileFormat, AttachmentManager attachmentManager, ConversionType conversionType, AbstractConverter[] converters, MemoryReserveService memoryReserveService) {
        super(conversionState, attachment);
        this.inFileFormat = inFileFormat;
        this.attachmentManager = attachmentManager;
        this.conversionType = conversionType;
        this.converters = converters;
        this.memoryReserveService = memoryReserveService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doWork(FileOutputStream fos) throws Exception {
        AbstractConverter converter = this.getConverter(this.inFileFormat);
        if (converter == null) {
            log.error("Format cannot be converted");
            return;
        }
        log.debug(MARKER_JVM, "Starting conversion ({})", (Object)this.conversionType);
        log.debug(MARKER_JVM, "AttachmentId={}, FileName={}, FileSize={} ", new Object[]{this.attachment.getId(), this.attachment.getFileName(), this.attachment.getFileSize()});
        InputStream attachmentData = this.attachmentManager.getAttachmentData(this.attachment);
        try {
            switch (this.conversionType) {
                case THUMBNAIL: {
                    this.generateThumbnail(fos, converter, attachmentData);
                    break;
                }
                case DOCUMENT: {
                    FileFormat bestFormat = converter.getBestOutputFormat(this.inFileFormat);
                    if (bestFormat == null) break;
                    converter.convertDocDirect(this.inFileFormat, bestFormat, attachmentData, (OutputStream)fos);
                    try (FileOutputStream mimeFos = new FileOutputStream(this.file.getAbsolutePath() + "mime");){
                        IOUtils.write((String)bestFormat.getDefaultMimeType(), (OutputStream)mimeFos);
                        break;
                    }
                }
            }
            log.debug(MARKER_JVM, "Finished conversion");
        }
        finally {
            IOUtils.closeQuietly((InputStream)attachmentData);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void generateThumbnail(FileOutputStream fos, AbstractConverter converter, InputStream attachmentData) throws ConversionException {
        if (this.inFileFormat != FileFormat.PDF) {
            converter.generateThumbnailDirect(this.inFileFormat, FileFormat.JPG, attachmentData, (OutputStream)fos, 1, 320.0, 320.0);
            return;
        }
        boolean memoryReserved = false;
        try {
            memoryReserved = this.memoryReserveService.reserveMemory(this.attachment.getFileSize());
            if (!memoryReserved) {
                throw new CouldNotReserveMemoryForConversionException();
            }
            converter.generateThumbnailDirect(this.inFileFormat, FileFormat.JPG, attachmentData, (OutputStream)fos, 1, 320.0, 320.0);
        }
        finally {
            if (memoryReserved) {
                this.memoryReserveService.releaseMemory(this.attachment.getFileSize());
            }
        }
    }

    private AbstractConverter getConverter(FileFormat fileFormat) {
        if (fileFormat == null) {
            return null;
        }
        for (AbstractConverter converter : this.converters) {
            if (!converter.handlesFileFormat(fileFormat)) continue;
            return converter;
        }
        return null;
    }
}

