/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.context.OutputMimeTypeAwareContext
 *  com.opensymphony.xwork2.util.ValueStack
 *  org.apache.struts2.views.velocity.StrutsVelocityContext
 *  org.apache.velocity.VelocityContext
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareContext;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.List;
import org.apache.struts2.views.velocity.StrutsVelocityContext;
import org.apache.velocity.VelocityContext;

public final class OutputAwareStrutsVelocityContext
extends StrutsVelocityContext
implements OutputMimeTypeAwareContext {
    private String outputMimeType = "application/octet-stream";

    public OutputAwareStrutsVelocityContext(List<VelocityContext> chainedContexts, ValueStack stack) {
        super(chainedContexts, stack);
    }

    public String getOutputMimeType() {
        return this.outputMimeType;
    }

    public void setOutputMimeType(String outputMimeType) {
        this.outputMimeType = outputMimeType;
    }
}

