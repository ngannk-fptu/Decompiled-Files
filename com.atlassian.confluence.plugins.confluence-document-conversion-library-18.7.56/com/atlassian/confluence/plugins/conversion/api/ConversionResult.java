/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException
 */
package com.atlassian.confluence.plugins.conversion.api;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.api.ConversionData;
import com.atlassian.confluence.plugins.conversion.api.ConversionStatus;
import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Optional;

public abstract class ConversionResult {
    public final String conversionUrl;
    protected final Attachment attachment;

    protected ConversionResult(Attachment attachment, String conversionUrl) {
        this.attachment = attachment;
        this.conversionUrl = Objects.requireNonNull(conversionUrl);
    }

    public abstract ConversionStatus getConversionStatus();

    public String getConversionUrl() {
        return this.conversionUrl;
    }

    public abstract ConversionData getConversionData(Optional<String> var1) throws RangeNotSatisfiableException, FileNotFoundException;

    public abstract Optional<String> getContentType();
}

