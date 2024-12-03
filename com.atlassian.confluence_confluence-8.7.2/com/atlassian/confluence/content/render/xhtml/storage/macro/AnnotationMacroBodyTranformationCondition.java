/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.macro.annotation.RequiresFormat;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroBodyTransformationCondition;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.XhtmlMacroManager;
import java.lang.reflect.Method;
import java.util.Map;

public class AnnotationMacroBodyTranformationCondition
implements MacroBodyTransformationCondition {
    private final MacroManager macroManager;

    public AnnotationMacroBodyTranformationCondition(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    @Override
    public boolean shouldTransform(String macroName) {
        Macro macro = this.macroManager.getMacroByName(macroName);
        if (macro != null) {
            Method execute;
            macro = XhtmlMacroManager.unwrapMacroProxy(macro);
            try {
                execute = macro.getClass().getMethod("execute", Map.class, String.class, ConversionContext.class);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("A macro without an execute method...", e);
            }
            RequiresFormat format = execute.getAnnotation(RequiresFormat.class);
            if (format != null) {
                switch (format.value()) {
                    case Storage: {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
        return true;
    }
}

