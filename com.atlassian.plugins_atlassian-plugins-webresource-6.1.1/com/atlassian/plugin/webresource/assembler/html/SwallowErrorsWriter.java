/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.impl.support.Support;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import javax.annotation.Nonnull;

class SwallowErrorsWriter {
    private static final String ERROR_TAG_BEGIN = "<!-- Error loading resource \"";
    private static final String ERROR_TAG_END = "<!-- Error loading resource \"";
    private static final String NOT_FOUND_ERROR_MESSAGE = "\".  No resource formatter matches \"";
    private final Writer writer;

    SwallowErrorsWriter(@Nonnull Writer writer) {
        this.writer = Objects.requireNonNull(writer, "The writer is mandatory for the creation of SwallowErrorsWriter.");
    }

    void write(@Nonnull ResourceUrls resourceUrls) {
        Objects.requireNonNull(resourceUrls, "The resource urls are mandatory for the creation of the tags.");
        this.write("<!-- Error loading resource \"", resourceUrls.getResourceUrl().getKey(), NOT_FOUND_ERROR_MESSAGE, resourceUrls.getResourceUrl().getName(), "<!-- Error loading resource \"");
    }

    void write(String ... contents) {
        Objects.requireNonNull(contents, "The contents are mandatory.");
        try {
            for (String content : contents) {
                this.writer.write(content);
            }
        }
        catch (IOException exception) {
            Support.LOGGER.error("IOException encountered rendering resource", (Throwable)exception);
        }
    }
}

