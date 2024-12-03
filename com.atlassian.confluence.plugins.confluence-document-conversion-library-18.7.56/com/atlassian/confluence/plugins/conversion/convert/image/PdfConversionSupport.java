/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.ConversionException
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.bean.BeanFile
 *  com.atlassian.plugins.conversion.convert.bean.BeanResult
 *  com.atlassian.plugins.conversion.convert.store.ConversionStore
 *  com.google.common.io.ByteStreams
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.convert.image;

import com.atlassian.confluence.plugins.conversion.convert.image.MemoryAwarePDFRenderer;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import com.google.common.io.ByteStreams;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfConversionSupport {
    private static final Logger log = LoggerFactory.getLogger(PdfConversionSupport.class);
    private static final int MAX_WIDTH = 2496;
    private static final int MAX_HEIGHT = 1560;
    private static final int ZOOM_LEVEL = 3;

    public int getTotalPageNumber(InputStream inputStream) throws IOException {
        PDFFile pdfFile = PdfConversionSupport.getInputPdfFile(inputStream);
        return pdfFile == null ? 0 : pdfFile.getNumPages();
    }

    public BeanResult convert(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, ConversionStore conversionStore, String fileName, Collection<Integer> pageNumbers) throws IOException, ConversionException {
        if (inFileFormat != FileFormat.PDF) {
            throw new ConversionException("Invalid format for PDF conversion");
        }
        BeanResult result = new BeanResult();
        switch (outFileFormat) {
            case JPG: 
            case PNG: {
                int startPageNumberOffset;
                boolean hasPageNumbers = pageNumbers != null && !pageNumbers.isEmpty();
                log.debug("Rendering {} pages for {}", hasPageNumbers ? Integer.valueOf(pageNumbers.size()) : "all", (Object)fileName);
                long startTime = System.currentTimeMillis();
                PDFFile pdfFile = PdfConversionSupport.getInputPdfFile(inStream);
                result.numPages = pdfFile.getNumPages();
                result.result = new ArrayList();
                boolean synchronous = true;
                int n = startPageNumberOffset = pdfFile.getPage(result.numPages, true) != null ? 1 : 0;
                if (hasPageNumbers) {
                    for (Integer pageNumber : pageNumbers) {
                        if (pageNumber == null || pageNumber < 0 || pageNumber >= result.numPages) continue;
                        result.result.add(this.renderPage(pdfFile, pageNumber, startPageNumberOffset, outFileFormat, conversionStore, fileName));
                    }
                } else {
                    for (int j = 0; j < result.numPages; ++j) {
                        result.result.add(this.renderPage(pdfFile, (Integer)j, startPageNumberOffset, outFileFormat, conversionStore, fileName));
                    }
                }
                log.debug("Completed rendering {} pages for {} in {} ms", new Object[]{result.result.size(), fileName, System.currentTimeMillis() - startTime});
                return result;
            }
        }
        throw new ConversionException("Unsupported output format: " + outFileFormat + ", expected PNG or JPG");
    }

    public void generateThumbnailDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream, int pageNumber, double maxWidth, double maxHeight) throws ConversionException {
        if (inFileFormat != FileFormat.PDF) {
            throw new ConversionException("Unknown in format");
        }
        try {
            PDFFile pdfFile = PdfConversionSupport.getInputPdfFile(inStream);
            boolean synchronous = true;
            int startPageNumberOffset = pdfFile.getPage(pdfFile.getNumPages(), true) != null ? 1 : 0;
            this.renderPage(pdfFile, pageNumber, startPageNumberOffset, outFileFormat, outStream, 1.0);
        }
        catch (IOException e) {
            throw new ConversionException((Throwable)e);
        }
    }

    private BeanFile renderPage(PDFFile pdfFile, Integer pageNumber, int startPageNumberOffset, FileFormat outFileFormat, ConversionStore conversionStore, String fileName) {
        int pageNumberWithinPdfFile = pageNumber + startPageNumberOffset;
        PDFPage pdfPage = pdfFile.getPage(pageNumberWithinPdfFile, true);
        BeanFile result = null;
        if (pdfPage != null) {
            try {
                result = this.convertPdfPage(pdfPage, pageNumber, outFileFormat, conversionStore, fileName);
                pdfFile.flushPage(pageNumberWithinPdfFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void renderPage(PDFFile pdfFile, int pageNumber, int startPageNumberOffset, FileFormat outFileFormat, OutputStream out, double resolution) {
        int pageNumberWithinPdfFile = pageNumber + startPageNumberOffset;
        PDFPage pdfPage = pdfFile.getPage(pageNumberWithinPdfFile, true);
        if (pdfPage != null) {
            try {
                this.convertPdfPage(pdfPage, outFileFormat, out, resolution);
                pdfFile.flushPage(pageNumberWithinPdfFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BeanFile convertPdfPage(PDFPage pdfPage, int pageNumber, FileFormat outFileFormat, ConversionStore conversionStore, String fileName) throws IOException {
        log.trace("Rendering page number {} for {}", (Object)pdfPage.getPageNumber(), (Object)fileName);
        long startTime = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
        OutputStream out = conversionStore.createFile(uuid);
        log.debug("Converting PDF page, creating file: " + uuid.toString());
        try {
            this.convertPdfPage(pdfPage, outFileFormat, out, 3.0);
        }
        finally {
            IOUtils.closeQuietly((OutputStream)out);
        }
        if (log.isDebugEnabled()) {
            log.trace("Completed rendering page number {} for {} in {} ms", new Object[]{pdfPage.getPageNumber(), fileName, System.currentTimeMillis() - startTime});
        }
        return new BeanFile(uuid, pageNumber, "", outFileFormat);
    }

    private void convertPdfPage(PDFPage pdfPage, FileFormat outFileFormat, OutputStream out, double resolution) throws IOException {
        Rectangle documentDimensions = PdfConversionSupport.limitTo(new Rectangle(0, 0, (int)(pdfPage.getBBox().getWidth() * resolution), (int)(pdfPage.getBBox().getHeight() * resolution)), 2496, 1560);
        BufferedImage bufferedImage = new BufferedImage(documentDimensions.width, documentDimensions.height, 1);
        Graphics2D graphics2d = bufferedImage.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        MemoryAwarePDFRenderer renderer = new MemoryAwarePDFRenderer(pdfPage, graphics2d, documentDimensions, null, Color.WHITE);
        renderer.run();
        ImageIO.write((RenderedImage)bufferedImage, outFileFormat.name().toLowerCase(), out);
    }

    public static Rectangle limitTo(Rectangle bounds, int maxWidth, int maxHeight) {
        float widthRatio = (float)maxWidth / (float)bounds.width;
        float heightRatio = (float)maxHeight / (float)bounds.height;
        if (widthRatio < 1.0f && widthRatio < heightRatio) {
            return new Rectangle(bounds.x, bounds.y, maxWidth, (int)((float)bounds.height * widthRatio));
        }
        if (heightRatio < 1.0f) {
            return new Rectangle(bounds.x, bounds.y, (int)((float)bounds.width * heightRatio), maxHeight);
        }
        return bounds;
    }

    private static PDFFile getInputPdfFile(InputStream inStream) throws IOException {
        if (inStream instanceof FileInputStream) {
            try (FileInputStream fileInputStream = (FileInputStream)inStream;){
                PDFFile pDFFile;
                block13: {
                    FileChannel fileChannel = fileInputStream.getChannel();
                    try {
                        MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, fileChannel.position(), fileChannel.size());
                        pDFFile = new PDFFile(byteBuffer);
                        if (fileChannel == null) break block13;
                    }
                    catch (Throwable throwable) {
                        if (fileChannel != null) {
                            try {
                                fileChannel.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    fileChannel.close();
                }
                return pDFFile;
            }
        }
        return new PDFFile(ByteBuffer.wrap(ByteStreams.toByteArray((InputStream)inStream)));
    }
}

