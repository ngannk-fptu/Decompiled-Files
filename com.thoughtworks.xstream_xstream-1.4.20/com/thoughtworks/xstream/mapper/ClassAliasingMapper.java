/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClassAliasingMapper
extends MapperWrapper {
    private final Map typeToName = new HashMap();
    private final Map classToName = new HashMap();
    private transient Map nameToType = new HashMap();

    public ClassAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addClassAlias(String name, Class type) {
        this.nameToType.put(name, type.getName());
        this.classToName.put(type.getName(), name);
    }

    public void addClassAttributeAlias(String name, Class type) {
        this.addClassAlias(name, type);
    }

    public void addTypeAlias(String name, Class type) {
        this.nameToType.put(name, type.getName());
        this.typeToName.put(type, name);
    }

    public String serializedClass(Class type) {
        String alias = (String)this.classToName.get(type.getName());
        if (alias != null) {
            return alias;
        }
        Iterator iter = this.typeToName.keySet().iterator();
        while (iter.hasNext()) {
            Class compatibleType = (Class)iter.next();
            if (!compatibleType.isAssignableFrom(type)) continue;
            return (String)this.typeToName.get(compatibleType);
        }
        return super.serializedClass(type);
    }

    public Class realClass(String elementName) {
        String mappedName = (String)this.nameToType.get(elementName);
        if (mappedName != null) {
            Class type = Primitives.primitiveType(mappedName);
            if (type != null) {
                return type;
            }
            elementName = mappedName;
        }
        return super.realClass(elementName);
    }

    public boolean itemTypeAsAttribute(Class clazz) {
        return this.classToName.containsKey(clazz.getName());
    }

    public boolean aliasIsAttribute(String name) {
        return this.nameToType.containsKey(name);
    }

    private Object readResolve() {
        Object type;
        this.nameToType = new HashMap();
        Iterator iter = this.classToName.keySet().iterator();
        while (iter.hasNext()) {
            type = iter.next();
            this.nameToType.put(this.classToName.get(type), type);
        }
        iter = this.typeToName.keySet().iterator();
        while (iter.hasNext()) {
            type = (Class)iter.next();
            this.nameToType.put(this.typeToName.get(type), ((Class)type).getName());
        }
        return this;
    }
}

