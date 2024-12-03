/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.AbstractDownloadableResource;
import java.io.IOException;
import java.io.InputStream;

public class EmptyDownloadableResource
extends AbstractDownloadableResource {
    private static final InputStream EMPTY_INPUT_STREAM = new EmptyInputStream();

    public EmptyDownloadableResource(Plugin plugin, ResourceLocation resourceLocation) {
        super(plugin, resourceLocation, null);
    }

    @Override
    protected InputStream getResourceAsStream(String resourceLocation) {
        return EMPTY_INPUT_STREAM;
    }

    private static class EmptyInputStream
    extends InputStream {
        private EmptyInputStream() {
        }

        @Override
        public int read() throws IOException {
            return -1;
        }
    }
}

