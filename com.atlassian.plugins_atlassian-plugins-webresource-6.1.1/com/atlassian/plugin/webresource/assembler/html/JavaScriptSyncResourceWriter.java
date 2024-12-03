/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  javax.annotation.Nonnull
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.helpers.ResourceServingHelpers;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

class JavaScriptSyncResourceWriter {
    private static final String CONTEXT_BATCH_TYPE = PluginUrlResource.BatchType.CONTEXT.name().toLowerCase();
    private static final String JAVASCRIPT_EXTENSION = "js";
    private static final String LINE_SEPARATOR = "\n";
    private final RequestCache cache;
    private final Globals globals;
    private final Writer writer;

    JavaScriptSyncResourceWriter(@Nonnull RequestState requestState, @Nonnull Writer writer) {
        Objects.requireNonNull(requestState, "The request state is mandatory for the creation of the SyncResourceWriter.");
        this.writer = Objects.requireNonNull(writer, "The writer is mandatory for the creation of the SyncResourceWriter.");
        this.cache = Objects.requireNonNull(requestState.getRequestCache());
        this.globals = Objects.requireNonNull(requestState.getGlobals());
    }

    void write(@Nonnull Collection<ResourceUrls> resources) {
        Objects.requireNonNull(resources, "The resource set is mandatory for to generate the data to be written.");
        try {
            LinkedHashMap<String, Set<Object>> urlParams = new LinkedHashMap<String, Set<Object>>();
            urlParams.put("data-wrm-key", new HashSet());
            urlParams.put("data-wrm-batch-type", new HashSet());
            Collection contents = resources.stream().map(ResourceUrls::getResourceUrl).flatMap(urls -> {
                urls.getParams().forEach((k, v) -> {
                    urlParams.putIfAbsent((String)k, new HashSet());
                    ((Set)urlParams.get(k)).add(v);
                });
                ((Set)urlParams.get("data-wrm-key")).add(urls.getKey());
                ((Set)urlParams.get("data-wrm-batch-type")).add(urls.getBatchType().name().toLowerCase());
                return urls.getResources(this.cache).stream();
            }).filter(resource -> JAVASCRIPT_EXTENSION.equals(resource.getNameType())).map(resource -> ResourceServingHelpers.transform(this.globals, new LinkedHashSet<String>(Collections.singletonList(resource.getParent().getKey())), null, resource, resource.getParams(), true)).collect(Collectors.toList());
            urlParams.put("data-initially-rendered", Collections.emptySet());
            if (((Set)urlParams.get("data-wrm-batch-type")).contains(CONTEXT_BATCH_TYPE)) {
                urlParams.put("data-wrm-batch-type", new HashSet<String>(Collections.singletonList(CONTEXT_BATCH_TYPE)));
            }
            if (CollectionUtils.isNotEmpty((Collection)contents)) {
                SyncOutputStream outputStream = new SyncOutputStream(this.writer);
                this.writer.write("<script");
                for (Map.Entry entry : urlParams.entrySet()) {
                    String attr = (String)entry.getKey();
                    String val = String.join((CharSequence)",", (Iterable)entry.getValue());
                    this.writer.write(" ");
                    this.writer.write(attr);
                    if (!StringUtils.isNotBlank((CharSequence)val)) continue;
                    this.writer.write("=\"");
                    this.writer.write(StringEscapeUtils.escapeHtml4((String)val));
                    this.writer.write("\"");
                }
                this.writer.write(">");
                this.writer.write(LINE_SEPARATOR);
                for (Content content : contents) {
                    content.writeTo(outputStream, false);
                    this.writer.write(LINE_SEPARATOR);
                }
                this.writer.write("</script>");
                this.writer.write(LINE_SEPARATOR);
            }
        }
        catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static final class SyncOutputStream
    extends OutputStream {
        private final Writer writer;

        private SyncOutputStream(Writer writer) {
            this.writer = writer;
        }

        @Override
        public void write(int data) throws IOException {
            this.writer.write(data);
        }
    }
}

