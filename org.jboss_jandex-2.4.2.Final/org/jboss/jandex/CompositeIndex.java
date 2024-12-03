/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.ModuleInfo;

public class CompositeIndex
implements IndexView {
    final Collection<IndexView> indexes;

    private CompositeIndex(Collection<IndexView> indexes) {
        this.indexes = indexes;
    }

    public static CompositeIndex create(Collection<IndexView> indexes) {
        return new CompositeIndex(indexes);
    }

    public static CompositeIndex create(IndexView ... indexes) {
        return new CompositeIndex(Arrays.asList(indexes));
    }

    public static CompositeIndex createMerged(CompositeIndex ... indexes) {
        ArrayList<IndexView> list = new ArrayList<IndexView>();
        for (CompositeIndex index : indexes) {
            list.addAll(index.indexes);
        }
        return CompositeIndex.create(list);
    }

    public List<AnnotationInstance> getAnnotations(DotName annotationName) {
        ArrayList<AnnotationInstance> allInstances = new ArrayList<AnnotationInstance>();
        for (IndexView index : this.indexes) {
            Collection<AnnotationInstance> list = index.getAnnotations(annotationName);
            if (list == null) continue;
            allInstances.addAll(list);
        }
        return Collections.unmodifiableList(allInstances);
    }

    @Override
    public Collection<AnnotationInstance> getAnnotationsWithRepeatable(DotName annotationName, IndexView index) {
        ArrayList<AnnotationInstance> allInstances = new ArrayList<AnnotationInstance>();
        for (IndexView i : this.indexes) {
            allInstances.addAll(i.getAnnotationsWithRepeatable(annotationName, index));
        }
        return Collections.unmodifiableList(allInstances);
    }

    public Set<ClassInfo> getKnownDirectSubclasses(DotName className) {
        HashSet<ClassInfo> allKnown = new HashSet<ClassInfo>();
        for (IndexView index : this.indexes) {
            Collection<ClassInfo> list = index.getKnownDirectSubclasses(className);
            if (list == null) continue;
            allKnown.addAll(list);
        }
        return Collections.unmodifiableSet(allKnown);
    }

    public Set<ClassInfo> getAllKnownSubclasses(DotName className) {
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
        for (IndexView index : this.indexes) {
            Collection<ClassInfo> list = index.getKnownDirectSubclasses(name);
            if (list == null) continue;
            for (ClassInfo clazz : list) {
                DotName className = clazz.name();
                if (processedClasses.contains(className)) continue;
                allKnown.add(clazz);
                subClassesToProcess.add(className);
            }
        }
    }

    @Override
    public Collection<ClassInfo> getKnownDirectImplementors(DotName className) {
        HashSet<ClassInfo> allKnown = new HashSet<ClassInfo>();
        for (IndexView index : this.indexes) {
            Collection<ClassInfo> list = index.getKnownDirectImplementors(className);
            if (list == null) continue;
            allKnown.addAll(list);
        }
        return Collections.unmodifiableSet(allKnown);
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
        for (IndexView index : this.indexes) {
            Collection<ClassInfo> list = index.getKnownDirectImplementors(name);
            if (list == null) continue;
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
        for (IndexView index : this.indexes) {
            ClassInfo info = index.getClassByName(className);
            if (info == null) continue;
            return info;
        }
        return null;
    }

    @Override
    public Collection<ClassInfo> getKnownClasses() {
        ArrayList<ClassInfo> allKnown = new ArrayList<ClassInfo>();
        for (IndexView index : this.indexes) {
            Collection<ClassInfo> list = index.getKnownClasses();
            if (list == null) continue;
            allKnown.addAll(list);
        }
        return Collections.unmodifiableCollection(allKnown);
    }

    @Override
    public ModuleInfo getModuleByName(DotName moduleName) {
        for (IndexView index : this.indexes) {
            ModuleInfo info = index.getModuleByName(moduleName);
            if (info == null) continue;
            return info;
        }
        return null;
    }

    @Override
    public Collection<ModuleInfo> getKnownModules() {
        ArrayList<ModuleInfo> allKnown = new ArrayList<ModuleInfo>();
        for (IndexView index : this.indexes) {
            Collection<ModuleInfo> list = index.getKnownModules();
            if (list == null) continue;
            allKnown.addAll(list);
        }
        return Collections.unmodifiableCollection(allKnown);
    }

    @Override
    public Collection<ClassInfo> getKnownUsers(DotName className) {
        ArrayList<ClassInfo> users = new ArrayList<ClassInfo>();
        HashSet<DotName> processedClasses = new HashSet<DotName>();
        for (IndexView index : this.indexes) {
            Collection<ClassInfo> set = index.getKnownUsers(className);
            if (set == null) continue;
            for (ClassInfo classInfo : set) {
                if (!processedClasses.add(classInfo.name())) continue;
                users.add(classInfo);
            }
        }
        return Collections.unmodifiableCollection(users);
    }
}

