/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.image;

import com.atlassian.plugins.conversion.AsposeAware;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public abstract class AbstractConverter
extends AsposeAware {
    public abstract BeanResult convert(FileFormat var1, FileFormat var2, InputStream var3, ConversionStore var4, String var5, Collection<Integer> var6) throws Exception;

    public abstract void convertDocDirect(FileFormat var1, FileFormat var2, InputStream var3, OutputStream var4) throws Exception;

    public abstract void generateThumbnailDirect(FileFormat var1, FileFormat var2, InputStream var3, OutputStream var4, int var5, double var6, double var8) throws ConversionException;

    public abstract boolean handlesFileFormat(FileFormat var1);

    public abstract FileFormat getBestOutputFormat(FileFormat var1);

    protected static String getOnlyName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(46));
    }

    public static double findRatio(double pageWidth, double pageHeight, double maxWidth, double maxHeight) {
        double ratioX = 1.0;
        double ratioY = 1.0;
        if (pageWidth > maxWidth) {
            ratioX = maxWidth / pageWidth;
        }
        if (pageHeight > maxHeight) {
            ratioY = maxHeight / pageHeight;
        }
        return Math.min(ratioX, ratioY);
    }
}

