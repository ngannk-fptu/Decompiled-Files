/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aspose.pdf.Pdf
 *  com.aspose.imaging.Color
 *  com.aspose.imaging.Image
 *  com.aspose.imaging.ImageOptionsBase
 *  com.aspose.imaging.coreexceptions.OperationInterruptedException
 *  com.aspose.imaging.imageoptions.EmfOptions
 *  com.aspose.imaging.imageoptions.EmfRasterizationOptions
 *  com.aspose.imaging.imageoptions.GifOptions
 *  com.aspose.imaging.imageoptions.JpegOptions
 *  com.aspose.imaging.imageoptions.PngOptions
 *  com.aspose.imaging.imageoptions.PsdOptions
 *  com.aspose.imaging.imageoptions.TiffOptions
 *  com.aspose.imaging.imageoptions.VectorRasterizationOptions
 *  com.aspose.imaging.imageoptions.WmfRasterizationOptions
 *  com.aspose.imaging.multithreading.IInterruptMonitor
 *  com.aspose.imaging.multithreading.InterruptMonitor
 *  com.aspose.imaging.system.io.MemoryStream
 *  com.aspose.imaging.system.io.Stream
 *  com.aspose.pdf.Document
 *  com.aspose.pdf.LoadOptions
 *  com.aspose.pdf.Page
 *  com.aspose.pdf.PageCollection
 *  com.aspose.pdf.PdfSaveOptions
 *  com.aspose.pdf.Rectangle
 *  com.aspose.pdf.SaveOptions
 *  com.aspose.pdf.XpsLoadOptions
 *  com.aspose.pdf.devices.EmfDevice
 *  com.aspose.pdf.devices.JpegDevice
 *  com.aspose.pdf.devices.PngDevice
 *  com.aspose.pdf.devices.Resolution
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.apache.pdfbox.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.conversion.convert.image;

import aspose.pdf.Pdf;
import com.aspose.imaging.Color;
import com.aspose.imaging.Image;
import com.aspose.imaging.ImageOptionsBase;
import com.aspose.imaging.coreexceptions.OperationInterruptedException;
import com.aspose.imaging.imageoptions.EmfOptions;
import com.aspose.imaging.imageoptions.EmfRasterizationOptions;
import com.aspose.imaging.imageoptions.GifOptions;
import com.aspose.imaging.imageoptions.JpegOptions;
import com.aspose.imaging.imageoptions.PngOptions;
import com.aspose.imaging.imageoptions.PsdOptions;
import com.aspose.imaging.imageoptions.TiffOptions;
import com.aspose.imaging.imageoptions.VectorRasterizationOptions;
import com.aspose.imaging.imageoptions.WmfRasterizationOptions;
import com.aspose.imaging.multithreading.IInterruptMonitor;
import com.aspose.imaging.multithreading.InterruptMonitor;
import com.aspose.imaging.system.io.MemoryStream;
import com.aspose.imaging.system.io.Stream;
import com.aspose.pdf.Document;
import com.aspose.pdf.LoadOptions;
import com.aspose.pdf.Page;
import com.aspose.pdf.PageCollection;
import com.aspose.pdf.PdfSaveOptions;
import com.aspose.pdf.Rectangle;
import com.aspose.pdf.SaveOptions;
import com.aspose.pdf.XpsLoadOptions;
import com.aspose.pdf.devices.EmfDevice;
import com.aspose.pdf.devices.JpegDevice;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.image.ImagingPdfHelper;
import com.atlassian.plugins.conversion.convert.image.PsdConverter;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagingConverter
extends AbstractConverter {
    public static final int PDF_MARGIN = 12;
    public static final int DEFAULT_PDF_DPI = 72;
    public static final float DEFAULT_PDF_DPI_PERCENTAGE = 0.72f;
    private static final ScheduledExecutorService interruptionScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("conversion-imaging-interrupter-thread-%d").setPriority(1).build());
    public static final Integer TIMEOUT_IN_SECONDS = Integer.getInteger("confluence.document.conversion.imaging.convert.timeout", 30);
    private static final Logger logger = LoggerFactory.getLogger(ImagingConverter.class);
    private final PsdConverter psdConverter = new PsdConverter();

    private InterruptMonitor getScheduledInterrupt() {
        InterruptMonitor monitor = new InterruptMonitor();
        interruptionScheduler.schedule(() -> ((InterruptMonitor)monitor).interrupt(), (long)TIMEOUT_IN_SECONDS.intValue(), TimeUnit.SECONDS);
        return monitor;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public BeanResult convert(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, ConversionStore conversionStore, String fileName, Collection<Integer> pageNumbers) throws ConversionException {
        if (this.shouldSkipThisFileFormat(inFileFormat)) {
            return new BeanResult();
        }
        if (inFileFormat != FileFormat.PSD) {
            monitor = this.getScheduledInterrupt();
            InterruptMonitor.setThreadLocalInstance((IInterruptMonitor)monitor);
        }
        try {
            onlyName = ImagingConverter.getOnlyName(fileName);
            result = new BeanResult();
            result.numPages = 1;
            block15 : switch (2.$SwitchMap$com$atlassian$plugins$conversion$convert$FileFormat[inFileFormat.ordinal()]) {
                case 5: 
                case 6: {
                    image = Image.load((InputStream)inStream);
                    try {
                        uuid = UUID.randomUUID();
                        outputStream = conversionStore.createFile(uuid);
                        options = this.getMetaFileSaveOptions(inFileFormat, image.getWidth(), image.getHeight());
                        image.save(outputStream, options);
                        break;
                    }
                    finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                case 2: 
                case 3: 
                case 4: 
                case 7: {
                    image = Image.load((InputStream)inStream);
                    try {
                        uuid = UUID.randomUUID();
                        pageNum = 0;
                        outputStream = conversionStore.createFile(uuid);
                        try {
                            switch (2.$SwitchMap$com$atlassian$plugins$conversion$convert$FileFormat[outFileFormat.ordinal()]) {
                                case 1: {
                                    pageNum = -1;
                                    ImagingPdfHelper.convertToPdf(image, outputStream);
                                    ** break;
lbl35:
                                    // 1 sources

                                    break;
                                }
                                case 2: 
                                case 3: 
                                case 4: {
                                    opt = outFileFormat == FileFormat.PNG ? new PngOptions() : new JpegOptions();
                                    image.save(outputStream, (ImageOptionsBase)opt);
                                    ** break;
lbl40:
                                    // 1 sources

                                    break;
                                }
                                default: {
                                    throw new ConversionException("Unknown format");
                                }
                            }
                        }
                        finally {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        }
                        result.result = Collections.singletonList(new BeanFile(uuid, pageNum, onlyName + "." + outFileFormat.name().toLowerCase(), outFileFormat));
                        break;
                    }
                    finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                case 8: {
                    uuid = UUID.randomUUID();
                    pageNum = 0;
                    outputStream = conversionStore.createFile(uuid);
                    try {
                        this.psdConverter.convert(inStream, outputStream, outFileFormat, new BeanFile(uuid, pageNum, onlyName + "." + outFileFormat.name().toLowerCase(), outFileFormat));
                        break;
                    }
                    finally {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                }
                case 9: 
                case 10: {
                    reader = this.getImageReader(inFileFormat, inStream);
                    result.result = new ArrayList<BeanFile>();
                    switch (2.$SwitchMap$com$atlassian$plugins$conversion$convert$FileFormat[outFileFormat.ordinal()]) {
                        case 1: {
                            uuid = UUID.randomUUID();
                            outputStream = conversionStore.createFile(uuid);
                            try {
                                ImagingPdfHelper.convertToPdf(reader, outputStream);
                            }
                            finally {
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
                            result.result.add(new BeanFile(uuid, -1, onlyName + ".pdf", outFileFormat));
                            break block15;
                        }
                        case 2: 
                        case 3: {
                            formatName = outFileFormat.name().toLowerCase();
                            imgIdx = 0;
                            try {
                                while (true) {
                                    image = reader.read(imgIdx);
                                    uuid = UUID.randomUUID();
                                    outputStream = conversionStore.createFile(uuid);
                                    try {
                                        ImageIO.write((RenderedImage)image, formatName, outputStream);
                                    }
                                    finally {
                                        if (outputStream != null) {
                                            outputStream.close();
                                        }
                                    }
                                    result.result.add(new BeanFile(uuid, imgIdx, onlyName + "-" + imgIdx + "." + formatName, outFileFormat));
                                    ++imgIdx;
                                }
                            }
                            catch (IndexOutOfBoundsException var12_31) {
                                break block15;
                            }
                        }
                    }
                    throw new ConversionException("Unknown format");
                }
                default: {
                    throw new ConversionException("Unknown format");
                }
            }
            var9_11 = result;
            return var9_11;
        }
        catch (IOException e) {
            throw new ConversionException("IO Error.", e);
        }
        catch (OperationInterruptedException e) {
            throw new ConversionException("Conversion interrupted due to timeout (" + ImagingConverter.TIMEOUT_IN_SECONDS + " seconds).", e);
        }
        finally {
            InterruptMonitor.setThreadLocalInstance(null);
        }
    }

    public ImageReader getImageReader(FileFormat inFileFormat, InputStream inStream) throws IOException, ConversionException {
        ImageReader reader;
        switch (inFileFormat) {
            case ICNS: {
                reader = ImageIO.getImageReadersByMIMEType(FileFormat.ICNS.getDefaultMimeType()).next();
                break;
            }
            case ICO: {
                reader = ImageIO.getImageReadersByMIMEType(FileFormat.ICO.getDefaultMimeType()).next();
                break;
            }
            default: {
                throw new ConversionException("Unknown format");
            }
        }
        reader.setInput(ImageIO.createImageInputStream(inStream));
        return reader;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void convertDocDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream) throws ConversionException {
        if (this.shouldSkipThisFileFormat(inFileFormat)) {
            return;
        }
        if (inFileFormat != FileFormat.PSD) {
            InterruptMonitor monitor = this.getScheduledInterrupt();
            InterruptMonitor.setThreadLocalInstance((IInterruptMonitor)monitor);
        }
        try {
            if (inFileFormat == outFileFormat) {
                IOUtils.copy((InputStream)inStream, (OutputStream)outStream);
                return;
            }
            switch (inFileFormat) {
                case WMF: 
                case EMF: {
                    try (Image image = Image.load((InputStream)inStream);){
                        ImageOptionsBase options = this.getMetaFileSaveOptions(inFileFormat, image.getWidth(), image.getHeight());
                        image.save(outStream, options);
                        return;
                    }
                }
                case PNG: 
                case JPG: 
                case TIF: {
                    try (Image image = Image.load((InputStream)inStream);){
                        switch (outFileFormat) {
                            case PNG: 
                            case JPG: 
                            case TIF: {
                                ImageOptionsBase opts = ImagingConverter.getImageSaveOptions(outFileFormat);
                                image.save(outStream, opts);
                                return;
                            }
                            case PDF: {
                                ImagingPdfHelper.convertToPdf(image, outStream);
                                return;
                            }
                            default: {
                                throw new ConversionException("Unknown format");
                            }
                        }
                    }
                }
                case PSD: {
                    this.psdConverter.convertDocDirect(outFileFormat, inStream, outStream);
                    return;
                }
                case ICO: 
                case ICNS: {
                    ImageReader reader = this.getImageReader(inFileFormat, inStream);
                    switch (outFileFormat) {
                        case PDF: {
                            ImagingPdfHelper.convertToPdf(reader, outStream);
                            return;
                        }
                    }
                    throw new ConversionException("Unknown format");
                }
                case XPS: {
                    XpsLoadOptions loadOptions = new XpsLoadOptions();
                    Document doc = new Document(inStream, (LoadOptions)loadOptions);
                    doc.save(outStream, (SaveOptions)new PdfSaveOptions());
                    return;
                }
                default: {
                    throw new ConversionException("Unknown format");
                }
            }
        }
        catch (IOException e) {
            throw new ConversionException("IO Error.", e);
        }
        catch (OperationInterruptedException e) {
            throw new ConversionException("Conversion interrupted while generating preview due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).", e);
        }
        finally {
            InterruptMonitor.setThreadLocalInstance(null);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void generateThumbnailDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream, int pageNumber, double maxWidth, double maxHeight) throws ConversionException {
        if (this.shouldSkipThisFileFormat(inFileFormat)) {
            return;
        }
        if (inFileFormat != FileFormat.PSD) {
            InterruptMonitor monitor = this.getScheduledInterrupt();
            InterruptMonitor.setThreadLocalInstance((IInterruptMonitor)monitor);
        }
        try {
            switch (inFileFormat) {
                case WMF: 
                case EMF: {
                    try (Image image = Image.load((InputStream)inStream);){
                        double ratio = ImagingConverter.findRatio(image.getWidth(), image.getHeight(), maxWidth, maxHeight);
                        ImageOptionsBase opts = this.getMetaFileSaveOptions(inFileFormat, (int)((double)image.getWidth() * ratio), (int)((double)image.getHeight() * ratio));
                        image.save(outStream, opts);
                        return;
                    }
                }
                case PDF: {
                    this.thumbnailForPdf(inFileFormat, outFileFormat, inStream, outStream, pageNumber, maxWidth, maxHeight);
                    return;
                }
                case PNG: 
                case JPG: 
                case GIF: 
                case TIF: {
                    try (Image image = Image.load((InputStream)inStream);){
                        double ratio = ImagingConverter.findRatio(image.getWidth(), image.getHeight(), maxWidth, maxHeight);
                        if (FileFormat.GIF.equals((Object)inFileFormat)) {
                            image.resize((int)((double)image.getWidth() * ratio), (int)((double)image.getHeight() * ratio));
                        } else {
                            image.resize((int)((double)image.getWidth() * ratio), (int)((double)image.getHeight() * ratio), 6);
                        }
                        ImageOptionsBase opts = null;
                        if (outFileFormat == FileFormat.PDF) {
                            ImagingPdfHelper.convertToPdf(image, outStream);
                        } else {
                            opts = ImagingConverter.getImageSaveOptions(outFileFormat);
                        }
                        if (opts == null) return;
                        image.save(outStream, opts);
                        return;
                    }
                }
                case PSD: {
                    this.psdConverter.generateThumbnailDirect(outFileFormat, inStream, outStream, maxWidth, maxHeight);
                    return;
                }
                case ICO: 
                case ICNS: {
                    ImageReader imageReader = this.getImageReader(inFileFormat, inStream);
                    int imageIndex = pageNumber - 1;
                    BufferedImage bufImg = imageReader.read(imageIndex > imageReader.getMinIndex() ? imageIndex : imageReader.getMinIndex());
                    ImageIO.write((RenderedImage)bufImg, inFileFormat.name(), outStream);
                    return;
                }
                default: {
                    throw new ConversionException("Unknown format");
                }
            }
        }
        catch (IOException e) {
            throw new ConversionException("IO Error.", e);
        }
        catch (OperationInterruptedException e) {
            throw new ConversionException("Conversion interrupted while generating thumbnail due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).", e);
        }
        finally {
            InterruptMonitor.setThreadLocalInstance(null);
        }
    }

    private boolean shouldSkipThisFileFormat(FileFormat inFileFormat) {
        if (inFileFormat == FileFormat.TIF && !this.isTifEnabled()) {
            logger.info("File conversion skipped as conversions for this file type (" + inFileFormat.name() + ") are disabled. They can be enabled via the confluence.document.conversion.imaging.enabled.tif system property.");
            return true;
        }
        if (inFileFormat == FileFormat.PSD && !this.isPsdEnabled()) {
            logger.info("File conversion skipped as conversions for this file type (" + inFileFormat.name() + ") are disabled. They can be enabled via the confluence.document.conversion.imaging.enabled.psd system property.");
            return true;
        }
        return false;
    }

    public boolean isTifEnabled() {
        return Boolean.getBoolean("confluence.document.conversion.imaging.enabled.tif");
    }

    public boolean isPsdEnabled() {
        return Boolean.getBoolean("confluence.document.conversion.imaging.enabled.psd");
    }

    private ImageOptionsBase getMetaFileSaveOptions(FileFormat inFileFormat, float width, float height) throws ConversionException {
        WmfRasterizationOptions rasterizationOptions;
        switch (inFileFormat) {
            case WMF: {
                rasterizationOptions = new WmfRasterizationOptions();
                break;
            }
            case EMF: {
                rasterizationOptions = new EmfRasterizationOptions();
                break;
            }
            default: {
                throw new ConversionException("Unknown metafile format");
            }
        }
        rasterizationOptions.setPageWidth(width);
        rasterizationOptions.setPageHeight(height);
        rasterizationOptions.setBackgroundColor(Color.getWhiteSmoke());
        JpegOptions options = new JpegOptions();
        options.setVectorRasterizationOptions((VectorRasterizationOptions)rasterizationOptions);
        return options;
    }

    private static ImageOptionsBase getImageSaveOptions(FileFormat outFileFormat) throws ConversionException {
        Object opts;
        switch (outFileFormat) {
            case PNG: {
                PngOptions pngOptions = new PngOptions();
                pngOptions.setColorType(6);
                opts = pngOptions;
                break;
            }
            case JPG: {
                opts = new JpegOptions();
                break;
            }
            case TIF: {
                opts = new TiffOptions(0);
                break;
            }
            case PSD: {
                opts = new PsdOptions();
                break;
            }
            case WMF: {
                opts = new ImageOptionsBase(){

                    protected Object memberwiseClone() {
                        return this.deepClone();
                    }
                };
                break;
            }
            case EMF: {
                opts = new EmfOptions();
                break;
            }
            case GIF: {
                opts = new GifOptions();
                break;
            }
            default: {
                throw new ConversionException("Unknown format");
            }
        }
        return opts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void thumbnailForPdf(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream, int pageNumber, double maxWidth, double maxHeight) throws ConversionException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            PngDevice device;
            ClassLoader cl = ImagingConverter.class.getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            if (inFileFormat != FileFormat.PDF) {
                throw new ConversionException("Unknown in format");
            }
            Document doc = new Document(inStream);
            PageCollection pages = doc.getPages();
            if (pageNumber < 1 || pageNumber > pages.size()) {
                throw new ConversionException("Invalid page number (" + pageNumber + " out of 1-" + pages.size() + ")");
            }
            Page page = pages.get_Item(pageNumber);
            Rectangle mediaBox = page.getMediaBox();
            double ratio = ImagingConverter.findRatio(mediaBox.getWidth(), mediaBox.getHeight(), maxWidth, maxHeight);
            Resolution res = new Resolution((int)(72.0 * ratio));
            switch (outFileFormat) {
                case PNG: {
                    device = new PngDevice(res);
                    break;
                }
                case JPG: {
                    device = new JpegDevice(res);
                    break;
                }
                case EMF: {
                    device = new EmfDevice(res);
                    break;
                }
                default: {
                    throw new ConversionException("Unknown out format");
                }
            }
            device.process(page, outStream);
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    public void flattenPdf(InputStream inStream, FileFormat imgFileFormat, OutputStream outStream) throws ConversionException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = ImagingConverter.class.getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            Document doc = new Document(inStream);
            PageCollection pages = doc.getPages();
            if (pages.size() <= 0) {
                ImagingConverter.copyStream(inStream, outStream);
                return;
            }
            Pdf outPdf = ImagingPdfHelper.createPdf();
            Resolution res = new Resolution(100);
            for (Object pageObj : pages) {
                PngDevice device;
                Page page = (Page)pageObj;
                Rectangle mediaBox = page.getMediaBox();
                switch (imgFileFormat) {
                    case PNG: {
                        device = new PngDevice(res);
                        break;
                    }
                    case JPG: {
                        device = new JpegDevice(res);
                        break;
                    }
                    default: {
                        throw new ConversionException("Unknown image format");
                    }
                }
                MemoryStream memoryStream = new MemoryStream();
                device.process(page, memoryStream.toOutputStream());
                memoryStream.setPosition(0L);
                ImagingPdfHelper.addPdfPage(outPdf, imgFileFormat, (Stream)memoryStream, mediaBox.getWidth(), mediaBox.getHeight(), true);
            }
            outPdf.save(outStream);
        }
        catch (IOException e) {
            throw new ConversionException(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        int read;
        byte[] buf = new byte[65535];
        while ((read = in.read(buf)) > 0) {
            out.write(buf, 0, read);
        }
    }

    @Override
    public boolean handlesFileFormat(FileFormat inFileFormat) {
        switch (inFileFormat) {
            case PDF: 
            case PNG: 
            case JPG: 
            case GIF: 
            case WMF: 
            case EMF: 
            case TIF: 
            case PSD: 
            case ICO: 
            case ICNS: 
            case XPS: {
                return true;
            }
        }
        return false;
    }

    @Override
    public FileFormat getBestOutputFormat(FileFormat inFileFormat) {
        switch (inFileFormat) {
            case PDF: 
            case TIF: 
            case ICO: 
            case ICNS: 
            case XPS: {
                return FileFormat.PDF;
            }
            case PNG: {
                return FileFormat.PNG;
            }
            case JPG: 
            case WMF: 
            case EMF: 
            case PSD: {
                return FileFormat.JPG;
            }
            case GIF: {
                return FileFormat.GIF;
            }
        }
        return null;
    }
}

