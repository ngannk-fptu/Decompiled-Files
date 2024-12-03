/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.ConfigurationException;
import org.apache.velocity.tools.config.ToolConfiguration;
import org.apache.velocity.tools.config.ToolboxConfiguration;

public class InvalidScopeException
extends ConfigurationException {
    private final transient ToolConfiguration tool;

    public InvalidScopeException(ToolboxConfiguration toolbox, ToolConfiguration tool) {
        super((Configuration)toolbox, "Toolbox with scope '" + toolbox.getScope() + "' may not contain a " + tool.getClassname() + '.');
        this.tool = tool;
    }

    public ToolConfiguration getToolConfiguration() {
        return this.tool;
    }
}

