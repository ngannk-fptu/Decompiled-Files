/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.tools;

import org.apache.velocity.tools.view.ViewContext;

@Deprecated
public class CookieTool
extends org.apache.velocity.tools.view.CookieTool {
    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            ViewContext ctx = (ViewContext)obj;
            this.setRequest(ctx.getRequest());
            this.setResponse(ctx.getResponse());
        }
    }
}

