/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.POJOPropertyBuilder;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.util.BeanUtil;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class POJOPropertiesCollector {
    protected final MapperConfig<?> _config;
    protected final boolean _forSerialization;
    protected final JavaType _type;
    protected final AnnotatedClass _classDef;
    protected final VisibilityChecker<?> _visibilityChecker;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final LinkedHashMap<String, POJOPropertyBuilder> _properties = new LinkedHashMap();
    protected LinkedList<POJOPropertyBuilder> _creatorProperties = null;
    protected LinkedList<AnnotatedMethod> _anyGetters = null;
    protected LinkedList<AnnotatedMethod> _anySetters = null;
    protected LinkedList<AnnotatedMethod> _jsonValueGetters = null;
    protected Set<String> _ignoredPropertyNames;
    protected Set<String> _ignoredPropertyNamesForDeser;
    protected LinkedHashMap<Object, AnnotatedMember> _injectables;

    protected POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef) {
        this._config = config;
        this._forSerialization = forSerialization;
        this._type = type;
        this._classDef = classDef;
        this._annotationIntrospector = config.isAnnotationProcessingEnabled() ? this._config.getAnnotationIntrospector() : null;
        this._visibilityChecker = this._annotationIntrospector == null ? this._config.getDefaultVisibilityChecker() : this._annotationIntrospector.findAutoDetectVisibility(classDef, this._config.getDefaultVisibilityChecker());
    }

    public MapperConfig<?> getConfig() {
        return this._config;
    }

    public JavaType getType() {
        return this._type;
    }

    public AnnotatedClass getClassDef() {
        return this._classDef;
    }

    public AnnotationIntrospector getAnnotationIntrospector() {
        return this._annotationIntrospector;
    }

    public List<BeanPropertyDefinition> getProperties() {
        return new ArrayList<BeanPropertyDefinition>(this._properties.values());
    }

    public Map<Object, AnnotatedMember> getInjectables() {
        return this._injectables;
    }

    public AnnotatedMethod getJsonValueMethod() {
        if (this._jsonValueGetters != null) {
            if (this._jsonValueGetters.size() > 1) {
                this.reportProblem("Multiple value properties defined (" + this._jsonValueGetters.get(0) + " vs " + this._jsonValueGetters.get(1) + ")");
            }
            return this._jsonValueGetters.get(0);
        }
        return null;
    }

    public AnnotatedMethod getAnyGetterMethod() {
        if (this._anyGetters != null) {
            if (this._anyGetters.size() > 1) {
                this.reportProblem("Multiple 'any-getters' defined (" + this._anyGetters.get(0) + " vs " + this._anyGetters.get(1) + ")");
            }
            return this._anyGetters.getFirst();
        }
        return null;
    }

    public AnnotatedMethod getAnySetterMethod() {
        if (this._anySetters != null) {
            if (this._anySetters.size() > 1) {
                this.reportProblem("Multiple 'any-setters' defined (" + this._anySetters.get(0) + " vs " + this._anySetters.get(1) + ")");
            }
            return this._anySetters.getFirst();
        }
        return null;
    }

    public Set<String> getIgnoredPropertyNames() {
        return this._ignoredPropertyNames;
    }

    public Set<String> getIgnoredPropertyNamesForDeser() {
        return this._ignoredPropertyNamesForDeser;
    }

    protected Map<String, POJOPropertyBuilder> getPropertyMap() {
        return this._properties;
    }

    public POJOPropertiesCollector collect() {
        this._properties.clear();
        this._addFields();
        this._addMethods();
        this._addCreators();
        this._addInjectables();
        this._removeUnwantedProperties();
        this._renameProperties();
        PropertyNamingStrategy naming = this._config.getPropertyNamingStrategy();
        if (naming != null) {
            this._renameUsing(naming);
        }
        for (POJOPropertyBuilder property : this._properties.values()) {
            property.trimByVisibility();
        }
        for (POJOPropertyBuilder property : this._properties.values()) {
            property.mergeAnnotations(this._forSerialization);
        }
        this._sortProperties();
        return this;
    }

    /*
     * WARNING - void declaration
     */
    protected void _sortProperties() {
        AnnotationIntrospector intr = this._config.getAnnotationIntrospector();
        Boolean alpha = intr.findSerializationSortAlphabetically(this._classDef);
        boolean sort = alpha == null ? this._config.shouldSortPropertiesAlphabetically() : alpha.booleanValue();
        String[] propertyOrder = intr.findSerializationPropertyOrder(this._classDef);
        if (!sort && this._creatorProperties == null && propertyOrder == null) {
            return;
        }
        int size = this._properties.size();
        AbstractMap all = sort ? new TreeMap() : new LinkedHashMap(size + size);
        for (POJOPropertyBuilder prop : this._properties.values()) {
            all.put(prop.getName(), prop);
        }
        LinkedHashMap<Object, POJOPropertyBuilder> ordered = new LinkedHashMap<Object, POJOPropertyBuilder>(size + size);
        if (propertyOrder != null) {
            for (String string : propertyOrder) {
                void var11_12;
                POJOPropertyBuilder w = (POJOPropertyBuilder)all.get(string);
                if (w == null) {
                    for (POJOPropertyBuilder prop : this._properties.values()) {
                        if (!string.equals(prop.getInternalName())) continue;
                        w = prop;
                        String string2 = prop.getName();
                        break;
                    }
                }
                if (w == null) continue;
                ordered.put(var11_12, w);
            }
        }
        if (this._creatorProperties != null) {
            for (POJOPropertyBuilder prop : this._creatorProperties) {
                ordered.put(prop.getName(), prop);
            }
        }
        ordered.putAll(all);
        this._properties.clear();
        this._properties.putAll(ordered);
    }

    protected void _addFields() {
        AnnotationIntrospector ai = this._annotationIntrospector;
        for (AnnotatedField f : this._classDef.fields()) {
            boolean visible;
            String implName = f.getName();
            String explName = ai == null ? null : (this._forSerialization ? ai.findSerializablePropertyName(f) : ai.findDeserializablePropertyName(f));
            if ("".equals(explName)) {
                explName = implName;
            }
            boolean bl = visible = explName != null;
            if (!visible) {
                visible = this._visibilityChecker.isFieldVisible(f);
            }
            boolean ignored = ai != null && ai.hasIgnoreMarker(f);
            this._property(implName).addField(f, explName, visible, ignored);
        }
    }

    protected void _addCreators() {
        POJOPropertyBuilder prop;
        String name;
        AnnotatedParameter param;
        int i;
        int len;
        AnnotationIntrospector ai = this._annotationIntrospector;
        if (ai == null) {
            return;
        }
        for (AnnotatedConstructor ctor : this._classDef.getConstructors()) {
            if (this._creatorProperties == null) {
                this._creatorProperties = new LinkedList();
            }
            len = ctor.getParameterCount();
            for (i = 0; i < len; ++i) {
                param = ctor.getParameter(i);
                name = ai.findPropertyNameForParam(param);
                if (name == null) continue;
                prop = this._property(name);
                prop.addCtor(param, name, true, false);
                this._creatorProperties.add(prop);
            }
        }
        for (AnnotatedMethod factory : this._classDef.getStaticMethods()) {
            if (this._creatorProperties == null) {
                this._creatorProperties = new LinkedList();
            }
            len = factory.getParameterCount();
            for (i = 0; i < len; ++i) {
                param = factory.getParameter(i);
                name = ai.findPropertyNameForParam(param);
                if (name == null) continue;
                prop = this._property(name);
                prop.addCtor(param, name, true, false);
                this._creatorProperties.add(prop);
            }
        }
    }

    protected void _addMethods() {
        AnnotationIntrospector ai = this._annotationIntrospector;
        for (AnnotatedMethod m : this._classDef.memberMethods()) {
            boolean ignore;
            boolean visible;
            String implName;
            String explName;
            int argCount = m.getParameterCount();
            if (argCount == 0) {
                if (ai != null) {
                    if (ai.hasAnyGetterAnnotation(m)) {
                        if (this._anyGetters == null) {
                            this._anyGetters = new LinkedList();
                        }
                        this._anyGetters.add(m);
                        continue;
                    }
                    if (ai.hasAsValueAnnotation(m)) {
                        if (this._jsonValueGetters == null) {
                            this._jsonValueGetters = new LinkedList();
                        }
                        this._jsonValueGetters.add(m);
                        continue;
                    }
                }
                String string = explName = ai == null ? null : ai.findGettablePropertyName(m);
                if (explName == null) {
                    implName = BeanUtil.okNameForRegularGetter(m, m.getName());
                    if (implName == null) {
                        implName = BeanUtil.okNameForIsGetter(m, m.getName());
                        if (implName == null) continue;
                        visible = this._visibilityChecker.isIsGetterVisible(m);
                    } else {
                        visible = this._visibilityChecker.isGetterVisible(m);
                    }
                } else {
                    implName = BeanUtil.okNameForGetter(m);
                    if (implName == null) {
                        implName = m.getName();
                    }
                    if (explName.length() == 0) {
                        explName = implName;
                    }
                    visible = true;
                }
                ignore = ai == null ? false : ai.hasIgnoreMarker(m);
                this._property(implName).addGetter(m, explName, visible, ignore);
                continue;
            }
            if (argCount == 1) {
                String string = explName = ai == null ? null : ai.findSettablePropertyName(m);
                if (explName == null) {
                    implName = BeanUtil.okNameForSetter(m);
                    if (implName == null) continue;
                    visible = this._visibilityChecker.isSetterVisible(m);
                } else {
                    implName = BeanUtil.okNameForSetter(m);
                    if (implName == null) {
                        implName = m.getName();
                    }
                    if (explName.length() == 0) {
                        explName = implName;
                    }
                    visible = true;
                }
                ignore = ai == null ? false : ai.hasIgnoreMarker(m);
                this._property(implName).addSetter(m, explName, visible, ignore);
                continue;
            }
            if (argCount != 2 || ai == null || !ai.hasAnySetterAnnotation(m)) continue;
            if (this._anySetters == null) {
                this._anySetters = new LinkedList();
            }
            this._anySetters.add(m);
        }
    }

    protected void _addInjectables() {
        AnnotationIntrospector ai = this._annotationIntrospector;
        if (ai == null) {
            return;
        }
        for (AnnotatedField f : this._classDef.fields()) {
            this._doAddInjectable(ai.findInjectableValueId(f), f);
        }
        for (AnnotatedMethod m : this._classDef.memberMethods()) {
            if (m.getParameterCount() != 1) continue;
            this._doAddInjectable(ai.findInjectableValueId(m), m);
        }
    }

    protected void _doAddInjectable(Object id, AnnotatedMember m) {
        AnnotatedMember prev;
        if (id == null) {
            return;
        }
        if (this._injectables == null) {
            this._injectables = new LinkedHashMap();
        }
        if ((prev = this._injectables.put(id, m)) != null) {
            String type = id == null ? "[null]" : id.getClass().getName();
            throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
        }
    }

    protected void _removeUnwantedProperties() {
        Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            POJOPropertyBuilder prop = entry.getValue();
            if (!prop.anyVisible()) {
                it.remove();
                continue;
            }
            if (prop.anyIgnorals()) {
                this._addIgnored(prop);
                if (!prop.anyExplicitNames()) {
                    it.remove();
                    continue;
                }
                prop.removeIgnored();
            }
            prop.removeNonVisible();
        }
    }

    private void _addIgnored(POJOPropertyBuilder prop) {
        if (this._forSerialization) {
            return;
        }
        String name = prop.getName();
        this._ignoredPropertyNames = this.addToSet(this._ignoredPropertyNames, name);
        if (prop.anyDeserializeIgnorals()) {
            this._ignoredPropertyNamesForDeser = this.addToSet(this._ignoredPropertyNamesForDeser, name);
        }
    }

    protected void _renameProperties() {
        Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
        LinkedList<POJOPropertyBuilder> renamed = null;
        while (it.hasNext()) {
            Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            POJOPropertyBuilder prop = entry.getValue();
            String newName = prop.findNewName();
            if (newName == null) continue;
            if (renamed == null) {
                renamed = new LinkedList<POJOPropertyBuilder>();
            }
            prop = prop.withName(newName);
            renamed.add(prop);
            it.remove();
        }
        if (renamed != null) {
            for (POJOPropertyBuilder prop : renamed) {
                String name = prop.getName();
                POJOPropertyBuilder old = this._properties.get(name);
                if (old == null) {
                    this._properties.put(name, prop);
                    continue;
                }
                old.addAll(prop);
            }
        }
    }

    protected void _renameUsing(PropertyNamingStrategy naming) {
        POJOPropertyBuilder[] props = this._properties.values().toArray(new POJOPropertyBuilder[this._properties.size()]);
        this._properties.clear();
        for (POJOPropertyBuilder prop : props) {
            POJOPropertyBuilder old;
            String name = prop.getName();
            if (this._forSerialization) {
                if (prop.hasGetter()) {
                    name = naming.nameForGetterMethod(this._config, prop.getGetter(), name);
                } else if (prop.hasField()) {
                    name = naming.nameForField(this._config, prop.getField(), name);
                }
            } else if (prop.hasSetter()) {
                name = naming.nameForSetterMethod(this._config, prop.getSetter(), name);
            } else if (prop.hasConstructorParameter()) {
                name = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), name);
            } else if (prop.hasField()) {
                name = naming.nameForField(this._config, prop.getField(), name);
            } else if (prop.hasGetter()) {
                name = naming.nameForGetterMethod(this._config, prop.getGetter(), name);
            }
            if (!name.equals(prop.getName())) {
                prop = prop.withName(name);
            }
            if ((old = this._properties.get(name)) == null) {
                this._properties.put(name, prop);
                continue;
            }
            old.addAll(prop);
        }
    }

    protected void reportProblem(String msg) {
        throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
    }

    protected POJOPropertyBuilder _property(String implName) {
        POJOPropertyBuilder prop = this._properties.get(implName);
        if (prop == null) {
            prop = new POJOPropertyBuilder(implName);
            this._properties.put(implName, prop);
        }
        return prop;
    }

    private Set<String> addToSet(Set<String> set, String str) {
        if (set == null) {
            set = new HashSet<String>();
        }
        set.add(str);
        return set;
    }
}

