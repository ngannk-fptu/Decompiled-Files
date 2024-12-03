/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.benryan.components;

import com.atlassian.confluence.pages.Attachment;
import com.benryan.components.ConversionCacheManager;
import com.benryan.components.DefaultSlideCacheManager;
import com.benryan.conversion.SlidePageConversionData;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.Future;

public interface SlideCacheManager
extends ConversionCacheManager {
    public Future<SlidePageConversionData> getSlideConversionData(Attachment var1, int var2) throws IOException;

    public void removeFromQueue(long var1);

    public Set<DefaultSlideCacheManager.QueueData> getBeingConvertedKeys();

    public File getTempDir();

    public void writeSlideToStream(SlidePageConversionData var1, OutputStream var2) throws IOException;
}

