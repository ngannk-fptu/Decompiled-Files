/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component;

import aQute.bnd.component.AnnotationReader;
import aQute.bnd.component.ReferenceDef;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.version.Version;
import aQute.bnd.xmlattribute.ExtensionDef;
import aQute.bnd.xmlattribute.Namespaces;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.lib.collections.MultiMap;
import aQute.lib.tag.Tag;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.ServiceScope;

class ComponentDef
extends ExtensionDef {
    static final String NAMESPACE_STEM = "http://www.osgi.org/xmlns/scr";
    static final String MARKER = new String("|marker");
    final List<String> properties = new ArrayList<String>();
    final MultiMap<String, String> property = new MultiMap();
    final Map<String, String> propertyType = new HashMap<String, String>();
    final Map<String, ReferenceDef> references = new LinkedHashMap<String, ReferenceDef>();
    Version version;
    String name;
    String factory;
    Boolean immediate;
    ServiceScope scope;
    ConfigurationPolicy configurationPolicy;
    Descriptors.TypeRef implementation;
    Descriptors.TypeRef[] service;
    String activate;
    String deactivate;
    String modified;
    Boolean enabled;
    String xmlns;
    String[] configurationPid;
    List<Tag> propertyTags = new ArrayList<Tag>();

    public ComponentDef(XMLAttributeFinder finder, Version minVersion) {
        super(finder);
        this.version = minVersion;
    }

    String effectiveName() {
        if (this.name != null) {
            return this.name;
        }
        if (this.implementation != null) {
            return this.implementation.getFQN();
        }
        return "<name not yet determined>";
    }

    void prepare(Analyzer analyzer) throws Exception {
        this.prepareVersion(analyzer);
        if (this.implementation == null) {
            analyzer.error("No Implementation defined for component %s", this.name);
            return;
        }
        analyzer.referTo(this.implementation);
        if (this.name == null) {
            this.name = this.implementation.getFQN();
        }
        if (this.service != null && this.service.length > 0) {
            for (Descriptors.TypeRef interfaceName : this.service) {
                analyzer.referTo(interfaceName);
            }
        } else if (this.scope != null && this.scope != ServiceScope.BUNDLE) {
            analyzer.warning("The servicefactory:=true directive is set but no service is provided, ignoring it", new Object[0]);
        }
        for (Map.Entry kvs : this.property.entrySet()) {
            Tag property = new Tag("property", new Object[0]);
            String name = (String)kvs.getKey();
            String type = this.propertyType.get(name);
            property.addAttribute("name", name);
            if (type != null) {
                property.addAttribute("type", type);
            }
            if (((List)kvs.getValue()).size() == 1) {
                String value = (String)((List)kvs.getValue()).get(0);
                value = this.check(type, value, analyzer);
                property.addAttribute("value", value);
            } else {
                StringBuilder sb = new StringBuilder();
                String del = "";
                for (String v : (List)kvs.getValue()) {
                    if (v == MARKER) continue;
                    sb.append(del);
                    v = this.check(type, v, analyzer);
                    sb.append(v);
                    del = "\n";
                }
                property.addContent(sb.toString());
            }
            this.propertyTags.add(property);
        }
    }

    private void prepareVersion(Analyzer analyzer) throws Exception {
        for (ReferenceDef ref : this.references.values()) {
            ref.prepare(analyzer);
            this.updateVersion(ref.version);
        }
        if (this.configurationPolicy != null) {
            this.updateVersion(AnnotationReader.V1_1);
        }
        if (this.configurationPid != null) {
            this.updateVersion(AnnotationReader.V1_2);
        }
        if (this.modified != null) {
            this.updateVersion(AnnotationReader.V1_1);
        }
    }

    void sortReferences() {
        TreeMap<String, ReferenceDef> temp = new TreeMap<String, ReferenceDef>(this.references);
        this.references.clear();
        this.references.putAll(temp);
    }

    Tag getTag() {
        String xmlns = this.xmlns;
        if (xmlns == null && !this.version.equals(AnnotationReader.V1_0)) {
            xmlns = "http://www.osgi.org/xmlns/scr/v" + this.version;
        }
        Tag component = new Tag(xmlns == null ? "component" : "scr:component", new Object[0]);
        Namespaces namespaces = null;
        if (xmlns != null) {
            namespaces = new Namespaces();
            namespaces.registerNamespace("scr", xmlns);
            this.addNamespaces(namespaces, xmlns);
            for (ReferenceDef ref : this.references.values()) {
                ref.addNamespaces(namespaces, xmlns);
            }
            namespaces.addNamespaces(component);
        }
        component.addAttribute("name", this.name);
        if (this.configurationPolicy != null) {
            component.addAttribute("configuration-policy", this.configurationPolicy.toString().toLowerCase());
        }
        if (this.enabled != null) {
            component.addAttribute("enabled", this.enabled);
        }
        if (this.immediate != null) {
            component.addAttribute("immediate", this.immediate);
        }
        if (this.factory != null) {
            component.addAttribute("factory", this.factory);
        }
        if (this.activate != null && !this.version.equals(AnnotationReader.V1_0)) {
            component.addAttribute("activate", this.activate);
        }
        if (this.deactivate != null && !this.version.equals(AnnotationReader.V1_0)) {
            component.addAttribute("deactivate", this.deactivate);
        }
        if (this.modified != null) {
            component.addAttribute("modified", this.modified);
        }
        if (this.configurationPid != null) {
            StringBuilder b = new StringBuilder();
            String space = "";
            for (String pid : this.configurationPid) {
                if ("$".equals(pid)) {
                    pid = this.name;
                }
                b.append(space).append(pid);
                space = " ";
            }
            component.addAttribute("configuration-pid", b.toString());
        }
        this.addAttributes(component, namespaces);
        Tag impl = new Tag(component, "implementation", new Object[0]);
        impl.addAttribute("class", this.implementation.getFQN());
        if (this.service != null && this.service.length != 0) {
            Tag s = new Tag(component, "service", new Object[0]);
            if (this.scope != null) {
                if (AnnotationReader.V1_3.compareTo(this.version) > 0) {
                    if (this.scope == ServiceScope.PROTOTYPE) {
                        throw new IllegalStateException("verification failed, pre 1.3 component with scope PROTOTYPE");
                    }
                    s.addAttribute("servicefactory", this.scope == ServiceScope.BUNDLE);
                } else {
                    s.addAttribute("scope", this.scope.toString().toLowerCase());
                }
            }
            for (Descriptors.TypeRef ss : this.service) {
                Tag provide = new Tag(s, "provide", new Object[0]);
                provide.addAttribute("interface", ss.getFQN());
            }
        }
        for (ReferenceDef ref : this.references.values()) {
            Tag refTag = ref.getTag(namespaces);
            component.addContent(refTag);
        }
        for (Tag tag : this.propertyTags) {
            component.addContent(tag);
        }
        for (String entry : this.properties) {
            Tag properties = new Tag(component, "properties", new Object[0]);
            properties.addAttribute("entry", entry);
        }
        return component;
    }

    private String check(String type, String v, Analyzer analyzer) {
        if (type == null) {
            return v;
        }
        try {
            Class<Object> c;
            if (type.equals("Char")) {
                type = "Character";
            }
            if ((c = Class.forName("java.lang." + type)) == String.class) {
                return v;
            }
            v = v.trim();
            if (c == Character.class) {
                c = Integer.class;
            }
            Method m = c.getMethod("valueOf", String.class);
            m.invoke(null, v);
        }
        catch (ClassNotFoundException e) {
            analyzer.error("Invalid data type %s", type);
        }
        catch (NoSuchMethodException e) {
            analyzer.error("Cannot convert data %s to type %s", v, type);
        }
        catch (NumberFormatException e) {
            analyzer.error("Not a valid number %s for %s, %s", v, type, e.getMessage());
        }
        catch (Exception e) {
            analyzer.error("Cannot convert data %s to type %s", v, type);
        }
        return v;
    }

    void updateVersion(Version version) {
        this.version = ComponentDef.max(this.version, version);
    }

    static <T extends Comparable<T>> T max(T a, T b) {
        int n = a.compareTo(b);
        if (n >= 0) {
            return a;
        }
        return b;
    }
}

