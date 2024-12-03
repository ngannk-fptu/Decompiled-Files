/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classpath;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClassLoaderOrder {
    private final List<Map.Entry<ClassLoader, ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry>> classLoaderOrder = new ArrayList<Map.Entry<ClassLoader, ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry>>();
    public ReflectionUtils reflectionUtils;
    private final Set<ClassLoader> added = Collections.newSetFromMap(new IdentityHashMap());
    private final Set<ClassLoader> delegatedTo = Collections.newSetFromMap(new IdentityHashMap());
    private final Set<ClassLoader> allParentClassLoaders = Collections.newSetFromMap(new IdentityHashMap());
    private final Map<ClassLoader, ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry> classLoaderToClassLoaderHandlerRegistryEntry = new IdentityHashMap<ClassLoader, ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry>();

    public ClassLoaderOrder(ReflectionUtils reflectionUtils) {
        this.reflectionUtils = reflectionUtils;
    }

    public List<Map.Entry<ClassLoader, ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry>> getClassLoaderOrder() {
        return this.classLoaderOrder;
    }

    public Set<ClassLoader> getAllParentClassLoaders() {
        return this.allParentClassLoaders;
    }

    private ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry getRegistryEntry(ClassLoader classLoader, LogNode log) {
        ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry entry = this.classLoaderToClassLoaderHandlerRegistryEntry.get(classLoader);
        if (entry == null) {
            for (Class<?> currClassLoaderClass = classLoader.getClass(); currClassLoaderClass != Object.class && currClassLoaderClass != null; currClassLoaderClass = currClassLoaderClass.getSuperclass()) {
                for (ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry ent : ClassLoaderHandlerRegistry.CLASS_LOADER_HANDLERS) {
                    if (!ent.canHandle(currClassLoaderClass, log)) continue;
                    entry = ent;
                    break;
                }
                if (entry != null) break;
            }
            if (entry == null) {
                entry = ClassLoaderHandlerRegistry.FALLBACK_HANDLER;
            }
            this.classLoaderToClassLoaderHandlerRegistryEntry.put(classLoader, entry);
        }
        return entry;
    }

    public void add(ClassLoader classLoader, LogNode log) {
        ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry entry;
        if (classLoader == null) {
            return;
        }
        if (this.added.add(classLoader) && (entry = this.getRegistryEntry(classLoader, log)) != null) {
            this.classLoaderOrder.add(new AbstractMap.SimpleEntry<ClassLoader, ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry>(classLoader, entry));
        }
    }

    public void delegateTo(ClassLoader classLoader, boolean isParent, LogNode log) {
        if (classLoader == null) {
            return;
        }
        if (isParent) {
            this.allParentClassLoaders.add(classLoader);
        }
        if (this.delegatedTo.add(classLoader)) {
            ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry entry = this.getRegistryEntry(classLoader, log);
            entry.findClassLoaderOrder(classLoader, this, log);
        }
    }
}

