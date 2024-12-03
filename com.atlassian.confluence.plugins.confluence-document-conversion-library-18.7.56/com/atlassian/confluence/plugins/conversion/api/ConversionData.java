/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.StreamingOutput
 */
package com.atlassian.confluence.plugins.conversion.api;

import java.util.Optional;
import javax.ws.rs.core.StreamingOutput;

public interface ConversionData {
    public StreamingOutput getStreamingOutput();

    public long getContentLength();

    public Optional<String> getContentRange();

    public String getContentType();
}

