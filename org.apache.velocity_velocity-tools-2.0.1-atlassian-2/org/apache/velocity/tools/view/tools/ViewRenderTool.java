/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.view.tools;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.generic.RenderTool;

@Deprecated
public class ViewRenderTool
extends RenderTool {
    @Deprecated
    public void init(Object obj) {
        if (obj instanceof Context) {
            this.setVelocityContext((Context)obj);
        }
    }
}

