/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.tools;

import org.apache.velocity.tools.view.BrowserTool;
import org.apache.velocity.tools.view.ViewContext;

@Deprecated
public class BrowserSnifferTool
extends BrowserTool {
    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            this.setRequest(((ViewContext)obj).getRequest());
        }
    }
}

