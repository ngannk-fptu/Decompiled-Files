/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.impresence2.config;

import com.atlassian.confluence.extra.impresence2.config.ServerPresenceConfigAction;

public class WildfirePresenceConfigAction
extends ServerPresenceConfigAction {
    @Override
    protected String getServiceKey() {
        return "wildfire";
    }

    @Override
    protected String getServiceName() {
        return this.getText("wildfire.config.service.name");
    }
}

