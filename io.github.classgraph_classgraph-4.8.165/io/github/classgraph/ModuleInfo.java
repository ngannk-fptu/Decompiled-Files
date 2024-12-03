/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ClasspathElement;
import io.github.classgraph.HasName;
import io.github.classgraph.ModuleRef;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.PackageInfoList;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ModuleInfo
implements Comparable<ModuleInfo>,
HasName {
    private String name;
    private transient ClasspathElement classpathElement;
    private transient ModuleRef moduleRef;
    private transient URI locationURI;
    private Set<AnnotationInfo> annotationInfoSet;
    private AnnotationInfoList annotationInfo;
    private Set<PackageInfo> packageInfoSet;
    private Set<ClassInfo> classInfoSet;

    ModuleInfo() {
    }

    ModuleInfo(ModuleRef moduleRef, ClasspathElement classpathElement) {
        this.moduleRef = moduleRef;
        this.classpathElement = classpathElement;
        this.name = classpathElement.getModuleName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public URI getLocation() {
        if (this.locationURI == null) {
            URI uRI = this.locationURI = this.moduleRef != null ? this.moduleRef.getLocation() : null;
            if (this.locationURI == null) {
                this.locationURI = this.classpathElement.getURI();
            }
        }
        return this.locationURI;
    }

    public ModuleRef getModuleRef() {
        return this.moduleRef;
    }

    void addClassInfo(ClassInfo classInfo) {
        if (this.classInfoSet == null) {
            this.classInfoSet = new HashSet<ClassInfo>();
        }
        this.classInfoSet.add(classInfo);
    }

    public ClassInfo getClassInfo(String className) {
        for (ClassInfo ci : this.classInfoSet) {
            if (!ci.getName().equals(className)) continue;
            return ci;
        }
        return null;
    }

    public ClassInfoList getClassInfo() {
        return new ClassInfoList(this.classInfoSet, true);
    }

    void addPackageInfo(PackageInfo packageInfo) {
        if (this.packageInfoSet == null) {
            this.packageInfoSet = new HashSet<PackageInfo>();
        }
        this.packageInfoSet.add(packageInfo);
    }

    public PackageInfo getPackageInfo(String packageName) {
        if (this.packageInfoSet == null) {
            return null;
        }
        for (PackageInfo pi : this.packageInfoSet) {
            if (!pi.getName().equals(packageName)) continue;
            return pi;
        }
        return null;
    }

    public PackageInfoList getPackageInfo() {
        if (this.packageInfoSet == null) {
            return new PackageInfoList(1);
        }
        PackageInfoList packageInfoList = new PackageInfoList((Collection<PackageInfo>)this.packageInfoSet);
        CollectionUtils.sortIfNotEmpty(packageInfoList);
        return packageInfoList;
    }

    void addAnnotations(AnnotationInfoList moduleAnnotations) {
        if (moduleAnnotations != null && !moduleAnnotations.isEmpty()) {
            if (this.annotationInfoSet == null) {
                this.annotationInfoSet = new LinkedHashSet<AnnotationInfo>();
            }
            this.annotationInfoSet.addAll(moduleAnnotations);
        }
    }

    public AnnotationInfo getAnnotationInfo(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.getAnnotationInfo(annotation.getName());
    }

    public AnnotationInfo getAnnotationInfo(String annotationName) {
        return (AnnotationInfo)this.getAnnotationInfo().get(annotationName);
    }

    public AnnotationInfoList getAnnotationInfo() {
        if (this.annotationInfo == null) {
            if (this.annotationInfoSet == null) {
                this.annotationInfo = AnnotationInfoList.EMPTY_LIST;
            } else {
                this.annotationInfo = new AnnotationInfoList();
                this.annotationInfo.addAll(this.annotationInfoSet);
            }
        }
        return this.annotationInfo;
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.hasAnnotation(annotation.getName());
    }

    public boolean hasAnnotation(String annotationName) {
        return this.getAnnotationInfo().containsName(annotationName);
    }

    @Override
    public int compareTo(ModuleInfo other) {
        int diff = this.name.compareTo(other.name);
        if (diff != 0) {
            return diff;
        }
        URI thisLoc = this.getLocation();
        URI otherLoc = other.getLocation();
        if (thisLoc != null && otherLoc != null) {
            return thisLoc.compareTo(otherLoc);
        }
        return 0;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ModuleInfo)) {
            return false;
        }
        return this.compareTo((ModuleInfo)obj) == 0;
    }

    public String toString() {
        return this.name;
    }
}

