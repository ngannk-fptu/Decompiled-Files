/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.image.AbstractConverter
 */
package com.atlassian.confluence.plugins.conversion.api;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.api.ConversionResult;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;

public interface ConversionManager {
    public FileFormat getFileFormat(Attachment var1);

    public boolean isConvertible(FileFormat var1);

    public AbstractConverter[] getConverters();

    public ConversionResult getConversionResult(Attachment var1, ConversionType var2);

    public String getConversionUrl(long var1, int var3, ConversionType var4);

    public String getBatchConversionUrl(ConversionType var1);
}

