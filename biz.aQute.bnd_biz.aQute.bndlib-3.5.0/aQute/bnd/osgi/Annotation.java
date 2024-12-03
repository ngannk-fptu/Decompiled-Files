/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Annotation {
    private Descriptors.TypeRef name;
    private Map<String, Object> elements;
    private ElementType member;
    private RetentionPolicy policy;

    public Annotation(Descriptors.TypeRef name, Map<String, Object> elements, ElementType member, RetentionPolicy policy) {
        this.name = name;
        this.elements = elements == null ? null : elements;
        this.member = member;
        this.policy = policy;
    }

    public Descriptors.TypeRef getName() {
        return this.name;
    }

    public ElementType getElementType() {
        return this.member;
    }

    public RetentionPolicy getRetentionPolicy() {
        return this.policy;
    }

    public String toString() {
        return this.name + ":" + (Object)((Object)this.member) + ":" + (Object)((Object)this.policy) + ":" + (this.elements == null ? "{}" : this.elements);
    }

    public <T> T get(String string) {
        if (this.elements == null) {
            return null;
        }
        return (T)this.elements.get(string);
    }

    public <T> void put(String string, Object v) {
        if (this.elements == null) {
            this.elements = new LinkedHashMap<String, Object>();
        }
        this.elements.put(string, v);
    }

    public Set<String> keySet() {
        if (this.elements == null) {
            return Collections.emptySet();
        }
        return this.elements.keySet();
    }

    public <T extends java.lang.annotation.Annotation> T getAnnotation() throws Exception {
        return this.getAnnotation(this.getClass().getClassLoader());
    }

    public <T extends java.lang.annotation.Annotation> T getAnnotation(ClassLoader cl) throws Exception {
        String cname = this.name.getFQN();
        try {
            Class<?> c = cl.loadClass(cname);
            return (T)this.getAnnotation(c);
        }
        catch (ClassNotFoundException e) {
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        return null;
    }

    public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> c) throws Exception {
        String cname = this.name.getFQN();
        if (!c.getName().equals(cname)) {
            return null;
        }
        return (T)((java.lang.annotation.Annotation)Configurable.createConfigurable(c, this.elements == null ? (this.elements = new LinkedHashMap<String, Object>()) : this.elements));
    }

    public void merge(Annotation annotation) {
        if (annotation.elements == null) {
            return;
        }
        for (Map.Entry<String, Object> e : annotation.elements.entrySet()) {
            if (this.elements.containsKey(e.getKey())) continue;
            this.elements.put(e.getKey(), e.getValue());
        }
    }

    public void addDefaults(Clazz c) throws Exception {
        Map<String, Object> defaults = c.getDefaults();
        if (defaults == null || defaults.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> e : defaults.entrySet()) {
            if (this.elements != null && this.elements.containsKey(e.getKey())) continue;
            this.put(e.getKey(), e.getValue());
        }
    }
}

