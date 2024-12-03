/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.core.rest.representations.ErrorRepresentation;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SafeModeErrorReenablingPluginModuleRepresentation
extends ErrorRepresentation {
    @JsonProperty
    private final String pluginKey;
    @JsonProperty
    private final String pluginName;
    @JsonProperty
    private final String moduleKey;
    @JsonProperty
    private final String moduleName;
    @JsonProperty
    private final boolean enabling;

    @JsonCreator
    public SafeModeErrorReenablingPluginModuleRepresentation(@JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="pluginName") String pluginName, @JsonProperty(value="moduleKey") String moduleKey, @JsonProperty(value="moduleName") String moduleName, @JsonProperty(value="enabling") boolean enabling) {
        super("System failed to restore from Safe Mode. " + (enabling ? "Reenabling" : "Disabling") + " plugin module '" + moduleName + "' from '" + pluginName + "' failed while exiting safe mode.", "upm.safeMode.error.enabling.plugin.module.failed");
        this.pluginKey = pluginKey;
        this.pluginName = pluginName;
        this.moduleKey = moduleKey;
        this.moduleName = moduleName;
        this.enabling = enabling;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public boolean isEnabling() {
        return this.enabling;
    }
}

