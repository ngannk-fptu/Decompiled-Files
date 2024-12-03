/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.HasName;
import io.github.classgraph.PackageInfoList;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class PackageInfo
implements Comparable<PackageInfo>,
HasName {
    private String name;
    private Set<AnnotationInfo> annotationInfoSet;
    private AnnotationInfoList annotationInfo;
    private PackageInfo parent;
    private Set<PackageInfo> children;
    private Map<String, ClassInfo> memberClassNameToClassInfo;

    PackageInfo() {
    }

    PackageInfo(String packageName) {
        this.name = packageName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    void addAnnotations(AnnotationInfoList packageAnnotations) {
        if (packageAnnotations != null && !packageAnnotations.isEmpty()) {
            if (this.annotationInfoSet == null) {
                this.annotationInfoSet = new LinkedHashSet<AnnotationInfo>();
            }
            this.annotationInfoSet.addAll(packageAnnotations);
        }
    }

    void addClassInfo(ClassInfo classInfo) {
        if (this.memberClassNameToClassInfo == null) {
            this.memberClassNameToClassInfo = new HashMap<String, ClassInfo>();
        }
        this.memberClassNameToClassInfo.put(classInfo.getName(), classInfo);
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

    public PackageInfo getParent() {
        return this.parent;
    }

    public PackageInfoList getChildren() {
        if (this.children == null) {
            return PackageInfoList.EMPTY_LIST;
        }
        PackageInfoList childrenSorted = new PackageInfoList((Collection<PackageInfo>)this.children);
        CollectionUtils.sortIfNotEmpty(childrenSorted, new Comparator<PackageInfo>(){

            @Override
            public int compare(PackageInfo o1, PackageInfo o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return childrenSorted;
    }

    public ClassInfo getClassInfo(String className) {
        return this.memberClassNameToClassInfo == null ? null : this.memberClassNameToClassInfo.get(className);
    }

    public ClassInfoList getClassInfo() {
        return this.memberClassNameToClassInfo == null ? ClassInfoList.EMPTY_LIST : new ClassInfoList(new HashSet<ClassInfo>(this.memberClassNameToClassInfo.values()), true);
    }

    private void obtainClassInfoRecursive(Set<ClassInfo> reachableClassInfo) {
        if (this.memberClassNameToClassInfo != null) {
            reachableClassInfo.addAll(this.memberClassNameToClassInfo.values());
        }
        for (PackageInfo subPackageInfo : this.getChildren()) {
            subPackageInfo.obtainClassInfoRecursive(reachableClassInfo);
        }
    }

    public ClassInfoList getClassInfoRecursive() {
        HashSet<ClassInfo> reachableClassInfo = new HashSet<ClassInfo>();
        this.obtainClassInfoRecursive(reachableClassInfo);
        return new ClassInfoList(reachableClassInfo, true);
    }

    static String getParentPackageName(String packageOrClassName) {
        if (packageOrClassName.isEmpty()) {
            return null;
        }
        int lastDotIdx = packageOrClassName.lastIndexOf(46);
        return lastDotIdx < 0 ? "" : packageOrClassName.substring(0, lastDotIdx);
    }

    static PackageInfo getOrCreatePackage(String packageName, Map<String, PackageInfo> packageNameToPackageInfo, ScanSpec scanSpec) {
        PackageInfo parentPackageInfo;
        String parentPackageName;
        PackageInfo packageInfo = packageNameToPackageInfo.get(packageName);
        if (packageInfo != null) {
            return packageInfo;
        }
        packageInfo = new PackageInfo(packageName);
        packageNameToPackageInfo.put(packageName, packageInfo);
        if (!packageName.isEmpty() && (scanSpec.packageAcceptReject.isAcceptedAndNotRejected(parentPackageName = PackageInfo.getParentPackageName(packageInfo.name)) || scanSpec.packagePrefixAcceptReject.isAcceptedAndNotRejected(parentPackageName)) && (parentPackageInfo = PackageInfo.getOrCreatePackage(parentPackageName, packageNameToPackageInfo, scanSpec)) != null) {
            if (parentPackageInfo.children == null) {
                parentPackageInfo.children = new HashSet<PackageInfo>();
            }
            parentPackageInfo.children.add(packageInfo);
            packageInfo.parent = parentPackageInfo;
        }
        return packageInfo;
    }

    @Override
    public int compareTo(PackageInfo o) {
        return this.name.compareTo(o.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PackageInfo)) {
            return false;
        }
        return this.name.equals(((PackageInfo)obj).name);
    }

    public String toString() {
        return this.name;
    }
}

