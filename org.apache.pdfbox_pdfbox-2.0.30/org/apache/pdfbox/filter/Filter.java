/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.MissingImageReaderException;

public abstract class Filter {
    private static final Log LOG = LogFactory.getLog(Filter.class);
    public static final String SYSPROP_DEFLATELEVEL = "org.apache.pdfbox.filter.deflatelevel";

    protected Filter() {
    }

    public abstract DecodeResult decode(InputStream var1, OutputStream var2, COSDictionary var3, int var4) throws IOException;

    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        return this.decode(encoded, decoded, parameters, index);
    }

    public final void encode(InputStream input, OutputStream encoded, COSDictionary parameters, int index) throws IOException {
        this.encode(input, encoded, parameters.asUnmodifiableDictionary());
    }

    protected abstract void encode(InputStream var1, OutputStream var2, COSDictionary var3) throws IOException;

    protected COSDictionary getDecodeParams(COSDictionary dictionary, int index) {
        COSBase filter = dictionary.getDictionaryObject(COSName.F, COSName.FILTER);
        COSBase obj = dictionary.getDictionaryObject(COSName.DP, COSName.DECODE_PARMS);
        if (filter instanceof COSName && obj instanceof COSDictionary) {
            return (COSDictionary)obj;
        }
        if (filter instanceof COSArray && obj instanceof COSArray) {
            COSBase objAtIndex;
            COSArray array = (COSArray)obj;
            if (index < array.size() && (objAtIndex = array.getObject(index)) instanceof COSDictionary) {
                return (COSDictionary)objAtIndex;
            }
        } else if (obj != null && !(filter instanceof COSArray) && !(obj instanceof COSArray)) {
            LOG.error((Object)("Expected DecodeParams to be an Array or Dictionary but found " + obj.getClass().getName()));
        }
        return new COSDictionary();
    }

    protected static ImageReader findImageReader(String formatName, String errorCause) throws MissingImageReaderException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formatName);
        while (readers.hasNext()) {
            ImageReader reader = readers.next();
            if (reader == null || !reader.canReadRaster()) continue;
            return reader;
        }
        throw new MissingImageReaderException("Cannot read " + formatName + " image: " + errorCause);
    }

    public static int getCompressionLevel() {
        int compressionLevel = -1;
        try {
            compressionLevel = Integer.parseInt(System.getProperty(SYSPROP_DEFLATELEVEL, "-1"));
        }
        catch (NumberFormatException ex) {
            LOG.warn((Object)ex.getMessage(), (Throwable)ex);
        }
        return Math.max(-1, Math.min(9, compressionLevel));
    }
}

