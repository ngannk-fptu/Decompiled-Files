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

public class SafeModeErrorReenablingPluginRepresentation
extends ErrorRepresentation {
    @JsonProperty
    private final String pluginKey;
    @JsonProperty
    private final String pluginName;
    @JsonProperty
    private final boolean enabling;

    @JsonCreator
    public SafeModeErrorReenablingPluginRepresentation(@JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="pluginName") String pluginName, @JsonProperty(value="enabling") boolean enabling) {
        super("System failed to restore from Safe Mode. " + (enabling ? "Reenabling" : "Disabling") + " plugin '" + pluginName + "' failed while exiting safe mode.", "upm.safeMode.error.enabling.plugin.failed");
        this.pluginKey = pluginKey;
        this.pluginName = pluginName;
        this.enabling = enabling;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public boolean isEnabling() {
        return this.enabling;
    }
}

