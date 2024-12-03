/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl;

import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.internal.usercodedeployment.impl.ClassData;
import com.hazelcast.internal.usercodedeployment.impl.ClassLocator;
import com.hazelcast.internal.usercodedeployment.impl.ClassSource;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.EmptyStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public final class ClassDataProvider {
    private final UserCodeDeploymentConfig.ProviderMode providerMode;
    private final ClassLoader parent;
    private final ConcurrentMap<String, ClassSource> classSourceMap;
    private final ConcurrentMap<String, ClassSource> clientClassSourceMap;
    private final ILogger logger;

    public ClassDataProvider(UserCodeDeploymentConfig.ProviderMode providerMode, ClassLoader parent, ConcurrentMap<String, ClassSource> classSourceMap, ConcurrentMap<String, ClassSource> clientClassSourceMap, ILogger logger) {
        this.providerMode = providerMode;
        this.parent = parent;
        this.classSourceMap = classSourceMap;
        this.clientClassSourceMap = clientClassSourceMap;
        this.logger = logger;
    }

    public ClassData getClassDataOrNull(String className) {
        ClassData classData = this.loadBytecodesFromClientCache(className);
        if (classData != null) {
            return classData;
        }
        if (this.providerMode == UserCodeDeploymentConfig.ProviderMode.OFF) {
            return null;
        }
        classData = this.loadBytecodesFromParent(className);
        if (classData == null && this.providerMode == UserCodeDeploymentConfig.ProviderMode.LOCAL_AND_CACHED_CLASSES) {
            classData = this.loadBytecodesFromCache(className);
        }
        return classData;
    }

    private ClassData loadBytecodesFromCache(String className) {
        ClassSource classSource = (ClassSource)this.classSourceMap.get(ClassLocator.extractMainClassName(className));
        if (classSource == null) {
            return null;
        }
        return classSource.getClassData(className);
    }

    private ClassData loadBytecodesFromClientCache(String className) {
        ClassSource classSource = (ClassSource)this.clientClassSourceMap.get(ClassLocator.extractMainClassName(className));
        if (classSource == null) {
            return null;
        }
        return classSource.getClassData(className);
    }

    private ClassData loadBytecodesFromParent(String className) {
        byte[] mainClassDefinition = this.loadBytecodeFromParent(className);
        if (mainClassDefinition == null) {
            return null;
        }
        Map<String, byte[]> innerClassDefinitions = this.loadInnerClasses(className);
        innerClassDefinitions = this.loadAnonymousClasses(className, innerClassDefinitions);
        ClassData classData = new ClassData();
        if (innerClassDefinitions != null) {
            classData.setInnerClassDefinitions(innerClassDefinitions);
        }
        classData.setMainClassDefinition(mainClassDefinition);
        return classData;
    }

    private Map<String, byte[]> loadAnonymousClasses(String className, Map<String, byte[]> innerClassDefinitions) {
        int i = 1;
        try {
            while (true) {
                String innerClassName = className + "$" + i;
                this.parent.loadClass(innerClassName);
                byte[] innerByteCode = this.loadBytecodeFromParent(innerClassName);
                if (innerClassDefinitions == null) {
                    innerClassDefinitions = new HashMap<String, byte[]>();
                }
                innerClassDefinitions.put(innerClassName, innerByteCode);
                ++i;
            }
        }
        catch (ClassNotFoundException e) {
            return innerClassDefinitions;
        }
    }

    private Map<String, byte[]> loadInnerClasses(String className) {
        HashMap<String, byte[]> innerClassDefinitions = null;
        try {
            Class<?>[] declaredClasses;
            Class<?> aClass = this.parent.loadClass(className);
            for (Class<?> declaredClass : declaredClasses = aClass.getDeclaredClasses()) {
                String innerClassName = declaredClass.getName();
                byte[] innerByteCode = this.loadBytecodeFromParent(innerClassName);
                if (innerClassDefinitions == null) {
                    innerClassDefinitions = new HashMap<String, byte[]>();
                }
                innerClassDefinitions.put(innerClassName, innerByteCode);
            }
        }
        catch (ClassNotFoundException e) {
            EmptyStatement.ignore(e);
        }
        return innerClassDefinitions;
    }

    /*
     * Exception decompiling
     */
    private byte[] loadBytecodeFromParent(String className) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

