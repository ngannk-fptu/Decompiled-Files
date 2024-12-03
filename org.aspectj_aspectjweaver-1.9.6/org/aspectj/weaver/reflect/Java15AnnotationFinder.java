/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import org.aspectj.apache.bcel.classfile.AnnotationDefault;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.LocalVariable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.util.ClassLoaderRepository;
import org.aspectj.apache.bcel.util.NonCachingClassLoaderRepository;
import org.aspectj.apache.bcel.util.Repository;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelWeakClassLoaderReference;
import org.aspectj.weaver.reflect.AnnotationFinder;
import org.aspectj.weaver.reflect.ArgNameFinder;

public class Java15AnnotationFinder
implements AnnotationFinder,
ArgNameFinder {
    public static final ResolvedType[][] NO_PARAMETER_ANNOTATIONS = new ResolvedType[0][];
    private Repository bcelRepository;
    private BcelWeakClassLoaderReference classLoaderRef;
    private World world;
    private static boolean useCachingClassLoaderRepository;

    @Override
    public void setClassLoader(ClassLoader aLoader) {
        this.classLoaderRef = new BcelWeakClassLoaderReference(aLoader);
        this.bcelRepository = useCachingClassLoaderRepository ? new ClassLoaderRepository(this.classLoaderRef) : new NonCachingClassLoaderRepository(this.classLoaderRef);
    }

    @Override
    public void setWorld(World aWorld) {
        this.world = aWorld;
    }

    @Override
    public Object getAnnotation(ResolvedType annotationType, Object onObject) {
        try {
            Class<?> annotationClass = Class.forName(annotationType.getName(), false, this.getClassLoader());
            if (onObject.getClass().isAnnotationPresent(annotationClass)) {
                return onObject.getClass().getAnnotation(annotationClass);
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return null;
    }

    public Object getAnnotationFromClass(ResolvedType annotationType, Class aClass) {
        try {
            Class<?> annotationClass = Class.forName(annotationType.getName(), false, this.getClassLoader());
            if (aClass.isAnnotationPresent(annotationClass)) {
                return aClass.getAnnotation(annotationClass);
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public Object getAnnotationFromMember(ResolvedType annotationType, Member aMember) {
        if (!(aMember instanceof AccessibleObject)) {
            return null;
        }
        AccessibleObject ao = (AccessibleObject)((Object)aMember);
        try {
            Class<?> annotationClass = Class.forName(annotationType.getName(), false, this.getClassLoader());
            if (ao.isAnnotationPresent(annotationClass)) {
                return ao.getAnnotation(annotationClass);
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return null;
    }

    private ClassLoader getClassLoader() {
        return this.classLoaderRef.getClassLoader();
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType, Member onMember) {
        if (!(onMember instanceof AccessibleObject)) {
            return null;
        }
        try {
            JavaClass jc = this.bcelRepository.loadClass(onMember.getDeclaringClass());
            AnnotationGen[] anns = new AnnotationGen[]{};
            if (onMember instanceof java.lang.reflect.Method) {
                Method bcelMethod = jc.getMethod((java.lang.reflect.Method)onMember);
                if (bcelMethod != null) {
                    anns = bcelMethod.getAnnotations();
                }
            } else if (onMember instanceof Constructor) {
                Method bcelCons = jc.getMethod((Constructor)onMember);
                anns = bcelCons.getAnnotations();
            } else if (onMember instanceof java.lang.reflect.Field) {
                Field bcelField = jc.getField((java.lang.reflect.Field)onMember);
                anns = bcelField.getAnnotations();
            }
            this.bcelRepository.clear();
            if (anns == null) {
                anns = new AnnotationGen[]{};
            }
            for (int i = 0; i < anns.length; ++i) {
                if (!anns[i].getTypeSignature().equals(ofType.getSignature())) continue;
                return new BcelAnnotation(anns[i], this.world);
            }
            return null;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    @Override
    public String getAnnotationDefaultValue(Member onMember) {
        try {
            Method bcelMethod;
            JavaClass jc = this.bcelRepository.loadClass(onMember.getDeclaringClass());
            if (onMember instanceof java.lang.reflect.Method && (bcelMethod = jc.getMethod((java.lang.reflect.Method)onMember)) != null) {
                Attribute[] attrs = bcelMethod.getAttributes();
                for (int i = 0; i < attrs.length; ++i) {
                    Attribute attribute = attrs[i];
                    if (!attribute.getName().equals("AnnotationDefault")) continue;
                    AnnotationDefault def = (AnnotationDefault)attribute;
                    return def.getElementValue().stringifyValue();
                }
                return null;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public ResolvedType[] getAnnotations(Member onMember, boolean areRuntimeAnnotationsSufficient) {
        AccessibleObject ao;
        Annotation[] anns;
        if (!(onMember instanceof AccessibleObject)) {
            return ResolvedType.NONE;
        }
        if (!areRuntimeAnnotationsSufficient) {
            try {
                JavaClass jc = this.bcelRepository.loadClass(onMember.getDeclaringClass());
                AnnotationGen[] anns2 = null;
                if (onMember instanceof java.lang.reflect.Method) {
                    Method bcelMethod = jc.getMethod((java.lang.reflect.Method)onMember);
                    if (bcelMethod != null) {
                        anns2 = bcelMethod.getAnnotations();
                    }
                } else if (onMember instanceof Constructor) {
                    Method bcelCons = jc.getMethod((Constructor)onMember);
                    anns2 = bcelCons.getAnnotations();
                } else if (onMember instanceof java.lang.reflect.Field) {
                    Field bcelField = jc.getField((java.lang.reflect.Field)onMember);
                    anns2 = bcelField.getAnnotations();
                }
                this.bcelRepository.clear();
                if (anns2 == null || anns2.length == 0) {
                    return ResolvedType.NONE;
                }
                ResolvedType[] annotationTypes = new ResolvedType[anns2.length];
                for (int i = 0; i < anns2.length; ++i) {
                    annotationTypes[i] = this.world.resolve(UnresolvedType.forSignature(anns2[i].getTypeSignature()));
                }
                return annotationTypes;
            }
            catch (ClassNotFoundException jc) {
                // empty catch block
            }
        }
        if ((anns = (ao = (AccessibleObject)((Object)onMember)).getDeclaredAnnotations()).length == 0) {
            return ResolvedType.NONE;
        }
        ResolvedType[] annotationTypes = new ResolvedType[anns.length];
        for (int i = 0; i < anns.length; ++i) {
            annotationTypes[i] = UnresolvedType.forName(anns[i].annotationType().getName()).resolve(this.world);
        }
        return annotationTypes;
    }

    public ResolvedType[] getAnnotations(Class forClass, World inWorld) {
        try {
            JavaClass jc = this.bcelRepository.loadClass(forClass);
            AnnotationGen[] anns = jc.getAnnotations();
            this.bcelRepository.clear();
            if (anns == null) {
                return ResolvedType.NONE;
            }
            ResolvedType[] ret = new ResolvedType[anns.length];
            for (int i = 0; i < ret.length; ++i) {
                ret[i] = inWorld.resolve(UnresolvedType.forSignature(anns[i].getTypeSignature()));
            }
            return ret;
        }
        catch (ClassNotFoundException jc) {
            Annotation[] classAnnotations = forClass.getAnnotations();
            ResolvedType[] ret = new ResolvedType[classAnnotations.length];
            for (int i = 0; i < classAnnotations.length; ++i) {
                ret[i] = inWorld.resolve(classAnnotations[i].annotationType().getName());
            }
            return ret;
        }
    }

    @Override
    public String[] getParameterNames(Member forMember) {
        if (!(forMember instanceof AccessibleObject)) {
            return null;
        }
        try {
            JavaClass jc = this.bcelRepository.loadClass(forMember.getDeclaringClass());
            LocalVariableTable lvt = null;
            int numVars = 0;
            if (forMember instanceof java.lang.reflect.Method) {
                Method bcelMethod = jc.getMethod((java.lang.reflect.Method)forMember);
                lvt = bcelMethod.getLocalVariableTable();
                numVars = bcelMethod.getArgumentTypes().length;
            } else if (forMember instanceof Constructor) {
                Method bcelCons = jc.getMethod((Constructor)forMember);
                lvt = bcelCons.getLocalVariableTable();
                numVars = bcelCons.getArgumentTypes().length;
            }
            return this.getParameterNamesFromLVT(lvt, numVars);
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    private String[] getParameterNamesFromLVT(LocalVariableTable lvt, int numVars) {
        if (lvt == null) {
            return null;
        }
        LocalVariable[] vars = lvt.getLocalVariableTable();
        if (vars.length < numVars) {
            return null;
        }
        String[] ret = new String[numVars];
        for (int i = 0; i < numVars; ++i) {
            ret[i] = vars[i + 1].getName();
        }
        return ret;
    }

    @Override
    public ResolvedType[][] getParameterAnnotationTypes(Member onMember) {
        if (!(onMember instanceof AccessibleObject)) {
            return NO_PARAMETER_ANNOTATIONS;
        }
        try {
            JavaClass jc = this.bcelRepository.loadClass(onMember.getDeclaringClass());
            AnnotationGen[][] anns = null;
            if (onMember instanceof java.lang.reflect.Method) {
                Method bcelMethod = jc.getMethod((java.lang.reflect.Method)onMember);
                if (bcelMethod != null) {
                    anns = bcelMethod.getParameterAnnotations();
                }
            } else if (onMember instanceof Constructor) {
                Method bcelCons = jc.getMethod((Constructor)onMember);
                anns = bcelCons.getParameterAnnotations();
            } else if (onMember instanceof java.lang.reflect.Field) {
                // empty if block
            }
            this.bcelRepository.clear();
            if (anns == null) {
                return NO_PARAMETER_ANNOTATIONS;
            }
            ResolvedType[][] result = new ResolvedType[anns.length][];
            for (int i = 0; i < anns.length; ++i) {
                if (anns[i] == null) continue;
                result[i] = new ResolvedType[anns[i].length];
                for (int j = 0; j < anns[i].length; ++j) {
                    result[i][j] = this.world.resolve(UnresolvedType.forSignature(anns[i][j].getTypeSignature()));
                }
            }
            return result;
        }
        catch (ClassNotFoundException jc) {
            AccessibleObject ao = (AccessibleObject)((Object)onMember);
            Annotation[][] anns = null;
            if (onMember instanceof java.lang.reflect.Method) {
                anns = ((java.lang.reflect.Method)ao).getParameterAnnotations();
            } else if (onMember instanceof Constructor) {
                anns = ((Constructor)ao).getParameterAnnotations();
            } else if (onMember instanceof java.lang.reflect.Field) {
                // empty if block
            }
            if (anns == null) {
                return NO_PARAMETER_ANNOTATIONS;
            }
            ResolvedType[][] result = new ResolvedType[anns.length][];
            for (int i = 0; i < anns.length; ++i) {
                if (anns[i] == null) continue;
                result[i] = new ResolvedType[anns[i].length];
                for (int j = 0; j < anns[i].length; ++j) {
                    result[i][j] = UnresolvedType.forName(anns[i][j].annotationType().getName()).resolve(this.world);
                }
            }
            return result;
        }
    }

    static {
        try {
            useCachingClassLoaderRepository = System.getProperty("Xset:bcelRepositoryCaching", "true").equalsIgnoreCase("true");
        }
        catch (Throwable t) {
            useCachingClassLoaderRepository = false;
        }
    }
}

