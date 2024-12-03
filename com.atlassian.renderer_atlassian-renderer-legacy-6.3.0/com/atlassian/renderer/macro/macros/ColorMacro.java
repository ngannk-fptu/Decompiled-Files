/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.radeox.macro.parameter.MacroParameter
 */
package com.atlassian.renderer.macro.macros;

import com.atlassian.renderer.macro.BaseMacro;
import com.opensymphony.util.TextUtils;
import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.parameter.MacroParameter;

public class ColorMacro
extends BaseMacro {
    private String[] myParamDescription = new String[]{"1: name"};

    public String getName() {
        return "color";
    }

    public String[] getParamDescription() {
        return this.myParamDescription;
    }

    public void execute(Writer writer, MacroParameter macroParameter) throws IllegalArgumentException, IOException {
        String color = TextUtils.noNull((String)macroParameter.get(0)).trim();
        writer.write("<font color=\"" + color + "\">" + macroParameter.getContent() + "</font>");
    }
}

