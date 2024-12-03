/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.google.common.base.Predicate
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.WebResource;
import com.google.common.base.Predicate;
import java.io.Writer;

@ExperimentalApi
public interface WebResourceSet {
    public void writeHtmlTags(Writer var1, UrlMode var2);

    public void writeHtmlTags(Writer var1, UrlMode var2, Predicate<WebResource> var3);

    public void writePrefetchLinks(Writer var1, UrlMode var2);

    public Iterable<WebResource> getResources();

    public <T extends WebResource> Iterable<T> getResources(Class<T> var1);

    public boolean isComplete();
}

