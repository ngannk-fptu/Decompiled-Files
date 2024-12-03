/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.WebResource
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.WebResourceInformation;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.plugin.webresource.assembler.html.SwallowErrorsWriter;
import com.atlassian.plugin.webresource.data.DataTagWriter;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.webresource.api.assembler.WebResource;
import com.google.common.base.Predicate;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public abstract class HtmlTagWriter {
    private final Collection<HtmlTagFormatter> formatters;
    private final RequestState requestState;
    private final SwallowErrorsWriter swallowErrorsWriter;
    private final Writer writer;

    public HtmlTagWriter(@Nonnull RequestState requestState, @Nonnull Writer writer, @Nonnull Collection<HtmlTagFormatter> formatters) {
        this.requestState = Objects.requireNonNull(requestState, "The request state is mandatory for the creation of HtmlTagWriter.");
        this.writer = Objects.requireNonNull(writer, "The writer is mandatory for the creation of HtmlTagWriter.");
        this.formatters = formatters;
        this.swallowErrorsWriter = new SwallowErrorsWriter(writer);
    }

    public void writeHtmlTag(@Nonnull WebResourceInformation information, @Nonnull Predicate<WebResource> predicate, @Nonnull Predicate<ResourceUrl> legacyPredicate) {
        Objects.requireNonNull(information, "The information of the resources is mandatory to write html tags.");
        Objects.requireNonNull(predicate, "The predicate for the WebResource is mandatory for the filtering of tags to be written");
        Objects.requireNonNull(legacyPredicate, "The predicate for the ResourceUrl is mandatory for the filtering of tags to be written");
        this.writeResourceData(information, predicate);
        Collection resources = information.getResourceUrls().stream().filter(container -> predicate.apply(container.getPluginUrlResource())).filter(container -> legacyPredicate.apply((Object)container.getResourceUrl())).collect(Collectors.toCollection(LinkedList::new));
        this.writeHtmlTag(resources);
    }

    public void writeHtmlTag(@Nonnull Collection<ResourceUrls> resources) {
        Objects.requireNonNull(resources, "The resource urls are mandatory for the creation the HTML tags.");
        for (HtmlTagFormatter formatter : this.formatters) {
            Iterator<ResourceUrls> iterator = resources.iterator();
            while (iterator.hasNext()) {
                ResourceUrls resource = iterator.next();
                if (!formatter.matches(resource.getResourceUrl().getName())) continue;
                this.writeHtmlTag(this.generateHtmlTag(resource, formatter));
                iterator.remove();
            }
        }
        resources.forEach(this.swallowErrorsWriter::write);
    }

    protected void writeHtmlTag(String ... html) {
        this.swallowErrorsWriter.write(html);
    }

    @Nonnull
    abstract String generateHtmlTag(@Nonnull ResourceUrls var1, @Nonnull HtmlTagFormatter var2);

    private void writeResourceData(WebResourceInformation information, Predicate<WebResource> predicate) {
        try {
            Collection resourcesData = information.getData().stream().filter(arg_0 -> predicate.apply(arg_0)).collect(Collectors.toList());
            new DataTagWriter().write(this.writer, resourcesData);
        }
        catch (IOException exception) {
            Support.LOGGER.error("IOException encountered rendering data tags", (Throwable)exception);
        }
    }
}

