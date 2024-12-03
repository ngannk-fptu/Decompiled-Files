/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml;

import com.atlassian.confluence.plugins.macros.advanced.xhtml.AbstractPanelMacro;

public abstract class PanelMacro
extends AbstractPanelMacro {
    @Override
    protected String getPanelCSSClass() {
        return "panel";
    }

    @Override
    protected String getPanelContentCSSClass() {
        return "panelContent";
    }

    @Override
    protected String getPanelHeaderCSSClass() {
        return "panelHeader";
    }
}

