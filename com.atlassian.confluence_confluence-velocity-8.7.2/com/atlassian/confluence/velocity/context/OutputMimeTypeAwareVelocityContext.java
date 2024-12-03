/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.velocity.context;

import com.atlassian.confluence.velocity.context.DefaultValueStackProvider;
import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareContext;
import java.util.Map;
import java.util.Objects;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public final class OutputMimeTypeAwareVelocityContext
extends VelocityContext
implements OutputMimeTypeAwareContext,
DefaultValueStackProvider {
    private String outputMimeType = "application/octet-stream";

    public OutputMimeTypeAwareVelocityContext() {
    }

    public OutputMimeTypeAwareVelocityContext(Map map) {
        super(map);
    }

    public OutputMimeTypeAwareVelocityContext(Context context) {
        super(context);
    }

    public OutputMimeTypeAwareVelocityContext(Map map, Context context) {
        super(map, context);
    }

    private OutputMimeTypeAwareVelocityContext(Map map, String outputMimeType) {
        super(map);
        this.outputMimeType = outputMimeType;
    }

    public static OutputMimeTypeAwareVelocityContext newHtmlContext(Map map) {
        return new OutputMimeTypeAwareVelocityContext(map, "text/html");
    }

    @Override
    public String getOutputMimeType() {
        return this.outputMimeType;
    }

    public void setOutputMimeType(String outputMimeType) {
        this.outputMimeType = Objects.requireNonNull(outputMimeType);
    }
}

