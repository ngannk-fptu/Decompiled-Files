/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.tools;

import java.util.List;
import org.apache.velocity.tools.view.ViewContext;

@Deprecated
public abstract class AbstractSearchTool
extends org.apache.velocity.tools.view.AbstractSearchTool {
    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            this.setRequest(((ViewContext)obj).getRequest());
        }
    }

    public boolean hasResults() {
        return this.hasItems();
    }

    public List getResults() {
        return this.getItems();
    }
}

