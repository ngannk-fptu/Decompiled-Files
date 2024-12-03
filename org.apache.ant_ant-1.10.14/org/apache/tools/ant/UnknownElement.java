/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.UnsupportedElementException;
import org.apache.tools.ant.taskdefs.PreSetDef;

public class UnknownElement
extends Task {
    private final String elementName;
    private String namespace = "";
    private String qname;
    private Object realThing;
    private List<UnknownElement> children = null;
    private boolean presetDefed = false;

    public UnknownElement(String elementName) {
        this.elementName = elementName;
    }

    public List<UnknownElement> getChildren() {
        return this.children;
    }

    public String getTag() {
        return this.elementName;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        if (namespace.equals("ant:current")) {
            ComponentHelper helper = ComponentHelper.getComponentHelper(this.getProject());
            namespace = helper.getCurrentAntlibUri();
        }
        this.namespace = namespace == null ? "" : namespace;
    }

    public String getQName() {
        return this.qname;
    }

    public void setQName(String qname) {
        this.qname = qname;
    }

    @Override
    public RuntimeConfigurable getWrapper() {
        return super.getWrapper();
    }

    @Override
    public void maybeConfigure() throws BuildException {
        Object copy = this.realThing;
        if (copy != null) {
            return;
        }
        this.configure(this.makeObject(this, this.getWrapper()));
    }

    public void configure(Object realObject) {
        if (realObject == null) {
            return;
        }
        this.realThing = realObject;
        this.getWrapper().setProxy(realObject);
        Task task = null;
        if (realObject instanceof Task) {
            task = (Task)realObject;
            task.setRuntimeConfigurableWrapper(this.getWrapper());
            if (this.getWrapper().getId() != null) {
                this.getOwningTarget().replaceChild((Task)this, (Task)realObject);
            }
        }
        if (task != null) {
            task.maybeConfigure();
        } else {
            this.getWrapper().maybeConfigure(this.getProject());
        }
        this.handleChildren(realObject, this.getWrapper());
    }

    @Override
    protected void handleOutput(String output) {
        Object copy = this.realThing;
        if (copy instanceof Task) {
            ((Task)copy).handleOutput(output);
        } else {
            super.handleOutput(output);
        }
    }

    @Override
    protected int handleInput(byte[] buffer, int offset, int length) throws IOException {
        Object copy = this.realThing;
        if (copy instanceof Task) {
            return ((Task)copy).handleInput(buffer, offset, length);
        }
        return super.handleInput(buffer, offset, length);
    }

    @Override
    protected void handleFlush(String output) {
        Object copy = this.realThing;
        if (copy instanceof Task) {
            ((Task)copy).handleFlush(output);
        } else {
            super.handleFlush(output);
        }
    }

    @Override
    protected void handleErrorOutput(String output) {
        Object copy = this.realThing;
        if (copy instanceof Task) {
            ((Task)copy).handleErrorOutput(output);
        } else {
            super.handleErrorOutput(output);
        }
    }

    @Override
    protected void handleErrorFlush(String output) {
        Object copy = this.realThing;
        if (copy instanceof Task) {
            ((Task)copy).handleErrorFlush(output);
        } else {
            super.handleErrorFlush(output);
        }
    }

    @Override
    public void execute() {
        Object copy = this.realThing;
        if (copy == null) {
            return;
        }
        try {
            if (copy instanceof Task) {
                ((Task)copy).execute();
            }
        }
        finally {
            if (this.getWrapper().getId() == null) {
                this.realThing = null;
                this.getWrapper().setProxy(null);
            }
        }
    }

    public void addChild(UnknownElement child) {
        if (this.children == null) {
            this.children = new ArrayList<UnknownElement>();
        }
        this.children.add(child);
    }

    protected void handleChildren(Object parent, RuntimeConfigurable parentWrapper) throws BuildException {
        if (this.children == null || this.children.isEmpty()) {
            return;
        }
        if (parent instanceof TypeAdapter) {
            parent = ((TypeAdapter)parent).getProxy();
        }
        String parentUri = this.getNamespace();
        Class<?> parentClass = parent.getClass();
        IntrospectionHelper ih = IntrospectionHelper.getHelper(this.getProject(), parentClass);
        Iterator<UnknownElement> it = this.children.iterator();
        int i = 0;
        while (it.hasNext()) {
            RuntimeConfigurable childWrapper = parentWrapper.getChild(i);
            UnknownElement child = it.next();
            try {
                if (!(!childWrapper.isEnabled(child) && ih.supportsNestedElement(parentUri, ProjectHelper.genComponentName(child.getNamespace(), child.getTag())) || this.handleChild(parentUri, ih, parent, child, childWrapper))) {
                    if (!(parent instanceof TaskContainer)) {
                        ih.throwNotSupported(this.getProject(), parent, child.getTag());
                    } else {
                        TaskContainer container = (TaskContainer)parent;
                        container.addTask(child);
                    }
                }
            }
            catch (UnsupportedElementException ex) {
                throw new BuildException(parentWrapper.getElementTag() + " doesn't support the nested \"" + ex.getElement() + "\" element.", ex);
            }
            ++i;
        }
    }

    protected String getComponentName() {
        return ProjectHelper.genComponentName(this.getNamespace(), this.getTag());
    }

    public void applyPreSet(UnknownElement u) {
        if (this.presetDefed) {
            return;
        }
        this.getWrapper().applyPreSet(u.getWrapper());
        if (u.children != null) {
            ArrayList<UnknownElement> newChildren = new ArrayList<UnknownElement>(u.children);
            if (this.children != null) {
                newChildren.addAll(this.children);
            }
            this.children = newChildren;
        }
        this.presetDefed = true;
    }

    protected Object makeObject(UnknownElement ue, RuntimeConfigurable w) {
        if (!w.isEnabled(ue)) {
            return null;
        }
        ComponentHelper helper = ComponentHelper.getComponentHelper(this.getProject());
        String name = ue.getComponentName();
        Object o = helper.createComponent(ue, ue.getNamespace(), name);
        if (o == null) {
            throw this.getNotFoundException("task or type", name);
        }
        if (o instanceof PreSetDef.PreSetDefinition) {
            PreSetDef.PreSetDefinition def = (PreSetDef.PreSetDefinition)o;
            if ((o = def.createObject(ue.getProject())) == null) {
                throw this.getNotFoundException("preset " + name, def.getPreSets().getComponentName());
            }
            ue.applyPreSet(def.getPreSets());
            if (o instanceof Task) {
                Task task = (Task)o;
                task.setTaskType(ue.getTaskType());
                task.setTaskName(ue.getTaskName());
                task.init();
            }
        }
        if (o instanceof UnknownElement) {
            o = ((UnknownElement)o).makeObject((UnknownElement)o, w);
        }
        if (o instanceof Task) {
            ((Task)o).setOwningTarget(this.getOwningTarget());
        }
        if (o instanceof ProjectComponent) {
            ((ProjectComponent)o).setLocation(this.getLocation());
        }
        return o;
    }

    protected Task makeTask(UnknownElement ue, RuntimeConfigurable w) {
        Task task = this.getProject().createTask(ue.getTag());
        if (task != null) {
            task.setLocation(this.getLocation());
            task.setOwningTarget(this.getOwningTarget());
            task.init();
        }
        return task;
    }

    protected BuildException getNotFoundException(String what, String name) {
        ComponentHelper helper = ComponentHelper.getComponentHelper(this.getProject());
        String msg = helper.diagnoseCreationFailure(name, what);
        return new BuildException(msg, this.getLocation());
    }

    @Override
    public String getTaskName() {
        Object copy = this.realThing;
        return !(copy instanceof Task) ? super.getTaskName() : ((Task)copy).getTaskName();
    }

    public Task getTask() {
        Object copy = this.realThing;
        if (copy instanceof Task) {
            return (Task)copy;
        }
        return null;
    }

    public Object getRealThing() {
        return this.realThing;
    }

    public void setRealThing(Object realThing) {
        this.realThing = realThing;
    }

    private boolean handleChild(String parentUri, IntrospectionHelper ih, Object parent, UnknownElement child, RuntimeConfigurable childWrapper) {
        String childName = ProjectHelper.genComponentName(child.getNamespace(), child.getTag());
        if (ih.supportsNestedElement(parentUri, childName, this.getProject(), parent)) {
            IntrospectionHelper.Creator creator = null;
            try {
                creator = ih.getElementCreator(this.getProject(), parentUri, parent, childName, child);
            }
            catch (UnsupportedElementException use) {
                if (!ih.isDynamic()) {
                    throw use;
                }
                return false;
            }
            creator.setPolyType(childWrapper.getPolyType());
            Object realChild = creator.create();
            if (realChild instanceof PreSetDef.PreSetDefinition) {
                PreSetDef.PreSetDefinition def = (PreSetDef.PreSetDefinition)realChild;
                realChild = creator.getRealObject();
                child.applyPreSet(def.getPreSets());
            }
            childWrapper.setCreator(creator);
            childWrapper.setProxy(realChild);
            if (realChild instanceof Task) {
                Task childTask = (Task)realChild;
                childTask.setRuntimeConfigurableWrapper(childWrapper);
                childTask.setTaskName(childName);
                childTask.setTaskType(childName);
            }
            if (realChild instanceof ProjectComponent) {
                ((ProjectComponent)realChild).setLocation(child.getLocation());
            }
            childWrapper.maybeConfigure(this.getProject());
            child.handleChildren(realChild, childWrapper);
            creator.store();
            return true;
        }
        return false;
    }

    public boolean similar(Object obj) {
        int childrenSize;
        if (obj == null) {
            return false;
        }
        if (!this.getClass().getName().equals(obj.getClass().getName())) {
            return false;
        }
        UnknownElement other = (UnknownElement)obj;
        if (!Objects.equals(this.elementName, other.elementName)) {
            return false;
        }
        if (!this.namespace.equals(other.namespace)) {
            return false;
        }
        if (!this.qname.equals(other.qname)) {
            return false;
        }
        if (!this.getWrapper().getAttributeMap().equals(other.getWrapper().getAttributeMap())) {
            return false;
        }
        if (!this.getWrapper().getText().toString().equals(other.getWrapper().getText().toString())) {
            return false;
        }
        int n = childrenSize = this.children == null ? 0 : this.children.size();
        if (childrenSize == 0) {
            return other.children == null || other.children.isEmpty();
        }
        if (other.children == null) {
            return false;
        }
        if (childrenSize != other.children.size()) {
            return false;
        }
        for (int i = 0; i < childrenSize; ++i) {
            UnknownElement child = this.children.get(i);
            if (child.similar(other.children.get(i))) continue;
            return false;
        }
        return true;
    }

    public UnknownElement copy(Project newProject) {
        UnknownElement ret = new UnknownElement(this.getTag());
        ret.setNamespace(this.getNamespace());
        ret.setProject(newProject);
        ret.setQName(this.getQName());
        ret.setTaskType(this.getTaskType());
        ret.setTaskName(this.getTaskName());
        ret.setLocation(this.getLocation());
        if (this.getOwningTarget() == null) {
            Target t = new Target();
            t.setProject(this.getProject());
            ret.setOwningTarget(t);
        } else {
            ret.setOwningTarget(this.getOwningTarget());
        }
        RuntimeConfigurable copyRC = new RuntimeConfigurable(ret, this.getTaskName());
        copyRC.setPolyType(this.getWrapper().getPolyType());
        Hashtable<String, Object> m = this.getWrapper().getAttributeMap();
        for (Map.Entry entry : m.entrySet()) {
            copyRC.setAttribute((String)entry.getKey(), (String)entry.getValue());
        }
        copyRC.addText(this.getWrapper().getText().toString());
        for (RuntimeConfigurable r : Collections.list(this.getWrapper().getChildren())) {
            UnknownElement ueChild = (UnknownElement)r.getProxy();
            UnknownElement copyChild = ueChild.copy(newProject);
            copyRC.addChild(copyChild.getWrapper());
            ret.addChild(copyChild);
        }
        return ret;
    }
}

