/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.AbstractTransformedDownloadableResource;
import com.atlassian.plugin.webresource.transformer.TransformerUtils;
import java.io.OutputStream;
import java.nio.charset.Charset;

public abstract class CharSequenceDownloadableResource
extends AbstractTransformedDownloadableResource {
    protected CharSequenceDownloadableResource(DownloadableResource originalResource) {
        super(originalResource);
    }

    public void streamResource(OutputStream out) throws DownloadException {
        TransformerUtils.transformAndStreamResource(this.getOriginalResource(), TransformerUtils.UTF8, out, this::transform);
    }

    protected Charset encoding() {
        return TransformerUtils.UTF8;
    }

    protected abstract CharSequence transform(CharSequence var1);
}

