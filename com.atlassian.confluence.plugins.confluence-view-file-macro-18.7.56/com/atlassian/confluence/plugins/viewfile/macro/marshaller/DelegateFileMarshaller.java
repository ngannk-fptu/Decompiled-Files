/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.pages.Attachment
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.viewfile.macro.marshaller;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.viewfile.macro.marshaller.DefaultFilePlaceholderMarshaller;
import com.atlassian.confluence.plugins.viewfile.macro.marshaller.FilePlaceholderMarshaller;
import com.atlassian.confluence.plugins.viewfile.macro.marshaller.UnknownAttachmentFilePlaceholderMarshaller;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class DelegateFileMarshaller
implements FilePlaceholderMarshaller {
    private final DefaultFilePlaceholderMarshaller defaultFilePlaceholderMarshaller;
    private final List<FilePlaceholderMarshaller> filePlaceholderMarshallers;

    public DelegateFileMarshaller(DefaultFilePlaceholderMarshaller defaultFilePlaceholderMarshaller, UnknownAttachmentFilePlaceholderMarshaller unknownAttachmentFilePlaceholderMarshaller) {
        this.defaultFilePlaceholderMarshaller = defaultFilePlaceholderMarshaller;
        this.filePlaceholderMarshallers = new ArrayList<FilePlaceholderMarshaller>();
        this.filePlaceholderMarshallers.add(unknownAttachmentFilePlaceholderMarshaller);
    }

    @Override
    public ImagePlaceholder getImagePlaceholder(Attachment attachment, Map<String, String> params) {
        return this.getFilePlaceholderMarshaller(attachment).getImagePlaceholder(attachment, params);
    }

    @Override
    public boolean handles(Attachment attachment) {
        return true;
    }

    @Override
    public Streamable getRenderedContentStreamable(Attachment attachment, Map<String, String> params, ConversionContext conversionContext) {
        return this.getFilePlaceholderMarshaller(attachment).getRenderedContentStreamable(attachment, params, conversionContext);
    }

    private FilePlaceholderMarshaller getFilePlaceholderMarshaller(final Attachment attachment) {
        FilePlaceholderMarshaller marshaller = (FilePlaceholderMarshaller)Iterables.find(this.filePlaceholderMarshallers, (Predicate)new Predicate<FilePlaceholderMarshaller>(){

            public boolean apply(@Nullable FilePlaceholderMarshaller filePlaceholderMarshaller) {
                return filePlaceholderMarshaller.handles(attachment);
            }
        }, null);
        if (marshaller != null) {
            return marshaller;
        }
        return this.defaultFilePlaceholderMarshaller;
    }
}

