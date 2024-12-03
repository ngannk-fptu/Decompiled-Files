/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.tools;

import org.apache.velocity.tools.view.PagerTool;
import org.apache.velocity.tools.view.ViewContext;

@Deprecated
public abstract class AbstractPagerTool
extends PagerTool {
    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            this.setRequest(((ViewContext)obj).getRequest());
        }
    }
}

