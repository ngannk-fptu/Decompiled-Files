/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.module.scanner;

import com.atlassian.plugins.rest.module.scanner.JarIndexerException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarIndexer {
    public static final String THRESHOLD_ENTRIES_PROPERTY = "rest.annotation.scanner.jar.indexer.threshold.limit";
    private static final int THRESHOLD_ENTRIES = Integer.parseInt(System.getProperty("rest.annotation.scanner.jar.indexer.threshold.limit", "50000"));
    private static final Logger LOGGER = LoggerFactory.getLogger(JarIndexer.class);
    private final JarFile jar;
    private final Set<String> packageNames;
    private final boolean indexBundledJars;
    private final Set<String> annotations;
    private final Bundle bundle;

    JarIndexer(JarFile jar, Set<String> packageNames, boolean indexBundledJars, Set<String> annotations, Bundle bundle2) {
        this.jar = jar;
        this.packageNames = packageNames;
        this.indexBundledJars = indexBundledJars;
        this.annotations = annotations;
        this.bundle = bundle2;
    }

    public Set<Class<?>> scanJar() {
        HashSet classes = new HashSet();
        int totalEntryArchive = 0;
        Enumeration<JarEntry> entries = this.jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry;
            if (!this.isJarEntryThresholdSafe(++totalEntryArchive)) {
                LOGGER.warn("Archive {} exceeds a threshold of {} entries, which can lead to inodes exhaustion of the system", (Object)this.jar.getName(), (Object)THRESHOLD_ENTRIES);
                totalEntryArchive = Integer.MIN_VALUE;
            }
            if (this.isClassFile(jarEntry = entries.nextElement()) && this.shouldBeScanned(jarEntry)) {
                classes.add(this.analyzeClassFile(this.jar, jarEntry));
                continue;
            }
            if (!this.isJarFile(jarEntry) || !this.indexBundledJars) continue;
            classes.addAll(this.scanDeeply(jarEntry));
        }
        classes.remove(null);
        return classes;
    }

    private Class<?> analyzeClassFile(JarFile jarFile, JarEntry entry) {
        AnnotatedClassVisitor visitor = new AnnotatedClassVisitor(this.annotations);
        this.getClassReader(jarFile, entry).accept(visitor, 0);
        return visitor.hasAnnotation() ? this.getClassForName(visitor.className) : null;
    }

    private Class<?> getClassForName(String className) {
        try {
            return this.bundle.loadClass(className.replace("/", "."));
        }
        catch (ClassNotFoundException ex) {
            throw new JarIndexerException("A class file of the class name, " + className + " is identified but the class could not be loaded", ex);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private ClassReader getClassReader(JarFile jarFile, JarEntry entry) {
        try (InputStream is = jarFile.getInputStream(entry);){
            ClassReader classReader = new ClassReader(is);
            return classReader;
        }
        catch (IOException | IllegalArgumentException ex) {
            throw new JarIndexerException("Error accessing input stream of the jar file " + jarFile.getName(), ex);
        }
    }

    /*
     * Exception decompiling
     */
    private Set<Class<?>> scanDeeply(JarEntry jarEntry) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 4 blocks at once
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

    private File extractIsToTempFile(String fileName, InputStream fileInputStream) {
        try {
            File f = File.createTempFile(fileName, ".jar");
            FileUtils.copyInputStreamToFile((InputStream)fileInputStream, (File)f);
            return f;
        }
        catch (IOException e) {
            throw new JarIndexerException("An error has occurred while extracting from jar input stream.", e);
        }
    }

    private boolean isClassFile(JarEntry jarEntry) {
        return !jarEntry.isDirectory() && jarEntry.getName().endsWith(".class") && !jarEntry.getName().endsWith("module-info.class");
    }

    private boolean isJarFile(JarEntry jarEntry) {
        return !jarEntry.isDirectory() && jarEntry.getName().endsWith(".jar");
    }

    private boolean shouldBeScanned(JarEntry jarEntry) {
        return this.packageNames.isEmpty() || this.packageNames.stream().anyMatch(packageName -> jarEntry.getName().startsWith((String)packageName));
    }

    private boolean isJarEntryThresholdSafe(int totalEntryArchive) {
        return totalEntryArchive <= THRESHOLD_ENTRIES;
    }

    private static final class AnnotatedClassVisitor
    extends ClassVisitor {
        private final Set<String> annotations;
        private String className;
        private boolean isScoped;
        private boolean isAnnotated;

        public AnnotatedClassVisitor(Set<String> annotations) {
            super(458752);
            this.annotations = annotations;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            this.isScoped = (access & 1) != 0;
            this.isAnnotated = false;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            this.isAnnotated |= this.annotations.contains(desc);
            return null;
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            if (this.className.equals(name)) {
                this.isScoped = (access & 1) != 0;
                this.isScoped &= (access & 8) == 8;
            }
        }

        boolean hasAnnotation() {
            return this.isScoped && this.isAnnotated;
        }

        @Override
        public void visitEnd() {
        }

        @Override
        public void visitOuterClass(String string, String string0, String string1) {
        }

        @Override
        public FieldVisitor visitField(int i, String string, String string0, String string1, Object object) {
            return null;
        }

        @Override
        public void visitSource(String string, String string0) {
        }

        @Override
        public void visitAttribute(Attribute attribute) {
        }

        @Override
        public MethodVisitor visitMethod(int i, String string, String string0, String string1, String[] string2) {
            if (this.isAnnotated) {
                return null;
            }
            return new MethodVisitor(327680){

                @Override
                public AnnotationVisitor visitAnnotationDefault() {
                    return null;
                }

                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                    isAnnotated = isAnnotated | annotations.contains(desc);
                    return null;
                }

                @Override
                public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
                    return null;
                }

                @Override
                public void visitAttribute(Attribute attr) {
                }

                @Override
                public void visitCode() {
                }

                @Override
                public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
                }

                @Override
                public void visitInsn(int opcode) {
                }

                @Override
                public void visitIntInsn(int opcode, int operand) {
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                }

                @Override
                public void visitTypeInsn(int opcode, String type) {
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                }

                @Override
                public void visitJumpInsn(int opcode, Label label) {
                }

                @Override
                public void visitLabel(Label label) {
                }

                @Override
                public void visitLdcInsn(Object cst) {
                }

                @Override
                public void visitIincInsn(int var, int increment) {
                }

                @Override
                public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
                }

                @Override
                public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
                }

                @Override
                public void visitMultiANewArrayInsn(String desc, int dims) {
                }

                @Override
                public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                }

                @Override
                public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                }

                @Override
                public void visitLineNumber(int line, Label start) {
                }

                @Override
                public void visitMaxs(int maxStack, int maxLocals) {
                }

                @Override
                public void visitEnd() {
                }
            };
        }
    }
}

