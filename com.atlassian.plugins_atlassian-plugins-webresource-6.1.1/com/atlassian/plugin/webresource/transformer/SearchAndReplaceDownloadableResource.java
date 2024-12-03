/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadableResource
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;
import com.atlassian.plugin.webresource.transformer.SearchAndReplacer;

@Deprecated
public class SearchAndReplaceDownloadableResource
extends CharSequenceDownloadableResource {
    private final SearchAndReplacer grep;

    public SearchAndReplaceDownloadableResource(DownloadableResource originalResource, SearchAndReplacer grep) {
        super(originalResource);
        this.grep = grep;
    }

    @Override
    public CharSequence transform(CharSequence originalContent) {
        return this.grep.replaceAll(originalContent);
    }
}

