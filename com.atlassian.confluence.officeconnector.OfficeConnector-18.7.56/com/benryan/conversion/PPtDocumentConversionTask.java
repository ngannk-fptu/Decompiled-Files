/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.bean.BeanResult
 *  com.atlassian.plugins.conversion.convert.image.SlidesConverter
 *  com.atlassian.plugins.conversion.convert.store.ConversionStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.image.SlidesConverter;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import com.benryan.conversion.AbstractSlideConversionTask;
import com.benryan.conversion.SlideConversionDataHolder;
import com.benryan.conversion.SlideDocConversionData;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class PPtDocumentConversionTask
extends AbstractSlideConversionTask<SlideConversionDataHolder> {
    private static final Logger log = LoggerFactory.getLogger(PPtDocumentConversionTask.class);
    private final SlideDocConversionData data;
    private final ConversionStore conversionStore;
    private final List<Integer> toConvertPages;

    public PPtDocumentConversionTask(Attachment attachment, String attachmentName, Resource resource, ConversionStore conversionStore, SlideDocConversionData data, List<Integer> toConvertPages) {
        super(attachment, attachmentName, resource);
        this.conversionStore = conversionStore;
        this.data = data;
        this.toConvertPages = toConvertPages;
    }

    @Override
    protected SlideConversionDataHolder convertFile() throws Exception {
        log.debug("Rendering pages for {}", (Object)this.getAttachmentDescription());
        long startTime = System.currentTimeMillis();
        try (InputStream in = this.getInputResource().getInputStream();){
            FileFormat inFormat = PPtDocumentConversionTask.getPptFormat(this.getAttachmentName());
            FileFormat outFormat = FileFormat.JPG;
            SlidesConverter converter = new SlidesConverter();
            BeanResult result = converter.convert(inFormat, outFormat, in, this.conversionStore, this.getAttachmentName(), this.toConvertPages);
            this.data.setNumSlides(result.numPages);
            SlideConversionDataHolder serializer = SlideConversionDataHolder.fromBeanResult(result, this.data);
            log.debug("Completed rendering {} pages for {} in {} ms", (Object)this.toConvertPages.size(), (Object)new Object[]{this.getAttachmentDescription(), System.currentTimeMillis() - startTime});
            SlideConversionDataHolder slideConversionDataHolder = serializer;
            return slideConversionDataHolder;
        }
    }

    public static FileFormat getPptFormat(String attachmentFileName) throws IllegalArgumentException {
        String lowerCaseAttachmentName = attachmentFileName.toLowerCase();
        if (lowerCaseAttachmentName.endsWith(".ppt")) {
            return FileFormat.PPT;
        }
        if (lowerCaseAttachmentName.endsWith(".pptx")) {
            return FileFormat.PPTX;
        }
        throw new IllegalArgumentException("Cannot convert slide, can only handle .ppt or .pptx, but instead got :" + attachmentFileName);
    }
}

