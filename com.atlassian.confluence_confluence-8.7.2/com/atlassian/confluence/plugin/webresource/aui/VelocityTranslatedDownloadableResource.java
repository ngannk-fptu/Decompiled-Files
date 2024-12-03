/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource
 */
package com.atlassian.confluence.plugin.webresource.aui;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;

public class VelocityTranslatedDownloadableResource
extends CharSequenceDownloadableResource {
    public VelocityTranslatedDownloadableResource(DownloadableResource nextResource) {
        super(nextResource);
    }

    protected CharSequence transform(CharSequence originalContent) {
        return VelocityUtils.getRenderedContent(originalContent, MacroUtils.defaultVelocityContext());
    }
}

