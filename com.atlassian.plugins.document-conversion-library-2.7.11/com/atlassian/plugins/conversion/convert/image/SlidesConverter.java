/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.slides.IInterruptionToken
 *  com.aspose.slides.ISlide
 *  com.aspose.slides.ISlideCollection
 *  com.aspose.slides.InterruptionTokenSource
 *  com.aspose.slides.LoadOptions
 *  com.aspose.slides.Presentation
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 */
package com.atlassian.plugins.conversion.convert.image;

import com.aspose.slides.IInterruptionToken;
import com.aspose.slides.ISlide;
import com.aspose.slides.ISlideCollection;
import com.aspose.slides.InterruptionTokenSource;
import com.aspose.slides.LoadOptions;
import com.aspose.slides.Presentation;
import com.atlassian.plugins.conversion.AsposeUtils;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class SlidesConverter
extends AbstractConverter {
    private static final ScheduledExecutorService interruptionScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("conversion-slides-interrupter-thread-%d").setPriority(1).build());
    private static final Integer TIMEOUT_IN_SECONDS = Integer.getInteger("confluence.document.conversion.slides.convert.timeout", 30);

    private InterruptionTokenSource getScheduledInterruptionTokenSource() {
        InterruptionTokenSource interruptTokenSource = new InterruptionTokenSource();
        interruptionScheduler.schedule(() -> ((InterruptionTokenSource)interruptTokenSource).interrupt(), (long)TIMEOUT_IN_SECONDS.intValue(), TimeUnit.SECONDS);
        return interruptTokenSource;
    }

    private void throwConversionExceptionIfInterrupted(InterruptionTokenSource token, String message) throws ConversionException {
        if (token.isInterruptionRequested()) {
            throw new ConversionException(message);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getTotalPageNumber(InputStream inputStream) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        InterruptionTokenSource interruptTokenSource = this.getScheduledInterruptionTokenSource();
        ClassLoader cl = SlidesConverter.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        Presentation doc = SlidesConverter.loadPresentation(inputStream, interruptTokenSource);
        try {
            if (doc == null) {
                int n = 0;
                return n;
            }
            ISlideCollection slideCollection = doc.getSlides();
            if (slideCollection == null) {
                int n = 0;
                return n;
            }
            int n = slideCollection.size();
            return n;
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            if (doc != null) {
                doc.dispose();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BeanResult convert(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, ConversionStore conversionStore, String fileName, Collection<Integer> pageNumbers) throws ConversionException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        InterruptionTokenSource interruptTokenSource = this.getScheduledInterruptionTokenSource();
        try {
            BeanResult beanResult;
            block15: {
                ClassLoader cl = SlidesConverter.class.getClassLoader();
                Thread.currentThread().setContextClassLoader(cl);
                BeanResult result = new BeanResult();
                Presentation doc = SlidesConverter.loadPresentation(inStream, interruptTokenSource);
                try {
                    this.throwConversionExceptionIfInterrupted(interruptTokenSource, "Conversion interrupted due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).");
                    result.numPages = doc.getSlides().size();
                    switch (outFileFormat) {
                        case PDF: {
                            UUID uuid = UUID.randomUUID();
                            OutputStream outputStream = conversionStore.createFile(uuid);
                            doc.save(outputStream, 1);
                            outputStream.close();
                            BeanFile beanFile = new BeanFile(uuid, -1, "", outFileFormat);
                            result.result = Collections.singletonList(beanFile);
                            break;
                        }
                        case JPG: 
                        case PNG: {
                            if (pageNumbers == null || pageNumbers.isEmpty()) {
                                pageNumbers = new ArrayList<Integer>(result.numPages);
                                for (int j = 0; j < result.numPages; ++j) {
                                    pageNumbers.add(j);
                                }
                            }
                            result.result = this.renderSlides(outFileFormat, conversionStore, doc, pageNumbers);
                            this.throwConversionExceptionIfInterrupted(interruptTokenSource, "Conversion interrupted due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).");
                            break;
                        }
                        default: {
                            throw new ConversionException("Unknown format");
                        }
                    }
                    beanResult = result;
                    if (doc == null) break block15;
                }
                catch (Throwable throwable) {
                    try {
                        if (doc != null) {
                            doc.dispose();
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new ConversionException(e);
                    }
                }
                doc.dispose();
            }
            return beanResult;
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void convertDocDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream) throws ConversionException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        InterruptionTokenSource interruptTokenSource = this.getScheduledInterruptionTokenSource();
        try {
            ClassLoader cl = SlidesConverter.class.getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            switch (outFileFormat) {
                case PDF: {
                    Presentation doc = SlidesConverter.loadPresentation(inStream, interruptTokenSource);
                    try {
                        this.throwConversionExceptionIfInterrupted(interruptTokenSource, "Conversion interrupted while generating preview due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).");
                        doc.save(outStream, 1);
                        this.throwConversionExceptionIfInterrupted(interruptTokenSource, "Conversion interrupted while generating preview due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).");
                        break;
                    }
                    finally {
                        if (doc != null) {
                            doc.dispose();
                        }
                    }
                }
                default: {
                    throw new ConversionException("Unknown format");
                }
            }
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateThumbnailDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream, int pageNumber, double maxWidth, double maxHeight) throws ConversionException {
        InterruptionTokenSource interruptTokenSource = this.getScheduledInterruptionTokenSource();
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = SlidesConverter.class.getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            switch (outFileFormat) {
                case JPG: 
                case PNG: {
                    Presentation doc = SlidesConverter.loadPresentation(inStream, interruptTokenSource);
                    try {
                        this.throwConversionExceptionIfInterrupted(interruptTokenSource, "Conversion interrupted while generating thumbnail due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).");
                        ISlideCollection slides = doc.getSlides();
                        if (pageNumber < 1 || pageNumber > slides.size()) {
                            throw new ConversionException("Invalid page number (" + pageNumber + " out of 1-" + slides.size() + ")");
                        }
                        ISlide slide = slides.get_Item(pageNumber - 1);
                        Dimension2D size = doc.getSlideSize().getSize();
                        double ratio = SlidesConverter.findRatio(size.getWidth(), size.getHeight(), maxWidth, maxHeight);
                        BufferedImage thumbnail = slide.getThumbnail((float)ratio, (float)ratio);
                        this.throwConversionExceptionIfInterrupted(interruptTokenSource, "Conversion interrupted while generating thumbnail due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).");
                        ImageIO.write((RenderedImage)thumbnail, outFileFormat.name(), outStream);
                        this.throwConversionExceptionIfInterrupted(interruptTokenSource, "Conversion interrupted while generating thumbnail due to timeout (" + TIMEOUT_IN_SECONDS + " seconds).");
                        break;
                    }
                    finally {
                        if (doc != null) {
                            doc.dispose();
                        }
                    }
                }
                default: {
                    throw new ConversionException("Unknown format");
                }
            }
        }
        catch (IOException e) {
            throw new ConversionException(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private static Presentation loadPresentation(InputStream inStream, InterruptionTokenSource interruptTokenSource) {
        LoadOptions loadOptions = new LoadOptions();
        loadOptions.setDefaultAsianFont(AsposeUtils.SLIDES_DEFAULT_ASIAN_FONT);
        loadOptions.setDefaultRegularFont(AsposeUtils.SLIDES_DEFAULT_REGULAR_FONT);
        loadOptions.setDefaultSymbolFont(AsposeUtils.SLIDES_DEFAULT_SYMBOL_FONT);
        loadOptions.setInterruptionToken((IInterruptionToken)interruptTokenSource.getToken());
        return new Presentation(inStream, loadOptions);
    }

    @Override
    public boolean handlesFileFormat(FileFormat inFileFormat) {
        return inFileFormat == FileFormat.PPT || inFileFormat == FileFormat.PPTX;
    }

    @Override
    public FileFormat getBestOutputFormat(FileFormat inFileFormat) {
        return this.handlesFileFormat(inFileFormat) ? FileFormat.PDF : null;
    }

    private List<BeanFile> renderSlides(FileFormat outFileFormat, ConversionStore conversionStore, Presentation doc, Collection<Integer> pageNumbers) throws IOException {
        ISlideCollection slides = doc.getSlides();
        ArrayList<BeanFile> result = new ArrayList<BeanFile>(pageNumbers.size());
        String formatName = outFileFormat.name().toLowerCase();
        for (ISlide slide : slides) {
            int pageNum;
            int slideNumber = slide.getSlideNumber();
            if (slideNumber <= 0 || !pageNumbers.contains(pageNum = slideNumber - 1)) continue;
            UUID uuid = UUID.randomUUID();
            OutputStream out = conversionStore.createFile(uuid);
            BufferedImage thumbnail = slide.getThumbnail(1.0f, 1.0f);
            ImageIO.write((RenderedImage)thumbnail, formatName, out);
            out.close();
            result.add(new BeanFile(uuid, pageNum, "", outFileFormat));
        }
        return result;
    }
}

