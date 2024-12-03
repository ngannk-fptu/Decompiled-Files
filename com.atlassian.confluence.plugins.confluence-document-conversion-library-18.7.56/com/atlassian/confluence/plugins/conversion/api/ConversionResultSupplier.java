/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.plugins.conversion.api;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.api.ConversionManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionResult;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;

public abstract class ConversionResultSupplier {
    protected ConversionManager conversionManager;

    public void setConversionManager(ConversionManager conversionManager) {
        this.conversionManager = conversionManager;
    }

    public abstract ConversionResult getConversionResult(Attachment var1, ConversionType var2);
}

