/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.util.HashMap;
import java.util.List;
import org.apache.tomcat.util.bcel.classfile.AnnotationEntry;
import org.apache.tomcat.util.bcel.classfile.Annotations;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;

public class JavaClass {
    private final int accessFlags;
    private final String className;
    private final String superclassName;
    private final String[] interfaceNames;
    private final Annotations runtimeVisibleAnnotations;
    private final List<Annotations> runtimeVisibleFieldOrMethodAnnotations;

    JavaClass(String className, String superclassName, int accessFlags, ConstantPool constantPool, String[] interfaceNames, Annotations runtimeVisibleAnnotations, List<Annotations> runtimeVisibleFieldOrMethodAnnotations) {
        this.accessFlags = accessFlags;
        this.runtimeVisibleAnnotations = runtimeVisibleAnnotations;
        this.runtimeVisibleFieldOrMethodAnnotations = runtimeVisibleFieldOrMethodAnnotations;
        this.className = className;
        this.superclassName = superclassName;
        this.interfaceNames = interfaceNames;
    }

    public final int getAccessFlags() {
        return this.accessFlags;
    }

    public AnnotationEntry[] getAllAnnotationEntries() {
        HashMap<String, Object> annotationEntries = new HashMap<String, Object>();
        if (this.runtimeVisibleAnnotations != null) {
            for (AnnotationEntry annotationEntry : this.runtimeVisibleAnnotations.getAnnotationEntries()) {
                annotationEntries.put(annotationEntry.getAnnotationType(), annotationEntry);
            }
        }
        if (this.runtimeVisibleFieldOrMethodAnnotations != null) {
            for (Annotations annotations : this.runtimeVisibleFieldOrMethodAnnotations.toArray(Annotations.EMPTY_ARRAY)) {
                for (AnnotationEntry annotationEntry : annotations.getAnnotationEntries()) {
                    annotationEntries.putIfAbsent(annotationEntry.getAnnotationType(), annotationEntry);
                }
            }
        }
        if (annotationEntries.isEmpty()) {
            return null;
        }
        return annotationEntries.values().toArray(AnnotationEntry.EMPTY_ARRAY);
    }

    public AnnotationEntry[] getAnnotationEntries() {
        if (this.runtimeVisibleAnnotations != null) {
            return this.runtimeVisibleAnnotations.getAnnotationEntries();
        }
        return null;
    }

    public String getClassName() {
        return this.className;
    }

    public String[] getInterfaceNames() {
        return this.interfaceNames;
    }

    public String getSuperclassName() {
        return this.superclassName;
    }
}

