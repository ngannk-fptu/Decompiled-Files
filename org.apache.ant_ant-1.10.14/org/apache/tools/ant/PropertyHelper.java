/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.property.GetProperty;
import org.apache.tools.ant.property.NullReturn;
import org.apache.tools.ant.property.ParseProperties;
import org.apache.tools.ant.property.PropertyExpander;

public class PropertyHelper
implements GetProperty {
    private static final PropertyEvaluator TO_STRING = new PropertyEvaluator(){
        private final String PREFIX = "toString:";
        private final int PREFIX_LEN = "toString:".length();

        @Override
        public Object evaluate(String property, PropertyHelper propertyHelper) {
            Object o = null;
            if (property.startsWith("toString:") && propertyHelper.getProject() != null) {
                o = propertyHelper.getProject().getReference(property.substring(this.PREFIX_LEN));
            }
            return o == null ? null : o.toString();
        }
    };
    private static final PropertyExpander DEFAULT_EXPANDER = (s, pos, notUsed) -> {
        int index = pos.getIndex();
        if (s.length() - index >= 3 && '$' == s.charAt(index) && '{' == s.charAt(index + 1)) {
            int start = index + 2;
            int end = s.indexOf(125, start);
            if (end < 0) {
                throw new BuildException("Syntax error in property: " + s.substring(index));
            }
            pos.setIndex(end + 1);
            return start == end ? "" : s.substring(start, end);
        }
        return null;
    };
    private static final PropertyExpander SKIP_DOUBLE_DOLLAR = (s, pos, notUsed) -> {
        int index = pos.getIndex();
        if (s.length() - index >= 2 && '$' == s.charAt(index) && '$' == s.charAt(++index)) {
            pos.setIndex(index);
        }
        return null;
    };
    private static final PropertyEvaluator FROM_REF = new PropertyEvaluator(){
        private final String PREFIX = "ant.refid:";
        private final int PREFIX_LEN = "ant.refid:".length();

        @Override
        public Object evaluate(String prop, PropertyHelper helper) {
            return prop.startsWith("ant.refid:") && helper.getProject() != null ? helper.getProject().getReference(prop.substring(this.PREFIX_LEN)) : null;
        }
    };
    private Project project;
    private PropertyHelper next;
    private final Hashtable<Class<? extends Delegate>, List<Delegate>> delegates = new Hashtable();
    private final Hashtable<String, Object> properties = new Hashtable();
    private final Hashtable<String, Object> userProperties = new Hashtable();
    private final Hashtable<String, Object> inheritedProperties = new Hashtable();

    protected PropertyHelper() {
        this.add(FROM_REF);
        this.add(TO_STRING);
        this.add(SKIP_DOUBLE_DOLLAR);
        this.add(DEFAULT_EXPANDER);
    }

    public static Object getProperty(Project project, String name) {
        return PropertyHelper.getPropertyHelper(project).getProperty(name);
    }

    public static void setProperty(Project project, String name, Object value) {
        PropertyHelper.getPropertyHelper(project).setProperty(name, value, true);
    }

    public static void setNewProperty(Project project, String name, Object value) {
        PropertyHelper.getPropertyHelper(project).setNewProperty(name, value);
    }

    public void setProject(Project p) {
        this.project = p;
    }

    public Project getProject() {
        return this.project;
    }

    @Deprecated
    public void setNext(PropertyHelper next) {
        this.next = next;
    }

    @Deprecated
    public PropertyHelper getNext() {
        return this.next;
    }

    public static synchronized PropertyHelper getPropertyHelper(Project project) {
        PropertyHelper helper = null;
        if (project != null) {
            helper = (PropertyHelper)project.getReference("ant.PropertyHelper");
        }
        if (helper != null) {
            return helper;
        }
        helper = new PropertyHelper();
        helper.setProject(project);
        if (project != null) {
            project.addReference("ant.PropertyHelper", helper);
        }
        return helper;
    }

    public Collection<PropertyExpander> getExpanders() {
        return this.getDelegates(PropertyExpander.class);
    }

    @Deprecated
    public boolean setPropertyHook(String ns, String name, Object value, boolean inherited, boolean user, boolean isNew) {
        if (this.getNext() != null) {
            return this.getNext().setPropertyHook(ns, name, value, inherited, user, isNew);
        }
        return false;
    }

    @Deprecated
    public Object getPropertyHook(String ns, String name, boolean user) {
        Object o;
        if (this.getNext() != null && (o = this.getNext().getPropertyHook(ns, name, user)) != null) {
            return o;
        }
        if (this.project != null && name.startsWith("toString:")) {
            Object v = this.project.getReference(name = name.substring("toString:".length()));
            return v == null ? null : v.toString();
        }
        return null;
    }

    @Deprecated
    public void parsePropertyString(String value, Vector<String> fragments, Vector<String> propertyRefs) throws BuildException {
        PropertyHelper.parsePropertyStringDefault(value, fragments, propertyRefs);
    }

    public String replaceProperties(String ns, String value, Hashtable<String, Object> keys) throws BuildException {
        return this.replaceProperties(value);
    }

    public String replaceProperties(String value) throws BuildException {
        Object o = this.parseProperties(value);
        return o == null || o instanceof String ? (String)o : o.toString();
    }

    public Object parseProperties(String value) throws BuildException {
        return new ParseProperties(this.getProject(), this.getExpanders(), this).parseProperties(value);
    }

    public boolean containsProperties(String value) {
        return new ParseProperties(this.getProject(), this.getExpanders(), this).containsProperties(value);
    }

    @Deprecated
    public boolean setProperty(String ns, String name, Object value, boolean verbose) {
        return this.setProperty(name, value, verbose);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setProperty(String name, Object value, boolean verbose) {
        for (PropertySetter setter : this.getDelegates(PropertySetter.class)) {
            if (!setter.set(name, value, this)) continue;
            return true;
        }
        PropertyHelper propertyHelper = this;
        synchronized (propertyHelper) {
            if (this.userProperties.containsKey(name)) {
                if (this.project != null && verbose) {
                    this.project.log("Override ignored for user property \"" + name + "\"", 3);
                }
                return false;
            }
            if (this.project != null && verbose) {
                if (this.properties.containsKey(name)) {
                    this.project.log("Overriding previous definition of property \"" + name + "\"", 3);
                }
                this.project.log("Setting project property: " + name + " -> " + value, 4);
            }
            if (name != null && value != null) {
                this.properties.put(name, value);
            }
            return true;
        }
    }

    @Deprecated
    public void setNewProperty(String ns, String name, Object value) {
        this.setNewProperty(name, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setNewProperty(String name, Object value) {
        for (PropertySetter setter : this.getDelegates(PropertySetter.class)) {
            if (!setter.setNew(name, value, this)) continue;
            return;
        }
        PropertyHelper propertyHelper = this;
        synchronized (propertyHelper) {
            if (this.project != null && this.properties.containsKey(name)) {
                this.project.log("Override ignored for property \"" + name + "\"", 3);
                return;
            }
            if (this.project != null) {
                this.project.log("Setting project property: " + name + " -> " + value, 4);
            }
            if (name != null && value != null) {
                this.properties.put(name, value);
            }
        }
    }

    @Deprecated
    public void setUserProperty(String ns, String name, Object value) {
        this.setUserProperty(name, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setUserProperty(String name, Object value) {
        if (this.project != null) {
            this.project.log("Setting ro project property: " + name + " -> " + value, 4);
        }
        PropertyHelper propertyHelper = this;
        synchronized (propertyHelper) {
            this.userProperties.put(name, value);
            this.properties.put(name, value);
        }
    }

    @Deprecated
    public void setInheritedProperty(String ns, String name, Object value) {
        this.setInheritedProperty(name, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInheritedProperty(String name, Object value) {
        if (this.project != null) {
            this.project.log("Setting ro project property: " + name + " -> " + value, 4);
        }
        PropertyHelper propertyHelper = this;
        synchronized (propertyHelper) {
            this.inheritedProperties.put(name, value);
            this.userProperties.put(name, value);
            this.properties.put(name, value);
        }
    }

    @Deprecated
    public Object getProperty(String ns, String name) {
        return this.getProperty(name);
    }

    @Override
    public Object getProperty(String name) {
        if (name == null) {
            return null;
        }
        for (PropertyEvaluator evaluator : this.getDelegates(PropertyEvaluator.class)) {
            Object o = evaluator.evaluate(name, this);
            if (o == null) continue;
            return o instanceof NullReturn ? null : o;
        }
        return this.properties.get(name);
    }

    public Set<String> getPropertyNames() {
        HashSet<String> names = new HashSet<String>(this.getProperties().keySet());
        this.getDelegates(PropertyEnumerator.class).forEach(e -> names.addAll(e.getPropertyNames()));
        return Collections.unmodifiableSet(names);
    }

    @Deprecated
    public Object getUserProperty(String ns, String name) {
        return this.getUserProperty(name);
    }

    public Object getUserProperty(String name) {
        if (name == null) {
            return null;
        }
        return this.userProperties.get(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Hashtable<String, Object> getProperties() {
        Hashtable<String, Object> hashtable = this.properties;
        synchronized (hashtable) {
            return new Hashtable<String, Object>(this.properties);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Hashtable<String, Object> getUserProperties() {
        Hashtable<String, Object> hashtable = this.userProperties;
        synchronized (hashtable) {
            return new Hashtable<String, Object>(this.userProperties);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Hashtable<String, Object> getInheritedProperties() {
        Hashtable<String, Object> hashtable = this.inheritedProperties;
        synchronized (hashtable) {
            return new Hashtable<String, Object>(this.inheritedProperties);
        }
    }

    protected Hashtable<String, Object> getInternalProperties() {
        return this.properties;
    }

    protected Hashtable<String, Object> getInternalUserProperties() {
        return this.userProperties;
    }

    protected Hashtable<String, Object> getInternalInheritedProperties() {
        return this.inheritedProperties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void copyInheritedProperties(Project other) {
        Hashtable<String, Object> hashtable = this.inheritedProperties;
        synchronized (hashtable) {
            for (Map.Entry<String, Object> entry : this.inheritedProperties.entrySet()) {
                String arg = entry.getKey();
                if (other.getUserProperty(arg) != null) continue;
                other.setInheritedProperty(arg, entry.getValue().toString());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void copyUserProperties(Project other) {
        Hashtable<String, Object> hashtable = this.userProperties;
        synchronized (hashtable) {
            for (Map.Entry<String, Object> entry : this.userProperties.entrySet()) {
                String arg = entry.getKey();
                if (this.inheritedProperties.containsKey(arg)) continue;
                other.setUserProperty(arg, entry.getValue().toString());
            }
        }
    }

    static void parsePropertyStringDefault(String value, Vector<String> fragments, Vector<String> propertyRefs) throws BuildException {
        int pos;
        int prev = 0;
        while ((pos = value.indexOf(36, prev)) >= 0) {
            if (pos > 0) {
                fragments.addElement(value.substring(prev, pos));
            }
            if (pos == value.length() - 1) {
                fragments.addElement("$");
                prev = pos + 1;
                continue;
            }
            if (value.charAt(pos + 1) != '{') {
                if (value.charAt(pos + 1) == '$') {
                    fragments.addElement("$");
                } else {
                    fragments.addElement(value.substring(pos, pos + 2));
                }
                prev = pos + 2;
                continue;
            }
            int endName = value.indexOf(125, pos);
            if (endName < 0) {
                throw new BuildException("Syntax error in property: " + value);
            }
            String propertyName = value.substring(pos + 2, endName);
            fragments.addElement(null);
            propertyRefs.addElement(propertyName);
            prev = endName + 1;
        }
        if (prev < value.length()) {
            fragments.addElement(value.substring(prev));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(Delegate delegate) {
        Hashtable<Class<? extends Delegate>, List<Delegate>> hashtable = this.delegates;
        synchronized (hashtable) {
            for (Class<? extends Delegate> key : PropertyHelper.getDelegateInterfaces(delegate)) {
                List<Delegate> list = this.delegates.get(key);
                if (list == null) {
                    list = new ArrayList<Delegate>();
                } else {
                    list = new ArrayList<Delegate>(list);
                    list.remove(delegate);
                }
                list.add(0, delegate);
                this.delegates.put(key, Collections.unmodifiableList(list));
            }
        }
    }

    protected <D extends Delegate> List<D> getDelegates(Class<D> type) {
        List<Delegate> result = this.delegates.get(type);
        return result == null ? Collections.emptyList() : result;
    }

    protected static Set<Class<? extends Delegate>> getDelegateInterfaces(Delegate d) {
        HashSet<Class<? extends Delegate>> result = new HashSet<Class<? extends Delegate>>();
        for (Class<?> c = d.getClass(); c != null; c = c.getSuperclass()) {
            for (Class<?> ifc : c.getInterfaces()) {
                if (!Delegate.class.isAssignableFrom(ifc)) continue;
                result.add(ifc);
            }
        }
        result.remove(Delegate.class);
        return result;
    }

    public static Boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (value instanceof String) {
            String s = (String)value;
            if (Project.toBoolean(s)) {
                return Boolean.TRUE;
            }
            if ("off".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s) || "no".equalsIgnoreCase(s)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    private static boolean nullOrEmpty(Object value) {
        return value == null || "".equals(value);
    }

    private boolean evalAsBooleanOrPropertyName(Object value) {
        Boolean b = PropertyHelper.toBoolean(value);
        if (b != null) {
            return b;
        }
        return this.getProperty(String.valueOf(value)) != null;
    }

    public boolean testIfCondition(Object value) {
        return PropertyHelper.nullOrEmpty(value) || this.evalAsBooleanOrPropertyName(value);
    }

    public boolean testUnlessCondition(Object value) {
        return PropertyHelper.nullOrEmpty(value) || !this.evalAsBooleanOrPropertyName(value);
    }

    public static interface PropertyEvaluator
    extends Delegate {
        public Object evaluate(String var1, PropertyHelper var2);
    }

    public static interface Delegate {
    }

    public static interface PropertySetter
    extends Delegate {
        public boolean setNew(String var1, Object var2, PropertyHelper var3);

        public boolean set(String var1, Object var2, PropertyHelper var3);
    }

    public static interface PropertyEnumerator
    extends Delegate {
        public Set<String> getPropertyNames();
    }
}

