/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.GraphvizDotfileGenerator;
import io.github.classgraph.MappableInfoList;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ClassInfoList
extends MappableInfoList<ClassInfo> {
    private final transient Set<ClassInfo> directlyRelatedClasses;
    private final boolean sortByName;
    private static final long serialVersionUID = 1L;
    static final ClassInfoList EMPTY_LIST = new ClassInfoList();

    public static ClassInfoList emptyList() {
        return EMPTY_LIST;
    }

    ClassInfoList(Set<ClassInfo> reachableClasses, Set<ClassInfo> directlyRelatedClasses, boolean sortByName) {
        super(reachableClasses);
        this.sortByName = sortByName;
        if (sortByName) {
            CollectionUtils.sortIfNotEmpty(this);
        }
        this.directlyRelatedClasses = directlyRelatedClasses == null ? reachableClasses : directlyRelatedClasses;
    }

    ClassInfoList(ClassInfo.ReachableAndDirectlyRelatedClasses reachableAndDirectlyRelatedClasses, boolean sortByName) {
        this(reachableAndDirectlyRelatedClasses.reachableClasses, reachableAndDirectlyRelatedClasses.directlyRelatedClasses, sortByName);
    }

    ClassInfoList(Set<ClassInfo> reachableClasses, boolean sortByName) {
        this(reachableClasses, null, sortByName);
    }

    public ClassInfoList() {
        super(1);
        this.sortByName = false;
        this.directlyRelatedClasses = new HashSet<ClassInfo>(2);
    }

    public ClassInfoList(int sizeHint) {
        super(sizeHint);
        this.sortByName = false;
        this.directlyRelatedClasses = new HashSet<ClassInfo>(2);
    }

    public ClassInfoList(Collection<ClassInfo> classInfoCollection) {
        this((Set<ClassInfo>)(classInfoCollection instanceof Set ? (Set<Object>)classInfoCollection : new HashSet<ClassInfo>(classInfoCollection)), null, true);
    }

    public <T> List<Class<T>> loadClasses(Class<T> superclassOrInterfaceType, boolean ignoreExceptions) {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList classRefs = new ArrayList();
        for (ClassInfo classInfo : this) {
            Class<T> classRef = classInfo.loadClass(superclassOrInterfaceType, ignoreExceptions);
            if (classRef == null) continue;
            classRefs.add(classRef);
        }
        return classRefs.isEmpty() ? Collections.emptyList() : classRefs;
    }

    public <T> List<Class<T>> loadClasses(Class<T> superclassOrInterfaceType) {
        return this.loadClasses(superclassOrInterfaceType, false);
    }

    public List<Class<?>> loadClasses(boolean ignoreExceptions) {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList classRefs = new ArrayList();
        for (ClassInfo classInfo : this) {
            Class<?> classRef = classInfo.loadClass(ignoreExceptions);
            if (classRef == null) continue;
            classRefs.add(classRef);
        }
        return classRefs.isEmpty() ? Collections.emptyList() : classRefs;
    }

    public List<Class<?>> loadClasses() {
        return this.loadClasses(false);
    }

    public ClassInfoList directOnly() {
        return new ClassInfoList(this.directlyRelatedClasses, this.directlyRelatedClasses, this.sortByName);
    }

    public ClassInfoList union(ClassInfoList ... others) {
        LinkedHashSet<ClassInfo> reachableClassesUnion = new LinkedHashSet<ClassInfo>(this);
        LinkedHashSet<ClassInfo> directlyRelatedClassesUnion = new LinkedHashSet<ClassInfo>(this.directlyRelatedClasses);
        for (ClassInfoList other : others) {
            reachableClassesUnion.addAll(other);
            directlyRelatedClassesUnion.addAll(other.directlyRelatedClasses);
        }
        return new ClassInfoList(reachableClassesUnion, directlyRelatedClassesUnion, this.sortByName);
    }

    public ClassInfoList intersect(ClassInfoList ... others) {
        ArrayDeque<ClassInfoList> intersectionOrder = new ArrayDeque<ClassInfoList>();
        intersectionOrder.add(this);
        boolean foundFirst = false;
        for (ClassInfoList other : others) {
            if (other.sortByName) {
                intersectionOrder.add(other);
                continue;
            }
            if (!foundFirst) {
                foundFirst = true;
                intersectionOrder.push(other);
                continue;
            }
            intersectionOrder.add(other);
        }
        ClassInfoList first = (ClassInfoList)intersectionOrder.remove();
        LinkedHashSet<ClassInfo> reachableClassesIntersection = new LinkedHashSet<ClassInfo>(first);
        while (!intersectionOrder.isEmpty()) {
            reachableClassesIntersection.retainAll((Collection)intersectionOrder.remove());
        }
        LinkedHashSet<ClassInfo> directlyRelatedClassesIntersection = new LinkedHashSet<ClassInfo>(this.directlyRelatedClasses);
        for (ClassInfoList other : others) {
            directlyRelatedClassesIntersection.retainAll(other.directlyRelatedClasses);
        }
        return new ClassInfoList(reachableClassesIntersection, directlyRelatedClassesIntersection, first.sortByName);
    }

    public ClassInfoList exclude(ClassInfoList other) {
        LinkedHashSet<ClassInfo> reachableClassesDifference = new LinkedHashSet<ClassInfo>(this);
        LinkedHashSet<ClassInfo> directlyRelatedClassesDifference = new LinkedHashSet<ClassInfo>(this.directlyRelatedClasses);
        reachableClassesDifference.removeAll(other);
        directlyRelatedClassesDifference.removeAll(other.directlyRelatedClasses);
        return new ClassInfoList(reachableClassesDifference, directlyRelatedClassesDifference, this.sortByName);
    }

    public ClassInfoList filter(ClassInfoFilter filter) {
        LinkedHashSet<ClassInfo> reachableClassesFiltered = new LinkedHashSet<ClassInfo>(this.size());
        LinkedHashSet<ClassInfo> directlyRelatedClassesFiltered = new LinkedHashSet<ClassInfo>(this.directlyRelatedClasses.size());
        for (ClassInfo ci : this) {
            if (!filter.accept(ci)) continue;
            reachableClassesFiltered.add(ci);
            if (!this.directlyRelatedClasses.contains(ci)) continue;
            directlyRelatedClassesFiltered.add(ci);
        }
        return new ClassInfoList(reachableClassesFiltered, directlyRelatedClassesFiltered, this.sortByName);
    }

    public ClassInfoList getStandardClasses() {
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return ci.isStandardClass();
            }
        });
    }

    public ClassInfoList getInterfaces() {
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return ci.isInterface();
            }
        });
    }

    public ClassInfoList getInterfacesAndAnnotations() {
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return ci.isInterfaceOrAnnotation();
            }
        });
    }

    public ClassInfoList getImplementedInterfaces() {
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return ci.isImplementedInterface();
            }
        });
    }

    public ClassInfoList getAnnotations() {
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return ci.isAnnotation();
            }
        });
    }

    public ClassInfoList getEnums() {
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return ci.isEnum();
            }
        });
    }

    public ClassInfoList getRecords() {
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return ci.isRecord();
            }
        });
    }

    public ClassInfoList getAssignableTo(ClassInfo superclassOrInterface) {
        if (superclassOrInterface == null) {
            throw new IllegalArgumentException("assignableToClass parameter cannot be null");
        }
        final HashSet<ClassInfo> allAssignableFromClasses = new HashSet<ClassInfo>();
        if (superclassOrInterface.isStandardClass()) {
            allAssignableFromClasses.addAll(superclassOrInterface.getSubclasses());
        } else if (superclassOrInterface.isInterfaceOrAnnotation()) {
            allAssignableFromClasses.addAll(superclassOrInterface.getClassesImplementing());
        }
        allAssignableFromClasses.add(superclassOrInterface);
        return this.filter(new ClassInfoFilter(){

            @Override
            public boolean accept(ClassInfo ci) {
                return allAssignableFromClasses.contains(ci);
            }
        });
    }

    public String generateGraphVizDotFileFromInterClassDependencies(float sizeX, float sizeY, boolean includeExternalClasses) {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }
        ScanSpec scanSpec = ((ClassInfo)this.get((int)0)).scanResult.scanSpec;
        if (!scanSpec.enableInterClassDependencies) {
            throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
        }
        return GraphvizDotfileGenerator.generateGraphVizDotFileFromInterClassDependencies(this, sizeX, sizeY, includeExternalClasses);
    }

    public String generateGraphVizDotFileFromInterClassDependencies(float sizeX, float sizeY) {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }
        ScanSpec scanSpec = ((ClassInfo)this.get((int)0)).scanResult.scanSpec;
        if (!scanSpec.enableInterClassDependencies) {
            throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
        }
        return GraphvizDotfileGenerator.generateGraphVizDotFileFromInterClassDependencies(this, sizeX, sizeY, scanSpec.enableExternalClasses);
    }

    public String generateGraphVizDotFileFromInterClassDependencies() {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }
        ScanSpec scanSpec = ((ClassInfo)this.get((int)0)).scanResult.scanSpec;
        if (!scanSpec.enableInterClassDependencies) {
            throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
        }
        return GraphvizDotfileGenerator.generateGraphVizDotFileFromInterClassDependencies(this, 10.5f, 8.0f, scanSpec.enableExternalClasses);
    }

    @Deprecated
    public String generateGraphVizDotFileFromClassDependencies() {
        return this.generateGraphVizDotFileFromInterClassDependencies();
    }

    public String generateGraphVizDotFile(float sizeX, float sizeY, boolean showFields, boolean showFieldTypeDependencyEdges, boolean showMethods, boolean showMethodTypeDependencyEdges, boolean showAnnotations, boolean useSimpleNames) {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }
        ScanSpec scanSpec = ((ClassInfo)this.get((int)0)).scanResult.scanSpec;
        if (!scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return GraphvizDotfileGenerator.generateGraphVizDotFile(this, sizeX, sizeY, showFields, showFieldTypeDependencyEdges, showMethods, showMethodTypeDependencyEdges, showAnnotations, useSimpleNames, scanSpec);
    }

    public String generateGraphVizDotFile(float sizeX, float sizeY, boolean showFields, boolean showFieldTypeDependencyEdges, boolean showMethods, boolean showMethodTypeDependencyEdges, boolean showAnnotations) {
        return this.generateGraphVizDotFile(sizeX, sizeY, showFields, showFieldTypeDependencyEdges, showMethods, showMethodTypeDependencyEdges, showAnnotations, true);
    }

    public String generateGraphVizDotFile(float sizeX, float sizeY) {
        return this.generateGraphVizDotFile(sizeX, sizeY, true, true, true, true, true);
    }

    public String generateGraphVizDotFile() {
        return this.generateGraphVizDotFile(10.5f, 8.0f, true, true, true, true, true);
    }

    public void generateGraphVizDotFile(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(file);){
            writer.print(this.generateGraphVizDotFile());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClassInfoList)) {
            return false;
        }
        ClassInfoList other = (ClassInfoList)obj;
        if (this.directlyRelatedClasses == null != (other.directlyRelatedClasses == null)) {
            return false;
        }
        if (this.directlyRelatedClasses == null) {
            return super.equals(other);
        }
        return super.equals(other) && this.directlyRelatedClasses.equals(other.directlyRelatedClasses);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (this.directlyRelatedClasses == null ? 0 : this.directlyRelatedClasses.hashCode());
    }

    static {
        EMPTY_LIST.makeUnmodifiable();
    }

    @FunctionalInterface
    public static interface ClassInfoFilter {
        public boolean accept(ClassInfo var1);
    }
}

