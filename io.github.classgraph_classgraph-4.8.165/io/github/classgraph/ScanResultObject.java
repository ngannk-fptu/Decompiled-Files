/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.utils.LogNode;

abstract class ScanResultObject {
    protected transient ScanResult scanResult;
    private transient ClassInfo classInfo;
    protected transient Class<?> classRef;

    ScanResultObject() {
    }

    void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    final Set<ClassInfo> findReferencedClassInfo(LogNode log) {
        LinkedHashSet<ClassInfo> refdClassInfo = new LinkedHashSet<ClassInfo>();
        if (this.scanResult != null) {
            this.findReferencedClassInfo(this.scanResult.classNameToClassInfo, refdClassInfo, log);
        }
        return refdClassInfo;
    }

    protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        ClassInfo ci = this.getClassInfo();
        if (ci != null) {
            refdClassInfo.add(ci);
        }
    }

    protected abstract String getClassName();

    ClassInfo getClassInfo() {
        if (this.classInfo == null) {
            if (this.scanResult == null) {
                return null;
            }
            String className = this.getClassName();
            if (className == null) {
                throw new IllegalArgumentException("Class name is not set");
            }
            this.classInfo = this.scanResult.getClassInfo(className);
        }
        return this.classInfo;
    }

    private String getClassInfoNameOrClassName() {
        String className;
        ClassInfo ci = null;
        try {
            ci = this.getClassInfo();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        if (ci == null) {
            ci = this.classInfo;
        }
        if ((className = ci != null ? ci.getName() : this.getClassName()) == null) {
            throw new IllegalArgumentException("Class name is not set");
        }
        return className;
    }

    <T> Class<T> loadClass(Class<T> superclassOrInterfaceType, boolean ignoreExceptions) {
        block5: {
            if (this.classRef == null) {
                String className = this.getClassInfoNameOrClassName();
                if (this.scanResult != null) {
                    this.classRef = this.scanResult.loadClass(className, superclassOrInterfaceType, ignoreExceptions);
                } else {
                    try {
                        this.classRef = Class.forName(className);
                    }
                    catch (Throwable t) {
                        if (ignoreExceptions) break block5;
                        throw new IllegalArgumentException("Could not load class " + className, t);
                    }
                }
            }
        }
        Class<?> classT = this.classRef;
        return classT;
    }

    <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
        return this.loadClass(superclassOrInterfaceType, false);
    }

    Class<?> loadClass(boolean ignoreExceptions) {
        block5: {
            if (this.classRef == null) {
                String className = this.getClassInfoNameOrClassName();
                if (this.scanResult != null) {
                    this.classRef = this.scanResult.loadClass(className, ignoreExceptions);
                } else {
                    try {
                        this.classRef = Class.forName(className);
                    }
                    catch (Throwable t) {
                        if (ignoreExceptions) break block5;
                        throw new IllegalArgumentException("Could not load class " + className, t);
                    }
                }
            }
        }
        return this.classRef;
    }

    Class<?> loadClass() {
        return this.loadClass(false);
    }

    protected abstract void toString(boolean var1, StringBuilder var2);

    String toString(boolean useSimpleNames) {
        StringBuilder buf = new StringBuilder();
        this.toString(useSimpleNames, buf);
        return buf.toString();
    }

    public String toStringWithSimpleNames() {
        StringBuilder buf = new StringBuilder();
        this.toString(true, buf);
        return buf.toString();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        this.toString(false, buf);
        return buf.toString();
    }
}

