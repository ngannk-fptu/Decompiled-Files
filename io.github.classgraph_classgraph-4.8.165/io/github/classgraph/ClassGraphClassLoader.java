/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClasspathElementModule;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class ClassGraphClassLoader
extends ClassLoader {
    private final ScanResult scanResult;
    private final boolean initializeLoadedClasses;
    private Set<ClassLoader> environmentClassLoaderDelegationOrder;
    private List<ClassLoader> overrideClassLoaders;
    private final ClassLoader classpathClassLoader;
    private Set<ClassLoader> addedClassLoaderDelegationOrder;

    ClassGraphClassLoader(ScanResult scanResult) {
        super(null);
        List<URL> classpathURLs;
        boolean clasloadersAdded;
        ClassGraphClassLoader.registerAsParallelCapable();
        this.scanResult = scanResult;
        ScanSpec scanSpec = scanResult.scanSpec;
        this.initializeLoadedClasses = scanSpec.initializeLoadedClasses;
        boolean classpathOverridden = scanSpec.overrideClasspath != null && !scanSpec.overrideClasspath.isEmpty();
        boolean classloadersOverridden = scanSpec.overrideClassLoaders != null && !scanSpec.overrideClassLoaders.isEmpty();
        boolean bl = clasloadersAdded = scanSpec.addedClassLoaders != null && !scanSpec.addedClassLoaders.isEmpty();
        if (!classpathOverridden && !classloadersOverridden) {
            this.environmentClassLoaderDelegationOrder = new LinkedHashSet<ClassLoader>();
            this.environmentClassLoaderDelegationOrder.add(null);
            ClassLoader[] envClassLoaderOrder = scanResult.getClassLoaderOrderRespectingParentDelegation();
            if (envClassLoaderOrder != null) {
                this.environmentClassLoaderDelegationOrder.addAll(Arrays.asList(envClassLoaderOrder));
            }
        }
        this.classpathClassLoader = (classpathURLs = scanResult.getClasspathURLs()).isEmpty() ? null : new URLClassLoader(classpathURLs.toArray(new URL[0]));
        List<ClassLoader> list = this.overrideClassLoaders = classloadersOverridden ? scanSpec.overrideClassLoaders : null;
        if (this.overrideClassLoaders == null && classpathOverridden && this.classpathClassLoader != null) {
            this.overrideClassLoaders = Collections.singletonList(this.classpathClassLoader);
        }
        if (clasloadersAdded) {
            this.addedClassLoaderDelegationOrder = new LinkedHashSet<ClassLoader>();
            this.addedClassLoaderDelegationOrder.addAll(scanSpec.addedClassLoaders);
            if (this.environmentClassLoaderDelegationOrder != null) {
                this.addedClassLoaderDelegationOrder.removeAll(this.environmentClassLoaderDelegationOrder);
            }
        }
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException, LinkageError, SecurityException {
        ResourceList classfileResources;
        ClassLoader classInfoClassLoader;
        LinkageError linkageError;
        block45: {
            ClassInfo classInfo;
            ClassGraphClassLoader delegateClassGraphClassLoader = this.scanResult.classpathFinder.getDelegateClassGraphClassLoader();
            linkageError = null;
            if (delegateClassGraphClassLoader != null) {
                try {
                    return Class.forName(className, this.initializeLoadedClasses, delegateClassGraphClassLoader);
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
                catch (LinkageError e) {
                    linkageError = e;
                }
            }
            if (this.overrideClassLoaders != null) {
                for (ClassLoader overrideClassLoader : this.overrideClassLoaders) {
                    try {
                        return Class.forName(className, this.initializeLoadedClasses, overrideClassLoader);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                    }
                    catch (LinkageError e) {
                        if (linkageError != null) continue;
                        linkageError = e;
                    }
                }
            }
            if (this.overrideClassLoaders == null && this.environmentClassLoaderDelegationOrder != null && !this.environmentClassLoaderDelegationOrder.isEmpty()) {
                for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
                    try {
                        return Class.forName(className, this.initializeLoadedClasses, envClassLoader);
                    }
                    catch (ClassNotFoundException e) {
                    }
                    catch (LinkageError e) {
                        if (linkageError != null) continue;
                        linkageError = e;
                    }
                }
            }
            classInfoClassLoader = null;
            ClassInfo classInfo2 = classInfo = this.scanResult.classNameToClassInfo == null ? null : this.scanResult.classNameToClassInfo.get(className);
            if (classInfo != null) {
                block44: {
                    classInfoClassLoader = classInfo.classLoader;
                    if (!(classInfoClassLoader == null || this.environmentClassLoaderDelegationOrder != null && this.environmentClassLoaderDelegationOrder.contains(classInfoClassLoader))) {
                        try {
                            return Class.forName(className, this.initializeLoadedClasses, classInfoClassLoader);
                        }
                        catch (ClassNotFoundException e) {
                        }
                        catch (LinkageError e) {
                            if (linkageError != null) break block44;
                            linkageError = e;
                        }
                    }
                }
                if (classInfo.classpathElement instanceof ClasspathElementModule && !classInfo.isPublic()) {
                    throw new ClassNotFoundException("Classfile for class " + className + " was found in a module, but the context and system classloaders could not load the class, probably because the class is not public.");
                }
            }
            if (this.overrideClassLoaders == null && this.classpathClassLoader != null) {
                try {
                    return Class.forName(className, this.initializeLoadedClasses, this.classpathClassLoader);
                }
                catch (ClassNotFoundException e) {
                }
                catch (LinkageError e) {
                    if (linkageError != null) break block45;
                    linkageError = e;
                }
            }
        }
        if (this.addedClassLoaderDelegationOrder != null && !this.addedClassLoaderDelegationOrder.isEmpty()) {
            for (ClassLoader classLoader : this.addedClassLoaderDelegationOrder) {
                if (classLoader == classInfoClassLoader) continue;
                try {
                    return Class.forName(className, this.initializeLoadedClasses, classLoader);
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
                catch (LinkageError e) {
                    if (linkageError != null) continue;
                    linkageError = e;
                }
            }
        }
        if ((classfileResources = this.scanResult.getResourcesWithPath(JarUtils.classNameToClassfilePath(className))) != null) {
            for (Resource resource : classfileResources) {
                Resource resourceToClose = resource;
                try {
                    Class<?> clazz = this.defineClass(className, resourceToClose.read(), null);
                    if (resourceToClose != null) {
                        resourceToClose.close();
                    }
                    return clazz;
                }
                catch (Throwable throwable) {
                    try {
                        if (resourceToClose != null) {
                            try {
                                resourceToClose.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new ClassNotFoundException("Could not load classfile for class " + className + " : " + e);
                    }
                    catch (LinkageError e) {
                        if (linkageError != null) continue;
                        linkageError = e;
                    }
                }
            }
        }
        if (linkageError != null) {
            String string;
            if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows && (string = linkageError.getMessage()) != null) {
                String theWrongName;
                String wrongName = "(wrong name: ";
                int wrongNameIdx = string.indexOf("(wrong name: ");
                if (wrongNameIdx > -1 && (theWrongName = string.substring(wrongNameIdx + "(wrong name: ".length(), string.length() - 1)).replace('/', '.').equalsIgnoreCase(className)) {
                    throw new LinkageError("You appear to have two classfiles with the same case-insensitive name in the same directory on a case-insensitive filesystem -- this is not allowed on Windows, and therefore your code is not portable. Class name: " + className, linkageError);
                }
            }
            throw linkageError;
        }
        throw new ClassNotFoundException("Could not find or load classfile for class " + className);
    }

    public URL[] getURLs() {
        return this.scanResult.getClasspathURLs().toArray(new URL[0]);
    }

    @Override
    public URL getResource(String path) {
        ResourceList resourceList;
        URL resource;
        if (!this.environmentClassLoaderDelegationOrder.isEmpty()) {
            for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
                resource = envClassLoader.getResource(path);
                if (resource == null) continue;
                return resource;
            }
        }
        if (!this.addedClassLoaderDelegationOrder.isEmpty()) {
            for (ClassLoader additionalClassLoader : this.addedClassLoaderDelegationOrder) {
                resource = additionalClassLoader.getResource(path);
                if (resource == null) continue;
                return resource;
            }
        }
        if ((resourceList = this.scanResult.getResourcesWithPath(path)) == null || resourceList.isEmpty()) {
            return super.getResource(path);
        }
        return ((Resource)resourceList.get(0)).getURL();
    }

    @Override
    public Enumeration<URL> getResources(String path) throws IOException {
        ResourceList resourceList;
        Enumeration<URL> resources;
        if (!this.environmentClassLoaderDelegationOrder.isEmpty()) {
            for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
                resources = envClassLoader.getResources(path);
                if (resources == null || !resources.hasMoreElements()) continue;
                return resources;
            }
        }
        if (!this.addedClassLoaderDelegationOrder.isEmpty()) {
            for (ClassLoader additionalClassLoader : this.addedClassLoaderDelegationOrder) {
                resources = additionalClassLoader.getResources(path);
                if (resources == null || !resources.hasMoreElements()) continue;
                return resources;
            }
        }
        if ((resourceList = this.scanResult.getResourcesWithPath(path)) == null || resourceList.isEmpty()) {
            return Collections.emptyEnumeration();
        }
        return new Enumeration<URL>(){
            int idx;

            @Override
            public boolean hasMoreElements() {
                return this.idx < resourceList.size();
            }

            @Override
            public URL nextElement() {
                return ((Resource)resourceList.get(this.idx++)).getURL();
            }
        };
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        ResourceList resourceList;
        InputStream inputStream;
        if (!this.environmentClassLoaderDelegationOrder.isEmpty()) {
            for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
                inputStream = envClassLoader.getResourceAsStream(path);
                if (inputStream == null) continue;
                return inputStream;
            }
        }
        if (!this.addedClassLoaderDelegationOrder.isEmpty()) {
            for (ClassLoader additionalClassLoader : this.addedClassLoaderDelegationOrder) {
                inputStream = additionalClassLoader.getResourceAsStream(path);
                if (inputStream == null) continue;
                return inputStream;
            }
        }
        if ((resourceList = this.scanResult.getResourcesWithPath(path)) == null || resourceList.isEmpty()) {
            return super.getResourceAsStream(path);
        }
        try {
            return ((Resource)resourceList.get(0)).open();
        }
        catch (IOException e) {
            return null;
        }
    }
}

