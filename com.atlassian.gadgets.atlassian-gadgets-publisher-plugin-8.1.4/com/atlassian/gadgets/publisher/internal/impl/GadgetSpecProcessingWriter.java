/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.google.common.base.Preconditions
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.publisher.internal.GadgetProcessor;
import com.atlassian.gadgets.publisher.internal.PublishedGadgetSpecNotFoundException;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

final class GadgetSpecProcessingWriter {
    private final GadgetProcessor gadgetProcessor;

    GadgetSpecProcessingWriter(GadgetProcessor gadgetProcessor) {
        this.gadgetProcessor = (GadgetProcessor)Preconditions.checkNotNull((Object)gadgetProcessor, (Object)"gadgetProcessor");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void write(PluginGadgetSpec pluginGadgetSpec, OutputStream output) throws IOException {
        Preconditions.checkNotNull((Object)pluginGadgetSpec, (Object)"pluginGadgetSpec");
        Preconditions.checkNotNull((Object)output, (Object)"output");
        InputStream gadgetSpecStream = pluginGadgetSpec.getInputStream();
        if (gadgetSpecStream == null) {
            throw new PublishedGadgetSpecNotFoundException(String.format("Could not write gadget spec: %s because the resource was not found", pluginGadgetSpec));
        }
        try {
            this.gadgetProcessor.process(gadgetSpecStream, output);
        }
        finally {
            IOUtils.closeQuietly((InputStream)gadgetSpecStream);
        }
    }
}

