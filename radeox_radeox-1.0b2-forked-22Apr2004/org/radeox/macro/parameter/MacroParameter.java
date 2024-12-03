/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.parameter;

import java.util.Map;
import org.radeox.api.engine.context.RenderContext;

public interface MacroParameter {
    public void setParams(String var1);

    public String getContent();

    public void setContent(String var1);

    public int getLength();

    public String get(String var1, int var2);

    public String get(String var1);

    public String get(int var1);

    public Map getParams();

    public void setStart(int var1);

    public void setEnd(int var1);

    public int getStart();

    public int getEnd();

    public void setContentStart(int var1);

    public void setContentEnd(int var1);

    public int getContentStart();

    public int getContentEnd();

    public RenderContext getContext();
}

