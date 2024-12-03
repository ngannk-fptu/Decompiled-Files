/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PackageAliasingMapper
extends MapperWrapper
implements Serializable {
    private static final Comparator REVERSE = new Comparator(){

        public int compare(Object o1, Object o2) {
            return ((String)o2).compareTo((String)o1);
        }
    };
    private Map packageToName = new TreeMap(REVERSE);
    protected transient Map nameToPackage = new HashMap();

    public PackageAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addPackageAlias(String name, String pkg) {
        if (name.length() > 0 && name.charAt(name.length() - 1) != '.') {
            name = name + '.';
        }
        if (pkg.length() > 0 && pkg.charAt(pkg.length() - 1) != '.') {
            pkg = pkg + '.';
        }
        this.nameToPackage.put(name, pkg);
        this.packageToName.put(pkg, name);
    }

    public String serializedClass(Class type) {
        String className = type.getName();
        int length = className.length();
        int dot = -1;
        do {
            String pkg;
            String alias;
            if ((alias = (String)this.packageToName.get(pkg = (dot = className.lastIndexOf(46, length)) < 0 ? "" : className.substring(0, dot + 1))) != null) {
                return alias + (dot < 0 ? className : className.substring(dot + 1));
            }
            length = dot - 1;
        } while (dot >= 0);
        return super.serializedClass(type);
    }

    public Class realClass(String elementName) {
        int length = elementName.length();
        int dot = -1;
        do {
            String name;
            String packageName;
            if ((packageName = (String)this.nameToPackage.get(name = (dot = elementName.lastIndexOf(46, length)) < 0 ? "" : elementName.substring(0, dot) + '.')) != null) {
                elementName = packageName + (dot < 0 ? elementName : elementName.substring(dot + 1));
                break;
            }
            length = dot - 1;
        } while (dot >= 0);
        return super.realClass(elementName);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(new HashMap(this.packageToName));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.packageToName = new TreeMap(REVERSE);
        this.packageToName.putAll((Map)in.readObject());
        this.nameToPackage = new HashMap();
        Iterator iter = this.packageToName.keySet().iterator();
        while (iter.hasNext()) {
            Object type = iter.next();
            this.nameToPackage.put(this.packageToName.get(type), type);
        }
    }
}

