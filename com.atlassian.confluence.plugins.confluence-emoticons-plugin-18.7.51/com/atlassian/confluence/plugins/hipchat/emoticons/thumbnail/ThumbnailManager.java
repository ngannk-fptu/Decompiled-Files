/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.SavableAttachment
 *  com.atlassian.plugins.conversion.convert.ConversionException
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail;

import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.plugins.conversion.convert.ConversionException;
import java.util.Collection;

public interface ThumbnailManager {
    public Collection<SavableAttachment> generateThumbnails(SavableAttachment var1) throws ConversionException;
}

