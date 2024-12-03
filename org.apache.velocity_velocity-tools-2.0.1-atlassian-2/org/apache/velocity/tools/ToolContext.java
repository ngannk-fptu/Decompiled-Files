/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Toolbox;

public class ToolContext
implements Context {
    public static final String PATH_KEY = "requestPath";
    public static final String CONTEXT_KEY = "velocityContext";
    public static final String ENGINE_KEY = "velocityEngine";
    public static final String LOCALE_KEY = "locale";
    public static final String LOG_KEY = "log";
    public static final String CATCH_EXCEPTIONS_KEY = "catchExceptions";
    private List<Toolbox> toolboxes = new ArrayList<Toolbox>();
    private Map<String, Object> toolProps = new HashMap<String, Object>(12);
    private Map<String, Object> localContext = new HashMap<String, Object>();
    private boolean userOverwrite = true;

    public ToolContext() {
        this.putToolProperty(CONTEXT_KEY, this);
    }

    public ToolContext(VelocityEngine engine) {
        this();
        this.putVelocityEngine(engine);
    }

    public ToolContext(Map<String, Object> toolProps) {
        this();
        if (toolProps != null) {
            this.toolProps.putAll(toolProps);
        }
    }

    public void setUserCanOverwriteTools(boolean overwrite) {
        this.userOverwrite = overwrite;
    }

    public boolean getUserCanOverwriteTools() {
        return this.userOverwrite;
    }

    public void addToolbox(Toolbox toolbox) {
        this.toolboxes.add(toolbox);
    }

    public Map<String, Object> getToolbox() {
        HashMap<String, Object> aggregate = new HashMap<String, Object>();
        Map<String, Object> toolProps = this.getToolProperties();
        for (Toolbox toolbox : this.getToolboxes()) {
            aggregate.putAll(toolbox.getAll(toolProps));
        }
        return aggregate;
    }

    public Map<String, Class> getToolClassMap() {
        HashMap<String, Class> toolClasses = new HashMap<String, Class>();
        int n = this.getToolboxes().size();
        for (int i = n - 1; i >= 0; --i) {
            Toolbox toolbox = this.getToolboxes().get(i);
            toolClasses.putAll(toolbox.getToolClassMap());
        }
        return toolClasses;
    }

    protected List<Toolbox> getToolboxes() {
        return this.toolboxes;
    }

    protected Map<String, Object> getToolProperties() {
        return this.toolProps;
    }

    public void putVelocityEngine(VelocityEngine engine) {
        this.putToolProperty(ENGINE_KEY, engine);
        this.putToolProperty(LOG_KEY, engine.getLog());
        Object ehme = engine.getProperty("eventhandler.methodexception.class");
        if (ehme != null) {
            this.putToolProperty(CATCH_EXCEPTIONS_KEY, Boolean.FALSE);
        }
    }

    public Object putToolProperty(String key, Object value) {
        return this.toolProps.put(key, value);
    }

    public void putToolProperties(Map<String, Object> props) {
        if (props != null) {
            for (Map.Entry<String, Object> prop : props.entrySet()) {
                this.putToolProperty(prop.getKey(), prop.getValue());
            }
        }
    }

    public Object put(String key, Object value) {
        return this.localContext.put(key, value);
    }

    public Object get(String key) {
        Object value;
        Object object = value = this.userOverwrite ? this.internalGet(key) : this.findTool(key);
        if (value == null) {
            value = this.userOverwrite ? this.findTool(key) : this.internalGet(key);
        }
        return value;
    }

    protected Object internalGet(String key) {
        return this.localContext.get(key);
    }

    protected Object findTool(String key) {
        String path = (String)this.toolProps.get(PATH_KEY);
        for (Toolbox toolbox : this.getToolboxes()) {
            Object tool = toolbox.get(key, path, this.toolProps);
            if (tool == null) continue;
            return tool;
        }
        return null;
    }

    public Set<String> keySet() {
        HashSet<String> keys = new HashSet<String>();
        for (Toolbox toolbox : this.getToolboxes()) {
            keys.addAll(toolbox.getKeys());
        }
        keys.addAll(this.localContext.keySet());
        return keys;
    }

    public boolean containsKey(Object key) {
        return this.keySet().contains(key);
    }

    public Object[] getKeys() {
        return this.keySet().toArray();
    }

    public Object remove(Object key) {
        return this.localContext.remove(key);
    }

    public void putAll(Map context) {
        this.localContext.putAll(context);
    }
}

