/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugins.conversion.convert.image.PdfConverter
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.bean.BeanResult
 *  com.atlassian.plugins.conversion.convert.store.ConversionStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.convert.image.PdfConverter;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import com.benryan.conversion.AbstractSlideConversionTask;
import com.benryan.conversion.SlideConversionDataHolder;
import com.benryan.conversion.SlideDocConversionData;
import java.io.InputStream;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class PdfSlideConversionBatchTask
extends AbstractSlideConversionTask<SlideConversionDataHolder> {
    private static final Logger log = LoggerFactory.getLogger(PdfSlideConversionBatchTask.class);
    private final Collection<Integer> pageNumbers;
    private final SlideDocConversionData conversionData;
    private final ConversionStore conversionStore;

    public PdfSlideConversionBatchTask(Attachment attachment, String attachmentName, Resource resource, SlideDocConversionData conversionData, Collection<Integer> pageNumbersToBeRendered, ConversionStore conversionStore) {
        super(attachment, attachmentName, resource);
        this.pageNumbers = pageNumbersToBeRendered;
        this.conversionData = conversionData;
        this.conversionStore = conversionStore;
        if (!this.getAttachmentName().endsWith(".pdf")) {
            throw new IllegalArgumentException("Invalid file type for conversion, only PDF suppported : " + this.getAttachmentName());
        }
    }

    @Override
    protected SlideConversionDataHolder convertFile() throws Exception {
        log.debug("Rendering {} pages for {}", (Object)this.pageNumbers.size(), (Object)this.getAttachmentDescription());
        long startTime = System.currentTimeMillis();
        try (InputStream inputStream = this.getInputResource().getInputStream();){
            BeanResult result = new PdfConverter().convert(FileFormat.PDF, FileFormat.JPG, inputStream, this.conversionStore, this.getAttachmentName(), this.pageNumbers);
            this.conversionData.setNumSlides(result.numPages);
            log.debug("Completed rendering {} pages for {} in {} ms", new Object[]{this.pageNumbers.size(), this.getAttachmentDescription(), System.currentTimeMillis() - startTime});
            SlideConversionDataHolder slideConversionDataHolder = SlideConversionDataHolder.fromBeanResult(result, this.conversionData);
            return slideConversionDataHolder;
        }
    }
}

