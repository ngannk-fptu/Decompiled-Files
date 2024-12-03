/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 */
package com.atlassian.confluence.plugins.emailgateway.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager;

public class EmailGatewayEnableCondition
extends BaseConfluenceCondition {
    private EmailGatewaySettingsManager emailGatewaySettingsManager;
    private InboundMailServerManager inboundMailServerManager;

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        return this.inboundMailServerManager.getMailServer() != null && (this.emailGatewaySettingsManager.isAllowToCreateCommentByEmail() || this.emailGatewaySettingsManager.isAllowToCreatePageByEmail());
    }

    public void setEmailGatewaySettingsManager(EmailGatewaySettingsManager emailGatewaySettingsManager) {
        this.emailGatewaySettingsManager = emailGatewaySettingsManager;
    }

    public void setInboundMailServerManager(InboundMailServerManager inboundMailServerManager) {
        this.inboundMailServerManager = inboundMailServerManager;
    }
}

