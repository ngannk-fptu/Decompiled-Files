/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumbnail
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.core.util.thumbnail.Thumbnail;
import java.io.File;
import java.io.InputStream;

public interface ThumbnailGenerator {
    public Thumbnail generate(InputStream var1, File var2, int var3, int var4);
}

