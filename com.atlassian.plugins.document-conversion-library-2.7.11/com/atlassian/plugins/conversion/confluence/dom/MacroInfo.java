/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom;

public class MacroInfo {
    public static MacroInfo MISSING_MACRO = new MacroInfo(true, false, false);
    boolean _isInline;
    boolean _hasBody;
    boolean _renderBody;

    public MacroInfo(boolean isInline, boolean hasBody, boolean renderBody) {
        this._isInline = isInline;
        this._hasBody = hasBody;
        this._renderBody = renderBody;
    }

    public boolean isInline() {
        return this._isInline;
    }

    public boolean hasBody() {
        return this._hasBody;
    }

    public boolean renderBody() {
        return this._renderBody;
    }
}

