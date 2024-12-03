/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.mail.server.MailServerManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class IsMailServerConfiguredCondition
implements Condition {
    private final MailServerManager mailServerManager;

    public IsMailServerConfiguredCondition(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return this.mailServerManager.isDefaultSMTPMailServerDefined();
    }
}

