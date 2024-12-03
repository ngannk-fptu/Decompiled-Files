/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.finder;

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public interface ClassFinder {
    public boolean isAnnotationPresent(Class<? extends Annotation> var1);

    public List<String> getClassesNotLoaded();

    public List<Package> findAnnotatedPackages(Class<? extends Annotation> var1);

    public List<Class<?>> findAnnotatedClasses(Class<? extends Annotation> var1);

    public List<Method> findAnnotatedMethods(Class<? extends Annotation> var1);

    public List<Constructor<?>> findAnnotatedConstructors(Class<? extends Annotation> var1);

    public List<Field> findAnnotatedFields(Class<? extends Annotation> var1);

    public List<Class<?>> findClassesInPackage(String var1, boolean var2);

    public List<Class<?>> findClasses(Test<ClassInfo> var1);

    public List<Class<?>> findClasses();

    public ClassLoaderInterface getClassLoaderInterface();

    public static class FieldInfo
    extends Annotatable
    implements Info {
        private final String name;
        private final String type;
        private final ClassInfo declaringClass;

        public FieldInfo(ClassInfo info, Field field) {
            super(field);
            this.declaringClass = info;
            this.name = field.getName();
            this.type = field.getType().getName();
        }

        public FieldInfo(ClassInfo declaringClass, String name, String type) {
            this.declaringClass = declaringClass;
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public ClassInfo getDeclaringClass() {
            return this.declaringClass;
        }

        public String getType() {
            return this.type;
        }

        public String toString() {
            return this.declaringClass + "#" + this.name;
        }
    }

    public static class MethodInfo
    extends Annotatable
    implements Info {
        private final ClassInfo declaringClass;
        private final String returnType;
        private final String name;
        private final List<List<AnnotationInfo>> parameterAnnotations = new ArrayList<List<AnnotationInfo>>();

        public MethodInfo(ClassInfo info, Constructor<?> constructor) {
            super(constructor);
            this.declaringClass = info;
            this.name = "<init>";
            this.returnType = Void.TYPE.getName();
        }

        public MethodInfo(ClassInfo info, Method method) {
            super(method);
            this.declaringClass = info;
            this.name = method.getName();
            this.returnType = method.getReturnType().getName();
        }

        public MethodInfo(ClassInfo declarignClass, String name, String returnType) {
            this.declaringClass = declarignClass;
            this.name = name;
            this.returnType = returnType;
        }

        public List<List<AnnotationInfo>> getParameterAnnotations() {
            return this.parameterAnnotations;
        }

        public List<AnnotationInfo> getParameterAnnotations(int index) {
            if (index >= this.parameterAnnotations.size()) {
                for (int i = this.parameterAnnotations.size(); i <= index; ++i) {
                    ArrayList annotationInfos = new ArrayList();
                    this.parameterAnnotations.add(i, annotationInfos);
                }
            }
            return this.parameterAnnotations.get(index);
        }

        @Override
        public String getName() {
            return this.name;
        }

        public ClassInfo getDeclaringClass() {
            return this.declaringClass;
        }

        public String getReturnType() {
            return this.returnType;
        }

        public String toString() {
            return this.declaringClass + "@" + this.name;
        }
    }

    public static class ClassInfo
    extends Annotatable
    implements Info {
        private final String name;
        private final List<MethodInfo> methods = new ArrayList<MethodInfo>();
        private final List<MethodInfo> constructors = new ArrayList<MethodInfo>();
        private final String superType;
        private final List<String> interfaces = new ArrayList<String>();
        private final List<String> superInterfaces = new ArrayList<String>();
        private final List<FieldInfo> fields = new ArrayList<FieldInfo>();
        private final ClassFinder classFinder;
        private Class<?> clazz;
        private ClassNotFoundException notFound;

        public ClassInfo(Class<?> clazz, ClassFinder classFinder) {
            super(clazz);
            this.clazz = clazz;
            this.classFinder = classFinder;
            this.name = clazz.getName();
            Class<?> superclass = clazz.getSuperclass();
            this.superType = superclass != null ? superclass.getName() : null;
        }

        public ClassInfo(String name, String superType, ClassFinder classFinder) {
            this.name = name;
            this.superType = superType;
            this.classFinder = classFinder;
        }

        public String getPackageName() {
            return this.name.indexOf(46) > 0 ? this.name.substring(0, this.name.lastIndexOf(46)) : "";
        }

        public List<MethodInfo> getConstructors() {
            return this.constructors;
        }

        public List<String> getInterfaces() {
            return this.interfaces;
        }

        public List<String> getSuperInterfaces() {
            return this.superInterfaces;
        }

        public List<FieldInfo> getFields() {
            return this.fields;
        }

        public List<MethodInfo> getMethods() {
            return this.methods;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public String getSuperType() {
            return this.superType;
        }

        public Class<?> get() throws ClassNotFoundException {
            if (this.clazz != null) {
                return this.clazz;
            }
            if (this.notFound != null) {
                throw this.notFound;
            }
            try {
                this.clazz = this.classFinder.getClassLoaderInterface().loadClass(this.name);
                return this.clazz;
            }
            catch (ClassNotFoundException notFound) {
                this.classFinder.getClassesNotLoaded().add(this.name);
                this.notFound = notFound;
                throw notFound;
            }
        }

        public String toString() {
            return this.name;
        }
    }

    public static class PackageInfo
    extends Annotatable
    implements Info {
        private final String name;
        private final ClassInfo info;
        private final Package pkg;

        public PackageInfo(Package pkg) {
            super(pkg);
            this.pkg = pkg;
            this.name = pkg.getName();
            this.info = null;
        }

        public PackageInfo(String name, ClassFinder classFinder) {
            this.info = new ClassInfo(name, null, classFinder);
            this.name = name;
            this.pkg = null;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public Package get() throws ClassNotFoundException {
            return this.pkg != null ? this.pkg : this.info.get().getPackage();
        }
    }

    public static class Annotatable {
        private final List<AnnotationInfo> annotations = new ArrayList<AnnotationInfo>();

        public Annotatable(AnnotatedElement element) {
            for (Annotation annotation : element.getAnnotations()) {
                this.annotations.add(new AnnotationInfo(annotation.annotationType().getName()));
            }
        }

        public Annotatable() {
        }

        public List<AnnotationInfo> getAnnotations() {
            return this.annotations;
        }
    }

    public static class AnnotationInfo
    extends Annotatable
    implements Info {
        private final String name;

        public AnnotationInfo(Annotation annotation) {
            this(annotation.getClass().getName());
        }

        public AnnotationInfo(Class<? extends Annotation> annotation) {
            this.name = annotation.getName().intern();
        }

        public AnnotationInfo(String name) {
            name = name.replaceAll("^L|;$", "");
            name = name.replace('/', '.');
            this.name = name.intern();
        }

        @Override
        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }

    public static interface Info {
        public String getName();

        public List<AnnotationInfo> getAnnotations();
    }
}

