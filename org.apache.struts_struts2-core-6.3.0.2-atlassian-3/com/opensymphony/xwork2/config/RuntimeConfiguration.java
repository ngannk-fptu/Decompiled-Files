/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import java.io.Serializable;
import java.util.Map;

public interface RuntimeConfiguration
extends Serializable {
    public ActionConfig getActionConfig(String var1, String var2);

    public Map<String, Map<String, ActionConfig>> getActionConfigs();
}

