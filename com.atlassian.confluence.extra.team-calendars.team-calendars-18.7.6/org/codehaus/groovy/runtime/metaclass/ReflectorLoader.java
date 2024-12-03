/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.runtime.Reflector;

public class ReflectorLoader
extends ClassLoader {
    private boolean inDefine = false;
    private final Map loadedClasses = new HashMap();
    private final ClassLoader delegatationLoader = this.getClass().getClassLoader();
    private static final String REFLECTOR = Reflector.class.getName();

    protected Class findClass(String name) throws ClassNotFoundException {
        if (this.delegatationLoader == null) {
            return super.findClass(name);
        }
        return this.delegatationLoader.loadClass(name);
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (this.inDefine && name.equals(REFLECTOR)) {
            return Reflector.class;
        }
        return super.loadClass(name, resolve);
    }

    public synchronized Class defineClass(String name, byte[] bytecode, ProtectionDomain domain) {
        this.inDefine = true;
        Class<?> c = this.defineClass(name, bytecode, 0, bytecode.length, domain);
        this.loadedClasses.put(name, c);
        this.resolveClass(c);
        this.inDefine = false;
        return c;
    }

    public ReflectorLoader(ClassLoader parent) {
        super(parent);
    }

    public synchronized Class getLoadedClass(String name) {
        return (Class)this.loadedClasses.get(name);
    }

    static String getReflectorName(Class theClass) {
        String className = theClass.getName();
        if (className.startsWith("java.")) {
            String packagePrefix = "gjdk.";
            String name = packagePrefix + className + "_GroovyReflector";
            if (theClass.isArray()) {
                Class<?> clazz = theClass;
                name = packagePrefix;
                int level = 0;
                while (clazz.isArray()) {
                    clazz = clazz.getComponentType();
                    ++level;
                }
                String componentName = clazz.getName();
                name = packagePrefix + componentName + "_GroovyReflectorArray";
                if (level > 1) {
                    name = name + level;
                }
            }
            return name;
        }
        String name = className.replace('$', '_') + "_GroovyReflector";
        if (theClass.isArray()) {
            Class<?> clazz = theClass;
            int level = 0;
            while (clazz.isArray()) {
                clazz = clazz.getComponentType();
                ++level;
            }
            String componentName = clazz.getName();
            name = componentName.replace('$', '_') + "_GroovyReflectorArray";
            if (level > 1) {
                name = name + level;
            }
        }
        return name;
    }
}

