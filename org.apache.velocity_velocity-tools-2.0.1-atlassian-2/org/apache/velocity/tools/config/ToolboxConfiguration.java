/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import java.util.Collection;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.CompoundConfiguration;
import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.ConfigurationException;
import org.apache.velocity.tools.config.InvalidScopeException;
import org.apache.velocity.tools.config.ToolConfiguration;

public class ToolboxConfiguration
extends CompoundConfiguration<ToolConfiguration> {
    private String scope = "request";

    public ToolboxConfiguration() {
        this.setProperty("scope", this.scope);
    }

    public void setScope(String scope) {
        if (scope == null) {
            throw new NullPointerException("Toolbox scope cannot be null");
        }
        this.scope = scope;
        this.setProperty("scope", scope);
    }

    public String getScope() {
        return this.scope;
    }

    public void addTool(ToolConfiguration tool) {
        this.addChild(tool);
    }

    public void removeTool(ToolConfiguration tool) {
        this.removeChild(tool);
    }

    public ToolConfiguration getTool(String key) {
        for (ToolConfiguration tool : this.getTools()) {
            if (!key.equals(tool.getKey())) continue;
            return tool;
        }
        return null;
    }

    public Collection<ToolConfiguration> getTools() {
        return this.getChildren();
    }

    public void setTools(Collection<ToolConfiguration> tools) {
        this.setChildren(tools);
    }

    @Override
    public void validate() {
        super.validate();
        if (this.getScope() == null) {
            throw new ConfigurationException((Configuration)this, "Toolbox scope cannot be null");
        }
        if (!Scope.exists(this.getScope())) {
            throw new ConfigurationException((Configuration)this, "Scope '" + this.getScope() + "' is not recognized. Please correct or add your new custom scope with " + Scope.class.getName() + ".add(\"" + this.getScope() + "\").");
        }
        for (ToolConfiguration tool : this.getTools()) {
            for (String invalidScope : tool.getInvalidScopes()) {
                if (!this.getScope().equals(invalidScope)) continue;
                throw new InvalidScopeException(this, tool);
            }
            String[] validScopes = tool.getValidScopes();
            if (validScopes == null || validScopes.length <= 0) continue;
            boolean found = false;
            for (String validScope : validScopes) {
                if (!this.getScope().equals(validScope)) continue;
                found = true;
                break;
            }
            if (found) continue;
            throw new InvalidScopeException(this, tool);
        }
    }

    @Override
    public int compareTo(Configuration conf) {
        if (!(conf instanceof ToolboxConfiguration)) {
            throw new UnsupportedOperationException("ToolboxConfigurations can only be compared to other ToolboxConfigurations");
        }
        ToolboxConfiguration toolbox = (ToolboxConfiguration)conf;
        return this.getScope().compareTo(toolbox.getScope());
    }

    @Override
    public int hashCode() {
        return this.scope.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ToolboxConfiguration) {
            return this.scope.equals(((ToolboxConfiguration)obj).scope);
        }
        return false;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Toolbox '");
        out.append(this.scope);
        out.append("' ");
        this.appendProperties(out);
        this.appendChildren(out, "tools: \n  ", "\n  ");
        return out.toString();
    }
}

