/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.script.ScriptDef;

public class ScriptDefBase
extends Task
implements DynamicConfigurator {
    private Map<String, List<Object>> nestedElementMap = new HashMap<String, List<Object>>();
    private Map<String, String> attributes = new HashMap<String, String>();
    private String text;

    @Override
    public void execute() {
        this.getScript().executeScript(this.attributes, this.nestedElementMap, this);
    }

    private ScriptDef getScript() {
        String name = this.getTaskType();
        Map scriptRepository = (Map)this.getProject().getReference("org.apache.ant.scriptrepo");
        if (scriptRepository == null) {
            throw new BuildException("Script repository not found for " + name);
        }
        ScriptDef definition = (ScriptDef)scriptRepository.get(this.getTaskType());
        if (definition == null) {
            throw new BuildException("Script definition not found for " + name);
        }
        return definition;
    }

    @Override
    public Object createDynamicElement(String name) {
        List nestedElementList = this.nestedElementMap.computeIfAbsent(name, k -> new ArrayList());
        Object element = this.getScript().createNestedElement(name);
        nestedElementList.add(element);
        return element;
    }

    @Override
    public void setDynamicAttribute(String name, String value) {
        ScriptDef definition = this.getScript();
        if (!definition.isAttributeSupported(name)) {
            throw new BuildException("<%s> does not support the \"%s\" attribute", this.getTaskType(), name);
        }
        this.attributes.put(name, value);
    }

    public void addText(String text) {
        this.text = this.getProject().replaceProperties(text);
    }

    public String getText() {
        return this.text;
    }

    public void fail(String message) {
        throw new BuildException(message);
    }
}

