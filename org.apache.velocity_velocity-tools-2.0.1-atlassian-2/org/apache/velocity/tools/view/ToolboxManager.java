/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view;

import java.util.Map;
import org.apache.velocity.tools.view.ToolInfo;

@Deprecated
public interface ToolboxManager {
    public void addTool(ToolInfo var1);

    public void addData(ToolInfo var1);

    public Map getToolbox(Object var1);
}

