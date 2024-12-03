/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.basic;

import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.basic.AbstractPanelMacro;

public class PanelMacro
extends AbstractPanelMacro {
    public PanelMacro() {
    }

    public PanelMacro(SubRenderer subRenderer) {
        this.setSubRenderer(subRenderer);
    }

    @Override
    protected String getPanelCSSClass() {
        return "panel";
    }

    @Override
    protected String getPanelContentCSSClass() {
        return "panelContent";
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }

    @Override
    protected String getPanelHeaderCSSClass() {
        return "panelHeader";
    }
}

