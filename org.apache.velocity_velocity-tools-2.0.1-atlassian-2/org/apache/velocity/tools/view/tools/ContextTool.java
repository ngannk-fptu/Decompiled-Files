/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.tools;

import java.util.Map;
import org.apache.velocity.tools.view.ViewContext;
import org.apache.velocity.tools.view.ViewContextTool;

@Deprecated
public class ContextTool
extends ViewContextTool {
    @Deprecated
    public static final String OLD_SAFE_MODE_KEY = "safe-mode";

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            ViewContext ctx = (ViewContext)obj;
            this.context = ctx.getVelocityContext();
            this.request = ctx.getRequest();
            this.session = this.request.getSession(false);
            this.application = ctx.getServletContext();
        }
    }

    @Override
    public void configure(Map params) {
        if (params != null) {
            Object oldSafeMode = params.get(OLD_SAFE_MODE_KEY);
            if (oldSafeMode != null) {
                params.put("safeMode", oldSafeMode);
            }
            super.configure(params);
        }
    }
}

