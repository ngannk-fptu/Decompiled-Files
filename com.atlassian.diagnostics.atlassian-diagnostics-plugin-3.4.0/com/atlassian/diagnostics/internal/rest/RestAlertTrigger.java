/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.internal.rest.RestEntity;

public class RestAlertTrigger
extends RestEntity {
    public RestAlertTrigger(AlertTrigger trigger) {
        this.put("pluginKey", trigger.getPluginKey());
        trigger.getPluginVersion().ifPresent(version -> this.put("pluginVersion", version));
        trigger.getModule().ifPresent(module -> this.put("module", module));
    }
}

