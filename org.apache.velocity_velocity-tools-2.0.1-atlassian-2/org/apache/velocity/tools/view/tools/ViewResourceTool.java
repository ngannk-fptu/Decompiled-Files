/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.tools;

import org.apache.velocity.tools.generic.ResourceTool;
import org.apache.velocity.tools.view.ViewContext;

@Deprecated
public class ViewResourceTool
extends ResourceTool {
    public ViewResourceTool() {
        this.setDeprecationSupportMode(true);
    }

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            this.setLocale(((ViewContext)obj).getRequest().getLocale());
        }
    }
}

