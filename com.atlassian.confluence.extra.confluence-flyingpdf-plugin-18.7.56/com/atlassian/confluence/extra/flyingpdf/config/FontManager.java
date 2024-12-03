/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import java.io.IOException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public interface FontManager {
    public void installFont(Resource var1) throws IOException;

    public FileSystemResource getInstalledFont();

    public boolean isCustomFontInstalled();

    public void removeInstalledFont() throws IOException;
}

