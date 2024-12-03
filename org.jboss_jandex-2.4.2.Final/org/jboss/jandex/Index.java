/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.ModuleInfo;
import org.jboss.jandex.Type;

public final class Index
implements IndexView {
    private static final List<AnnotationInstance> EMPTY_ANNOTATION_LIST = Collections.emptyList();
    private static final List<ClassInfo> EMPTY_CLASSINFO_LIST = Collections.emptyList();
    static final DotName REPEATABLE = DotName.createSimple("java.lang.annotation.Repeatable");
    final Map<DotName, List<AnnotationInstance>> annotations;
    final Map<DotName, List<ClassInfo>> subclasses;
    final Map<DotName, List<ClassInfo>> implementors;
    final Map<DotName, ClassInfo> classes;
    final Map<DotName, ModuleInfo> modules;
    final Map<DotName, List<ClassInfo>> users;

    Index(Map<DotName, List<AnnotationInstance>> annotations, Map<DotName, List<ClassInfo>> subclasses, Map<DotName, List<ClassInfo>> implementors, Map<DotName, ClassInfo> classes, Map<DotName, ModuleInfo> modules, Map<DotName, List<ClassInfo>> users) {
        this.annotations = Collections.unmodifiableMap(annotations);
        this.classes = Collections.unmodifiableMap(classes);
        this.subclasses = Collections.unmodifiableMap(subclasses);
        this.implementors = Collections.unmodifiableMap(implementors);
        this.modules = Collections.unmodifiableMap(modules);
        this.users = Collections.unmodifiableMap(users);
    }

    public static Index create(Map<DotName, List<AnnotationInstance>> annotations, Map<DotName, List<ClassInfo>> subclasses, Map<DotName, List<ClassInfo>> implementors, Map<DotName, ClassInfo> classes) {
        return new Index(annotations, subclasses, implementors, classes, Collections.<DotName, ModuleInfo>emptyMap(), Collections.<DotName, List<ClassInfo>>emptyMap());
    }

    public static Index create(Map<DotName, List<AnnotationInstance>> annotations, Map<DotName, List<ClassInfo>> subclasses, Map<DotName, List<ClassInfo>> implementors, Map<DotName, ClassInfo> classes, Map<DotName, List<ClassInfo>> users) {
        return new Index(annotations, subclasses, implementors, classes, Collections.<DotName, ModuleInfo>emptyMap(), users);
    }

    public static Index of(Iterable<Class<?>> classes) throws IOException {
        Indexer indexer = new Indexer();
        for (Class<?> clazz : classes) {
            indexer.indexClass(clazz);
        }
        return indexer.complete();
    }

    public static Index of(Class<?> ... classes) throws IOException {
        return Index.of(Arrays.asList(classes));
    }

    public static Index of(File ... directories) throws IOException {
        Indexer indexer = new Indexer();
        for (File directory : directories) {
            File[] sources;
            if (directory == null || !directory.isDirectory()) {
                throw new IllegalArgumentException("not a directory: " + directory);
            }
            for (File source : sources = directory.listFiles(new FileFilter(){

                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(".class");
                }
            })) {
                indexer.index(new FileInputStream(source));
            }
        }
        return indexer.complete();
    }

    public List<AnnotationInstance> getAnnotations(DotName annotationName) {
        List<AnnotationInstance> list = this.annotations.get(annotationName);
        return list == null ? EMPTY_ANNOTATION_LIST : Collections.unmodifiableList(list);
    }

    @Override
    public Collection<AnnotationInstance> getAnnotationsWithRepeatable(DotName annotationName, IndexView index) {
        ClassInfo annotationClass = index.getClassByName(annotationName);
        if (annotationClass == null) {
            throw new IllegalArgumentException("Index does not contain the annotation definition: " + annotationName);
        }
        if (!annotationClass.isAnnotation()) {
            throw new IllegalArgumentException("Not an annotation type: " + annotationClass);
        }
        AnnotationInstance repeatable = annotationClass.classAnnotation(REPEATABLE);
        if (repeatable == null) {
            return this.getAnnotations(annotationName);
        }
        Type containing = repeatable.value().asClass();
        return this.getRepeatableAnnotations(annotationName, containing.name());
    }

    private Collection<AnnotationInstance> getRepeatableAnnotations(DotName annotationName, DotName containingAnnotationName) {
        ArrayList<AnnotationInstance> instances = new ArrayList<AnnotationInstance>();
        instances.addAll(this.getAnnotations(annotationName));
        for (AnnotationInstance containingInstance : this.getAnnotations(containingAnnotationName)) {
            for (AnnotationInstance nestedInstance : containingInstance.value().asNestedArray()) {
                instances.add(new AnnotationInstance(nestedInstance.name(), containingInstance.target(), nestedInstance.valueArray()));
            }
        }
        return instances;
    }

    public List<ClassInfo> getKnownDirectSubclasses(DotName className) {
        List<ClassInfo> list = this.subclasses.get(className);
        return list == null ? EMPTY_CLASSINFO_LIST : Collections.unmodifiableList(list);
    }

    @Override
    public Collection<ClassInfo> getAllKnownSubclasses(DotName className) {
        HashSet<ClassInfo> allKnown = new HashSet<ClassInfo>();
        HashSet<DotName> processedClasses = new HashSet<DotName>();
        this.getAllKnownSubClasses(className, allKnown, processedClasses);
        return allKnown;
    }

    private void getAllKnownSubClasses(DotName className, Set<ClassInfo> allKnown, Set<DotName> processedClasses) {
        HashSet<DotName> subClassesToProcess = new HashSet<DotName>();
        subClassesToProcess.add(className);
        while (!subClassesToProcess.isEmpty()) {
            Iterator toProcess = subClassesToProcess.iterator();
            DotName name = (DotName)toProcess.next();
            toProcess.remove();
            processedClasses.add(name);
            this.getAllKnownSubClasses(name, allKnown, subClassesToProcess, processedClasses);
        }
    }

    private void getAllKnownSubClasses(DotName name, Set<ClassInfo> allKnown, Set<DotName> subClassesToProcess, Set<DotName> processedClasses) {
        Collection list = this.getKnownDirectSubclasses(name);
        if (list != null) {
            for (ClassInfo clazz : list) {
                DotName className = clazz.name();
                if (processedClasses.contains(className)) continue;
                allKnown.add(clazz);
                subClassesToProcess.add(className);
            }
        }
    }

    public List<ClassInfo> getKnownDirectImplementors(DotName className) {
        List<ClassInfo> list = this.implementors.get(className);
        return list == null ? EMPTY_CLASSINFO_LIST : Collections.unmodifiableList(list);
    }

    public Set<ClassInfo> getAllKnownImplementors(DotName interfaceName) {
        HashSet<ClassInfo> allKnown = new HashSet<ClassInfo>();
        HashSet<DotName> subInterfacesToProcess = new HashSet<DotName>();
        HashSet<DotName> processedClasses = new HashSet<DotName>();
        subInterfacesToProcess.add(interfaceName);
        while (!subInterfacesToProcess.isEmpty()) {
            Iterator toProcess = subInterfacesToProcess.iterator();
            DotName name = (DotName)toProcess.next();
            toProcess.remove();
            processedClasses.add(name);
            this.getKnownImplementors(name, allKnown, subInterfacesToProcess, processedClasses);
        }
        return allKnown;
    }

    private void getKnownImplementors(DotName name, Set<ClassInfo> allKnown, Set<DotName> subInterfacesToProcess, Set<DotName> processedClasses) {
        Collection list = this.getKnownDirectImplementors(name);
        if (list != null) {
            for (ClassInfo clazz : list) {
                DotName className = clazz.name();
                if (processedClasses.contains(className)) continue;
                if (Modifier.isInterface(clazz.flags())) {
                    subInterfacesToProcess.add(className);
                    continue;
                }
                if (allKnown.contains(clazz)) continue;
                allKnown.add(clazz);
                processedClasses.add(className);
                this.getAllKnownSubClasses(className, allKnown, processedClasses);
            }
        }
    }

    @Override
    public ClassInfo getClassByName(DotName className) {
        return this.classes.get(className);
    }

    @Override
    public Collection<ClassInfo> getKnownClasses() {
        return this.classes.values();
    }

    @Override
    public Collection<ModuleInfo> getKnownModules() {
        return this.modules.values();
    }

    @Override
    public ModuleInfo getModuleByName(DotName moduleName) {
        return this.modules.get(moduleName);
    }

    public void printAnnotations() {
        System.out.println("Annotations:");
        for (Map.Entry<DotName, List<AnnotationInstance>> e : this.annotations.entrySet()) {
            System.out.println(e.getKey() + ":");
            for (AnnotationInstance instance : e.getValue()) {
                AnnotationTarget target = instance.target();
                if (target instanceof ClassInfo) {
                    System.out.println("    Class: " + target);
                } else if (target instanceof FieldInfo) {
                    System.out.println("    Field: " + target);
                } else if (target instanceof MethodInfo) {
                    System.out.println("    Method: " + target);
                } else if (target instanceof MethodParameterInfo) {
                    System.out.println("    Parameter: " + target);
                }
                List<AnnotationValue> values = instance.values();
                if (values.size() < 1) continue;
                StringBuilder builder = new StringBuilder("        (");
                for (int i = 0; i < values.size(); ++i) {
                    builder.append(values.get(i));
                    if (i >= values.size() - 1) continue;
                    builder.append(", ");
                }
                builder.append(')');
                System.out.println(builder.toString());
            }
        }
    }

    public void printSubclasses() {
        System.out.println("Subclasses:");
        for (Map.Entry<DotName, List<ClassInfo>> entry : this.subclasses.entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (ClassInfo clazz : entry.getValue()) {
                System.out.println("    " + clazz.name());
            }
        }
    }

    public List<ClassInfo> getKnownUsers(DotName className) {
        List<ClassInfo> ret = this.users.get(className);
        if (ret == null) {
            return EMPTY_CLASSINFO_LIST;
        }
        return Collections.unmodifiableList(ret);
    }
}

