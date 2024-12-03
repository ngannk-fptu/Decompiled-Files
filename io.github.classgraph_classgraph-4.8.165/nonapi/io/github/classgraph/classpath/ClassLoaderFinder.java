/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classpath;

import java.util.LinkedHashSet;
import nonapi.io.github.classgraph.classpath.CallStackReader;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClassLoaderFinder {
    private final ClassLoader[] contextClassLoaders;

    public ClassLoader[] getContextClassLoaders() {
        return this.contextClassLoaders;
    }

    ClassLoaderFinder(ScanSpec scanSpec, ReflectionUtils reflectionUtils, LogNode log) {
        LogNode classLoadersFoundLog;
        LinkedHashSet<Object> classLoadersUnique;
        if (scanSpec.overrideClassLoaders == null) {
            block11: {
                ClassLoader systemClassLoader;
                ClassLoader classLoader;
                classLoadersUnique = new LinkedHashSet();
                ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
                if (threadClassLoader != null) {
                    classLoadersUnique.add(threadClassLoader);
                }
                if ((classLoader = this.getClass().getClassLoader()) != null) {
                    classLoadersUnique.add(classLoader);
                }
                if ((systemClassLoader = ClassLoader.getSystemClassLoader()) != null) {
                    classLoadersUnique.add(systemClassLoader);
                }
                try {
                    Class<?>[] callStack = new CallStackReader(reflectionUtils).getClassContext(log);
                    for (int i = callStack.length - 1; i >= 0; --i) {
                        ClassLoader callerClassLoader = callStack[i].getClassLoader();
                        if (callerClassLoader == null) continue;
                        classLoadersUnique.add(callerClassLoader);
                    }
                }
                catch (IllegalArgumentException e) {
                    if (log == null) break block11;
                    log.log("Could not get call stack", e);
                }
            }
            if (scanSpec.addedClassLoaders != null) {
                classLoadersUnique.addAll(scanSpec.addedClassLoaders);
            }
            classLoadersFoundLog = log == null ? null : log.log("Found ClassLoaders:");
        } else {
            classLoadersUnique = new LinkedHashSet<ClassLoader>(scanSpec.overrideClassLoaders);
            LogNode logNode = classLoadersFoundLog = log == null ? null : log.log("Override ClassLoaders:");
        }
        if (classLoadersFoundLog != null) {
            for (ClassLoader classLoader : classLoadersUnique) {
                classLoadersFoundLog.log(classLoader.getClass().getName());
            }
        }
        this.contextClassLoaders = classLoadersUnique.toArray(new ClassLoader[0]);
    }
}

