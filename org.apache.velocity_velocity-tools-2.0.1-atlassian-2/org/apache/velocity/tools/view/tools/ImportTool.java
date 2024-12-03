/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.tools;

import org.apache.velocity.tools.view.ViewContext;

@Deprecated
public class ImportTool
extends org.apache.velocity.tools.view.ImportTool {
    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            ViewContext ctx = (ViewContext)obj;
            this.setRequest(ctx.getRequest());
            this.setResponse(ctx.getResponse());
            this.setServletContext(ctx.getServletContext());
            this.setLog(ctx.getVelocityEngine().getLog());
        }
    }
}

