/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.psd.Image
 *  com.aspose.psd.ImageOptionsBase
 *  com.aspose.psd.LoadOptions
 *  com.aspose.psd.imageloadoptions.PsdLoadOptions
 *  com.aspose.psd.imageoptions.GifOptions
 *  com.aspose.psd.imageoptions.JpegOptions
 *  com.aspose.psd.imageoptions.PngOptions
 *  com.aspose.psd.imageoptions.PsdOptions
 *  com.aspose.psd.imageoptions.TiffOptions
 *  com.aspose.psd.multithreading.IInterruptMonitor
 *  com.aspose.psd.multithreading.InterruptMonitor
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 */
package com.atlassian.plugins.conversion.convert.image;

import com.aspose.psd.Image;
import com.aspose.psd.ImageOptionsBase;
import com.aspose.psd.LoadOptions;
import com.aspose.psd.imageloadoptions.PsdLoadOptions;
import com.aspose.psd.imageoptions.GifOptions;
import com.aspose.psd.imageoptions.JpegOptions;
import com.aspose.psd.imageoptions.PngOptions;
import com.aspose.psd.imageoptions.PsdOptions;
import com.aspose.psd.imageoptions.TiffOptions;
import com.aspose.psd.multithreading.IInterruptMonitor;
import com.aspose.psd.multithreading.InterruptMonitor;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.image.ImagingConverter;
import com.atlassian.plugins.conversion.convert.image.ImagingPdfHelper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PsdConverter {
    private static final ScheduledExecutorService interruptionScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("conversion-psd-interrupter-thread-%d").setPriority(1).build());

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generateThumbnailDirect(FileFormat outFileFormat, InputStream inStream, OutputStream outStream, double maxWidth, double maxHeight) throws IOException, ConversionException {
        InterruptMonitor monitor = this.getScheduledInterrupt();
        InterruptMonitor.setThreadLocalInstance((IInterruptMonitor)monitor);
        try (Image image = this.loadImage(inStream);){
            double ratio = AbstractConverter.findRatio(image.getWidth(), image.getHeight(), maxWidth, maxHeight);
            image.resize((int)((double)image.getWidth() * ratio), (int)((double)image.getHeight() * ratio), 6);
            ImageOptionsBase opts = null;
            if (outFileFormat == FileFormat.PDF) {
                ImagingPdfHelper.convertToPdf(image, outStream);
            } else {
                opts = this.getImageSaveOptions(outFileFormat);
            }
            if (opts != null) {
                image.save(outStream, opts);
            }
        }
        finally {
            InterruptMonitor.setThreadLocalInstance(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void convertDocDirect(FileFormat outFileFormat, InputStream inStream, OutputStream outStream) throws ConversionException, IOException {
        InterruptMonitor monitor = this.getScheduledInterrupt();
        InterruptMonitor.setThreadLocalInstance((IInterruptMonitor)monitor);
        try (Image image = this.loadImage(inStream);){
            switch (outFileFormat) {
                case JPG: 
                case PNG: 
                case TIF: {
                    ImageOptionsBase opts = this.getImageSaveOptions(outFileFormat);
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
        finally {
            InterruptMonitor.setThreadLocalInstance(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public BeanResult convert(InputStream inStream, OutputStream outputStream, FileFormat outFileFormat, BeanFile beanFile) throws IOException, ConversionException {
        monitor = this.getScheduledInterrupt();
        InterruptMonitor.setThreadLocalInstance((IInterruptMonitor)monitor);
        result = new BeanResult();
        try {
            image = this.loadImage(inStream);
            try {
                switch (3.$SwitchMap$com$atlassian$plugins$conversion$convert$FileFormat[outFileFormat.ordinal()]) {
                    case 4: {
                        ImagingPdfHelper.convertToPdf(image, outputStream);
                        ** break;
lbl11:
                        // 1 sources

                        break;
                    }
                    case 1: 
                    case 2: 
                    case 5: {
                        opt = outFileFormat == FileFormat.PNG ? new PngOptions() : new JpegOptions();
                        image.save(outputStream, (ImageOptionsBase)opt);
                        ** break;
lbl16:
                        // 1 sources

                        break;
                    }
                    default: {
                        throw new ConversionException("Unknown format");
                    }
                }
            }
            finally {
                if (image != null) {
                    image.close();
                }
            }
        }
        finally {
            InterruptMonitor.setThreadLocalInstance(null);
        }
        result.result = Collections.singletonList(beanFile);
        return result;
    }

    private InterruptMonitor getScheduledInterrupt() {
        InterruptMonitor monitor = new InterruptMonitor();
        interruptionScheduler.schedule(() -> ((InterruptMonitor)monitor).interrupt(), (long)ImagingConverter.TIMEOUT_IN_SECONDS.intValue(), TimeUnit.SECONDS);
        return monitor;
    }

    private ImageOptionsBase getImageSaveOptions(FileFormat outFileFormat) throws ConversionException {
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
                opts = new ImageOptionsBase(){

                    protected Object memberwiseClone() {
                        return this.deepClone();
                    }
                };
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

    private Image loadImage(InputStream inStream) {
        PsdLoadOptions loadOptions = new PsdLoadOptions();
        loadOptions.setReadOnlyMode(true);
        Image image = Image.load((InputStream)inStream, (LoadOptions)loadOptions);
        return image;
    }
}

