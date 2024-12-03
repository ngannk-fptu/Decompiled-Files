/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.scanning;

import com.sun.jersey.core.osgi.OsgiRegistry;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import jersey.repackaged.org.objectweb.asm.AnnotationVisitor;
import jersey.repackaged.org.objectweb.asm.Attribute;
import jersey.repackaged.org.objectweb.asm.ClassReader;
import jersey.repackaged.org.objectweb.asm.ClassVisitor;
import jersey.repackaged.org.objectweb.asm.FieldVisitor;
import jersey.repackaged.org.objectweb.asm.MethodVisitor;

public class AnnotationScannerListener
implements ScannerListener {
    private final ClassLoader classloader;
    private final Set<Class<?>> classes;
    private final Set<String> annotations;
    private final AnnotatedClassVisitor classVisitor;

    public AnnotationScannerListener(Class<? extends Annotation> ... annotations) {
        this(AccessController.doPrivileged(ReflectionHelper.getContextClassLoaderPA()), annotations);
    }

    public AnnotationScannerListener(ClassLoader classloader, Class<? extends Annotation> ... annotations) {
        this.classloader = classloader;
        this.classes = new LinkedHashSet();
        this.annotations = this.getAnnotationSet(annotations);
        this.classVisitor = new AnnotatedClassVisitor();
    }

    public Set<Class<?>> getAnnotatedClasses() {
        return this.classes;
    }

    private Set<String> getAnnotationSet(Class<? extends Annotation> ... annotations) {
        HashSet<String> a = new HashSet<String>();
        for (Class<? extends Annotation> c : annotations) {
            a.add("L" + c.getName().replaceAll("\\.", "/") + ";");
        }
        return a;
    }

    @Override
    public boolean onAccept(String name) {
        return name.endsWith(".class");
    }

    @Override
    public void onProcess(String name, InputStream in) throws IOException {
        new ClassReader(in).accept(this.classVisitor, 0);
    }

    private final class AnnotatedClassVisitor
    extends ClassVisitor {
        private String className;
        private boolean isScoped;
        private boolean isAnnotated;

        private AnnotatedClassVisitor() {
            super(327680);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            this.isScoped = (access & 1) != 0;
            this.isAnnotated = false;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            this.isAnnotated |= AnnotationScannerListener.this.annotations.contains(desc);
            return null;
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            if (this.className.equals(name)) {
                this.isScoped = (access & 1) != 0;
                this.isScoped &= (access & 8) == 8;
            }
        }

        @Override
        public void visitEnd() {
            if (this.isScoped && this.isAnnotated) {
                AnnotationScannerListener.this.classes.add(this.getClassForName(this.className.replaceAll("/", ".")));
            }
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
            return null;
        }

        private Class getClassForName(String className) {
            try {
                OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
                if (osgiRegistry != null) {
                    return osgiRegistry.classForNameWithException(className);
                }
                return AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(className, AnnotationScannerListener.this.classloader));
            }
            catch (ClassNotFoundException ex) {
                String s = "A class file of the class name, " + className + "is identified but the class could not be found";
                throw new RuntimeException(s, ex);
            }
            catch (PrivilegedActionException ex) {
                String s = "A class file of the class name, " + className + "is identified but the class could not be found";
                throw new RuntimeException(s, ex);
            }
        }
    }
}

