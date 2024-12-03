/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.ConversionException
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.bean.BeanResult
 *  com.atlassian.plugins.conversion.convert.image.AbstractConverter
 *  com.atlassian.plugins.conversion.convert.store.ConversionStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.convert.image;

import com.atlassian.confluence.plugins.conversion.convert.image.PdfConversionSupport;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfConverter
extends AbstractConverter {
    private static final Logger log = LoggerFactory.getLogger(PdfConverter.class);
    private PdfConversionSupport pdfConversionSupport = new PdfConversionSupport();

    public BeanResult convert(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, ConversionStore conversionStore, String fileName, Collection<Integer> pageNumbers) throws IOException, ConversionException {
        return this.pdfConversionSupport.convert(inFileFormat, outFileFormat, inStream, conversionStore, fileName, pageNumbers);
    }

    public void convertDocDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream) {
        throw new UnsupportedOperationException();
    }

    public void generateThumbnailDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream, int pageNumber, double maxWidth, double maxHeight) throws ConversionException {
        this.pdfConversionSupport.generateThumbnailDirect(inFileFormat, outFileFormat, inStream, outStream, pageNumber, maxWidth, maxHeight);
    }

    public boolean handlesFileFormat(FileFormat fileFormat) {
        return fileFormat == FileFormat.PDF;
    }

    public FileFormat getBestOutputFormat(FileFormat fileFormat) {
        return FileFormat.PDF;
    }
}

