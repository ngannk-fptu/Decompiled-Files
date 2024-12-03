/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.reflect.GenericSignatureInformationProvider;
import org.aspectj.weaver.reflect.IReflectionWorld;
import org.aspectj.weaver.reflect.Java14GenericSignatureInformationProvider;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegate;
import org.aspectj.weaver.reflect.ReflectionBasedResolvedMemberImpl;

public class ReflectionBasedReferenceTypeDelegateFactory {
    public static ReflectionBasedReferenceTypeDelegate createDelegate(ReferenceType forReferenceType, World inWorld, ClassLoader usingClassLoader) {
        try {
            ReflectionBasedReferenceTypeDelegate rbrtd;
            Class<?> c = Class.forName(forReferenceType.getName(), false, usingClassLoader);
            if (LangUtil.is15VMOrGreater() && (rbrtd = ReflectionBasedReferenceTypeDelegateFactory.create15Delegate(forReferenceType, c, usingClassLoader, inWorld)) != null) {
                return rbrtd;
            }
            return new ReflectionBasedReferenceTypeDelegate(c, usingClassLoader, inWorld, forReferenceType);
        }
        catch (ClassNotFoundException cnfEx) {
            return null;
        }
    }

    public static ReflectionBasedReferenceTypeDelegate createDelegate(ReferenceType forReferenceType, World inWorld, Class<?> clazz) {
        ReflectionBasedReferenceTypeDelegate rbrtd;
        if (LangUtil.is15VMOrGreater() && (rbrtd = ReflectionBasedReferenceTypeDelegateFactory.create15Delegate(forReferenceType, clazz, clazz.getClassLoader(), inWorld)) != null) {
            return rbrtd;
        }
        return new ReflectionBasedReferenceTypeDelegate(clazz, clazz.getClassLoader(), inWorld, forReferenceType);
    }

    public static ReflectionBasedReferenceTypeDelegate create14Delegate(ReferenceType forReferenceType, World inWorld, ClassLoader usingClassLoader) {
        try {
            Class<?> c = Class.forName(forReferenceType.getName(), false, usingClassLoader);
            return new ReflectionBasedReferenceTypeDelegate(c, usingClassLoader, inWorld, forReferenceType);
        }
        catch (ClassNotFoundException cnfEx) {
            return null;
        }
    }

    private static ReflectionBasedReferenceTypeDelegate create15Delegate(ReferenceType forReferenceType, Class forClass, ClassLoader usingClassLoader, World inWorld) {
        try {
            Class<?> delegateClass = Class.forName("org.aspectj.weaver.reflect.Java15ReflectionBasedReferenceTypeDelegate");
            ReflectionBasedReferenceTypeDelegate ret = (ReflectionBasedReferenceTypeDelegate)delegateClass.newInstance();
            ret.initialize(forReferenceType, forClass, usingClassLoader, inWorld);
            return ret;
        }
        catch (ClassNotFoundException cnfEx) {
            throw new IllegalStateException("Attempted to create Java 1.5 reflection based delegate but org.aspectj.weaver.reflect.Java15ReflectionBasedReferenceTypeDelegate was not found on classpath");
        }
        catch (InstantiationException insEx) {
            throw new IllegalStateException("Attempted to create Java 1.5 reflection based delegate but InstantiationException: " + insEx + " occured");
        }
        catch (IllegalAccessException illAccEx) {
            throw new IllegalStateException("Attempted to create Java 1.5 reflection based delegate but IllegalAccessException: " + illAccEx + " occured");
        }
    }

    private static GenericSignatureInformationProvider createGenericSignatureProvider(World inWorld) {
        if (LangUtil.is15VMOrGreater()) {
            try {
                Class<?> providerClass = Class.forName("org.aspectj.weaver.reflect.Java15GenericSignatureInformationProvider");
                Constructor<?> cons = providerClass.getConstructor(World.class);
                GenericSignatureInformationProvider ret = (GenericSignatureInformationProvider)cons.newInstance(inWorld);
                return ret;
            }
            catch (ClassNotFoundException providerClass) {
            }
            catch (NoSuchMethodException nsmEx) {
                throw new IllegalStateException("Attempted to create Java 1.5 generic signature provider but: " + nsmEx + " occured");
            }
            catch (InstantiationException insEx) {
                throw new IllegalStateException("Attempted to create Java 1.5 generic signature provider but: " + insEx + " occured");
            }
            catch (InvocationTargetException invEx) {
                throw new IllegalStateException("Attempted to create Java 1.5 generic signature provider but: " + invEx + " occured");
            }
            catch (IllegalAccessException illAcc) {
                throw new IllegalStateException("Attempted to create Java 1.5 generic signature provider but: " + illAcc + " occured");
            }
        }
        return new Java14GenericSignatureInformationProvider();
    }

    public static ResolvedMember createResolvedMember(java.lang.reflect.Member reflectMember, World inWorld) {
        if (reflectMember instanceof Method) {
            return ReflectionBasedReferenceTypeDelegateFactory.createResolvedMethod((Method)reflectMember, inWorld);
        }
        if (reflectMember instanceof Constructor) {
            return ReflectionBasedReferenceTypeDelegateFactory.createResolvedConstructor((Constructor)reflectMember, inWorld);
        }
        return ReflectionBasedReferenceTypeDelegateFactory.createResolvedField((Field)reflectMember, inWorld);
    }

    public static ResolvedMember createResolvedMethod(Method aMethod, World inWorld) {
        ReflectionBasedResolvedMemberImpl ret = new ReflectionBasedResolvedMemberImpl(Member.METHOD, (UnresolvedType)ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(aMethod.getDeclaringClass(), (IReflectionWorld)((Object)inWorld)), aMethod.getModifiers(), (UnresolvedType)ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(aMethod.getReturnType(), (IReflectionWorld)((Object)inWorld)), aMethod.getName(), (UnresolvedType[])ReflectionBasedReferenceTypeDelegateFactory.toResolvedTypeArray(aMethod.getParameterTypes(), inWorld), (UnresolvedType[])ReflectionBasedReferenceTypeDelegateFactory.toResolvedTypeArray(aMethod.getExceptionTypes(), inWorld), aMethod);
        if (inWorld instanceof IReflectionWorld) {
            ret.setAnnotationFinder(((IReflectionWorld)((Object)inWorld)).getAnnotationFinder());
        }
        ret.setGenericSignatureInformationProvider(ReflectionBasedReferenceTypeDelegateFactory.createGenericSignatureProvider(inWorld));
        return ret;
    }

    public static ResolvedMember createResolvedAdviceMember(Method aMethod, World inWorld) {
        ReflectionBasedResolvedMemberImpl ret = new ReflectionBasedResolvedMemberImpl(Member.ADVICE, (UnresolvedType)ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(aMethod.getDeclaringClass(), (IReflectionWorld)((Object)inWorld)), aMethod.getModifiers(), (UnresolvedType)ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(aMethod.getReturnType(), (IReflectionWorld)((Object)inWorld)), aMethod.getName(), (UnresolvedType[])ReflectionBasedReferenceTypeDelegateFactory.toResolvedTypeArray(aMethod.getParameterTypes(), inWorld), (UnresolvedType[])ReflectionBasedReferenceTypeDelegateFactory.toResolvedTypeArray(aMethod.getExceptionTypes(), inWorld), aMethod);
        if (inWorld instanceof IReflectionWorld) {
            ret.setAnnotationFinder(((IReflectionWorld)((Object)inWorld)).getAnnotationFinder());
        }
        ret.setGenericSignatureInformationProvider(ReflectionBasedReferenceTypeDelegateFactory.createGenericSignatureProvider(inWorld));
        return ret;
    }

    public static ResolvedMember createStaticInitMember(Class forType, World inWorld) {
        return new ResolvedMemberImpl(Member.STATIC_INITIALIZATION, ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(forType, (IReflectionWorld)((Object)inWorld)), 8, UnresolvedType.VOID, "<clinit>", new UnresolvedType[0], new UnresolvedType[0]);
    }

    public static ResolvedMember createResolvedConstructor(Constructor aConstructor, World inWorld) {
        ReflectionBasedResolvedMemberImpl ret = new ReflectionBasedResolvedMemberImpl(Member.CONSTRUCTOR, (UnresolvedType)ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(aConstructor.getDeclaringClass(), (IReflectionWorld)((Object)inWorld)), aConstructor.getModifiers(), UnresolvedType.VOID, "<init>", (UnresolvedType[])ReflectionBasedReferenceTypeDelegateFactory.toResolvedTypeArray(aConstructor.getParameterTypes(), inWorld), (UnresolvedType[])ReflectionBasedReferenceTypeDelegateFactory.toResolvedTypeArray(aConstructor.getExceptionTypes(), inWorld), aConstructor);
        if (inWorld instanceof IReflectionWorld) {
            ret.setAnnotationFinder(((IReflectionWorld)((Object)inWorld)).getAnnotationFinder());
        }
        ret.setGenericSignatureInformationProvider(ReflectionBasedReferenceTypeDelegateFactory.createGenericSignatureProvider(inWorld));
        return ret;
    }

    public static ResolvedMember createResolvedField(Field aField, World inWorld) {
        ReflectionBasedResolvedMemberImpl ret = new ReflectionBasedResolvedMemberImpl(Member.FIELD, (UnresolvedType)ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(aField.getDeclaringClass(), (IReflectionWorld)((Object)inWorld)), aField.getModifiers(), (UnresolvedType)ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(aField.getType(), (IReflectionWorld)((Object)inWorld)), aField.getName(), new UnresolvedType[0], aField);
        if (inWorld instanceof IReflectionWorld) {
            ret.setAnnotationFinder(((IReflectionWorld)((Object)inWorld)).getAnnotationFinder());
        }
        ret.setGenericSignatureInformationProvider(ReflectionBasedReferenceTypeDelegateFactory.createGenericSignatureProvider(inWorld));
        return ret;
    }

    public static ResolvedMember createHandlerMember(Class exceptionType, Class inType, World inWorld) {
        return new ResolvedMemberImpl(Member.HANDLER, ReflectionBasedReferenceTypeDelegateFactory.toResolvedType(inType, (IReflectionWorld)((Object)inWorld)), 8, "<catch>", "(" + inWorld.resolve(exceptionType.getName()).getSignature() + ")V");
    }

    public static ResolvedType resolveTypeInWorld(Class aClass, World aWorld) {
        String className = aClass.getName();
        if (aClass.isArray()) {
            return aWorld.resolve(UnresolvedType.forSignature(className.replace('.', '/')));
        }
        return aWorld.resolve(className);
    }

    private static ResolvedType toResolvedType(Class aClass, IReflectionWorld aWorld) {
        return aWorld.resolve(aClass);
    }

    private static ResolvedType[] toResolvedTypeArray(Class[] classes, World inWorld) {
        ResolvedType[] ret = new ResolvedType[classes.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = ((IReflectionWorld)((Object)inWorld)).resolve(classes[i]);
        }
        return ret;
    }
}

