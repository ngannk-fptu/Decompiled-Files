/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl;

import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.usercodedeployment.impl.ClassData;
import com.hazelcast.internal.usercodedeployment.impl.ClassSource;
import com.hazelcast.internal.usercodedeployment.impl.ClassloadingMutexProvider;
import com.hazelcast.internal.usercodedeployment.impl.ThreadLocalClassCache;
import com.hazelcast.internal.usercodedeployment.impl.operation.ClassDataFinderOperation;
import com.hazelcast.internal.util.filter.Filter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import java.io.Closeable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ClassLocator {
    private static final Pattern CLASS_PATTERN = Pattern.compile("^(.*)\\$.*");
    private final ConcurrentMap<String, ClassSource> classSourceMap;
    private final ConcurrentMap<String, ClassSource> clientClassSourceMap;
    private final ClassLoader parent;
    private final Filter<String> classNameFilter;
    private final Filter<Member> memberFilter;
    private final UserCodeDeploymentConfig.ClassCacheMode classCacheMode;
    private final NodeEngine nodeEngine;
    private final ClassloadingMutexProvider mutexFactory = new ClassloadingMutexProvider();
    private final ILogger logger;

    public ClassLocator(ConcurrentMap<String, ClassSource> classSourceMap, ConcurrentMap<String, ClassSource> clientClassSourceMap, ClassLoader parent, Filter<String> classNameFilter, Filter<Member> memberFilter, UserCodeDeploymentConfig.ClassCacheMode classCacheMode, NodeEngine nodeEngine) {
        this.classSourceMap = classSourceMap;
        this.clientClassSourceMap = clientClassSourceMap;
        this.parent = parent;
        this.classNameFilter = classNameFilter;
        this.memberFilter = memberFilter;
        this.classCacheMode = classCacheMode;
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(ClassLocator.class);
    }

    public static void onStartDeserialization() {
        ThreadLocalClassCache.onStartDeserialization();
    }

    public static void onFinishDeserialization() {
        ThreadLocalClassCache.onFinishDeserialization();
    }

    public Class<?> handleClassNotFoundException(String name) throws ClassNotFoundException {
        if (!this.classNameFilter.accept(name)) {
            throw new ClassNotFoundException("Class " + name + " is not allowed to be loaded from other members");
        }
        Class<?> clazz = this.tryToGetClassFromLocalCache(name);
        if (clazz != null) {
            return clazz;
        }
        return this.tryToGetClassFromRemote(name);
    }

    public void defineClassesFromClient(List<Map.Entry<String, byte[]>> bundledClassDefinitions) {
        HashMap<String, byte[]> bundledClassDefMap = new HashMap<String, byte[]>();
        for (Map.Entry<String, byte[]> bundledClassDefinition : bundledClassDefinitions) {
            bundledClassDefMap.put(bundledClassDefinition.getKey(), bundledClassDefinition.getValue());
        }
        for (Map.Entry<String, byte[]> bundledClassDefinition : bundledClassDefinitions) {
            this.defineClassFromClient(bundledClassDefinition.getKey(), bundledClassDefinition.getValue(), bundledClassDefMap);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public Class<?> defineClassFromClient(String name, byte[] classDef, final Map<String, byte[]> bundledClassDefMap) {
        ClassSource classSource;
        Closeable classMutex;
        block9: {
            String mainClassName;
            block8: {
                Class clazz;
                mainClassName = ClassLocator.extractMainClassName(name);
                classMutex = this.mutexFactory.getMutexForClass(mainClassName);
                try {
                    Closeable closeable = classMutex;
                    // MONITORENTER : closeable
                    classSource = (ClassSource)this.clientClassSourceMap.get(mainClassName);
                    if (classSource == null) break block8;
                    if (classSource.getClazz(name) == null) break block9;
                    if (!Arrays.equals(classDef, classSource.getClassDefinition(name))) {
                        throw new IllegalStateException("Class " + name + " is already in local cache and has conflicting byte code representation");
                    }
                    if (this.logger.isFineEnabled()) {
                        this.logger.finest("Class " + name + " is already in local cache with equal byte code");
                    }
                    clazz = classSource.getClazz(name);
                    // MONITOREXIT : closeable
                }
                catch (Throwable throwable) {
                    IOUtil.closeResource(classMutex);
                    throw throwable;
                }
                IOUtil.closeResource(classMutex);
                return clazz;
            }
            classSource = AccessController.doPrivileged(new PrivilegedAction<ClassSource>(){

                @Override
                public ClassSource run() {
                    return new ClassSource(ClassLocator.this.parent, ClassLocator.this, bundledClassDefMap);
                }
            });
            this.clientClassSourceMap.put(mainClassName, classSource);
        }
        Class<?> clazz = classSource.define(name, classDef);
        // MONITOREXIT : closeable
        IOUtil.closeResource(classMutex);
        return clazz;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private Class<?> tryToGetClassFromRemote(String name) throws ClassNotFoundException {
        ClassData classData;
        ClassSource classSource;
        Closeable classMutex;
        String mainClassName;
        block11: {
            block10: {
                Class clazz;
                mainClassName = ClassLocator.extractMainClassName(name);
                classMutex = this.mutexFactory.getMutexForClass(mainClassName);
                try {
                    Closeable closeable = classMutex;
                    // MONITORENTER : closeable
                    classSource = (ClassSource)this.classSourceMap.get(mainClassName);
                    if (classSource == null) break block10;
                    Class clazz2 = classSource.getClazz(name);
                    if (clazz2 == null) break block11;
                    if (this.logger.isFineEnabled()) {
                        this.logger.finest("Class " + name + " is already in local cache");
                    }
                    clazz = clazz2;
                    // MONITOREXIT : closeable
                }
                catch (Throwable throwable) {
                    IOUtil.closeResource(classMutex);
                    throw throwable;
                }
                IOUtil.closeResource(classMutex);
                return clazz;
            }
            classSource = ThreadLocalClassCache.getFromCache(mainClassName) != null ? ThreadLocalClassCache.getFromCache(mainClassName) : AccessController.doPrivileged(new PrivilegedAction<ClassSource>(){

                @Override
                public ClassSource run() {
                    return new ClassSource(ClassLocator.this.parent, ClassLocator.this, Collections.emptyMap());
                }
            });
        }
        if ((classData = this.fetchBytecodeFromRemote(mainClassName)) == null) {
            throw new ClassNotFoundException("Failed to load class " + name + " from other members");
        }
        Map<String, byte[]> innerClassDefinitions = classData.getInnerClassDefinitions();
        classSource.define(mainClassName, classData.getMainClassDefinition());
        Object object = innerClassDefinitions.entrySet().iterator();
        while (true) {
            if (!object.hasNext()) {
                this.cacheClass(classSource, mainClassName);
                object = classSource.getClazz(name);
                // MONITOREXIT : closeable
                IOUtil.closeResource(classMutex);
                return object;
            }
            Map.Entry<String, byte[]> entry = object.next();
            classSource.define(entry.getKey(), entry.getValue());
        }
    }

    private Class<?> tryToGetClassFromLocalCache(String name) {
        Class clazz;
        String mainClassDefinition = ClassLocator.extractMainClassName(name);
        ClassSource classSource = (ClassSource)this.classSourceMap.get(mainClassDefinition);
        if (classSource != null && (clazz = classSource.getClazz(name)) != null) {
            if (this.logger.isFineEnabled()) {
                this.logger.finest("Class " + name + " is already in local cache");
            }
            return clazz;
        }
        classSource = (ClassSource)this.clientClassSourceMap.get(mainClassDefinition);
        if (classSource != null && (clazz = classSource.getClazz(name)) != null) {
            if (this.logger.isFineEnabled()) {
                this.logger.finest("Class " + name + " is already in local cache");
            }
            return clazz;
        }
        classSource = ThreadLocalClassCache.getFromCache(mainClassDefinition);
        if (classSource != null) {
            return classSource.getClazz(name);
        }
        return null;
    }

    static String extractMainClassName(String className) {
        Matcher matcher = CLASS_PATTERN.matcher(className);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return className;
    }

    private void cacheClass(ClassSource classSource, String outerClassName) {
        if (this.classCacheMode != UserCodeDeploymentConfig.ClassCacheMode.OFF) {
            this.classSourceMap.put(outerClassName, classSource);
        } else {
            ThreadLocalClassCache.store(outerClassName, classSource);
        }
    }

    private ClassData fetchBytecodeFromRemote(String className) {
        ClusterService cluster = this.nodeEngine.getClusterService();
        boolean interrupted = false;
        for (Member member : cluster.getMembers()) {
            if (this.isCandidateMember(member)) continue;
            try {
                ClassData classData = this.tryToFetchClassDataFromMember(className, member);
                if (classData == null) continue;
                if (this.logger.isFineEnabled()) {
                    this.logger.finest("Loaded class " + className + " from " + member);
                }
                return classData;
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
            catch (Exception e) {
                if (!this.logger.isFinestEnabled()) continue;
                this.logger.finest("Unable to get class data for class " + className + " from member " + member, e);
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private ClassData tryToFetchClassDataFromMember(String className, Member member) throws ExecutionException, InterruptedException {
        OperationService operationService = this.nodeEngine.getOperationService();
        ClassDataFinderOperation op = new ClassDataFinderOperation(className);
        InternalCompletableFuture classDataFuture = operationService.invokeOnTarget("user-code-deployment-service", op, member.getAddress());
        return (ClassData)classDataFuture.get();
    }

    private boolean isCandidateMember(Member member) {
        if (member.localMember()) {
            return true;
        }
        return !this.memberFilter.accept(member);
    }

    public Class<?> findLoadedClass(String name) {
        ClassSource classSource = (ClassSource)this.classSourceMap.get(ClassLocator.extractMainClassName(name));
        if (classSource == null) {
            return null;
        }
        return classSource.getClazz(name);
    }
}

