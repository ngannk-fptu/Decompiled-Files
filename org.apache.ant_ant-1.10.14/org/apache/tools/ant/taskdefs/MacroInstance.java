/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicAttribute;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.property.LocalProperties;
import org.apache.tools.ant.taskdefs.MacroDef;

public class MacroInstance
extends Task
implements DynamicAttribute,
TaskContainer {
    private MacroDef macroDef;
    private Map<String, String> map = new HashMap<String, String>();
    private Map<String, MacroDef.TemplateElement> nsElements = null;
    private Map<String, UnknownElement> presentElements;
    private Map<String, String> localAttributes;
    private String text = null;
    private String implicitTag = null;
    private List<Task> unknownElements = new ArrayList<Task>();
    private static final int STATE_NORMAL = 0;
    private static final int STATE_EXPECT_BRACKET = 1;
    private static final int STATE_EXPECT_NAME = 2;

    public void setMacroDef(MacroDef macroDef) {
        this.macroDef = macroDef;
    }

    public MacroDef getMacroDef() {
        return this.macroDef;
    }

    @Override
    public void setDynamicAttribute(String name, String value) {
        this.map.put(name.toLowerCase(Locale.ENGLISH), value);
    }

    @Deprecated
    public Object createDynamicElement(String name) throws BuildException {
        throw new BuildException("Not implemented any more");
    }

    private Map<String, MacroDef.TemplateElement> getNsElements() {
        if (this.nsElements == null) {
            this.nsElements = new HashMap<String, MacroDef.TemplateElement>();
            for (Map.Entry<String, MacroDef.TemplateElement> entry : this.macroDef.getElements().entrySet()) {
                this.nsElements.put(entry.getKey(), entry.getValue());
                MacroDef.TemplateElement te = entry.getValue();
                if (!te.isImplicit()) continue;
                this.implicitTag = te.getName();
            }
        }
        return this.nsElements;
    }

    @Override
    public void addTask(Task nestedTask) {
        this.unknownElements.add(nestedTask);
    }

    private void processTasks() {
        if (this.implicitTag != null) {
            return;
        }
        for (Task task : this.unknownElements) {
            UnknownElement ue = (UnknownElement)task;
            String name = ProjectHelper.extractNameFromComponentName(ue.getTag()).toLowerCase(Locale.ENGLISH);
            if (this.getNsElements().get(name) == null) {
                throw new BuildException("unsupported element %s", name);
            }
            if (this.presentElements.get(name) != null) {
                throw new BuildException("Element %s already present", name);
            }
            this.presentElements.put(name, ue);
        }
    }

    private String macroSubs(String s, Map<String, String> macroMapping) {
        if (s == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        StringBuilder macroName = null;
        int state = 0;
        block10: for (char ch : s.toCharArray()) {
            switch (state) {
                case 0: {
                    if (ch == '@') {
                        state = 1;
                        continue block10;
                    }
                    ret.append(ch);
                    continue block10;
                }
                case 1: {
                    if (ch == '{') {
                        state = 2;
                        macroName = new StringBuilder();
                        continue block10;
                    }
                    if (ch == '@') {
                        state = 0;
                        ret.append('@');
                        continue block10;
                    }
                    state = 0;
                    ret.append('@');
                    ret.append(ch);
                    continue block10;
                }
                case 2: {
                    if (ch == '}') {
                        state = 0;
                        String name = macroName.toString().toLowerCase(Locale.ENGLISH);
                        String value = macroMapping.get(name);
                        if (value == null) {
                            ret.append("@{");
                            ret.append(name);
                            ret.append("}");
                        } else {
                            ret.append(value);
                        }
                        macroName = null;
                        continue block10;
                    }
                    macroName.append(ch);
                    continue block10;
                }
            }
        }
        switch (state) {
            case 0: {
                break;
            }
            case 1: {
                ret.append('@');
                break;
            }
            case 2: {
                ret.append("@{");
                ret.append(macroName.toString());
                break;
            }
        }
        return ret.toString();
    }

    public void addText(String text) {
        this.text = text;
    }

    private UnknownElement copy(UnknownElement ue, boolean nested) {
        UnknownElement ret = new UnknownElement(ue.getTag());
        ret.setNamespace(ue.getNamespace());
        ret.setProject(this.getProject());
        ret.setQName(ue.getQName());
        ret.setTaskType(ue.getTaskType());
        ret.setTaskName(ue.getTaskName());
        ret.setLocation(this.macroDef.getBackTrace() ? ue.getLocation() : this.getLocation());
        if (this.getOwningTarget() == null) {
            Target t = new Target();
            t.setProject(this.getProject());
            ret.setOwningTarget(t);
        } else {
            ret.setOwningTarget(this.getOwningTarget());
        }
        RuntimeConfigurable rc = new RuntimeConfigurable(ret, ue.getTaskName());
        rc.setPolyType(ue.getWrapper().getPolyType());
        Hashtable<String, Object> m = ue.getWrapper().getAttributeMap();
        for (Map.Entry entry : m.entrySet()) {
            rc.setAttribute((String)entry.getKey(), this.macroSubs((String)entry.getValue(), this.localAttributes));
        }
        rc.addText(this.macroSubs(ue.getWrapper().getText().toString(), this.localAttributes));
        for (RuntimeConfigurable r : Collections.list(ue.getWrapper().getChildren())) {
            List<UnknownElement> list;
            MacroDef.TemplateElement templateElement;
            UnknownElement unknownElement = (UnknownElement)r.getProxy();
            String tag = unknownElement.getTaskType();
            if (tag != null) {
                tag = tag.toLowerCase(Locale.ENGLISH);
            }
            if ((templateElement = this.getNsElements().get(tag)) == null || nested) {
                UnknownElement child = this.copy(unknownElement, nested);
                rc.addChild(child.getWrapper());
                ret.addChild(child);
                continue;
            }
            if (templateElement.isImplicit()) {
                if (this.unknownElements.isEmpty() && !templateElement.isOptional()) {
                    throw new BuildException("Missing nested elements for implicit element %s", templateElement.getName());
                }
                for (Task task : this.unknownElements) {
                    UnknownElement child = this.copy((UnknownElement)task, true);
                    rc.addChild(child.getWrapper());
                    ret.addChild(child);
                }
                continue;
            }
            UnknownElement presentElement = this.presentElements.get(tag);
            if (presentElement == null) {
                if (templateElement.isOptional()) continue;
                throw new BuildException("Required nested element %s missing", templateElement.getName());
            }
            String presentText = presentElement.getWrapper().getText().toString();
            if (!presentText.isEmpty()) {
                rc.addText(this.macroSubs(presentText, this.localAttributes));
            }
            if ((list = presentElement.getChildren()) == null) continue;
            for (UnknownElement unknownElement2 : list) {
                UnknownElement child = this.copy(unknownElement2, true);
                rc.addChild(child.getWrapper());
                ret.addChild(child);
            }
        }
        return ret;
    }

    @Override
    public void execute() {
        this.presentElements = new HashMap<String, UnknownElement>();
        this.getNsElements();
        this.processTasks();
        this.localAttributes = new Hashtable<String, String>();
        HashSet<String> copyKeys = new HashSet<String>(this.map.keySet());
        for (MacroDef.Attribute attribute : this.macroDef.getAttributes()) {
            String value = this.map.get(attribute.getName());
            if (value == null && "description".equals(attribute.getName())) {
                value = this.getDescription();
            }
            if (value == null) {
                value = attribute.getDefault();
                value = this.macroSubs(value, this.localAttributes);
            }
            if (value == null) {
                throw new BuildException("required attribute %s not set", attribute.getName());
            }
            this.localAttributes.put(attribute.getName(), value);
            copyKeys.remove(attribute.getName());
        }
        copyKeys.remove("id");
        if (this.macroDef.getText() != null) {
            if (this.text == null) {
                String defaultText = this.macroDef.getText().getDefault();
                if (!this.macroDef.getText().getOptional() && defaultText == null) {
                    throw new BuildException("required text missing");
                }
                String string = this.text = defaultText == null ? "" : defaultText;
            }
            if (this.macroDef.getText().getTrim()) {
                this.text = this.text.trim();
            }
            this.localAttributes.put(this.macroDef.getText().getName(), this.text);
        } else if (this.text != null && !this.text.trim().isEmpty()) {
            throw new BuildException("The \"%s\" macro does not support nested text data.", this.getTaskName());
        }
        if (!copyKeys.isEmpty()) {
            throw new BuildException("Unknown attribute" + (copyKeys.size() > 1 ? "s " : " ") + copyKeys);
        }
        UnknownElement c = this.copy(this.macroDef.getNestedTask(), false);
        c.init();
        LocalProperties localProperties = LocalProperties.get(this.getProject());
        localProperties.enterScope();
        try {
            c.perform();
        }
        catch (BuildException ex) {
            if (this.macroDef.getBackTrace()) {
                throw ProjectHelper.addLocationToBuildException(ex, this.getLocation());
            }
            ex.setLocation(this.getLocation());
            throw ex;
        }
        finally {
            this.presentElements = null;
            this.localAttributes = null;
            localProperties.exitScope();
        }
    }

    public static class Element
    implements TaskContainer {
        private List<Task> unknownElements = new ArrayList<Task>();

        @Override
        public void addTask(Task nestedTask) {
            this.unknownElements.add(nestedTask);
        }

        public List<Task> getUnknownElements() {
            return this.unknownElements;
        }
    }
}

