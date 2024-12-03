/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.radeox.macro.BaseMacro
 */
package com.atlassian.renderer.macro;

import com.atlassian.renderer.macro.Macro;

public abstract class BaseMacro
extends org.radeox.macro.BaseMacro
implements Macro {
    String description;
    String resourcePath;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}

