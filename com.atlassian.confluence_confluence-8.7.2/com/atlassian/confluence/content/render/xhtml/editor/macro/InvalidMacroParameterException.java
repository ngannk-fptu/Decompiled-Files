/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.macro.browser.beans.MacroParameterType;

public class InvalidMacroParameterException
extends Exception {
    public InvalidMacroParameterException(String macroName, String parameterName, String value, MacroParameterType type, Throwable cause) {
        super("Invalid parameter value for macro '" + macroName + "' parameter '" + parameterName + "' (type " + type + "): '" + value + "'", cause);
    }
}

