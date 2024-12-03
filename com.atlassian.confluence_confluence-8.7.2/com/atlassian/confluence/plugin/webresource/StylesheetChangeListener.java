/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.plugin.webresource.CssResourceCounterManager;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;
import com.atlassian.event.api.EventListener;

public class StylesheetChangeListener {
    private final CssResourceCounterManager cssResourceCounterManager;

    public StylesheetChangeListener(CssResourceCounterManager cssResourceCounterManager) {
        this.cssResourceCounterManager = cssResourceCounterManager;
    }

    @EventListener
    public void handleEvent(LookAndFeelEvent event) {
        if (!(event instanceof Updated)) {
            return;
        }
        if (event.isGlobal()) {
            this.cssResourceCounterManager.invalidateGlobalCssResourceCounter();
        } else {
            this.cssResourceCounterManager.invalidateSpaceCssResourceCounter(event.getSpaceKey());
        }
    }
}

