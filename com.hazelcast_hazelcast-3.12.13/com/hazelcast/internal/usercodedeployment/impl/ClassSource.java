/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl;

import com.hazelcast.internal.usercodedeployment.impl.ClassData;
import com.hazelcast.internal.usercodedeployment.impl.ClassLocator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClassSource
extends ClassLoader {
    private final Map<String, Class> classes = new ConcurrentHashMap<String, Class>();
    private final Map<String, byte[]> classDefinitions = new ConcurrentHashMap<String, byte[]>();
    private final Map<String, byte[]> bundledClassDefinitions;
    private final ClassLocator classLocator;

    public ClassSource(ClassLoader parent, ClassLocator classLocator, Map<String, byte[]> bundledClassDefinitions) {
        super(parent);
        this.bundledClassDefinitions = bundledClassDefinitions;
        this.classLocator = classLocator;
    }

    public Class<?> define(String name, byte[] bytecode) {
        Class<?> clazz = this.defineClass(name, bytecode, 0, bytecode.length);
        this.classDefinitions.put(name, bytecode);
        this.classes.put(name, clazz);
        return clazz;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classDefinition = this.bundledClassDefinitions.get(name);
        if (classDefinition != null) {
            return this.classLocator.defineClassFromClient(name, classDefinition, this.bundledClassDefinitions);
        }
        return super.findClass(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class aClass = this.classes.get(name);
        if (aClass != null) {
            return aClass;
        }
        try {
            return super.loadClass(name, resolve);
        }
        catch (ClassNotFoundException e) {
            return this.classLocator.handleClassNotFoundException(name);
        }
    }

    byte[] getClassDefinition(String name) {
        return this.classDefinitions.get(name);
    }

    void addClassDefinition(String name, byte[] bytes) {
        this.classDefinitions.put(name, bytes);
    }

    Class getClazz(String name) {
        return this.classes.get(name);
    }

    ClassData getClassData(String className) {
        ClassData classData = new ClassData();
        HashMap<String, byte[]> innerClassDefinitions = new HashMap<String, byte[]>(this.classDefinitions);
        byte[] mainClassDefinition = innerClassDefinitions.remove(className);
        if (mainClassDefinition == null) {
            return null;
        }
        classData.setInnerClassDefinitions(innerClassDefinitions);
        classData.setMainClassDefinition(mainClassDefinition);
        return classData;
    }
}

