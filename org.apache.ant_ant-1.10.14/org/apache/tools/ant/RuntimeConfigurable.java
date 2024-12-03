/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Evaluable;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.UnsupportedAttributeException;
import org.apache.tools.ant.attribute.EnableAttribute;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroInstance;
import org.xml.sax.AttributeList;
import org.xml.sax.helpers.AttributeListImpl;

public class RuntimeConfigurable
implements Serializable {
    private static final long serialVersionUID = 1L;
    private String elementTag = null;
    private List<RuntimeConfigurable> children = null;
    private transient Object wrappedObject = null;
    @Deprecated
    private transient AttributeList attributes;
    private transient boolean namespacedAttribute = false;
    private LinkedHashMap<String, Object> attributeMap = null;
    private StringBuffer characters = null;
    private boolean proxyConfigured = false;
    private String polyType = null;
    private String id = null;

    public RuntimeConfigurable(Object proxy, String elementTag) {
        this.setProxy(proxy);
        this.setElementTag(elementTag);
        if (proxy instanceof Task) {
            ((Task)proxy).setRuntimeConfigurableWrapper(this);
        }
    }

    public synchronized void setProxy(Object proxy) {
        this.wrappedObject = proxy;
        this.proxyConfigured = false;
    }

    private AttributeComponentInformation isRestrictedAttribute(String name, ComponentHelper componentHelper) {
        if (!name.contains(":")) {
            return new AttributeComponentInformation(null, false);
        }
        String componentName = this.attrToComponent(name);
        String ns = ProjectHelper.extractUriFromComponentName(componentName);
        if (componentHelper.getRestrictedDefinitions(ProjectHelper.nsToComponentName(ns)) == null) {
            return new AttributeComponentInformation(null, false);
        }
        return new AttributeComponentInformation(componentName, true);
    }

    public boolean isEnabled(UnknownElement owner) {
        if (!this.namespacedAttribute) {
            return true;
        }
        ComponentHelper componentHelper = ComponentHelper.getComponentHelper(owner.getProject());
        IntrospectionHelper ih = IntrospectionHelper.getHelper(owner.getProject(), EnableAttributeConsumer.class);
        for (Map.Entry<String, Object> entry : this.attributeMap.entrySet()) {
            AttributeComponentInformation attributeComponentInformation = this.isRestrictedAttribute(entry.getKey(), componentHelper);
            if (!attributeComponentInformation.isRestricted()) continue;
            String value = (String)entry.getValue();
            EnableAttribute enable = null;
            try {
                enable = (EnableAttribute)ih.createElement(owner.getProject(), new EnableAttributeConsumer(), attributeComponentInformation.getComponentName());
            }
            catch (BuildException ex) {
                throw new BuildException("Unsupported attribute " + attributeComponentInformation.getComponentName());
            }
            if (enable == null || enable.isEnabled(owner, value = owner.getProject().replaceProperties(value))) continue;
            return false;
        }
        return true;
    }

    private String attrToComponent(String a) {
        int p1 = a.lastIndexOf(58);
        int p2 = a.lastIndexOf(58, p1 - 1);
        return a.substring(0, p2) + a.substring(p1);
    }

    synchronized void setCreator(IntrospectionHelper.Creator creator) {
    }

    public synchronized Object getProxy() {
        return this.wrappedObject;
    }

    public synchronized String getId() {
        return this.id;
    }

    public synchronized String getPolyType() {
        return this.polyType;
    }

    public synchronized void setPolyType(String polyType) {
        this.polyType = polyType;
    }

    @Deprecated
    public synchronized void setAttributes(AttributeList attributes) {
        this.attributes = new AttributeListImpl(attributes);
        for (int i = 0; i < attributes.getLength(); ++i) {
            this.setAttribute(attributes.getName(i), attributes.getValue(i));
        }
    }

    public synchronized void setAttribute(String name, String value) {
        if (name.contains(":")) {
            this.namespacedAttribute = true;
        }
        this.setAttribute(name, (Object)value);
    }

    public synchronized void setAttribute(String name, Object value) {
        if (name.equalsIgnoreCase("ant-type")) {
            this.polyType = value == null ? null : value.toString();
        } else {
            if (this.attributeMap == null) {
                this.attributeMap = new LinkedHashMap();
            }
            if ("refid".equalsIgnoreCase(name) && !this.attributeMap.isEmpty()) {
                LinkedHashMap<String, Object> newAttributeMap = new LinkedHashMap<String, Object>();
                newAttributeMap.put(name, value);
                newAttributeMap.putAll(this.attributeMap);
                this.attributeMap = newAttributeMap;
            } else {
                this.attributeMap.put(name, value);
            }
            if ("id".equals(name)) {
                this.id = value == null ? null : value.toString();
            }
        }
    }

    public synchronized void removeAttribute(String name) {
        this.attributeMap.remove(name);
    }

    public synchronized Hashtable<String, Object> getAttributeMap() {
        return new Hashtable<String, Object>(this.attributeMap == null ? Collections.emptyMap() : this.attributeMap);
    }

    @Deprecated
    public synchronized AttributeList getAttributes() {
        return this.attributes;
    }

    public synchronized void addChild(RuntimeConfigurable child) {
        this.children = this.children == null ? new ArrayList() : this.children;
        this.children.add(child);
    }

    synchronized RuntimeConfigurable getChild(int index) {
        return this.children.get(index);
    }

    public synchronized Enumeration<RuntimeConfigurable> getChildren() {
        return this.children == null ? Collections.emptyEnumeration() : Collections.enumeration(this.children);
    }

    public synchronized void addText(String data) {
        if (data.isEmpty()) {
            return;
        }
        this.characters = this.characters == null ? new StringBuffer(data) : this.characters.append(data);
    }

    public synchronized void addText(char[] buf, int start, int count) {
        if (count == 0) {
            return;
        }
        this.characters = (this.characters == null ? new StringBuffer(count) : this.characters).append(buf, start, count);
    }

    public synchronized StringBuffer getText() {
        return this.characters == null ? new StringBuffer(0) : this.characters;
    }

    public synchronized void setElementTag(String elementTag) {
        this.elementTag = elementTag;
    }

    public synchronized String getElementTag() {
        return this.elementTag;
    }

    public void maybeConfigure(Project p) throws BuildException {
        this.maybeConfigure(p, true);
    }

    public synchronized void maybeConfigure(Project p, boolean configureChildren) throws BuildException {
        if (this.proxyConfigured) {
            return;
        }
        if (this.attributeMap != null) {
            Object target = this.wrappedObject instanceof TypeAdapter ? ((TypeAdapter)this.wrappedObject).getProxy() : this.wrappedObject;
            IntrospectionHelper ih = IntrospectionHelper.getHelper(p, target.getClass());
            ComponentHelper componentHelper = ComponentHelper.getComponentHelper(p);
            for (Map.Entry<String, Object> entry : this.attributeMap.entrySet()) {
                String name = entry.getKey();
                AttributeComponentInformation attributeComponentInformation = this.isRestrictedAttribute(name, componentHelper);
                if (attributeComponentInformation.isRestricted()) continue;
                Object value = entry.getValue();
                Object attrValue = value instanceof Evaluable ? ((Evaluable)value).eval() : PropertyHelper.getPropertyHelper(p).parseProperties(value.toString());
                if (target instanceof MacroInstance) {
                    for (MacroDef.Attribute attr : ((MacroInstance)target).getMacroDef().getAttributes()) {
                        if (!attr.getName().equals(name)) continue;
                        if (attr.isDoubleExpanding()) break;
                        attrValue = value;
                        break;
                    }
                }
                try {
                    ih.setAttribute(p, target, name, attrValue);
                }
                catch (UnsupportedAttributeException be) {
                    if ("id".equals(name)) continue;
                    if (this.getElementTag() == null) {
                        throw be;
                    }
                    throw new BuildException(this.getElementTag() + " doesn't support the \"" + be.getAttribute() + "\" attribute", be);
                }
                catch (BuildException be) {
                    if ("id".equals(name)) continue;
                    throw be;
                }
            }
        }
        if (this.characters != null) {
            ProjectHelper.addText(p, this.wrappedObject, this.characters.substring(0));
        }
        if (this.id != null) {
            p.addReference(this.id, this.wrappedObject);
        }
        this.proxyConfigured = true;
    }

    public void reconfigure(Project p) {
        this.proxyConfigured = false;
        this.maybeConfigure(p);
    }

    public void applyPreSet(RuntimeConfigurable r) {
        if (r.attributeMap != null) {
            for (String name : r.attributeMap.keySet()) {
                if (this.attributeMap != null && this.attributeMap.get(name) != null) continue;
                this.setAttribute(name, (String)r.attributeMap.get(name));
            }
        }
        String string = this.polyType = this.polyType == null ? r.polyType : this.polyType;
        if (r.children != null) {
            ArrayList<RuntimeConfigurable> newChildren = new ArrayList<RuntimeConfigurable>(r.children);
            if (this.children != null) {
                newChildren.addAll(this.children);
            }
            this.children = newChildren;
        }
        if (r.characters != null && (this.characters == null || this.characters.toString().trim().isEmpty())) {
            this.characters = new StringBuffer(r.characters.toString());
        }
    }

    private static class AttributeComponentInformation {
        String componentName;
        boolean restricted;

        private AttributeComponentInformation(String componentName, boolean restricted) {
            this.componentName = componentName;
            this.restricted = restricted;
        }

        public String getComponentName() {
            return this.componentName;
        }

        public boolean isRestricted() {
            return this.restricted;
        }
    }

    private static class EnableAttributeConsumer {
        private EnableAttributeConsumer() {
        }

        public void add(EnableAttribute b) {
        }
    }
}

