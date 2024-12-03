/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools;

import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.tools.ToolInfo;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.ToolConfiguration;
import org.apache.velocity.tools.config.ToolboxConfiguration;

public class ToolboxFactory {
    public static final String DEFAULT_SCOPE = "request";
    private final Map<String, Map<String, ToolInfo>> scopedToolInfo = new HashMap<String, Map<String, ToolInfo>>();
    private final Map<String, Map<String, Object>> scopedProperties = new HashMap<String, Map<String, Object>>();
    private Map<String, Object> data;
    private Map<String, Object> globalProperties;

    public synchronized void configure(FactoryConfiguration config) {
        config.validate();
        for (Data datum : config.getData()) {
            this.putData(datum.getKey(), datum.getConvertedValue());
        }
        for (ToolboxConfiguration toolbox : config.getToolboxes()) {
            String scope = toolbox.getScope();
            for (ToolConfiguration toolConfiguration : toolbox.getTools()) {
                this.addToolInfo(scope, toolConfiguration.createInfo());
            }
            Map<String, Object> newToolboxProps = toolbox.getPropertyMap();
            this.putProperties(scope, newToolboxProps);
            for (ToolInfo info : this.getToolInfo(scope).values()) {
                info.addProperties(newToolboxProps);
            }
        }
        Map<String, Object> newGlobalProps = config.getPropertyMap();
        this.putGlobalProperties(newGlobalProps);
        for (Map<String, ToolInfo> toolbox : this.scopedToolInfo.values()) {
            for (ToolInfo toolInfo : toolbox.values()) {
                toolInfo.addProperties(newGlobalProps);
            }
        }
    }

    protected synchronized Object putData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }
        return this.data.put(key, value);
    }

    protected void addToolInfo(String scope, ToolInfo tool) {
        this.getToolInfo(scope).put(tool.getKey(), tool);
    }

    protected synchronized Map<String, ToolInfo> getToolInfo(String scope) {
        Map<String, ToolInfo> tools = this.scopedToolInfo.get(scope);
        if (tools == null) {
            tools = new HashMap<String, ToolInfo>();
            this.scopedToolInfo.put(scope, tools);
        }
        return tools;
    }

    protected synchronized void putGlobalProperties(Map<String, Object> props) {
        if (props != null && !props.isEmpty()) {
            if (this.globalProperties == null) {
                this.globalProperties = new HashMap<String, Object>(props);
            } else {
                this.globalProperties.putAll(props);
            }
        }
    }

    protected synchronized void putProperties(String scope, Map<String, Object> props) {
        if (props != null && !props.isEmpty()) {
            Map<String, Object> properties = this.scopedProperties.get(scope);
            if (properties == null) {
                properties = new HashMap<String, Object>(props);
                this.scopedProperties.put(scope, properties);
            } else {
                properties.putAll(props);
            }
        }
    }

    public Object getGlobalProperty(String name) {
        if (this.globalProperties == null) {
            return null;
        }
        return this.globalProperties.get(name);
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public boolean hasTools(String scope) {
        Map<String, ToolInfo> tools = this.scopedToolInfo.get(scope);
        if (tools != null && !tools.isEmpty()) {
            return true;
        }
        return this.data != null && "application".equals(scope);
    }

    public Toolbox createToolbox(String scope) {
        Toolbox toolbox;
        Map<String, ToolInfo> tools = this.scopedToolInfo.get(scope);
        Map<String, Object> properties = this.scopedProperties.get(scope);
        if (properties == null) {
            toolbox = this.globalProperties == null ? new Toolbox(tools) : new Toolbox(tools, this.globalProperties);
        } else {
            if (this.globalProperties != null) {
                properties.putAll(this.globalProperties);
            }
            toolbox = new Toolbox(tools, properties);
        }
        if (this.data != null && (this.scopedToolInfo.size() == 1 || scope.equals("application"))) {
            toolbox.cacheData(this.getData());
        }
        return toolbox;
    }
}

