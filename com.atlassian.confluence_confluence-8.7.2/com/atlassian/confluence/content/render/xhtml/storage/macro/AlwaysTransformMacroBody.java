/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroBodyTransformationCondition;

public class AlwaysTransformMacroBody
implements MacroBodyTransformationCondition {
    @Override
    public boolean shouldTransform(String macroName) {
        return true;
    }
}

