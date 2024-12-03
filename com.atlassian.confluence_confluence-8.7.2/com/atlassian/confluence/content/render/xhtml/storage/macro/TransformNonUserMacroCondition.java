/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroBodyTransformationCondition;
import com.atlassian.confluence.macro.GenericVelocityMacro;
import com.atlassian.confluence.macro.xhtml.MacroManager;

public class TransformNonUserMacroCondition
implements MacroBodyTransformationCondition {
    private final MacroManager xhtmlMacroManager;

    public TransformNonUserMacroCondition(MacroManager xhtmlMacroManager) {
        this.xhtmlMacroManager = xhtmlMacroManager;
    }

    @Override
    public boolean shouldTransform(String macroName) {
        return !(this.xhtmlMacroManager.getMacroByName(macroName) instanceof GenericVelocityMacro);
    }
}

