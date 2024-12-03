/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.confluence.plugins.highlight.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugins.highlight.service.HighlightOptionPanelConfigService;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;

public class DisplayPanelCondition
extends BaseConfluenceCondition {
    private final HighlightOptionPanelConfigService highlightOptionPanelConfigService;
    private boolean highlightConfigSupported;

    public DisplayPanelCondition(HighlightOptionPanelConfigService highlightOptionPanelConfigService) {
        this.highlightOptionPanelConfigService = highlightOptionPanelConfigService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.highlightConfigSupported = this.highlightOptionPanelConfigService.isSupported();
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        return !this.highlightConfigSupported || this.highlightOptionPanelConfigService.isEnabled();
    }
}

