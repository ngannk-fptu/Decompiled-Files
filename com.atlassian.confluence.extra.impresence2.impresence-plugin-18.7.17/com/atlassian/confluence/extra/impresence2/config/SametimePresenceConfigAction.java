/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.impresence2.config;

import com.atlassian.confluence.extra.impresence2.config.ServerPresenceConfigAction;

public class SametimePresenceConfigAction
extends ServerPresenceConfigAction {
    @Override
    protected String getServiceKey() {
        return "sametime";
    }

    @Override
    protected String getServiceName() {
        return this.getText("sametime.config.service.name");
    }
}

